# QuestWalk Clean Architecture + MVI Migration Plan

이 문서는 BoardMoa와 Yatzee 구현을 참고해 QuestWalk를 Clean Architecture + MVI로 정리하는 실행 계획이다.

## 참고한 패턴

### Yatzee

`feature:play`가 가장 직접적인 reducer형 MVI 예시다.

```text
PlayScreen
 -> onAction(PlayIntent)
 -> PlayViewModel.onAction()
 -> reduce(PlayUiState, PlayIntent)
 -> saveGameRecordUseCase()
 -> navigationEvent.emit(finalScore)
```

핵심:

- 상태 전이가 큰 플레이 화면은 Intent와 reducer로 관리한다.
- 게임 종료 같은 일회성 이동은 `MutableSharedFlow` event로 내보낸다.
- ViewModel은 UseCase만 호출한다.

### BoardMoa

`feature:record:add`가 복잡한 폼/저장 MVI 예시다.

```text
RecordAddScreen
 -> onAction(RecordAddIntent)
 -> RecordAddViewModel.onAction()
 -> reduce(state, intent)
 -> saveFormState()
 -> handleSideEffect(intent)
```

핵심:

- 입력 state 변경은 reducer에서 처리한다.
- 검색, 저장, 시리즈 데이터 로드 같은 외부 작업은 side effect로 분리한다.
- 저장 중 중복 클릭은 ViewModel 진입부에서 차단한다.
- 완료/실패는 Channel/Flow event로 UI에 전달한다.

## QuestWalk 목표 구조

```text
Route
    - ViewModel 주입
    - StateFlow 수집
    - Event 수집 후 navigation/snackbar/dialog 처리
    ↓
Screen
    - UiState 렌더링
    - 사용자 입력을 Intent로 변환
    ↓
ViewModel
    - onIntent()
    - reduce()
    - handleSideEffect()
    - UseCase 호출
    ↓
UseCase
    - 비즈니스 규칙
    - Repository interface 호출
    ↓
Repository/DataSource
```

## 공통 ViewModel 패턴

```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val useCase: SomeUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeatureUiState())
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<FeatureEvent>()
    val event = _event.asSharedFlow()

    fun onIntent(intent: FeatureIntent) {
        _uiState.update { state -> reduce(state, intent) }
        handleSideEffect(intent)
    }

    private fun reduce(state: FeatureUiState, intent: FeatureIntent): FeatureUiState {
        return when (intent) {
            // pure state transition
        }
    }

    private fun handleSideEffect(intent: FeatureIntent) {
        when (intent) {
            // usecase/service/event
        }
    }
}
```

## 적용 순서

### Phase 1. Home + Quest 연결

목표:

```text
QuestScreen select
 -> SelectQuestUseCase(keyword)
 -> Home current quest updates
```

작업:

- `QuestIntent.SelectQuest(keyword)` 추가
- `QuestEvent.QuestSelected` 또는 navigation callback 정리
- 실제 `SelectQuestUseCase` 호출 연결
- Home은 current quest StateFlow를 그대로 observe

### Phase 2. Home MVI 전환

목표:

```text
HomeIntent.StartClicked
 -> StartPlaySessionUseCase
 -> PlaySessionService.start
 -> state PLAYING
```

작업:

- `HomeIntent`, `HomeEvent` 추가
- `startSession/stopSession/resetSession` 직접 callback 제거
- `onIntent()`로 단일 진입점 정리
- 서비스 start/stop은 side effect로 이동
- 시작 실패 시 `HomeEvent.ShowError`

### Phase 3. Result 신규 Compose/MVI 구현

목표:

```text
Complete session
 -> CreateQuestResultUseCase
 -> PostHistoryUseCase
 -> UpdateQuestSuccessUseCase
 -> UpdateUserInfoUseCase
 -> ResultScreen
```

작업:

- `feature:result` 모듈 생성 또는 기존 route 연결
- `ResultIntent.Load(resultId)` / `ResultIntent.SaveCurrentSession`
- `ResultUiState.Loading/Success/Error`
- 기록 저장 후 resultId event emit

### Phase 4. Record -> Result 재진입

목표:

```text
RecordIntent.HistoryClicked(resultId)
 -> RecordEvent.NavigateToResult(resultId)
 -> ResultRoute(resultId)
```

### Phase 5. Camera/OCR Compose feature

목표:

```text
CameraIntent.Capture
 -> OCR/verification UseCase
 -> MarkSuccessUseCase
 -> Result 저장 플로우
```

카메라와 지도는 View 기반 화면을 복구하지 않고 Compose route에서 필요한 최소 AndroidView/공식 Compose wrapper만 사용한다.

## 현재 주의사항

- app 모듈의 legacy ViewBinding/Fragment 기반 코드는 제거 중이다.
- Camera/OCR/Maps 의존성은 app 모듈에 미리 두지 않고 해당 feature 구현 시 추가한다.
- Compose-only 기준이라 XML layout은 새로 추가하지 않는다.
- 기존 `UiState<T>`는 목록/상세 조회 화면에 유지 가능하다.
- 플레이/폼처럼 상태 전이가 큰 화면은 feature 전용 `UiState` data class를 우선한다.
