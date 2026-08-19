# Session Handoff

새 세션에서 이 문서를 먼저 읽는다. 작업을 마칠 때마다 갱신한다.

## 현재 상태

- 브랜치: `refactor/renewal` (origin과 동기화됨)
- 워킹 트리: 깨끗
- `dev`는 PR #135로 병합 완료. 이후 작업은 아직 `dev`에 반영되지 않았다
- `main`은 손대지 않았다

### 환경 주의사항

- `git` 명령이 dubious ownership으로 실패한다. 읽기/쓰기 모두 `git -c safe.directory=F:/001_android/QuestWalk ...` 형태로 실행한다
- 연결 가능한 기기: 갤럭시 S22(`R3CTB03BQ9H`, API 36), 레노버 태블릿(`HA23PJ05`, API 35)
- adb 경로: `C:\Users\ysh50\AppData\Local\Android\Sdk\platform-tools\adb.exe`
- 기기에 마지막으로 설치한 것은 **release 빌드**다. debug로 돌리려면 다시 설치해야 한다

## 완료된 작업

### 유저 누적 정보

`docs/USER_AGGREGATE_POLICY.md` 참고.

- `FirebaseUserDataSource`가 `runTransaction`과 `batch.commit()` 둘 다 `await()` 없이 호출해 원격에 아무것도 기록되지 않던 문제를 고쳤다
- 누적값을 더하지 않고 히스토리에서 매번 절대값으로 다시 계산한다. 멱등이라 이중 집계가 불가능하고 유실분도 복구된다
- `lastAggregatedAt`(UTC, `yyyy-MM-dd'T'HH:mm:ss'Z'`)을 기준선으로 쓴다. 히스토리에는 `registerAtUtc`를 함께 저장한다
- 실기기 검증 완료

### 결과 화면

- 경로 필터의 리스트 복사를 제거해 점 개수 제곱 연산을 없앴다
- 히스토리 복호화와 파싱을 `Dispatchers.Default`로 옮겼다
- 경로 가공(거리 필터, RDP, 리샘플링)을 `produceState` + 백그라운드로 옮겼다
- 데이터 대기와 지도 준비를 동시에 진행하고 로딩 단계를 하나로 합쳤다
- 경로 애니메이션 진행률을 `derivedStateOf`로 계단화해 프레임마다 리컴포지션이 일어나지 않게 했다
- 연출: 흰색에서 시작하는 그라데이션, 선두 점, 출발점 표시, 성공 핀 낙하와 파동, 마무리 카메라 맞춤

### 앱 시작

- 스플래시 대기를 최소 노출 시간으로 바꿨다. 예전에는 준비 시간에 2초가 그대로 더해졌다
- 로그인 판단을 로컬 캐시로 처리하고 원격 동기화는 `ApplicationScope`에서 백그라운드로 돌린다
- 결과: 4,375ms → 약 2,000ms

### 홈

- 뒤로가기 한 번에 앱이 종료되던 문제. 2초 안에 다시 눌러야 종료된다

### CI/CD

`docs/CI_CD.md` 참고.

- CI는 PR과 작업 브랜치 푸시에서 유닛 테스트 + `assembleDebug`를 돌린다. 통과 중이다
- 배포 워크플로는 `main` 푸시 시 서명 release APK를 Firebase App Distribution으로 보낸다. **아직 동작 확인 전이다**
- 등록된 Secrets: `LOCAL_PROPERTIES`, `GOOGLE_SERVICES_JSON`
- 미등록: `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`, `FIREBASE_APP_ID`, `FIREBASE_SERVICE_ACCOUNT`

## 성능 측정 방법

**debug 빌드로 판단하지 않는다.** Baseline Profile이 없고 ART 최적화가 꺼져 첫 실행이 실제보다 훨씬 느리다. 같은 화면에서 janky 비율이 debug 28~33%, release 14.6%로 갈렸다.

release는 디버그 키로 서명해 설치할 수 있다.

```bash
KEYSTORE_FILE="/c/Users/ysh50/.android/debug.keystore" \
KEYSTORE_PASSWORD=android KEY_ALIAS=androiddebugkey KEY_PASSWORD=android \
./gradlew :app:assembleRelease --offline
```

측정 명령:

```bash
adb -s R3CTB03BQ9H shell dumpsys gfxinfo com.hapataka.questwalk reset
# 재현 후
adb -s R3CTB03BQ9H shell dumpsys gfxinfo com.hapataka.questwalk | grep -E "Janky|percentile"
adb -s R3CTB03BQ9H logcat -d -v time Choreographer:I '*:S'
```

프레임 스킵 로그는 여러 프로세스가 섞이므로 `pidof`로 앱 PID를 걸러서 본다.

## 남은 과제

### 우선순위 높음

- **`targetSdk`가 26이다.** `build-logic` 어디에도 선언이 없어 AGP가 `minSdk` 값을 그대로 쓴다. Play Store는 36을 요구한다. `compileSdk`도 35라 같이 올려야 하고, AGP 상향이 필요할 수 있다. 포그라운드 서비스 타입, 알림 권한, 백그라운드 위치 권한이 줄줄이 걸린다
- **배포 파이프라인 미완성.** 키스토어 비밀번호를 찾지 못했다. `KEY_ALIAS`는 `key0`으로 확인됐고 `.idea/workspace.xml`에 남아 있었다. 비밀번호는 Android Studio의 Generate Signed APK 다이얼로그에 저장돼 있을 가능성이 높다
- **`main` 병합 전 버전 상향** — versionCode 13 / versionName 3.0으로 하기로 정했다

### 우선순위 낮음

- 스플래시가 두 번 뜨는 것처럼 보인다는 제보가 있다. `adb`로 띄웠을 때는 액티비티 재생성이 없었다. 런처 아이콘 실행에서 재현되는지 확인이 필요하다. `MainActivity`에 `android:screenOrientation="portrait"`만 있고 `android:configChanges`가 없는 점이 의심된다
- 앱 시작 시 `splash.start` 직후 메인 스레드가 막힌다. Hilt 그래프와 Firebase, Room이 ViewModel 생성 시점에 초기화되기 때문이다. 스플래시가 가려주고 있어 급하지 않다
- 결과 화면 첫 진입 시 지도 초기화로 메인 스레드가 잠시 막힌다. `MapView`는 View라 메인 스레드에 묶여 있어 옮길 수 없다. 없애려면 스플래시에서 `MapView`를 예열하는 방법뿐인데, 관리 부담 때문에 도입하지 않기로 했다. 더 손대려면 Baseline Profile이 정공법이다
- 집계가 최신 여부를 판단하려고 히스토리 전체를 내려받는다. `orderBy(registerAtUtc desc).limit(1)`로 줄일 수 있으나 Firestore 복합 색인이 필요하다
- MVI 미전환: Camera, Result, QuestDetail
- `UserInfo` 전역 상태 제거 (암호화 키)
- `.github/workflow/`(단수)는 오타로 남은 빈 디렉터리다. Actions가 읽지 않는다

## 실기기 검증 완료 항목

- 퀘스트 완료 후 누적 정보 반영, 앱 재시작 후 유지
- MyInfo 즉시 표시
- 사진 성공 후 포그라운드 서비스와 걸음 유지
- 경로 기록 품질 개선
- 결과 화면 경로 애니메이션 (release 빌드 기준 수용 가능)
