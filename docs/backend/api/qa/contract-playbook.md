# API v1 계약 QA 플레이북 (구현 후)

## 목표

- 구현된 v1 계약(`auth/users/profiles/groups/tasks/calendar/reactions`)에 대해 curl 기반 인수 검증을 수행한다.
- 결정적인 명령 출력 증적을 `.sisyphus/evidence/` 아래에 수집한다.
- RFC9457 problem details를 포함해 성공 경로와 실패 경로 동작을 모두 검증한다.

## 테스트 하네스

```bash
export BASE_URL="http://localhost:3000"
export OWNER_ACCESS_TOKEN="replace-owner-access-token"
export OWNER_REFRESH_TOKEN="replace-owner-refresh-token"
export OTHER_ACCESS_TOKEN="replace-other-access-token"
export PUBLIC_PROFILE_SLUG="replace-public-profile-slug"
export OWNER_GROUP_ID="replace-owner-group-id"
export OWNER_TASK_ID="replace-owner-task-id"
export PUBLIC_TASK_ID="replace-public-task-id"
export PRIVATE_TASK_ID="replace-private-task-id"
export REACTION_EMOJI=":thumbsup:"

mkdir -p .sisyphus/evidence
```

## 공통 검증 규칙

- 모든 명령은 응답 headers, body, status code를 증적 파일로 남긴다.
- 성공 검증에서는 예상 HTTP 상태(`200|201|204`, 작업별 상이)를 확인한다.
- 실패 검증(`401|403|404`)에서는 `Content-Type: application/problem+json`과 RFC9457 멤버 `type,title,status,detail,instance`를 확인한다.

## 공통 RFC9457 확인 템플릿

```bash
# usage:
# sh -c '...template...' _ STATUS_FILE HEADERS_FILE BODY_FILE
STATUS_FILE="$1"
HEADERS_FILE="$2"
BODY_FILE="$3"
STATUS="$(tr -d '\r\n' < "$STATUS_FILE")"

test "$STATUS" = "401" || test "$STATUS" = "403" || test "$STATUS" = "404"
grep -qi '^content-type:.*application/problem+json' "$HEADERS_FILE"
jq -e '.type and .title and .status and .detail and .instance' "$BODY_FILE" >/dev/null
```

## 인증(Auth)

### A1. 성공 - GitHub OAuth code 교환은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-auth-exchange-200.headers \
  -o .sisyphus/evidence/task-16-auth-exchange-200.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/auth/oauth/github/exchange" \
  -H 'Content-Type: application/json' \
  --data '{"data":{"code":"valid-oauth-code"}}' \
  > .sisyphus/evidence/task-16-auth-exchange-200.status
```

- 예상 결과: 상태 `200`; `.data.access_token`, `.data.refresh_token`, `.data.token_type`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-auth-exchange-200.{headers,json,status}`

### A2. 성공 - refresh 회전은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-auth-refresh-200.headers \
  -o .sisyphus/evidence/task-16-auth-refresh-200.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/auth/refresh" \
  -H 'Content-Type: application/json' \
  --data '{"data":{"refresh_token":"'"$OWNER_REFRESH_TOKEN"'"}}' \
  > .sisyphus/evidence/task-16-auth-refresh-200.status
```

- 예상 결과: 상태 `200`; `.data.access_token`, `.data.refresh_token`, `.data.token_type`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-auth-refresh-200.{headers,json,status}`

### A3. 성공 - 현재 디바이스 logout은 204를 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-auth-logout-204.headers \
  -o .sisyphus/evidence/task-16-auth-logout-204.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/auth/logout" \
  -H 'Content-Type: application/json' \
  --data '{"data":{"refresh_token":"'"$OWNER_REFRESH_TOKEN"'"}}' \
  > .sisyphus/evidence/task-16-auth-logout-204.status
```

- 예상 결과: 상태 `204`; 응답 body는 비어 있을 수 있다.
- 증적: `.sisyphus/evidence/task-16-auth-logout-204.{headers,json,status}`

### A4. 실패 - 재사용되었거나 무효한 refresh token은 401을 반환한다 (RFC9457)

```bash
curl -sS -D .sisyphus/evidence/task-16-auth-refresh-401.headers \
  -o .sisyphus/evidence/task-16-auth-refresh-401.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/auth/refresh" \
  -H 'Content-Type: application/json' \
  --data '{"data":{"refresh_token":"invalid-or-rotated-token"}}' \
  > .sisyphus/evidence/task-16-auth-refresh-401.status
