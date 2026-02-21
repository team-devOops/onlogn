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
