# 인증/세션 결정 (v1)

## 범위와 불변 조건

- 이 정책은 v1 API 계약의 규범적 인증/세션 동작을 정의한다.
- v1의 인증 제공자는 GitHub OAuth (`oauth/github`) 하나만 MUST로 사용한다.
- 플랫폼은 v1에서 다른 제공자를 추가하거나 문서화하면 안 된다.
- 액세스 토큰과 리프레시 토큰은 JSON body 응답으로 전달되어야 한다.
- v1에서 토큰 전달 방식은 cookies로 전환하면 안 된다.
- 인증 엔드포인트의 오류 응답은 RFC9457 `application/problem+json`을 사용해야 한다.

## OAuth 흐름 (`POST /api/v1/auth/oauth/github/exchange`)

- 클라이언트는 GitHub OAuth에서 받은 authorization `code`를 보낸다.
- 서버는 GitHub로 code를 검증하고 내부 사용자를 매핑/생성해야 한다.
- 성공 시 서버는 JSON body에 액세스 토큰과 리프레시 토큰을 모두 반환해야 한다.
- code가 잘못됐거나 만료된 경우 서버는 RFC9457 problem details와 함께 `401`을 반환해야 한다.
- 요청 payload 형식이 잘못된 경우 서버는 RFC9457 problem details와 함께 `400`을 반환해야 한다.
- exchange 엔드포인트는 외부에 노출되는 `sessions` 리소스를 생성하면 안 된다.

## 토큰 응답 스키마 (JSON body)

- 성공 payload는 `data.access_token`, `data.refresh_token`, TTL 메타데이터를 포함해야 한다.
- 상호운용성을 위해 성공 payload는 명시적 토큰 타입(`Bearer`)을 포함하는 것이 좋다.
- 토큰 필드는 JSON body에만 있어야 하며 cookies에 중복되면 안 된다.
- 클라이언트는 토큰을 HTTPS로만 전송해야 한다.
- 클라이언트는 리프레시 토큰을 안전한 저장소에 보관해야 하며 토큰 값을 로그에 남기면 안 된다.

성공 envelope 예시:

```json
{
  "data": {
    "access_token": "<jwt-or-opaque>",
    "refresh_token": "<opaque>",
    "token_type": "Bearer",
    "expires_in": 900,
    "refresh_expires_in": 1209600
  }
}
```

## 리프레시 회전 의미론 (`POST /api/v1/auth/refresh`)

- 각 refresh 호출은 성공할 때마다 리프레시 토큰을 회전해야 한다.
- 새 리프레시 토큰이 발급되면 이전 리프레시 토큰은 즉시 무효화되어야 한다.
- 무효화된 리프레시 토큰을 재사용하면 RFC9457 problem details와 함께 `401`을 반환해야 한다.
- 동일한 구 리프레시 토큰으로 동시 요청이 오면 정확히 하나는 성공, 하나는 `401` 실패가 되는 것이 바람직하다.
- refresh 엔드포인트는 JSON body에 새 액세스 토큰과 새 리프레시 토큰을 반환해야 한다.

## 폐기 및 로그아웃 의미론 (`POST /api/v1/auth/logout`)

- logout은 제시된 리프레시 토큰이 나타내는 현재 디바이스 세션만 폐기해야 한다.
- v1에서 logout은 전체 디바이스 또는 계정 전체 세션을 폐기하면 안 된다.
- logout 성공은 멱등적으로 동작하는 것이 좋고, 현재 디바이스 폐기가 적용되었거나 이미 없는 경우 `204`를 반환해야 한다.
- logout 자격 증명이 없거나 유효하지 않으면 RFC9457 problem details를 사용해 `401`을 반환해야 한다.
- logout 요청 검증 실패는 RFC9457 problem details를 사용해 `400`을 반환해야 한다.

## 보안 및 오류 참고

- 인증 엔드포인트는 TLS를 반드시 요구해야 하며, 비 TLS 전송은 배포 환경에서 거부해야 한다.
- API 계약에는 RFC9457 body와 함께 `401`, `400`, 관련 `429` 제한을 문서화해야 한다.
- RFC9457 problem 객체는 `type`, `title`, `status`, `detail`, `instance`를 포함해야 한다.
- 구현체는 로그, 추적, 분석 내보내기에서 민감한 토큰 데이터를 피해야 한다.
- 이 결정 문서는 후속 OpenAPI 인증 경로 설계(T7/T10 의존)에 대한 규범 문서다.
