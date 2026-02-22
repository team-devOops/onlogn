# O(NlogN) 기능 요구사항 명세서 (v1)

> 작성 기준일: 2026-02-21
> 버전: v1
> 언어: 한국어

---

## 1. 프로젝트 개요

O(NlogN)은 단순한 할 일 관리(TODO)가 아니라, 실행 기록을 꾸준히 축적하고 이를 공개/공유할 수 있는 구조를 제공하는 플랫폼이다.

개발자가 GitHub 커밋 기록으로 역량과 활동을 보여주듯, 이 플랫폼은 일상의 실행 데이터(할 일 수행, 학습, 루틴, 프로젝트 진행 등)를 구조화해 "증명 가능한 기록"으로 남기도록 설계한다. 기록이 쌓여서 개인의 성실성과 지속성이 객관적으로 증명되고, 그 기록 자체가 포트폴리오처럼 기능하는 것이 핵심 목표다.

---

## 2. 사용자 역할 정의

| 역할 | 설명 |
|------|------|
| `owner` | 본인 리소스에 접근하는 인증 사용자 |
| `other user` | 다른 사용자 리소스에 접근하는 인증 사용자 |
| `anonymous` | 인증되지 않은 방문자 |

---

## 3. 인증 및 세션 요구사항

### 3.1 인증 제공자

- v1에서 인증 제공자는 **GitHub OAuth** 하나만 지원한다.
- v1에서 다른 소셜 로그인 제공자를 추가하거나 문서화하면 안 된다.

### 3.2 토큰 발급

- 인증 성공 시 `access_token`과 `refresh_token` 쌍을 JSON body로 반환한다.
- v1에서 토큰 전달 방식은 cookies로 전환하면 안 된다.
- 성공 응답 payload는 `data.access_token`, `data.refresh_token`, TTL 메타데이터를 포함해야 한다.

토큰 응답 예시:

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

### 3.3 리프레시 토큰 회전

- 각 refresh 호출은 성공할 때마다 리프레시 토큰을 회전해야 한다.
- 새 리프레시 토큰이 발급되면 이전 리프레시 토큰은 즉시 무효화되어야 한다.
- 무효화된 리프레시 토큰을 재사용하면 `401`을 반환해야 한다.

### 3.4 로그아웃

- 로그아웃은 제시된 리프레시 토큰이 나타내는 **현재 디바이스 세션만** 폐기한다.
- v1에서 전체 디바이스 또는 계정 전체 세션 일괄 로그아웃은 지원하지 않는다.
- 로그아웃 성공은 멱등적으로 동작하며 `204`를 반환한다.

### 3.5 보안 요구사항

- 모든 인증 엔드포인트는 TLS를 반드시 요구한다.
- 구현체는 로그, 추적, 분석 내보내기에서 민감한 토큰 데이터를 노출하면 안 된다.

---

## 4. 엔드포인트 목록

총 23개 엔드포인트. 기준 경로 접두사: `/api/v1`

