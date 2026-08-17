# QuestWalk Working Conventions

이 문서는 QuestWalk 업데이트 작업 시 우선 참조할 기준이다. 아래 문서들을 분석해 현재 프로젝트에 맞는 규칙만 정리했다.

- `F:\001_android\yatzee\docs\*.md`
- `F:\000_projects\BoardMoa\.claude\*.md`
- `F:\000_projects\Subby\.claude\*.md`

세부 로컬 참조 문서는 `.claude/`에도 같은 기준으로 정리되어 있다. 단, `.claude/`는 현재 `.gitignore` 대상이므로 저장소 기준 문서는 이 파일을 우선한다.

## 1. 작업 흐름

새 기능이나 여러 계층을 건드리는 수정 전에는 call stack 형태로 먼저 브리핑한다.

```text
[Feature/ViewModel].method()
    - 사용자 action 수신
    - 호출: UseCase()
    ↓
[UseCase].invoke()
    - 비즈니스 규칙 적용
    - 호출: Repository.method()
    ↓
[Repository].method()
    - Model/Dto 변환
    - 호출: DataSource.method()
    ↓
[DataSource].method()
    - Firebase/Retrofit/Room 접근
    - return Result<T>
```

작업은 작은 단위로 진행하고, 관련 없는 리팩토링은 섞지 않는다.

업데이트를 위한 최소 플레이 루프는 아래 순서로 완성한다.

```text
Quest select
 -> Home current quest update
 -> Start session
 -> Stop/complete session
 -> Create result
 -> Save history
 -> Update quest success
 -> Update user aggregate
 -> Show result
 -> Record reopens result
```

카메라/OCR/이미지 업로드는 이 루프가 먼저 연결된 뒤 인증 수단으로 붙인다.

## 2. 아키텍처

QuestWalk는 Kotlin, Jetpack Compose, Hilt, Firebase, Retrofit, DataStore, Room 기반 Android 멀티모듈 앱이다.

의존성은 아래 방향으로만 흐른다.

```text
Screen/Composable
 -> ViewModel
 -> UseCase
 -> Repository interface
 -> Repository implementation
 -> DataSource interface
 -> Remote/Local implementation
```

직접 참조 금지:

- Screen에서 UseCase, Repository, DataSource 호출 금지
- ViewModel에서 Repository implementation, DataSource, Firebase, Room 직접 참조 금지
- UseCase에서 DataSource 직접 참조 금지
- feature 모듈에서 Entity, DAO, Firebase response 직접 참조 금지

모듈 책임:

| 모듈 | 책임 |
| --- | --- |
| `app` | 앱 패키징, `MyApplication`, manifest/meta-data |
| `feature:main` | Single Activity, Scaffold, 최상위 NavHost, MainNavigator |
| `feature:*` | Route/Screen/ViewModel/UiState/UiModel/navigation |
| `core:model` | 순수 domain model |
| `core:domain` | UseCase, Repository interface |
| `core:data` | Repository implementation, Dto -> Model |
| `core:data-api` | DataSource interface, Dto |
| `core:remote` | Firebase, Retrofit, Fused Location, Response -> Dto |
| `core:local` | Room, DataStore, Entity/DAO |
| `core:designsystem` | Theme, color, typography, 공통 디자인 컴포넌트 |
| `core:ui` | 공통 UI state, CompositionLocal, 재사용 UI |
| `core:service` | Android Service, sensor manager |

모델 네이밍:

| 계층 | 위치 | 접미사 | 예시 |
| --- | --- | --- | --- |
| Remote 원본 | `core:remote` | `Response` | `WeatherResponse` |
| DataSource 계약 | `core:data-api` | `Dto` | `HistoryDto` |
| Local DB | `core:local` | `Entity` | `UserEntity` |
| Domain | `core:model` | 없음 | `Quest`, `History` |
| Presentation | `feature:*` | `UiModel` | `QuestItemUiModel` |
| UI State | `feature:*` | `UiState` | `HomeUiState` |

필드명 표준:

| 개념 | 표준 이름 | 피할 이름 |
| --- | --- | --- |
| 사용자 이름 | `userName` | `nickName` |
| 캐릭터 타입 | `characterType` | `characterId` |
| 퀘스트 키워드 | `keyword` | `keyWord`, `quest` |
| 소요 시간 | `duration` | `time` |
| 이미지 URL | `imageUrl` | `questImg` |
| 업적 ID | `achievementId` | `achieveId` |
| 등록 시간 | `registerAt` | `date`, `created` |

매핑 책임:

| 변환 | 담당 |
| --- | --- |
| Response -> Dto | `core:remote` |
| Entity -> Dto/Model | `core:local` 또는 `core:data` |
| Dto -> Domain Model | `core:data` |
| Domain Model -> UiModel/UiState | feature ViewModel 또는 mapper |
| 날짜/거리/시간/칼로리 포맷 | feature ViewModel 또는 UiModel |

