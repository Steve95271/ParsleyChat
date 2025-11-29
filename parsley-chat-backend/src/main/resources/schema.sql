DROP TABLE IF EXISTS "users";
CREATE TABLE IF NOT EXISTS "users"
(
    id        BIGINT NOT NULL PRIMARY KEY,
    username  VARCHAR(128) NOT NULL
);

DROP TABLE IF EXISTS rooms;
CREATE TABLE IF NOT EXISTS rooms
(
    id         BIGINT NOT NULL PRIMARY KEY,
    name       VARCHAR(128) NOT NULL,
    creator_id BIGINT NOT NULL
);

DROP TABLE IF EXISTS messages;
CREATE TABLE IF NOT EXISTS messages
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    room_id      BIGINT NOT NULL,
    sender_id    BIGINT NOT NULL,
    sender_name  VARCHAR(128),
    content      VARCHAR(4000),
    time_stamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- In PostgresSQL, we need to create ENUM type first
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'room_role') THEN
CREATE TYPE room_role AS ENUM ('creator', 'admin', 'member');
END IF;
END$$;

DROP TABLE IF EXISTS room_members;
CREATE TABLE IF NOT EXISTS room_members
(
    room_id   BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    role      room_role
);