| # | Method | Path | 인증 요구 | 설명 |
|---|--------|------|-----------|------|
| 1 | POST | `/auth/oauth/github/exchange` | 불필요 | GitHub OAuth code를 토큰으로 교환 |
| 2 | POST | `/auth/refresh` | 불필요 | 리프레시 토큰으로 새 토큰 쌍 발급 |
| 3 | POST | `/auth/logout` | 불필요 | 현재 디바이스 세션 폐기 |
| 4 | GET | `/users/me` | 필요 | 현재 인증 사용자 정보 조회 |
| 5 | GET | `/profiles/{slug}` | 불필요 | 공개 프로필 조회 |
| 6 | GET | `/profiles/{slug}/tasks` | 불필요 | 공개 프로필의 public task 목록 |
| 7 | GET | `/profiles/{slug}/ai-summary` | 불필요 | 공개 프로필 AI 생산성 요약 조회 (기간별) |
| 8 | GET | `/profiles/{slug}/tasks/calendar/monthly` | 불필요 | 공개 프로필 월간 캘린더 집계 |
| 9 | GET | `/groups` | 필요 | 내 그룹 목록 조회 |
| 10 | POST | `/groups` | 필요 | 그룹 생성 |
| 11 | GET | `/groups/{group_id}` | 필요 | 그룹 상세 조회 |
| 12 | PATCH | `/groups/{group_id}` | 필요 | 그룹 수정 |
| 13 | DELETE | `/groups/{group_id}` | 필요 | 그룹 삭제 |
| 14 | GET | `/tasks` | 필요 | 내 task 목록 조회 |
| 15 | POST | `/tasks` | 필요 | task 생성 |
| 16 | POST | `/tasks/import/google` | 필요 | Google Tasks JSON 파일 가져오기 (단일 파일, 최대 2GB) |
| 17 | GET | `/tasks/{task_id}` | 필요 | task 상세 조회 (owner 전용) |
| 18 | PATCH | `/tasks/{task_id}` | 필요 | task 수정 (owner 전용) |
| 19 | DELETE | `/tasks/{task_id}` | 필요 | task 삭제 (owner 전용) |
| 20 | GET | `/tasks/calendar/monthly` | 필요 | 내 월간 캘린더 집계 |
| 21 | GET | `/tasks/{task_id}/reactions` | 불필요 | task reaction 집계 조회 (public task만) |
| 22 | POST | `/tasks/{task_id}/reactions` | 필요 | task reaction 토글 (public task만) |
| 23 | DELETE | `/tasks/{task_id}/reactions?emoji=` | 필요 | task reaction 제거 (public task만) |

---

## 5. 인가 매트릭스

| 엔드포인트 | owner | other user | anonymous | 비고 |
|-----------|-------|------------|-----------|------|
| `GET /users/me` | 허용 | N/A (self 전용) | 거부 (401) | 현재 인증 사용자만 반환 |
| `GET /profiles/{slug}` | 허용 | 허용 | 허용 | public profile allowlist 응답 |
| `GET /profiles/{slug}/tasks` | 허용 | 허용 | 허용 | visibility=public 강제 |
| `GET /profiles/{slug}/ai-summary` | 허용 | 허용 | 허용 | profile public + public task 범위 기반 요약 |
| `GET /profiles/{slug}/tasks/calendar/monthly` | 허용 | 허용 | 허용 | profile timezone 기준 |
| `GET /tasks` | 허용 | 거부 (403) | 거부 (401) | owner 전용 private 범위 |
| `POST /tasks` | 허용 | 거부 (403) | 거부 (401) | owner task 생성 |
| `POST /tasks/import/google` | 허용 | 거부 (403) | 거부 (401) | 단일 JSON 업로드 import |
| `GET /tasks/{task_id}` | 허용 | 거부 (403/404) | 거부 (401/404) | v1 직접 조회는 owner 범위 |
| `PATCH /tasks/{task_id}` | 허용 | 거부 (403) | 거부 (401) | owner 전용 수정 |
| `DELETE /tasks/{task_id}` | 허용 | 거부 (403) | 거부 (401) | owner 전용 삭제 |
| `GET /tasks/calendar/monthly` | 허용 | 거부 (403) | 거부 (401) | owner 전용 집계 |
| `GET /groups` | 허용 | 거부 (403) | 거부 (401) | owner 전용 group 목록 |
| `POST /groups` | 허용 | 거부 (403) | 거부 (401) | owner 전용 group 생성 |
| `GET /groups/{group_id}` | 허용 | 거부 (403) | 거부 (401) | owner 전용 group 상세 |
| `PATCH /groups/{group_id}` | 허용 | 거부 (403) | 거부 (401) | owner 전용 group 수정 |
| `DELETE /groups/{group_id}` | 허용 | 거부 (403) | 거부 (401) | 연결 task는 group_id=null |
| `GET /tasks/{task_id}/reactions` | 허용 (public task) | 허용 (public task) | 허용 (public task) | public task에만 적용 |
| `POST /tasks/{task_id}/reactions` | 허용 (public task) | 허용 (public task) | 거부 (401) | 인증 필요, public task만 |
| `DELETE /tasks/{task_id}/reactions?emoji=` | 허용 (public task) | 허용 (public task) | 거부 (401) | 인증 필요, public task만 |

---

## 6. 엔티티 스키마

### 6.1 tasks

기준 필수 필드: 13개

