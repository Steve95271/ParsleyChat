CREATE TABLE IF NOT EXISTS user
(
    id       BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    username VARCHAR(128)    NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS room
(
    id         BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    name       VARCHAR(128)    NOT NULL,
    creator_id BIGINT UNSIGNED NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS message
(
    id          BIGINT UNSIGNED NOT NULL PRIMARY KEY,
    room_id     BIGINT UNSIGNED NOT NULL,
    sender_id   BIGINT UNSIGNED NOT NULL,
    sender_name VARCHAR(128),
    content     VARCHAR(4000),
    time_stamp  DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS room_member
(
    room_id   BIGINT UNSIGNED NOT NULL,
    user_id   BIGINT UNSIGNED NOT NULL,
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    role      ENUM ('creator', 'admin', 'member')
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;