## 3. UI 아키텍처

Composable은 아래 계층으로 분리한다.

```text
Route(internal)
 -> Screen(private)
 -> Content/Section/Component(private)
```

Route:

- `hiltViewModel()`로 ViewModel을 주입한다.
- `collectAsStateWithLifecycle()`로 state를 수집한다.
- navigation callback, dialog dismiss, event collect를 연결한다.
- 실제 레이아웃과 비즈니스 계산을 넣지 않는다.

Screen:

- ViewModel을 직접 참조하지 않는다.
- 전달받은 state와 callback만 사용한다.
- `UiState.Loading`, `UiState.Success`, `UiState.Failure` 분기를 처리한다.

UI Model:

- UI에는 domain model을 직접 노출하지 않는 것을 우선한다.
- 날짜, 거리, 시간, 칼로리, 성공률 등 포맷팅은 ViewModel/UiModel에서 준비한다.
- 폼 화면은 primitive state를 사용할 수 있고, domain model 생성은 저장 시점에 ViewModel에서 한다.

## 3.1 MVI 기준

QuestWalk의 feature ViewModel은 Yatzee `PlayViewModel`과 BoardMoa `RecordAddViewModel` 패턴을 기준으로 MVI 형태를 사용한다.

기본 구조:

```text
Screen event
 -> Intent
 -> ViewModel.onIntent()
 -> reduce(state, intent)
 -> handleSideEffect(intent)
 -> StateFlow<UiState>
 -> SharedFlow/Channel<Event>
```

네이밍:

| 역할 | 이름 | 예시 |
| --- | --- | --- |
| 사용자 입력 | `{Feature}Intent` | `HomeIntent`, `QuestIntent` |
| 화면 상태 | `{Feature}UiState` | `HomeUiState` |
| 일회성 이벤트 | `{Feature}Event` | `HomeEvent.NavigateToCamera` |
| 상태 변경 함수 | `reduce(state, intent)` | `private fun reduce(...)` |
| 비동기/외부 작업 | `handleSideEffect(intent)` | 저장, 조회, navigation event emit |

Intent 규칙:

- 클릭, 입력, 선택, 새로고침, 저장 요청을 모두 Intent로 표현한다.
- 단순 navigation 클릭도 ViewModel 판단이 필요하면 Intent로 받는다.
- 상위 navigator가 즉시 처리해도 되는 순수 뒤로가기만 callback으로 유지할 수 있다.

Reducer 규칙:

- `reduce()`는 가능한 한 순수 함수로 둔다.
- `reduce()` 안에서 UseCase, Repository, Service, navigation event를 직접 호출하지 않는다.
- `reduce()`는 현재 state와 intent만 보고 다음 state를 만든다.
- 저장 중 중복 클릭 방지 같은 UI 상태 전이는 reducer에서 처리한다.

Side effect 규칙:

- UseCase 호출, Service start/stop, debounce search, 저장, event emit은 `handleSideEffect()` 또는 명시적 private function에서 처리한다.
- side effect 완료 후 `_uiState.update { ... }`로 결과 상태를 반영한다.
- navigation, snackbar, toast 같은 일회성 결과는 `SharedFlow` 또는 `Channel` event로 내보낸다.

QuestWalk 적용 우선순위:

1. `Home`: 플레이 세션 상태 전이가 핵심이므로 `HomeIntent`/`HomeEvent`로 전환
2. `Quest`: 퀘스트 선택을 실제 current quest 반영 이벤트로 연결
3. `Result`: 결과 저장/표시 플로우를 MVI로 신규 구현
4. `Record`: 기록 선택 -> Result 재진입 이벤트 정리
5. `Onboarding/MyInfo/Weather`: 단순 화면은 현재 구조 유지 후 점진 전환

예시:

```kotlin
sealed interface HomeIntent {
    data object StartClicked : HomeIntent
    data object StopClicked : HomeIntent
    data object CompleteClicked : HomeIntent
    data object CameraClicked : HomeIntent
    data object QuestChangeClicked : HomeIntent
}

sealed interface HomeEvent {
    data object NavigateToCamera : HomeEvent
    data object NavigateToQuest : HomeEvent
    data class ShowError(val message: String) : HomeEvent
}
```

Navigation:

- `NavController`는 하위 UI로 넘기지 않는다.
- Screen 하위에는 `onBackClick`, `onQuestSelected`, `onCameraClick` 같은 callback만 넘긴다.
- route argument는 `core:navigation`의 `@Serializable` route model을 사용한다.

Insets/Padding:

- `MainScreen`의 `Scaffold` padding은 `LocalPaddingValues`로 전달한다.
- Screen root에서 `.padding(padding)`을 적용한다.
- 각 화면이 임의로 system inset을 중복 처리하지 않는다.