| 필드 | 타입 | Nullable | Enum | 기본값 | 설명 |
|------|------|----------|------|--------|------|
| `id` | string (UUID) | false | - | - | 리소스 식별자, 불변 |
| `owner_user_id` | string (UUID) | false | - | - | task 소유자, users.id 참조 |
| `group_id` | string (UUID) | true | - | `null` | 그룹 미소속 시 null |
| `title` | string | false | - | - | 사람이 읽는 제목, 빈 문자열 불가 |
| `status` | string | false | `todo`, `in_progress`, `done` | `todo` | task 상태 |
| `visibility` | string | false | `private`, `public` | `private` | public은 profile 범위에서만 노출 |
| `created_at` | string (date-time) | false | - | - | 생성 시각 (UTC) |
| `updated_at` | string (date-time) | false | - | - | 마지막 수정 시각 (UTC) |
| `due_date` | string (date) | true | - | `null` | 날짜 단위 마감일 |
| `start_time` | string (date-time) | true | - | `null` | 예정 시작 시각 (UTC) |
| `end_time` | string (date-time) | true | - | `null` | 예정 종료 시각 (UTC) |
| `tags` | array(string) | false | - | `[]` | 태그 목록, 빈 배열 허용 |
| `reference_links` | array(string url) | false | - | `[]` | URL 참조 목록, 빈 배열 허용 |

### 6.2 users

| 필드 | 타입 | Nullable | 기본값 | 설명 |
|------|------|----------|--------|------|
| `id` | string (UUID) | false | - | 사용자 식별자 |
| `profile_slug` | string | false | - | profile 라우트용 공개 URL 키 |
| `email` | string (email) | true | `null` | 내부 이메일, public allowlist 미포함 |
| `display_name` | string | false | - | profile에 노출되는 표시 이름 |
| `avatar_url` | string (url) | true | `null` | profile 이미지 URL |
| `timezone` | string | false | `"UTC"` | IANA timezone 식별자 |
| `created_at` | string (date-time) | false | - | 계정 생성 시각 |
| `updated_at` | string (date-time) | false | - | 마지막 profile 수정 시각 |

### 6.3 groups

| 필드 | 타입 | Nullable | Enum | 기본값 | 설명 |
|------|------|----------|------|--------|------|
| `id` | string (UUID) | false | - | - | group 식별자, 불변 |
| `owner_user_id` | string (UUID) | false | - | - | group 소유자, users.id 참조 |
| `visibility` | string | false | `private`, `public` | `private` | group visibility |
| `description` | string | true | - | `null` | 선택 설명 텍스트 |
| `color` | string | true | - | `null` | 선택 UI 강조 색상 토큰 |
| `icon` | string | true | - | `null` | 선택 UI 아이콘 토큰 |
| `created_at` | string (date-time) | false | - | - | group 생성 시각 |
| `updated_at` | string (date-time) | false | - | - | group 수정 시각 |

삭제 정책: group 삭제 시 소속 task의 `group_id`는 `null`로 재할당한다.

### 6.4 profiles (공개 allowlist)

| 필드 | 타입 | Nullable | 기본값 | 설명 |
|------|------|----------|--------|------|
| `profile_slug` | string | false | - | 공개 URL 식별자 |
| `display_name` | string | false | - | 공개 표시 이름 |
| `avatar_url` | string (url) | true | `null` | 공개 아바타 이미지 URL |
| `bio` | string | true | `null` | 공개 자기소개 텍스트 |
| `timezone` | string | false | `"UTC"` | IANA timezone 식별자 |
| `created_at` | string (date-time) | false | - | 공개 노출 profile 생성 시각 |
| `stats_summary` | object | false | `{}` | allowlist 집계 payload |

`stats_summary` 필드:

| 필드 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| `total_tasks` | integer | `0` | profile 사용자 소유 task 수 |
| `done_tasks` | integer | `0` | status=done task 수 |
| `in_progress_tasks` | integer | `0` | status=in_progress task 수 |
| `todo_tasks` | integer | `0` | status=todo task 수 |

### 6.5 reactions (집계 응답 모델)

토글 정책: `(user_id, task_id, emoji)` 조합마다 논리적 reaction 행은 하나다. 같은 조합으로 다시 요청하면 reaction 상태가 토글된다.

