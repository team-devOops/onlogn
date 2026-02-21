# 인가 및 공개 범위 매트릭스 (v1)

## 범위 결정

- v1에서는 전역 public task 피드를 허용하지 않는다.
- public task 목록은 `GET /api/v1/profiles/{slug}/tasks`로만 노출한다.
- public profile 조회는 `GET /api/v1/profiles/{slug}`로 제공한다.
- task와 group 소유권은 사용자 범위이며, 사용자 간 owner 동작은 허용하지 않는다.
- reactions는 public task에서만 허용한다.

## 주체 정의

- `owner`: 본인 리소스에 접근하는 인증 사용자.
- `other user`: 다른 사용자 리소스에 접근하는 인증 사용자.
- `anonymous`: 인증되지 않은 요청자.

## 엔드포인트별 주체 매트릭스

| Endpoint | owner | other user | anonymous | Notes |
| --- | --- | --- | --- | --- |
| `GET /api/v1/users/me` | Allow | N/A (self endpoint only) | Deny (401) | 현재 인증 사용자만 반환한다. |
| `GET /api/v1/profiles/{slug}` | Allow | Allow | Allow | public profile 조회 allowlist 응답. |
| `GET /api/v1/profiles/{slug}/tasks` | Allow | Allow | Allow | 해당 profile slug의 public task만 반환한다. |
| `GET /api/v1/tasks` (my list) | Allow | Deny (403) | Deny (401) | owner 전용 private 범위 목록. |
| `POST /api/v1/tasks` | Allow | Deny (403) | Deny (401) | 인증된 owner의 task를 생성한다. |
| `GET /api/v1/tasks/{task_id}` | Allow (own task) | Deny unless task is public via profile path | Deny unless task is public via profile path | v1에서 직접 task 조회는 owner 범위다. |
| `PATCH /api/v1/tasks/{task_id}` | Allow (own task) | Deny (403) | Deny (401) | owner 전용 수정. |
| `DELETE /api/v1/tasks/{task_id}` | Allow (own task) | Deny (403) | Deny (401) | owner 전용 삭제. |
| `GET /api/v1/groups` (my groups) | Allow | Deny (403) | Deny (401) | owner 전용 group 목록. |
| `POST /api/v1/groups` | Allow | Deny (403) | Deny (401) | owner 전용 group 생성. |
| `GET /api/v1/groups/{group_id}` | Allow (own group) | Deny (403) | Deny (401) | owner 전용 group 상세. |
| `PATCH /api/v1/groups/{group_id}` | Allow (own group) | Deny (403) | Deny (401) | owner 전용 group 수정. |
| `DELETE /api/v1/groups/{group_id}` | Allow (own group) | Deny (403) | Deny (401) | owner 전용 group 삭제, 연결된 task는 `group_id = null`이 된다. |
| `GET /api/v1/tasks/{task_id}/reactions` | Allow when task is public | Allow when task is public | Allow when task is public | reactions는 public task에만 적용된다. |
| `POST /api/v1/tasks/{task_id}/reactions` | Allow when task is public | Allow when task is public | Deny (401) | emoji/user 단위 토글에는 인증 필요, public task 필요. |
| `DELETE /api/v1/tasks/{task_id}/reactions?emoji={emoji}` | Allow when task is public | Allow when task is public | Deny (401) | query `emoji` 기준 본인 reaction 제거는 인증 필요, public task 필요. |

## 오류 코드 매핑 (인가 및 공개 범위)

- `401 Unauthorized`: 요청자가 `anonymous`이고 엔드포인트가 인증을 요구함.
- `403 Forbidden`: 요청자는 인증됐지만 owner 전용 엔드포인트의 리소스 소유자가 아님.
- `404 Not Found`: 리소스가 없거나, 공개 범위 정책상 접근 불가 private 리소스를 비공개 처리해야 함.

## 비목표와 가드레일

- `/api/v1/tasks/public` 또는 동등한 전역 탐색/검색 엔드포인트는 추가하지 않는다.
- v1 groups에는 invite/role 권한 모델을 도입하지 않는다.
- 관리자 전용 사용자 간 탐색 기능을 도입하지 않는다.