Design:

- 색상은 `core:designsystem/theme/Color.kt` 토큰을 우선 사용한다.
- 폰트는 `Typography`를 우선 사용한다.
- 공통 버튼, 이미지 버튼, dialog, chip이 있으면 먼저 재사용한다.
- spacing, padding, size, corner radius는 4dp grid를 따른다.

권장 dp:

```text
4, 8, 12, 16, 20, 24, 28, 32, 36, 40, 44, 48, 56, 64, 80, 96
```

## 4. 코드 컨벤션

- Kotlin official style을 따른다.
- 들여쓰기는 4 spaces를 사용한다.
- wildcard import를 사용하지 않는다.
- 기본은 `private` 또는 `internal`을 우선한다.
- nullable은 의미가 있을 때만 사용하고, 임시 방편의 `!!`는 피한다.
- 복잡한 조건식은 의미 있는 private function으로 분리한다.

함수 시작부에서 필요한 값을 먼저 계산하고 변수 선언 블록을 만든다.

```kotlin
private fun createQuestResult(session: PlaySession, userId: String): History.QuestResult {
    val resultId = UUID.randomUUID().toString()
    val registeredAt = LocalDateTime.now()

    return History.QuestResult(
        id = resultId,
        userId = userId,
        registerAt = registeredAt,
        questKeyword = session.keyword,
        duration = session.duration,
        distance = session.distance,
        step = session.steps,
        isSuccess = session.isSuccess,
        route = session.route,
        successLocation = session.successLocation,
    )
}
```

ViewModel:

- `@HiltViewModel` 주입을 기본으로 한다.
- UseCase만 주입한다.
- UI state는 immutable data class로 둔다.
- 사용자 action은 `sealed interface {Feature}Action`으로 모은다.
- 일회성 event는 `SharedFlow`를 사용한다.

UseCase:

- 하나의 비즈니스 작업만 담당한다.
- `operator fun invoke()`를 기본으로 한다.
- 실패 가능 작업은 `Result<T>` 반환을 우선한다.
- Repository interface만 참조한다.
- 파라미터가 많으면 `Params` data class로 묶는다.

Repository/DataSource:

- Repository interface는 `core:domain`에 둔다.
- Repository implementation은 `core:data`에 둔다.
- 구현체 네이밍은 `Default{Name}Repository`를 사용한다.
- DataSource interface와 Dto는 `core:data-api`에 둔다.
- Remote 구현과 Response는 `core:remote`에 둔다.
- Local 구현과 Entity/DAO는 `core:local`에 둔다.

## 5. 커밋 컨벤션

형식:

```text
<type>: <subject>

- <bullet point 1>
- <bullet point 2>
- <bullet point 3>

<issue reference>
```

Type:

| Type | 용도 |
| --- | --- |
| `feat` | 사용자 기능 추가 또는 기존 기능 확장 |
| `fix` | 버그 수정 |
| `design` | UI 스타일, 레이아웃, 리소스 표현 변경 |
| `refactor` | 동작 변경 없는 구조 개선 |
| `docs` | 문서 추가/수정 |
| `test` | 테스트 추가/수정 |
| `chore` | 빌드, 설정, 의존성, 정리 작업 |
| `style` | 코드 포맷팅만 변경 |

Subject:

- 한국어로 작성한다.
- 50자 이내를 우선한다.
- 명사형 종결을 우선한다.
- 타입 뒤에는 콜론과 공백을 둔다.

예:

```text
feat: 퀘스트 선택 반영 추가
fix: 세션 정지 후 알림 종료 수정
design: 홈 진행 버튼 상태 스타일 변경
docs: 작업 컨벤션 문서 추가
```

금지:

- `Co-Authored-By` 태그 사용 금지
- 자동 생성 도구 문구 추가 금지
- 여러 의도를 한 커밋에 섞지 않기

## 6. 검증

일반 Android 변경 후:

```powershell
.\gradlew.bat :app:assembleDebug --offline --no-daemon --console=plain
```

필요 시:

```powershell
.\gradlew.bat :app:compileDebugKotlin --offline --no-daemon --console=plain
```

mapper, achievement 조건, 거리/시간 계산처럼 규칙성 있는 코드는 단위 테스트 추가를 우선 고려한다.

## 7. 리뷰 체크리스트

- 유저 플로우가 실제로 끝까지 이어지는가?
- ViewModel이 UseCase만 호출하는가?
- UseCase가 Repository interface만 호출하는가?
- feature에 Dto/Response/Entity/Firebase 타입이 새로 노출되지 않았는가?
- UI가 domain model 대신 UiState/UiModel을 표시하는가?
- 실패/로딩/빈 상태가 최소한으로 처리되는가?
- 중복 클릭으로 저장이 중복 실행되지 않는가?
- 빌드 또는 관련 테스트를 실행했는가?