```

- 예상 결과: 상태 `401`; RFC9457 필드가 존재하고 content type은 `application/problem+json`이다.
- 증적: `.sisyphus/evidence/task-16-auth-refresh-401.{headers,json,status}`

## 사용자(Users)

### U1. 성공 - 현재 사용자 조회는 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-users-me-200.headers \
  -o .sisyphus/evidence/task-16-users-me-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/users/me" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-users-me-200.status
```

- 예상 결과: 상태 `200`; `.data.id`, `.data.profile_slug`, `.data.email`이 존재한다.
- 증적: `.sisyphus/evidence/task-16-users-me-200.{headers,json,status}`

### U2. 실패 - bearer token 누락 시 401을 반환한다 (RFC9457)

```bash
curl -sS -D .sisyphus/evidence/task-16-users-me-401.headers \
  -o .sisyphus/evidence/task-16-users-me-401.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/users/me" \
  > .sisyphus/evidence/task-16-users-me-401.status
```

- 예상 결과: 상태 `401`; RFC9457 필드가 존재하고 content type은 `application/problem+json`이다.
- 증적: `.sisyphus/evidence/task-16-users-me-401.{headers,json,status}`

## 프로필(Profiles)

### P1. 성공 - slug 기반 public profile 조회는 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-profiles-slug-200.headers \
  -o .sisyphus/evidence/task-16-profiles-slug-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/profiles/$PUBLIC_PROFILE_SLUG" \
  > .sisyphus/evidence/task-16-profiles-slug-200.status
```

- 예상 결과: 상태 `200`; `.data.profile_slug`, `.data.display_name`, `.data.stats_summary`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-profiles-slug-200.{headers,json,status}`

### P2. 성공 - profile 범위 public task 목록은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-profiles-tasks-200.headers \
  -o .sisyphus/evidence/task-16-profiles-tasks-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/profiles/$PUBLIC_PROFILE_SLUG/tasks?offset=0&limit=20&sort=created_at%20desc" \
  > .sisyphus/evidence/task-16-profiles-tasks-200.status
```

- 예상 결과: 상태 `200`; `.data` 배열이 존재하고 public task 필드만 포함한다.
- 증적: `.sisyphus/evidence/task-16-profiles-tasks-200.{headers,json,status}`

## 그룹(Groups)

### G1. 성공 - owner group 목록은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-groups-list-200.headers \
  -o .sisyphus/evidence/task-16-groups-list-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/groups?offset=0&limit=20" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-groups-list-200.status
```

- 예상 결과: 상태 `200`; `.data`는 배열이고 `.meta.pagination`이 존재한다.
- 증적: `.sisyphus/evidence/task-16-groups-list-200.{headers,json,status}`

### G2. 성공 - owner group 상세 조회는 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-groups-detail-200.headers \
  -o .sisyphus/evidence/task-16-groups-detail-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/groups/$OWNER_GROUP_ID" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-groups-detail-200.status
```

- 예상 결과: 상태 `200`; `.data.id`가 요청한 group id와 같다.
- 증적: `.sisyphus/evidence/task-16-groups-detail-200.{headers,json,status}`

### G3. 성공 - group 생성은 201을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-groups-create-201.headers \
  -o .sisyphus/evidence/task-16-groups-create-201.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/groups" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{"visibility":"private","description":"QA created group","color":"#0ea5e9","icon":"tag"}' \
  > .sisyphus/evidence/task-16-groups-create-201.status
```

- 예상 결과: 상태 `201`; `.data.id`, `.data.visibility`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-groups-create-201.{headers,json,status}`

### G4. 성공 - group 수정은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-groups-update-200.headers \
  -o .sisyphus/evidence/task-16-groups-update-200.json \
  -w '%{http_code}' \
  -X PATCH "$BASE_URL/api/v1/groups/$OWNER_GROUP_ID" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{"description":"QA updated group"}' \
  > .sisyphus/evidence/task-16-groups-update-200.status
```

- 예상 결과: 상태 `200`; `.data.id`가 요청 group과 같고 수정 필드가 반영된다.
- 증적: `.sisyphus/evidence/task-16-groups-update-200.{headers,json,status}`

