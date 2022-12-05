-- liquibase formatted sql

--changeset gabriell:02122022-01
CREATE TABLE IF NOT EXISTS Notification_Task
(
    id           BIGINT PRIMARY KEY,
    userID       BIGINT,
    message      TEXT,
    notification TIMESTAMP
);