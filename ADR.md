# O(NlogN) Architecture Decision Record (v1)

> 작성 기준일: 2026-02-21
> 버전: v1

---

## ADR-001: Spring Boot 4 + Java 21 기반 백엔드 선택

- **상태**: 승인
- **결정**: Spring Boot 4.0.2, Java 21, Gradle 9.3 기반으로 백엔드 API를 구현한다.
- **근거**: Spring Boot 4는 최신 Jakarta EE 지원, Virtual Threads, Jackson 3.x 통합을 제공한다. Java 21은 LTS 버전으로 record, sealed class 등 현대적 문법을 사용할 수 있다.
- **결과**: `tools.jackson.databind` 패키지를 사용하며, `com.fasterxml.jackson.core:jackson-annotations`는 하위 호환으로 유지된다.

---

## ADR-002: H2 인메모리 데이터베이스 (개발 단계)

- **상태**: 승인
- **결정**: 개발/데모 단계에서 H2 인메모리 데이터베이스를 사용한다.
- **근거**: 별도 DB 설치 없이 즉시 실행 가능하고, JPA ddl-auto=create-drop으로 스키마 자동 생성이 가능하다.
- **결과**: 애플리케이션 재시작 시 데이터가 초기화된다. 프로덕션 배포 시 PostgreSQL 등으로 교체해야 한다.

---

## ADR-003: JWT 기반 Stateless 인증

- **상태**: 승인
- **결정**: access token은 JWT, refresh token은 opaque string으로 구현한다.
- **근거**: Stateless 인증은 수평 확장에 유리하고, refresh token rotation은 보안을 강화한다.
- **세부사항**:
  - access token TTL: 900초 (15분)
  - refresh token TTL: 1,209,600초 (14일)
  - 서명 알고리즘: HMAC-SHA384 (jjwt 0.12.6)
  - refresh token은 DB에 저장하고 1회 사용 후 즉시 revoke한다.

---

## ADR-004: RFC9457 Problem Details 오류 응답 표준화

- **상태**: 승인
- **결정**: 모든 API 오류 응답은 application/problem+json Content-Type과 RFC9457 핵심 멤버를 사용한다.
- **근거**: 클라이언트가 일관된 오류 파싱 로직을 구현할 수 있고, 표준 기반이므로 상호운용성이 높다.
- **결과**: GlobalExceptionHandler가 모든 예외를 ProblemResponse로 변환한다.

---

## ADR-005: Offset/Limit 페이지네이션

- **상태**: 승인
- **결정**: v1 목록 엔드포인트는 offset/limit 기반 페이지네이션만 지원한다.
- **근거**: 구현이 단순하고 클라이언트가 random access 가능하다.
- **세부사항**: offset 기본값 0, limit 기본값 20, 최대 limit 100, 정렬 created_at desc + id desc 고정

---

## ADR-006: Owner-Scoped 리소스 접근 제어

- **상태**: 승인
- **결정**: task, group은 owner_user_id로 소유권을 관리하며, owner만 CRUD 권한을 갖는다.
- **결과**: JWT에서 추출한 userId와 리소스의 ownerUserId를 비교한다. 불일치 시 403을 반환한다.

---

## ADR-007: Public Profile 범위 정책

- **상태**: 승인
- **결정**: public task는 /api/v1/profiles/{slug}/tasks 경로로만 노출하며, 전역 public feed는 제공하지 않는다.
- **결과**: 서버가 visibility=public 필터를 강제 적용한다.

---

## ADR-008: Reaction 토글 패턴

- **상태**: 승인
- **결정**: (user_id, task_id, emoji) 조합을 unique key로 하는 토글 패턴을 사용한다.
- **결과**: POST 요청 시 기존 reaction이 있으면 active 상태를 토글하고, 없으면 새로 생성한다.

---

## ADR-009: 월간 캘린더 Zero-Fill 정책

- **상태**: 승인
- **결정**: 월간 캘린더 응답은 해당 월의 모든 날짜를 포함하며, task가 없는 날은 0값으로 채운다.
- **결과**: 서버가 YearMonth.lengthOfMonth()로 해당 월의 일수를 계산하고, due_date 기준으로 task를 그룹화한다.

---

## ADR-010: GitHub OAuth Mock (개발 단계)

- **상태**: 임시
- **결정**: 개발 단계에서 GitHub OAuth code exchange를 mock으로 구현한다. code 값을 githubId로 사용하여 사용자를 찾거나 생성한다.
- **결과**: 프로덕션 배포 전에 실제 GitHub OAuth API 연동으로 교체해야 한다.

---

## ADR-011: Jackson 3.x 적용

