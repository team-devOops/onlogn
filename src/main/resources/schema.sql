-- ============================================================
-- O(NlogN) v1 Database Schema (H2 compatible)
-- ============================================================
-- 이 파일은 문서화 목적으로 유지한다.
-- 실제 DDL은 Hibernate ddl-auto=create-drop이 엔티티 기준으로 생성한다.
-- Spring Boot의 defer-datasource-initialization=true 설정으로
-- Hibernate DDL 이후에 data.sql이 실행된다.
-- ============================================================

-- 1. users
CREATE TABLE IF NOT EXISTS users (
    id              UUID            PRIMARY KEY,
    profile_slug    VARCHAR(255)    NOT NULL UNIQUE,
    email           VARCHAR(255),
    display_name    VARCHAR(255)    NOT NULL,
    avatar_url      VARCHAR(1024),
    bio             VARCHAR(2000),
    visibility      VARCHAR(16)     NOT NULL DEFAULT 'public',
    timezone        VARCHAR(64)     NOT NULL DEFAULT 'UTC',
    github_id       VARCHAR(255)    UNIQUE,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL
);

-- 2. groups (H2 예약어이므로 큰따옴표 사용)
CREATE TABLE IF NOT EXISTS "groups" (
    id              UUID            PRIMARY KEY,
    owner_user_id   UUID            NOT NULL,
    visibility      VARCHAR(16)     NOT NULL DEFAULT 'private',
    description     VARCHAR(2000),
    color           VARCHAR(32),
    icon            VARCHAR(64),
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    CONSTRAINT fk_groups_owner FOREIGN KEY (owner_user_id) REFERENCES users(id)
);

-- 3. tasks
CREATE TABLE IF NOT EXISTS tasks (
    id              UUID            PRIMARY KEY,
    owner_user_id   UUID            NOT NULL,
    group_id        UUID,
    title           VARCHAR(1000)   NOT NULL,
    status          VARCHAR(16)     NOT NULL DEFAULT 'todo',
    visibility      VARCHAR(16)     NOT NULL DEFAULT 'private',
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    due_date        DATE,
    start_time      TIMESTAMP,
    end_time        TIMESTAMP,
    CONSTRAINT fk_tasks_owner FOREIGN KEY (owner_user_id) REFERENCES users(id),
    CONSTRAINT fk_tasks_group FOREIGN KEY (group_id) REFERENCES "groups"(id)
);

-- 4. task_tags (ElementCollection)
CREATE TABLE IF NOT EXISTS task_tags (
    task_id         UUID            NOT NULL,
    tag             VARCHAR(255)    NOT NULL,
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 5. task_reference_links (ElementCollection)
CREATE TABLE IF NOT EXISTS task_reference_links (
    task_id         UUID            NOT NULL,
    link            VARCHAR(2048)   NOT NULL,
    CONSTRAINT fk_task_links_task FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 6. reactions
CREATE TABLE IF NOT EXISTS reactions (
    id              UUID            PRIMARY KEY,
    user_id         UUID,
    task_id         UUID            NOT NULL,
    emoji           VARCHAR(64)     NOT NULL,
    active          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP       NOT NULL,
    CONSTRAINT fk_reactions_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reactions_task FOREIGN KEY (task_id) REFERENCES tasks(id)
);

-- 7. refresh_tokens
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id              UUID            PRIMARY KEY,
    token           VARCHAR(512)    NOT NULL UNIQUE,
    user_id         UUID            NOT NULL,
    revoked         BOOLEAN         NOT NULL DEFAULT FALSE,
    expires_at      TIMESTAMP       NOT NULL,
    created_at      TIMESTAMP       NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);