### G5. 성공 - group 삭제는 204를 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-groups-delete-204.headers \
  -o .sisyphus/evidence/task-16-groups-delete-204.json \
  -w '%{http_code}' \
  -X DELETE "$BASE_URL/api/v1/groups/$OWNER_GROUP_ID" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-groups-delete-204.status
```

- 예상 결과: 상태 `204`; 응답 body는 비어 있을 수 있다.
- 증적: `.sisyphus/evidence/task-16-groups-delete-204.{headers,json,status}`

## 작업(Tasks)

### T1. 성공 - owner task 목록은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-tasks-list-200.headers \
  -o .sisyphus/evidence/task-16-tasks-list-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks?offset=0&limit=20&sort=created_at%20desc" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-tasks-list-200.status
```

- 예상 결과: 상태 `200`; `.data` 배열과 `.meta.pagination.sort`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-tasks-list-200.{headers,json,status}`

### T2. 성공 - owner task 생성은 201을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-tasks-create-201.headers \
  -o .sisyphus/evidence/task-16-tasks-create-201.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/tasks" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{"title":"QA created task","status":"todo","visibility":"private"}' \
  > .sisyphus/evidence/task-16-tasks-create-201.status
```

- 예상 결과: 상태 `201`; `.data.id`, `.data.title`, `.data.status`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-tasks-create-201.{headers,json,status}`

### T3. 성공 - owner task 수정은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-tasks-update-200.headers \
  -o .sisyphus/evidence/task-16-tasks-update-200.json \
  -w '%{http_code}' \
  -X PATCH "$BASE_URL/api/v1/tasks/$OWNER_TASK_ID" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{"status":"in_progress"}' \
  > .sisyphus/evidence/task-16-tasks-update-200.status
```

- 예상 결과: 상태 `200`; `.data.id`가 요청 task와 같고 `.data.status`가 갱신된다.
- 증적: `.sisyphus/evidence/task-16-tasks-update-200.{headers,json,status}`

### T4. 성공 - owner task 삭제는 204를 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-tasks-delete-204.headers \
  -o .sisyphus/evidence/task-16-tasks-delete-204.json \
  -w '%{http_code}' \
  -X DELETE "$BASE_URL/api/v1/tasks/$OWNER_TASK_ID" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-tasks-delete-204.status
```

- 예상 결과: 상태 `204`; 응답 body는 비어 있을 수 있다.
- 증적: `.sisyphus/evidence/task-16-tasks-delete-204.{headers,json,status}`

### T5. 실패 - 타 owner 상세 접근은 403을 반환한다 (RFC9457)

```bash
curl -sS -D .sisyphus/evidence/task-16-tasks-detail-403.headers \
  -o .sisyphus/evidence/task-16-tasks-detail-403.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks/$OWNER_TASK_ID" \
  -H "Authorization: Bearer $OTHER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-tasks-detail-403.status
```

- 예상 결과: 상태 `403`; RFC9457 필드가 존재하고 content type은 `application/problem+json`이다.
- 증적: `.sisyphus/evidence/task-16-tasks-detail-403.{headers,json,status}`

## 캘린더(Calendar)

### C1. 성공 - 월간 캘린더는 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-calendar-monthly-200.headers \
  -o .sisyphus/evidence/task-16-calendar-monthly-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks/calendar/monthly?year=2026&month=2" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-calendar-monthly-200.status
```

- 예상 결과: 상태 `200`; day bucket에 `date,planned_count,done_count,in_progress_count,completion_rate`가 포함되고 `completion_rate`는 `0..100` 백분율이다.
- 증적: `.sisyphus/evidence/task-16-calendar-monthly-200.{headers,json,status}`

### C2. 실패 - 잘못된 month는 400을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-calendar-monthly-400.headers \
  -o .sisyphus/evidence/task-16-calendar-monthly-400.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks/calendar/monthly?year=2026&month=13" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-calendar-monthly-400.status
```

- 예상 결과: 상태 `400`; problem 응답 payload가 반환된다.
- 증적: `.sisyphus/evidence/task-16-calendar-monthly-400.{headers,json,status}`

### C3. 실패 - 잘못된 year는 400을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-calendar-monthly-year-400.headers \
  -o .sisyphus/evidence/task-16-calendar-monthly-year-400.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks/calendar/monthly?year=1969&month=2" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-calendar-monthly-year-400.status
```