- **상태**: 승인
- **결정**: Spring Boot 4.0.2가 사용하는 Jackson 3.x를 그대로 사용한다.
- **세부사항**: tools.jackson.databind.ObjectMapper가 Spring Bean으로 자동 등록되며, 어노테이션은 com.fasterxml.jackson.core:jackson-annotations:2.20을 사용한다.

---

## ADR-012: 패키지 구조

- **상태**: 승인
- **결정**: 기능별 수평 레이어 구조를 사용한다.
- **구조**: common(dto/exception), config, entity, repository, service, controller, security

---

## ADR-013: AWS Bedrock 기반 AI 태그 자동 추출

- **상태**: 승인
- **결정**: Task 생성 시 사용자가 tags를 제공하지 않으면, AWS Bedrock Converse API로 Claude 3.5 Sonnet을 호출하여 title에서 tags를 자동 추출한다.
- **근거**: 사용자가 매번 태그를 수동 입력하지 않아도 일관된 분류가 가능하고, 검색/필터/통계 활용도를 높인다.
- **세부사항**:
  - SDK: AWS SDK for Java v2 `software.amazon.awssdk:bedrockruntime:2.31.76`
  - 모델: `anthropic.claude-3-5-sonnet-20241022-v2:0` (설정으로 변경 가능)
  - 추출 규칙: 1-5개, 소문자, 단어 또는 하이픈 연결
  - 실패 시 빈 배열로 graceful fallback (서버 에러 없음)
  - 사용자가 명시적으로 tags를 제공하면 AI 추출을 건너뜀
- **결과**: `app.bedrock.enabled=true`(기본값)일 때 활성화. AWS 크레덴셜은 DefaultCredentialsProvider로 자동 탐색.

---

## ADR-014: 랜딩 네비게이션에 Thymeleaf 컨텍스트 경로 링크 적용

- **상태**: 승인
- **결정**: 랜딩 페이지 상단 네비게이션 링크(`소개`, `기능`)에 정적 `href`와 함께 `th:href`를 적용한다.
- **근거**: 컨텍스트 경로가 있는 배포 환경에서 절대 경로만 사용하면 링크 이동이 실패하거나 오동작할 수 있다.
- **결과**: `/about`, `/features` 이동이 루트/비루트 배포 모두에서 일관되게 동작한다.

---

## ADR-015: 할 일 생성 모달에서 태그 수동 추가 버튼 제거

- **상태**: 승인
- **결정**: 할 일 생성 모달의 태그 영역에서 `+ 추가` 버튼과 `prompt` 기반 태그 생성 흐름을 제거한다.
- **근거**: 단발성 프롬프트 입력은 UI 일관성을 해치고, 실수 입력 및 모바일 사용성 저하를 유발한다.
- **결과**: 태그 영역은 기본/그룹 태그 선택에 집중하며, 기존 태그 선택 로직은 유지된다.

---

## ADR-016: 할 일 수정 모달도 생성 모달의 날짜/시간 피커를 공유

- **상태**: 승인
- **결정**: 할 일 수정 모달의 날짜 버튼은 생성 모달과 동일한 피커 컴포넌트를 재사용한다.
- **근거**: 생성/수정 간 상호작용이 다르면 학습 비용이 증가하고 사용자가 기능 누락으로 인식할 수 있다.
- **결과**: 수정 모달에서도 날짜/시간을 즉시 변경하고 저장할 수 있으며, 변경 값은 PATCH payload로 반영된다.

---

## ADR-017: start_time/end_time 전송 포맷을 Instant 호환 ISO-8601로 고정

- **상태**: 승인
- **결정**: 프론트엔드는 `start_time`, `end_time`을 `HH:mm:00Z` 형태로 보내지 않고, 날짜를 결합한 ISO-8601 UTC 문자열(`YYYY-MM-DDTHH:mm:ss.sssZ`)로 전송한다.
- **근거**: 백엔드 DTO 타입이 `Instant`이므로 시간만 포함된 문자열은 역직렬화 단계에서 파싱 예외를 발생시킨다.
- **결과**: 생성/수정 요청 모두에서 `DateTimeParseException` 없이 시간 정보가 저장된다.

---

## ADR-016: 공개 프로필 task 응답에서 그룹 visibility 이중 경계 적용

- **상태**: 승인
- **결정**: `GET /api/v1/profiles/{slug}/tasks`는 task `visibility=public` 뿐 아니라, task에 연결된 group이 존재할 경우 `group.visibility=public`도 동시에 만족해야 응답에 포함한다.
- **근거**: task 단위가 public이어도 group이 private이면 그룹 소속 자체가 민감한 맥락 정보가 될 수 있어 공개 프로필에서 노출되면 안 된다.
- **결과**: 공개 profile task API에서 private group 소속 task를 서버에서 제외하여 프론트 실수와 무관하게 비공개 경계를 강제한다.