| 필드 | 타입 | Nullable | 기본값 | 설명 |
|------|------|----------|--------|------|
| `task_id` | string (UUID) | false | - | 부모 task id |
| `emoji` | string | false | - | 이모지 집계 키 |
| `count` | integer | false | `0` | 해당 emoji를 활성화한 전체 사용자 수 |
| `requester_reacted` | boolean | false | `false` | 요청자가 해당 emoji를 활성화했는지 여부 |

### 6.6 월간 캘린더 집계 (일별 버킷)

| 필드 | 타입 | Nullable | 기본값 | 설명 |
|------|------|----------|--------|------|
| `date` | string (date) | false | - | 일자 버킷 키 (YYYY-MM-DD) |
| `planned_count` | integer | false | `0` | 해당 일자 전체 task 수 (모든 상태) |
| `done_count` | integer | false | `0` | status=done task 수 |
| `in_progress_count` | integer | false | `0` | status=in_progress task 수 |
| `completion_rate` | number | false | `0` | 일일 완료율 0..100; (done_count / planned_count) * 100 |

---

## 7. 페이지네이션 및 쿼리 규칙

### 7.1 페이지네이션 방식

- 방식: offset/limit (v1에서 cursor 방식은 지원하지 않는다)
- `offset` 기본값: `0`, 0 이상 정수
- `limit` 기본값: `20`, 1 이상 100 이하 정수
- `max_limit`: `100` (고정)
- 유효하지 않은 값은 RFC9457 problem details와 함께 `400`을 반환한다

### 7.2 정렬

- 기준 정렬: `created_at desc`
- 타이브레이커: `id desc`
- v1에서 다른 정렬 옵션은 지원하지 않는다. `sort` 파라미터로 다른 값을 요청하면 `400`을 반환한다

### 7.3 응답 meta

목록 응답은 아래 meta를 포함한다:

| 필드 | 설명 |
|------|------|
| `meta.offset` | 현재 offset 값 |
| `meta.limit` | 현재 limit 값 |
| `meta.total` | 전체 결과 수 |

### 7.4 task 목록 필터 (owner 전용, `GET /tasks`)

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `status` | string | 선택. `todo`, `in_progress`, `done` 중 하나 |
| `visibility` | string | 선택. `private`, `public` 중 하나 |
| `group_id` | string | 선택. UUID 또는 리터럴 `null` (그룹 미소속 task) |
| `due_date_from` | string (date) | 선택. YYYY-MM-DD |
| `due_date_to` | string (date) | 선택. YYYY-MM-DD, due_date_from 이상이어야 함 |

알려지지 않은 필터 키는 `400`을 반환한다.

### 7.5 task 목록 필터 (공개 프로필, `GET /profiles/{slug}/tasks`)

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `status` | string | 선택. `todo`, `in_progress`, `done` 중 하나 |
| `group_id` | string | 선택. UUID 또는 리터럴 `null` |
| `due_date_from` | string (date) | 선택. YYYY-MM-DD |
| `due_date_to` | string (date) | 선택. YYYY-MM-DD |

`visibility` 필터 입력은 무시하거나 거부한다. 서버는 항상 `visibility=public`을 강제한다.

추가 공개 경계 규칙:

- task가 `group_id`를 가진 경우, 연결된 group의 `visibility`가 `public`일 때만 해당 task를 공개 profile 응답에 포함한다.
- 연결된 group의 `visibility`가 `private`이면 task 자체를 공개 profile 응답에서 제외한다.
- `group_id`가 없는 task는 기존과 동일하게 task `visibility=public` 기준으로 노출한다.

### 7.6 AI 요약 조회 (공개 프로필, `GET /profiles/{slug}/ai-summary`)

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `period` | string | 선택. `weekly`, `monthly`, `30days` 중 하나. 기본값 `monthly` |

- 서버는 `period`에 따라 공개 task 기반 요약을 계산해 `summary`를 반환한다.
- `period`가 허용값이 아니면 `400 bad-request`를 반환한다.
- 프로필이 비공개(`visibility=private`)이면 `404 resource-not-found`로 응답해 비공개 상태를 노출하지 않는다.
- 집계 대상은 `task.visibility=public`이며, `group_id`가 있는 경우 연결 group `visibility=public`인 task만 포함한다.

