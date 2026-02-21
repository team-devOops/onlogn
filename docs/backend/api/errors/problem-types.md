# RFC9457 문제 유형 (v1)

## 목적과 범위

- 이 문서는 O(NlogN) v1 API의 기준 RFC9457 오류 분류 체계를 고정한다.
- 모든 오류 응답은 `application/problem+json`과 RFC9457 핵심 멤버 `type`, `title`, `status`, `detail`, `instance`를 사용해야 한다.
- 이 문서의 Problem `type` 값은 안정 식별자이며, 한 번 공개되면 변경하면 안 된다.

## RFC9457 기준선

- 필수 응답 미디어 타입: `application/problem+json`
- 모든 problem 객체에 필요한 핵심 멤버:
  - `type`: 문제 유형을 식별하는 URI 참조
  - `title`: 해당 유형의 짧고 사람이 읽을 수 있는 요약
  - `status`: 이 발생 건에 생성된 HTTP 상태 코드
  - `detail`: 이 발생 건에 대한 사람이 읽을 수 있는 설명
  - `instance`: 이 특정 발생 건을 식별하는 URI 참조
- 필요 시 확장 멤버를 추가할 수 있다(예: `errors`, `request_id`, `retry_after_seconds`).

## 기준 문제 유형 카탈로그

기준 네임스페이스: `https://api.onlogn.com/problems/`

| HTTP status | Canonical problem type | Title | Primary use |
|---|---|---|---|
| 400 | `https://api.onlogn.com/problems/bad-request` | Bad Request | 문법이 잘못됐거나, 지원하지 않는 파라미터 형태, 유효하지 않은 JSON body |
| 401 | `https://api.onlogn.com/problems/authentication-required` | Authentication Required | 액세스 토큰 누락, 만료, 또는 무효 |
| 403 | `https://api.onlogn.com/problems/permission-denied` | Permission Denied | 인증됐지만 해당 주체는 작업 권한이 없음 |
| 404 | `https://api.onlogn.com/problems/resource-not-found` | Resource Not Found | 대상 리소스가 없거나 공개 범위상 보이지 않음 |
| 409 | `https://api.onlogn.com/problems/state-conflict` | State Conflict | 리소스 상태 충돌(동시성, 고유 제약, 이미 적용된 작업) |
| 422 | `https://api.onlogn.com/problems/validation-failed` | Validation Failed | 문법은 맞지만 의미 검증에 실패한 입력 |
| 429 | `https://api.onlogn.com/problems/rate-limit-exceeded` | Rate Limit Exceeded | 스로틀링 정책으로 요청이 거부됨 |
| 500 | `https://api.onlogn.com/problems/internal-server-error` | Internal Server Error | 예기치 않은 서버 측 실패 |

## 엔드포인트 클래스별 고정 상태 매핑

| Endpoint class | Condition | HTTP status | Problem type |
|---|---|---|---|
| Auth endpoints (`/auth/*`) | bearer token 누락, 무효, 만료 | 401 | `https://api.onlogn.com/problems/authentication-required` |
| Auth endpoints (`/auth/*`) | OAuth code 형식 오류 또는 미지원 payload 형태 | 400 | `https://api.onlogn.com/problems/bad-request` |
| 보호된 owner 엔드포인트 (`/users/me`, `/groups`, `/tasks`) | 인증됐지만 대상 소유권 경계에서 허용되지 않음 | 403 | `https://api.onlogn.com/problems/permission-denied` |
| Slug/id 조회 엔드포인트 | 요청 엔터티를 찾지 못함 | 404 | `https://api.onlogn.com/problems/resource-not-found` |
| 쓰기 엔드포인트 (`POST/PUT/PATCH`) | 비즈니스 검증 실패(enum/date/range/domain 규칙) | 422 | `https://api.onlogn.com/problems/validation-failed` |
| 쓰기 엔드포인트 (`POST/PUT/PATCH/DELETE`) | 버전 충돌, 중복 키, 이미 확정된 전이 | 409 | `https://api.onlogn.com/problems/state-conflict` |
| 모든 엔드포인트 | 속도 제한 정책 초과 | 429 | `https://api.onlogn.com/problems/rate-limit-exceeded` |
| 모든 엔드포인트 | 처리되지 않은 서버 장애 | 500 | `https://api.onlogn.com/problems/internal-server-error` |

