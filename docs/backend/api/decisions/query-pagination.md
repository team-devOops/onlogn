# 쿼리/페이지네이션 결정 (v1)

이 문서는 O(NlogN) v1의 목록 엔드포인트와 월간 캘린더 집계 엔드포인트에 대한 결정적 쿼리 규칙을 정의한다.

## 범위

- `GET /api/v1/tasks`(owner 목록)에 적용된다.
- `GET /api/v1/profiles/{slug}/tasks`(public profile 목록)에 적용된다.
- 월간 집계 엔드포인트(owner 및 profile-public 변형)에 적용되며 `year`/`month` 쿼리 의미를 포함한다.

## 페이지네이션 계약 (`offset`/`limit`)

- `offset`는 정수이고 0 이상이어야 한다.
- `offset`를 생략하면 기본값은 `0`이다.
- `limit`는 `1` 이상 `max_limit` 이하의 정수여야 한다.
- `limit`를 생략하면 기본값은 `20`이다.
- v1에서 `max_limit`는 `100`으로 고정한다.
- `offset`/`limit`가 유효하지 않으면(음수, 정수 아님, `limit > max_limit`) RFC9457 problem details와 함께 `400`을 반환해야 한다.
- v1 목록 엔드포인트는 cursor pagination으로 전환하면 안 된다.

## 기준 정렬과 타이브레이커

- task 목록의 기준 정렬 순서는 `created_at desc`로 고정한다.
- `created_at`이 같을 때 결정적 타이브레이커는 `id desc`로 고정한다.
- `sort` 쿼리 파라미터를 받는 경우, v1은 정확히 `created_at desc`만 허용해야 하며 다른 정렬은 `400`을 반환해야 한다.
- 구현체는 `offset`, `limit`로 잘라내기 전에 `created_at desc` + 타이브레이커를 먼저 적용해야 한다.

## 엔드포인트별 허용 필터

### Owner 목록 (`GET /api/v1/tasks`)

- `status`: 선택, `todo|in_progress|done` 중 하나.
- `visibility`: 선택, `private|public` 중 하나.
- `group_id`: 선택, UUID 또는 그룹 미지정 task를 뜻하는 리터럴 `null` (`group_id = null`).
- `due_date_from`: 선택, `YYYY-MM-DD`.
- `due_date_to`: 선택, `YYYY-MM-DD`, 둘 다 있을 때 `due_date_from` 이상이어야 한다.
- 알려지지 않은 필터 키는 쿼리 동작의 결정성을 위해 `400`을 반환해야 한다.

### Profile public 목록 (`GET /api/v1/profiles/{slug}/tasks`)

- 서버는 호출자 입력과 무관하게 `visibility=public`을 강제해야 한다.
- `status`: 선택, `todo|in_progress|done` 중 하나.
- `group_id`: 선택, UUID 또는 리터럴 `null`.
- `due_date_from`, `due_date_to`: 선택 날짜 범위이며 owner 목록과 동일한 검증 규칙을 따른다.
- `visibility` 필터 입력은 무시하거나 거부해야 하며, non-public task를 노출하면 안 된다.
- 전역 탐색/검색 의미를 도입하면 안 된다(프로필 간/public feed 쿼리 확장 금지).

## 월간 캘린더 쿼리 규칙 (`year`/`month` window)

- 월간 엔드포인트 쿼리는 `year`, `month`를 필수 정수로 사용한다.
- 유효한 `year` 범위는 `1970..2100`이다.
- 유효한 `month` 범위는 `1..12`이다.
- `year` 또는 `month`가 누락되거나 잘못되면 RFC9457 problem details와 함께 `400`을 반환해야 한다.
- 월 버킷 기준 timezone은 인증 사용자는 요청자 timezone, 비인증 public profile 캘린더는 profile timezone, 없으면 `UTC`를 사용한다.
- `(year, month, timezone)` 튜플은 해당 timezone의 로컬 `YYYY-MM-01T00:00:00`부터 마지막 날 `23:59:59.999`까지 포함하는 월 범위를 정의한다.
- 출력 일별 시리즈는 해당 월 전체를 포함해야 하며, 대상 월의 매일 한 행씩 `date asc` 순서로 제공해야 한다.
- task가 없는 날도 0 값(`planned_count=0`, `done_count=0`, `in_progress_count=0`, `completion_rate=0`)으로 반드시 출력해야 한다.

## 응답 meta 기대사항

- 목록 응답은 `meta.offset`, `meta.limit`, `meta.total`을 포함하는 것이 좋다.
- `meta.sort`가 있으면 `created_at desc`와 타이브레이커 `id desc`를 보고하는 것이 좋다.
- 월간 응답은 집계에 사용한 `meta.year`, `meta.month`, `meta.timezone`을 포함하는 것이 좋다.