---

## 8. 월간 캘린더 집계 요구사항

### 8.1 쿼리 파라미터

| 파라미터 | 타입 | 필수 | 유효 범위 | 설명 |
|---------|------|------|-----------|------|
| `year` | integer | 필수 | 1970..2100 | 집계 연도 |
| `month` | integer | 필수 | 1..12 | 집계 월 |

`year` 또는 `month`가 누락되거나 범위를 벗어나면 `400`을 반환한다.

### 8.2 timezone 기준

| 요청 유형 | timezone 기준 |
|----------|--------------|
| 인증 사용자 (`GET /tasks/calendar/monthly`) | 요청자 timezone |
| 공개 프로필 (`GET /profiles/{slug}/tasks/calendar/monthly`) | profile timezone, 없으면 UTC |

### 8.3 출력 규칙

- 출력 일별 시리즈는 해당 월 전체를 포함한다 (date asc 순서)
- task가 없는 날도 0 값으로 반드시 출력한다 (`planned_count=0`, `done_count=0`, `in_progress_count=0`, `completion_rate=0`)
- 월간 응답 meta는 `meta.year`, `meta.month`, `meta.timezone`을 포함한다

---

## 9. 오류 처리 (RFC9457)

### 9.1 공통 규칙

- 모든 오류 응답은 `application/problem+json` 미디어 타입을 사용한다
- 모든 problem 객체는 `type`, `title`, `status`, `detail`, `instance` 핵심 멤버를 포함한다
- 기준 네임스페이스: `https://api.onlogn.com/problems/`

### 9.2 오류 유형 카탈로그

| HTTP 상태 | Problem type | Title | 주요 사용 |
|-----------|-------------|-------|-----------|
| 400 | `bad-request` | Bad Request | 잘못된 문법, 지원하지 않는 파라미터, 유효하지 않은 JSON body |
| 401 | `authentication-required` | Authentication Required | 액세스 토큰 누락, 만료, 무효 |
| 403 | `permission-denied` | Permission Denied | 인증됐지만 작업 권한 없음 |
| 404 | `resource-not-found` | Resource Not Found | 리소스 없음 또는 공개 범위상 비공개 처리 |
| 409 | `state-conflict` | State Conflict | 동시성 충돌, 중복 키, 이미 적용된 전이 |
| 422 | `validation-failed` | Validation Failed | 문법은 맞지만 의미 검증 실패 |
| 429 | `rate-limit-exceeded` | Rate Limit Exceeded | 속도 제한 초과 |
| 500 | `internal-server-error` | Internal Server Error | 예기치 않은 서버 측 실패 |

### 9.3 오류 클래스별 추가 규칙

- **401**: `detail`은 민감한 토큰 정보를 노출하지 않는다
- **403**: `detail`은 거부된 동작 경계를 설명한다
- **422**: 필드 단위 이슈를 담은 `errors` 확장 배열을 포함해야 한다
- **409**: 구분이 필요하면 `conflict_code` 확장을 포함한다
- **429**: `retry_after_seconds` 확장을 포함하고 `Retry-After` 헤더와 일치시킨다
- **500**: `detail`에서 내부 정보를 노출하지 않고 `request_id`를 포함한다

---

## 10. 화면 매핑

### 10.1 screen.png (개인 대시보드)

인증 사용자가 보는 개인 대시보드 화면이다.

| UI 요소 | 연결 API / 처리 방식 |
|---------|---------------------|
| 좌측 월간 캘린더 히트맵 | `GET /tasks/calendar/monthly` |
| 좌측 월간 캘린더 공휴일 표기 | 대한민국 공휴일 API(연도 단위) 조회 후 날짜 텍스트를 빨간색으로 렌더링 |
| MONTHLY GOAL COMPLETION (예: 72%) | 월간 완료율, 클라이언트 집계 |
| 그룹 섹션 (MORNING TASKS, WORK FOCUS 등) | `GET /tasks?group_id={id}` |
| task 체크박스 | `PATCH /tasks/{task_id}` (status: todo→done) |
| 이모지 카운트 (🧡 5 🦋 2 등) | `GET /tasks/{task_id}/reactions` |
| 시간 표시 (14:00-15:00) | task의 `start_time` / `end_time` 필드 |
| 참조 링크 (zoom.us 등) | task의 `reference_links` 필드 |
| 태그 뱃지 (HEALTH, MEETING, DEV) | task의 `tags` 필드 |
| "할 일 추가" 버튼 | `POST /tasks` |
| TODAY'S INSIGHT | 클라이언트 집계 (v1 API 미포함) |