## 오류 클래스 규칙

- Authentication (401): `title`은 `Authentication Required`를 유지해야 하며, `detail`은 민감한 토큰 정보를 노출하지 않고 누락/만료/무효 사유를 설명하는 것이 좋다.
- Permission (403): `title`은 `Permission Denied`를 유지해야 하며, `detail`은 거부된 동작 경계를 설명하는 것이 좋다(예: owner 전용 리소스).
- Validation (422): `title`은 `Validation Failed`를 유지해야 하며, 필드 단위 이슈를 담은 `errors` 확장 배열을 포함해야 한다.
- Conflict (409): `title`은 `State Conflict`를 유지해야 하며, 구분이 필요하면 `conflict_code` 같은 확장을 포함한다.
- Rate limit (429): `title`은 `Rate Limit Exceeded`를 유지해야 하며, `retry_after_seconds` 확장을 포함하고 `Retry-After` 헤더와 일치시킨다.
- Server error (500): `title`은 `Internal Server Error`를 유지해야 하며, `detail`에서 내부 정보를 노출하지 말고 지원 추적용 `request_id`를 포함한다.

## 문제 객체 예시

### 인증 오류 (401)

```json
{
  "type": "https://api.onlogn.com/problems/authentication-required",
  "title": "Authentication Required",
  "status": 401,
  "detail": "Access token is missing or expired.",
  "instance": "/api/v1/tasks/req_01JZ4T3Q8R9A2F4M7N6",
  "request_id": "req_01JZ4T3Q8R9A2F4M7N6"
}
```

### 권한 오류 (403)

```json
{
  "type": "https://api.onlogn.com/problems/permission-denied",
  "title": "Permission Denied",
  "status": 403,
  "detail": "You cannot modify tasks owned by another user.",
  "instance": "/api/v1/tasks/task_123/req_01JZ4T4H1X2B3C4D5E6"
}
```

### 검증 오류 (422)

```json
{
  "type": "https://api.onlogn.com/problems/validation-failed",
  "title": "Validation Failed",
  "status": 422,
  "detail": "Request body validation failed.",
  "instance": "/api/v1/tasks/req_01JZ4T59K8L7M6N5P4Q",
  "errors": [
    {
      "field": "status",
      "reason": "must be one of: todo, in_progress, done"
    },
    {
      "field": "due_date",
      "reason": "must be a valid date in YYYY-MM-DD format"
    }
  ]
}
```

### 충돌 오류 (409)

```json
{
  "type": "https://api.onlogn.com/problems/state-conflict",
  "title": "State Conflict",
  "status": 409,
  "detail": "Task transition to done is already applied.",
  "instance": "/api/v1/tasks/task_123/complete/req_01JZ4T6M9N8P7Q6R5S4",
  "conflict_code": "already_completed"
}
```

### 속도 제한 오류 (429)

```json
{
  "type": "https://api.onlogn.com/problems/rate-limit-exceeded",
  "title": "Rate Limit Exceeded",
  "status": 429,
  "detail": "Too many requests. Retry after 30 seconds.",
  "instance": "/api/v1/auth/refresh/req_01JZ4T7R3S2T1U0V9W8",
  "retry_after_seconds": 30
}
```

### 서버 오류 (500)

```json
{
  "type": "https://api.onlogn.com/problems/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred while processing the request.",
  "instance": "/api/v1/groups/req_01JZ4T8V6W5X4Y3Z2A1",
  "request_id": "req_01JZ4T8V6W5X4Y3Z2A1"
}
```

## 비협상 호환성 제약

- RFC9457 problem details를 대신하는 커스텀 성공/오류 envelope를 도입하지 않는다.
- 동일한 기준 `type`에 대해 `title`을 변경하지 않는다.
- 명시적인 정책 개정 없이 이 문서 밖의 다른 상태 코드로 엔드포인트 클래스를 재매핑하지 않는다.
