SET @source_user_uuid = '11111111-1111-1111-1111-111111111111';
SET @target_user_uuid = '3c87a218-4677-4700-acc5-5b4ec19f6bc5';

SET @source_user_bin = UUID_TO_BIN(@source_user_uuid);
SET @target_user_bin = UUID_TO_BIN(@target_user_uuid);

SET @old_sql_safe_updates = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

START TRANSACTION;

SELECT 'source_user_exists' AS check_name, COUNT(*) AS row_count
FROM users
WHERE id = @source_user_bin;

SELECT 'target_user_exists' AS check_name, COUNT(*) AS row_count
FROM users
WHERE id = @target_user_bin;

SELECT 'groups_before' AS metric, COUNT(*) AS row_count
FROM `groups`
WHERE owner_user_id = @source_user_bin;

SELECT 'tasks_before' AS metric, COUNT(*) AS row_count
FROM tasks
WHERE owner_user_id = @source_user_bin;

SELECT 'refresh_tokens_before' AS metric, COUNT(*) AS row_count
FROM refresh_tokens
WHERE user_id = @source_user_bin;

SELECT 'reactions_as_actor_before' AS metric, COUNT(*) AS row_count
FROM reactions
WHERE user_id = @source_user_bin;

DELETE r_source
FROM reactions r_source
JOIN reactions r_target
  ON r_target.task_id = r_source.task_id
 AND r_target.emoji = r_source.emoji
 AND r_target.user_id = @target_user_bin
WHERE r_source.user_id = @source_user_bin;

UPDATE `groups`
SET owner_user_id = @target_user_bin
WHERE owner_user_id = @source_user_bin;

UPDATE tasks
SET owner_user_id = @target_user_bin
WHERE owner_user_id = @source_user_bin;

UPDATE refresh_tokens
SET user_id = @target_user_bin
WHERE user_id = @source_user_bin;

UPDATE reactions
SET user_id = @target_user_bin
WHERE user_id = @source_user_bin;

SELECT 'groups_after_source' AS metric, COUNT(*) AS row_count
FROM `groups`
WHERE owner_user_id = @source_user_bin;

SELECT 'tasks_after_source' AS metric, COUNT(*) AS row_count
FROM tasks
WHERE owner_user_id = @source_user_bin;

SELECT 'groups_after_target' AS metric, COUNT(*) AS row_count
FROM `groups`
WHERE owner_user_id = @target_user_bin;

SELECT 'tasks_after_target' AS metric, COUNT(*) AS row_count
FROM tasks
WHERE owner_user_id = @target_user_bin;

COMMIT;

SET SQL_SAFE_UPDATES = @old_sql_safe_updates;
