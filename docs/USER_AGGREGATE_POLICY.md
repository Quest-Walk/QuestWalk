# User Aggregate Policy

유저 누적 정보(총 시간/거리/걸음, 성공 키워드, 업적)를 어떻게 맞추는지 정리한다.

## 원칙

누적값은 히스토리에서 파생된 값이다. 더하지 않고 **매번 전체에서 다시 계산해 절대값으로 덮어쓴다.**

읽고-더하는 방식이 아니므로

- 몇 번을 실행해도 결과가 같다
- 트랜잭션이 필요 없다
- 중간에 반영되지 못한 기록이 다음 집계에서 함께 복구된다

## 기준선

유저 문서의 `lastAggregatedAt`에 마지막으로 반영한 기록의 UTC 시각을 남긴다.

- 형식은 `yyyy-MM-dd'T'HH:mm:ss'Z'`
- 고정 폭이라 사전순 비교가 곧 시간순 비교다
- 히스토리에도 같은 형식으로 `registerAtUtc`를 함께 저장한다

기존 `registerAt`은 기기 로컬 시각이라 타임존이 바뀌면 순서가 뒤집힌다. 그래서 비교용 시각을 UTC로 따로 둔다.

## 흐름

집계는 화면을 막지 않는다. 화면은 로컬만 바라보고, 원격 동기화는 뒤에서 돌린 뒤 로컬에 반영한다.

```text
앱 시작 (Splash)
 -> FetchUserInfoUseCase (로그인/설정 여부 판단에 필요하므로 대기)
 -> ApplicationScope로 ReconcileUserAggregateUseCase 발사 후 진행

퀘스트 완료
 -> PostHistory (실패하면 중단)
 -> ReconcileUserAggregateUseCase (실패해도 무시)

MyInfo 진입
 -> GetUserInfoUseCase 로 로컬만 구독
```

집계가 로컬 캐시까지 갱신하므로 구독 중인 화면이 새 값을 자동으로 받는다. 사용자가 직접 새로고침할 때만 원격까지 다녀온다.

`fetchUserInfo`는 로컬 캐시를 원격 값으로 덮어쓴다. 집계보다 먼저 돌려야 최신값이 남는다.

## 조회 비용

집계는 경로를 쓰지 않으므로 `getUserActivitySummary`로 요약만 만든다. 전체 히스토리를 도메인 모델로 바꾸면 기록마다 경로 암호문을 복호화하고 파싱하게 되는데, 그 비용을 피하기 위한 별도 경로다.

## 건너뛰기 조건

`lastAggregatedAt`이 있고 그보다 나중인 기록이 없으면 아무것도 하지 않는다.
`lastAggregatedAt`이 비어 있으면 한 번도 집계한 적이 없다는 뜻이므로 항상 다시 계산한다.

`registerAtUtc`가 없는 과거 기록만 있는 경우에는 현재 시각을 기준선으로 삼는다. 그 뒤에 들어오는 기록은 모두 `registerAtUtc`를 갖는다.

## 삭제하지 않는다

`successKeywords`와 `achievements` 서브컬렉션은 문서 ID가 각각 키워드와 업적 ID다. 집계는 **추가만** 하고 지우지 않는다. 히스토리가 없는 과거 키워드나 업적이 남아 있어도 유실되지 않는다.

## 배경

이전 구현은 `runTransaction`과 `batch.commit()` 둘 다 `await()` 없이 호출했다. 빈 batch가 먼저 commit되고 실제 쓰기는 이미 닫힌 batch에 쌓여 실패했으며, 그 예외마저 삼켜져 원격에는 아무것도 기록되지 않았다. 로컬 캐시만 갱신된 상태에서 MyInfo 진입 시 옛 원격 값이 로컬을 덮어써 누적치가 되돌아갔다.