### 10.2 screen2.png (공개 프로필)

비인증 방문자도 접근 가능한 공개 프로필 화면이다.

| UI 요소 | 연결 API / 처리 방식 |
|---------|---------------------|
| 아바타 + 이름 + @slug + bio | `GET /profiles/{slug}` |
| 총 task 수 / 완료율 / 연속 일수 | `stats_summary` + 클라이언트 계산 |
| AI 생산성 요약 | 인증 사용자 우선 `POST /profiles/me/summary` 사용, 미인증/실패 시 `GET /profiles/{slug}/tasks?due_date_from&due_date_to&limit=100` 기반 폴백 집계 |
| 월간 히트맵 | `GET /profiles/{slug}/tasks?due_date_from&due_date_to&limit=100` 응답에서 `status=done` 완료 건수 기준 클라이언트 집계 |
| 그룹 분포 (디자인 45% / 개발 30% 등) | `GET /profiles/{slug}/tasks?limit=100` 기반 클라이언트 집계 |
| 관심 키워드 | `GET /profiles/{slug}/tasks?limit=100` 응답의 해당 회원 공개 task(`tags` + `title`) 기반 클라이언트 추출 |

- 연속 달성일(`stat-streak`)은 `GET /profiles/{slug}` 응답에 `streak_days`가 있으면 해당 값을 우선 사용한다.
- `streak_days`가 없으면 클라이언트는 `GET /profiles/{slug}/tasks?status=done&due_date_to=today`를 페이지네이션으로 조회해 `due_date` 기준 연속 완료일을 계산한다.
- 오늘 완료가 없으면 어제부터 역산하며, 날짜 공백이 나오면 streak를 종료한다.

---

## 11. 공통 데이터 규칙

### 11.1 식별자

- `id`, `owner_user_id`, `user_id`는 문자열 UUID다

### 11.2 날짜/시간 형식

- `date-time`: `YYYY-MM-DDTHH:MM:SSZ` (UTC ISO-8601)
- `date`: `YYYY-MM-DD`

### 11.3 nullable 규칙

- nullable로 명시되지 않은 필드는 응답에서 required로 간주한다
- `object`/array 필드는 null 값이 의미적으로 유효할 때만 nullable로 명시한다

---

## 12. v1 비목표

v1에서 명시적으로 지원하지 않는 기능 목록이다.

| 비목표 항목 | 설명 |
|------------|------|
| 전역 public task 피드/탐색/검색 | `/api/v1/tasks/public` 또는 동등한 전역 탐색 엔드포인트 미제공 |
| GitHub 외 소셜 로그인 | v1 인증 제공자는 GitHub OAuth 단일 제공자로 고정 |
| 전체 계정 일괄 로그아웃 | 로그아웃은 현재 디바이스 세션만 폐기 |
| 그룹 invite/role 권한 모델 | v1 groups에는 초대 및 역할 기반 권한 미지원 |
| cursor 기반 페이지네이션 | v1 목록 엔드포인트는 offset/limit만 지원 |
| created_at desc 외 정렬 옵션 | 정렬은 created_at desc + id desc 타이브레이커로 고정 |
| 사용자 간 관리자 탐색 기능 | 관리자 전용 사용자 간 탐색 기능 미지원 |

---

## 13. 수용 기준 요약

각 기능 영역의 최소 수용 기준이다.

