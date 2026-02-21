# 리소스 스키마 사전 (v1)

이 문서는 O(NlogN) v1 API의 `users`, `groups`, `tasks`, `profiles`, `reactions`, 월간 캘린더 집계 응답에 대한 기준 필드 사전이다.

## 공통 규칙

- `id`, `owner_user_id`, `user_id`는 문자열 UUID다.
- 날짜/시간 문자열은 UTC ISO-8601 형식을 사용한다.
  - `date-time`: `YYYY-MM-DDTHH:MM:SSZ`
  - `date`: `YYYY-MM-DD`
- `object`/array 필드는 null 값이 의미적으로 유효할 때만 nullable로 명시한다.
- 기준 응답 필드는 nullable로 표시되지 않은 경우 **응답에서 required**로 본다.

## tasks

기준 필수 task 필드(13개):

- `id`, `owner_user_id`, `group_id`, `title`, `status`, `visibility`, `created_at`, `updated_at`, `due_date`, `start_time`, `end_time`, `tags`, `reference_links`

### 필드

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `id` | string | false | - | - | 리소스 식별자(UUID), 불변 값. |
| `owner_user_id` | string | false | - | - | task 소유자, `users.id`에 매핑되어야 함. |
| `group_id` | string | true | - | `null` | task가 그룹에 속하지 않으면 `null` (`group_id = null`). |
| `title` | string | false | - | - | 사람이 읽는 제목, 빈 문자열 불가. |
| `status` | string | false | `todo` , `in_progress` , `done` | `todo` | task 상태 머신. |
| `visibility` | string | false | `private` , `public` | `private` | public task는 profile 범위 public 라우트에서만 노출된다. |
| `created_at` | string(date-time) | false | - | - | 생성 시각(UTC). |
| `updated_at` | string(date-time) | false | - | - | 마지막 수정 시각(UTC). |
| `due_date` | string(date) | true | - | `null` | 날짜 단위 마감일. |
| `start_time` | string(date-time) | true | - | `null` | 예정 시작 시각(UTC). |
| `end_time` | string(date-time) | true | - | `null` | 예정 종료 시각(UTC). |
| `tags` | array(string) | false | - | `[]` | 태그 목록, 빈 배열 허용. |
| `reference_links` | array(string(url)) | false | - | `[]` | URL 참조 목록, 빈 배열 허용. |

### 포함 규칙

- `tags`, `reference_links`는 create/update 입력과 응답 모두에서 배열이다.
- `tags`는 요청 payload 안정성을 위해 빈 배열을 허용한다.
- URL 참조가 없을 때 `reference_links`는 빈 배열을 허용한다.

## users

내부 인증 컨텍스트에서 사용하는 기준 사용자 payload.

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `id` | string | false | - | - | 사용자 식별자(UUID). |
| `profile_slug` | string | false | - | - | profile 라우트용 공개 URL 키. |
| `email` | string(email) | true | - | `null` | 내부/사용자 이메일 필드, public allowlist에는 없음. |
| `display_name` | string | false | - | - | profile에 노출되는 표시 이름. |
| `avatar_url` | string(url) | true | - | `null` | profile 이미지 URL, nullable. |
| `timezone` | string | false | - | `"UTC"` | IANA timezone 식별자. |
| `created_at` | string(date-time) | false | - | - | 계정 생성 시각. |
| `updated_at` | string(date-time) | false | - | - | 마지막 profile 수정 시각. |

## groups

기준 group 스키마.

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `id` | string | false | - | - | group 식별자(UUID), 불변 값. |
| `owner_user_id` | string | false | - | - | group 소유자, `users.id`에 매핑되어야 함. |
| `visibility` | string | false | `private` , `public` | `private` | group visibility 정책은 task visibility 기대와 연동된다. |
| `description` | string | true | - | `null` | 선택 설명 텍스트. |
| `color` | string | true | - | `null` | 선택 UI 강조 색상 토큰(hex 또는 토큰 문자열). |
| `icon` | string | true | - | `null` | 선택 UI 아이콘 토큰/문자열. |
| `created_at` | string(date-time) | false | - | - | group 생성 시각. |
| `updated_at` | string(date-time) | false | - | - | group 수정 시각. |

삭제 정책: group을 제거할 때 고아 task는 `group_id = null`로 재할당해야 한다.

## profiles (public allowlist)

공개 profile 응답은 아래 allowlist와 중첩 집계만 노출해야 한다.

- `profile_slug`
- `display_name`
- `avatar_url`
- `bio`
- `timezone`
- `created_at`
- `stats_summary`

### profile 필드

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `profile_slug` | string | false | - | - | `/profiles/{slug}`에서 쓰는 안정적인 공개 식별자. |
| `display_name` | string | false | - | - | 공개 표시 이름. |
| `avatar_url` | string(url) | true | - | `null` | 공개 아바타 이미지 URL. |
| `bio` | string | true | - | `null` | 공개 자기소개 텍스트. |
| `timezone` | string | false | - | `"UTC"` | IANA timezone 식별자. |
| `created_at` | string(date-time) | false | - | - | 공개 라우트에 노출되는 profile 생성 시각. |
| `stats_summary` | object | false | - | `{}` | allowlist 집계 payload(v1 기준 필드 아래 정의). |

### `stats_summary`

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `total_tasks` | integer | false | - | `0` | profile 사용자 소유 task 수. |
| `done_tasks` | integer | false | - | `0` | `status=done` task 수. |
| `in_progress_tasks` | integer | false | - | `0` | `status=in_progress` task 수. |
| `todo_tasks` | integer | false | - | `0` | `status=todo` task 수. |

## reactions

사용자별 이모지 토글 정책: `(user_id, task_id, emoji)` 조합마다 논리적 reaction 행은 하나다. 같은 조합으로 다시 요청하면 reaction 상태가 토글된다.

### 필드

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `task_id` | string | false | - | - | 대상 task id(UUID). |
| `user_id` | string | false | - | - | 동작 사용자 id(UUID). |
| `emoji` | string | false | - | - | reaction emoji 문자열(예: `👍`, `❤️`, `🎉`). |
| `created_at` | string(date-time) | false | - | - | reaction 생성 시각. |
| `updated_at` | string(date-time) | false | - | - | reaction 수정 시각(토글 갱신). |

### reaction 응답 모델

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `task_id` | string | false | - | - | 부모 task id. |
| `emoji` | string | false | - | - | 이모지 집계 키. |
| `count` | integer | false | - | `0` | 이 task에서 해당 emoji를 활성화한 전체 사용자 수. |
| `requester_reacted` | boolean | false | - | `false` | 요청자가 해당 emoji를 활성화했는지 여부. |

## 월간 캘린더 집계

월간 캘린더 응답 payload는 날짜 기준으로 그룹화된다.

| Field | Type | Nullability | Enum | Default | Notes |
| --- | --- | --- | --- | --- | --- |
| `date` | string(date) | false | - | - | 일자 버킷 키(`YYYY-MM-DD`). |
| `planned_count` | integer | false | - | `0` | 해당 일자에 계획된 전체 task 수(모든 상태). |
| `done_count` | integer | false | - | `0` | `status=done` task 수. |
| `in_progress_count` | integer | false | - | `0` | `status=in_progress` task 수. |
| `completion_rate` | number | false | - | `0` | 일일 완료율 `0..100`; `(done_count / nullif(planned_count,0)) * 100`, 필요 시 반올림. |
