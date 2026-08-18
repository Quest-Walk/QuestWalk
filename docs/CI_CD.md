# CI/CD

GitHub Actions 기반 CI/CD 구성 문서다.

## 워크플로

| 파일 | 트리거 | 하는 일 |
| --- | --- | --- |
| `.github/workflows/ci.yml` | `main`/`dev`로의 PR, `dev`/`refactor/**` 푸시, 수동 실행 | 유닛 테스트 + `assembleDebug` 검증, debug APK 아티팩트 업로드 |
| `.github/workflows/release.yml` | `main` 푸시, 수동 실행 | 서명된 release APK 빌드 후 Firebase App Distribution 배포 |

빌드 환경은 JDK 17(temurin) + Gradle 캐시(`gradle/actions/setup-gradle`)를 사용한다.

## 필요한 GitHub Secrets

저장소 Settings > Secrets and variables > Actions 에 등록한다.

| 이름 | 내용 | 사용 워크플로 |
| --- | --- | --- |
| `LOCAL_PROPERTIES` | `local.properties`의 base64. `sdk.dir`은 제외한다 | CI, Release |
| `GOOGLE_SERVICES_JSON` | `app/google-services.json`의 base64 | CI, Release |
| `KEYSTORE_BASE64` | 업로드 키스토어(`quest_walk_keypath.jks`)의 base64 | Release |
| `KEYSTORE_PASSWORD` | 키스토어 비밀번호 | Release |
| `KEY_ALIAS` | 키 별칭 | Release |
| `KEY_PASSWORD` | 키 비밀번호 | Release |
| `FIREBASE_APP_ID` | Firebase Android 앱 ID (`1:xxx:android:yyy`) | Release |
| `FIREBASE_SERVICE_ACCOUNT` | Firebase 서비스 계정 JSON의 base64 | Release |

선택 항목으로 Variables에 `FIREBASE_TESTER_GROUPS`를 두면 배포 대상 그룹을 지정할 수 있다. 없으면 `testers`를 사용한다.

### base64 인코딩 (PowerShell)

```powershell
# local.properties (sdk.dir 제외본을 먼저 만든다)
Get-Content local.properties | Where-Object { $_ -notmatch '^sdk\.dir' } | Set-Content ci-local.properties
[Convert]::ToBase64String([IO.File]::ReadAllBytes("ci-local.properties")) | Set-Clipboard

# google-services.json
[Convert]::ToBase64String([IO.File]::ReadAllBytes("app/google-services.json")) | Set-Clipboard

# 키스토어
[Convert]::ToBase64String([IO.File]::ReadAllBytes("quest_walk_keypath.jks")) | Set-Clipboard
```

### Firebase 서비스 계정

1. Firebase Console > 프로젝트 설정 > 서비스 계정에서 키를 생성한다.
2. Google Cloud IAM에서 해당 계정에 `Firebase App Distribution Admin` 역할을 부여한다.
3. 받은 JSON을 base64로 인코딩해 `FIREBASE_SERVICE_ACCOUNT`에 등록한다.

## 로컬 릴리즈 서명

`app/build.gradle.kts`의 release signingConfig는 두 경로에서 값을 읽는다.

1. 루트의 `keystore.properties` (gitignore 대상)
2. 환경 변수 `KEYSTORE_FILE`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`

둘 다 없으면 release 서명 설정을 적용하지 않으므로 기존 로컬 개발 빌드는 영향을 받지 않는다.

`keystore.properties` 예시:

```properties
storeFile=quest_walk_keypath.jks
storePassword=****
keyAlias=****
keyPassword=****
```

확인 명령:

```powershell
.\gradlew.bat :app:signingReport --console=plain
```

## 배포 흐름

```text
작업 브랜치 푸시 / PR
 -> ci.yml: 유닛 테스트 + assembleDebug
 -> dev 병합
 -> main 병합
 -> release.yml: assembleRelease + 서명 검증
 -> Firebase App Distribution 배포
```

릴리즈 노트는 `versionName`, `versionCode`, 커밋 해시와 최근 커밋 메시지 15개로 자동 생성한다.

## 주의

- `versionCode`는 수동 관리다. `main` 병합 전에 올려야 같은 버전이 덮어써지지 않는다.
- 워크플로는 종료 시 복원한 비밀 파일을 삭제한다.
- `.github/workflow/`(단수) 디렉터리는 과거 오타로 남은 것이며 Actions가 읽지 않는다.