---

## ADR-017: 공개 프로필 AI 인사이트를 기간 기반 API로 서버 제공

- **상태**: 승인
- **결정**: 공개 프로필 화면의 AI 인사이트는 `GET /api/v1/profiles/{slug}/ai-summary?period=weekly|monthly|30days` API로 제공하고, period 버튼 클릭마다 서버 응답을 갱신한다.
- **근거**: 클라이언트 고정 문구/더미 데이터는 실제 활동 변화와 불일치하며, 기간 토글 UI와 데이터 일관성을 보장할 수 없다.
- **결과**: 공개 범위 task(`task.visibility=public` + 연결 group `visibility=public`) 기반 요약을 반환하며, 비공개 프로필은 404로 차단하고 UI에서도 인사이트 영역을 숨긴다.

---

## ADR-018: 공개 프로필 AI 생산성 요약을 tasks API 응답으로 클라이언트 집계

- **상태**: 승인
- **결정**: `profile.html`의 AI 생산성 요약은 그룹 분포와 동일하게 `GET /api/v1/profiles/{slug}/tasks` 응답을 기간별(`weekly`, `monthly`, `30days`)로 조회해 클라이언트에서 집계한다.
- **근거**: 동일 공개 경계(public task + public group)를 공유하는 단일 데이터 소스를 사용하면 요약/분포 간 수치 불일치가 줄고, UI 토글 동작을 한 경로로 단순화할 수 있다.
- **결과**: AI 생산성 요약의 API 호출이 tasks 기반으로 일원화되며, 기간 버튼 클릭 시 due_date 범위 조회 결과로 요약 문구를 재계산한다.

---

## ADR-019: 존재하지 않는 공개 프로필 슬러그는 서버 라우트 단계에서 HTML 404 반환

- **상태**: 승인
- **결정**: `/@{slug}` 웹 라우트는 템플릿 렌더링 전에 `profile_slug` 존재 여부를 조회하고, 미존재 시 `ResponseStatusException(HttpStatus.NOT_FOUND)`를 발생시킨다.
- **근거**: 기존에는 페이지가 먼저 렌더링된 뒤 API 404로 데이터만 비어 보여 사용자 경험이 혼란스러웠다.
- **결과**: 존재하지 않는 슬러그 접근 시 즉시 `templates/error/404.html` 흐름으로 이동하여 빈 프로필 화면 노출을 방지한다.

---

## ADR-018: Dev 시드 SQL의 users visibility 필드 명시

- **상태**: 승인
- **결정**: `application-dev.yaml`에서 로드하는 `data-dev.sql`의 `users` 시드 INSERT에 `visibility` 컬럼을 명시하고 기본값을 `public`으로 채운다.
- **근거**: `users.visibility`는 `NOT NULL`이며 기본값 없는 INSERT가 MySQL 초기화 단계에서 실패해 `dev` 프로파일 부팅이 중단됐다.
- **결과**: `spring.profiles.active=dev` 실행 시 SQL 초기화 오류 없이 애플리케이션 컨텍스트가 정상 기동된다.

---

## ADR-019: 특정 사용자 대상 더미데이터 소유권 이전은 오프라인 SQL로 처리

- **상태**: 승인
- **결정**: 애플리케이션 로직을 추가하지 않고, `docs/migrate-dummy-data-to-user.sql` 오프라인 스크립트로 더미데이터 소유권 이전을 수행한다.
- **근거**: 대상은 운영 기능이 아닌 dev 데이터 정리 작업이며, owner FK를 일괄 갱신하는 작업은 트랜잭션 SQL이 가장 단순하고 재현 가능하다.
- **결과**: 지정한 source/target UUID 기준으로 `groups`, `tasks`, `refresh_tokens`, `reactions` 소유권을 이전하며, reaction unique 충돌은 사전 중복 삭제로 방지한다.

---

## ADR-020: 공개 프로필 관심 키워드는 슬러그 소유자의 공개 task 데이터만 사용

- **상태**: 승인
- **결정**: `profile.html`의 관심 키워드 영역은 `POST /api/v1/reviews/weekly`를 사용하지 않고, `GET /api/v1/profiles/{slug}/tasks?limit=100` 응답을 기반으로 키워드를 계산한다.
- **근거**: `/reviews/weekly`는 인증된 조회자 principal 기준 데이터이므로 타인 프로필 조회 시 키워드가 섞여 보일 수 있다.
- **결과**: 관심 키워드는 항상 프로필 소유자의 공개 task(`tags`, `title`)만 반영되어, 조회자 계정과 무관하게 일관된 프로필 컨텍스트를 유지한다.