| 영역 | 기준 |
|------|------|
| 인증 | GitHub OAuth code exchange 성공 시 access_token + refresh_token JSON body 반환 |
| 토큰 갱신 | refresh 호출마다 토큰 회전, 이전 토큰 즉시 무효화 |
| 로그아웃 | 현재 디바이스 세션만 폐기, 204 반환 |
| task CRUD | owner만 생성/수정/삭제 가능, 비인증 시 401, 타인 접근 시 403 |
| Google import | 단일 JSON 파일(최대 2GB) 업로드로 task import, 동일 `provider_task_id` 재실행 시 중복 생성되지 않음 |
| 공개 프로필 | 비인증 방문자도 profile 및 public task 목록 조회 가능 |
| 공개 프로필 페이지 라우팅 | 존재하지 않는 `@slug` 웹 페이지 접근 시 HTML 404 페이지를 반환 |
| 공개 프로필 그룹 경계 | public task라도 연결 group이 private이면 profile task 목록에서 제외 |
| 공개 프로필 AI 요약 | period(weekly/monthly/30days) 요청 시 공개 범위 task 기반 summary 반환, 비공개 프로필은 404 |
| 그룹 순서 | 그룹 관리에서 드래그로 변경한 순서는 새로고침 후에도 유지되어야 함 |
| 월간 캘린더 | year/month 필수, task 없는 날도 0값 포함, 전체 월 반환 |
| reaction | public task에만 적용, 인증 사용자만 토글 가능, (user_id, task_id, emoji) 단위 토글 |
| 페이지네이션 | offset/limit, 기본값 0/20, max 100, 알 수 없는 필터 키는 400 |
| 오류 응답 | 모든 오류는 application/problem+json, RFC9457 핵심 멤버 포함 |
| 엔드포인트 수 | 총 22개 엔드포인트 |

---

## 14. 랜딩 페이지 네비게이션 요구사항

- 랜딩 페이지 상단의 `소개`, `기능` 링크는 Thymeleaf 컨텍스트 경로를 반영하여 라우팅되어야 한다.
- 내부 링크는 `th:href`를 함께 사용해 루트 배포와 컨텍스트 경로 배포 모두에서 동일하게 동작해야 한다.
- 링크 클릭 시 동일 페이지 잔류 없이 각각 `/about`, `/features`로 이동해야 한다.

---

## 15. 할 일 생성 모달 태그 입력 UX 요구사항

- 할 일 생성 모달의 태그 영역에서는 태그 선택만 제공하고, `+ 추가` 버튼을 노출하지 않는다.
- 태그 추가를 위한 `prompt` 기반 인터랙션은 사용하지 않는다.
- 태그 영역은 기본 태그와 기존 그룹 태그를 렌더링해야 하며, 기존 선택 동작은 유지되어야 한다.

---

## 16. 할 일 수정 모달 날짜/시간 편집 요구사항

- 할 일 수정 모달의 캘린더 버튼 클릭 시 생성 모달과 동일한 날짜/시간 피커를 사용해야 한다.
- 피커에서 선택한 날짜와 시간은 수정 모달에 즉시 반영되어야 한다.
- 할 일 저장 시 수정된 `due_date`, `start_time`, `end_time` 값이 PATCH 요청 payload에 포함되어야 한다.
- `start_time`, `end_time`은 시간 문자열(`HH:mm`)이 아닌 RFC3339/ISO-8601 `date-time` 문자열(예: `2026-02-21T05:00:00Z`)로 전송해야 한다.

---

## 17. Dev 프로파일 데이터 시드 호환성 요구사항

- `dev` 프로파일에서 사용하는 `data-dev.sql`의 `users` INSERT는 현재 스키마의 `NOT NULL` 필드(`visibility` 포함)를 모두 채워야 한다.
- `dev` 프로파일 실행(`spring.profiles.active=dev`) 시 SQL 초기화 단계에서 `Field 'visibility' doesn't have a default value` 오류가 발생하면 안 된다.
- `data-dev.sql`의 사용자 시드 컬럼 구성은 `users` 엔티티/테이블 제약과 일관되어야 하며, 기본 공개 사용자 데이터는 `visibility='public'`을 명시한다.

---

## 18. 특정 사용자 ID 기준 더미데이터 소유권 이전 SQL 요구사항

- MySQL dev 환경에서 `source_user_uuid`의 더미데이터 소유권을 `target_user_uuid`로 이전할 수 있는 단일 SQL 스크립트를 제공해야 한다.
- 이전 범위는 `groups.owner_user_id`, `tasks.owner_user_id`, `refresh_tokens.user_id`, `reactions.user_id`를 포함해야 한다.
- `reactions`의 `(user_id, task_id, emoji)` unique 제약 충돌을 피하기 위해, 이전 전 중복 후보를 정리하는 단계가 있어야 한다.
- 스크립트는 트랜잭션(`START TRANSACTION`/`COMMIT`) 안에서 실행되어야 하며, 이전 전후 건수 확인용 조회를 포함해야 한다.
- UUID는 `UUID_TO_BIN`/`BIN_TO_UUID` 규칙을 사용하는 현재 MySQL 스키마와 호환되어야 한다.