- 예상 결과: 상태 `400`; problem 응답 payload가 반환된다.
- 증적: `.sisyphus/evidence/task-16-calendar-monthly-year-400.{headers,json,status}`

## 리액션(Reactions)

### R1. 성공 - public task 리액션 목록은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-reactions-list-200.headers \
  -o .sisyphus/evidence/task-16-reactions-list-200.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks/$PUBLIC_TASK_ID/reactions" \
  > .sisyphus/evidence/task-16-reactions-list-200.status
```

- 예상 결과: 상태 `200`; `.data` 배열에 reaction 집계가 포함된다.
- 증적: `.sisyphus/evidence/task-16-reactions-list-200.{headers,json,status}`

### R2. 성공 - reaction 토글은 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-reactions-toggle-200.headers \
  -o .sisyphus/evidence/task-16-reactions-toggle-200.json \
  -w '%{http_code}' \
  -X POST "$BASE_URL/api/v1/tasks/$PUBLIC_TASK_ID/reactions" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  --data '{"emoji":"'"$REACTION_EMOJI"'"}' \
  > .sisyphus/evidence/task-16-reactions-toggle-200.status
```

- 예상 결과: 상태 `200`; `.data.task_id`, `.data.emoji`, `.data.requester_reacted`가 존재한다.
- 증적: `.sisyphus/evidence/task-16-reactions-toggle-200.{headers,json,status}`

### R3. 성공 - reaction 제거는 200을 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-reactions-delete-200.headers \
  -o .sisyphus/evidence/task-16-reactions-delete-200.json \
  -w '%{http_code}' \
  -X DELETE "$BASE_URL/api/v1/tasks/$PUBLIC_TASK_ID/reactions?emoji=$REACTION_EMOJI" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-reactions-delete-200.status
```

- 예상 결과: 상태 `200`; mutation 응답에 `.data.emoji`, `.data.requester_reacted`가 반환된다.
- 증적: `.sisyphus/evidence/task-16-reactions-delete-200.{headers,json,status}`

### R4. 실패 - private 또는 미지정 task 리액션 조회는 404를 반환한다 (RFC9457)

```bash
curl -sS -D .sisyphus/evidence/task-16-reactions-list-404.headers \
  -o .sisyphus/evidence/task-16-reactions-list-404.json \
  -w '%{http_code}' \
  "$BASE_URL/api/v1/tasks/$PRIVATE_TASK_ID/reactions" \
  > .sisyphus/evidence/task-16-reactions-list-404.status
```

- 예상 결과: 상태 `404`; RFC9457 필드가 존재하고 content type은 `application/problem+json`이다.
- 증적: `.sisyphus/evidence/task-16-reactions-list-404.{headers,json,status}`

### R5. 실패 - 제거 시 emoji가 유효하지 않으면 422를 반환한다

```bash
curl -sS -D .sisyphus/evidence/task-16-reactions-delete-422.headers \
  -o .sisyphus/evidence/task-16-reactions-delete-422.json \
  -w '%{http_code}' \
  -X DELETE "$BASE_URL/api/v1/tasks/$PUBLIC_TASK_ID/reactions?emoji=" \
  -H "Authorization: Bearer $OWNER_ACCESS_TOKEN" \
  > .sisyphus/evidence/task-16-reactions-delete-422.status
```

- 예상 결과: 상태 `422`; validation problem payload에 `.errors`가 포함된다.
- 증적: `.sisyphus/evidence/task-16-reactions-delete-422.{headers,json,status}`

## 엔드포인트 패밀리 검증 체크리스트

- Auth: exchange (`200`), refresh (`200`), logout (`204`), invalid refresh (`401`).
- Users: 인증 self 조회 (`200`), bearer 누락 (`401`).
- Profiles: public profile 조회 (`200`), profile task 목록 (`200`).
- Groups: 목록 (`200`), 상세 (`200`), 생성 (`201`), 수정 (`200`), 삭제 (`204`).
- Tasks: 목록 (`200`), 생성 (`201`), 수정 (`200`), 삭제 (`204`), 타 owner 조회 거부 (`403`).
- Calendar: 월간 집계 (`200`), invalid month (`400`), invalid year (`400`).
- Reactions: 목록 (`200`), 토글 (`200`), 제거 (`200` query `emoji`), private/unknown task (`404`), invalid emoji (`422`).

## 선택적 배치 실행 스켈레톤

```bash
ls -1 .sisyphus/evidence/task-16-*.status
```