---

## 19. 액세스 토큰 만료 시 자동 리프레시 요구사항

- 클라이언트는 API 요청에서 `401` 응답을 받으면 `POST /api/v1/auth/refresh`를 호출해 토큰을 회전해야 한다.
- 리프레시 성공 시 새 `access_token`/`refresh_token`을 로컬 저장소에 반영하고, 실패한 원 요청을 1회 재시도해야 한다.
- `/api/v1/auth/*` 요청 자체는 자동 리프레시 재귀 대상에서 제외해야 한다.
- 리프레시 실패 시 세션 토큰(`access_token`, `refresh_token`, `token_expires_at`)을 정리해야 한다.

---

## 20. Google Tasks 가져오기(Import) 요구사항 (기획)

본 절은 구현 전 기획 확정 요구사항이다. 본 요구사항은 현재 화면(UI) 변경 없이 백엔드 import 동작을 대상으로 한다.

### 20.1 범위

- Google Tasks 공식 API 응답 필드만 매핑한다.
- 비공식/확장 필드(예: `starred`, `scheduled_time`, `task_type`, 커스텀 키)는 무시한다.
- 가져오기 결과는 내부 task를 1레벨 평탄 구조로 생성/갱신한다.

### 20.2 지원/비지원 필드 정책

| 항목 | 정책 |
|------|------|
| 기본 task(title/status) | 지원 |
| 하위 할 일(`parent`) | 구조는 보존하지 않고 1레벨 task로 import |
| star 기능 | 미지원(무시) |
| 마감일(`due`) | 미지원(무시) |
| 그룹(TaskList/parent group) | 미지원(내부 `group_id=null`) |

### 20.3 Google -> 내부 매핑 규칙

| Google Tasks 필드 | 내부 반영 |
|------------------|-----------|
| `id` | provider dedupe 키(`provider_task_id`)로 사용 |
| `title` | `tasks.title` |
| `status=completed` | `tasks.status=done` |
| `status=needsAction` | `tasks.status=todo` |
| `notes` | 내부 메모/참조 보존 경로로 저장(구현체 정의) |
| `parent` | 계층 연결 없이 일반 task로 생성 |
| `deleted=true` 또는 `hidden=true` | import 대상에서 제외 |

- 내부 생성 기본값은 `visibility=private`, `group_id=null`을 사용한다.
- import 시 `due` 필드는 무시하지만, 대시보드 날짜 기반 노출 일관성을 위해 내부 `due_date`는 import 실행 시점의 사용자 timezone 오늘 날짜로 설정한다.

### 20.4 멱등성/중복 방지

- 동일 사용자 재실행 시 중복 task를 생성하면 안 된다.
- 최소 키는 `(user_id, provider, provider_task_id)`를 사용한다.
- 페이지 재시도/네트워크 재시도로 요청이 반복되어도 최종 상태가 동일해야 한다.

### 20.5 페이지네이션/안정성

- `tasks.list`의 `nextPageToken`이 소진될 때까지 순회해야 한다.
- 같은 `provider_task_id`가 같은 런(run) 안에서 중복 수신되면 1회만 처리한다.
- 결과 응답은 최소 `created/skipped/failed` 집계를 포함해야 한다.

### 20.6 비목표

- 본 단계에서 Google Tasks 2-way sync(양방향 동기화)는 지원하지 않는다.
- 본 단계에서 import 전용 UI(설정 화면, 진행률 뷰)는 구현하지 않는다.

### 20.7 구현 상태

- `POST /api/v1/tasks/import/google` 엔드포인트를 구현해 단일 JSON 파일 업로드를 지원한다.
- 서버는 multipart 업로드 상한을 2GB로 제한한다.
- 대시보드 설정 모달에 `데이터 가져오기` 탭을 추가해 파일 1개 업로드와 import 실행을 지원한다.
