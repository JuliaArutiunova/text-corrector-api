--liquibase formatted sql

--changeset Julia Arutiunova:1

CREATE INDEX idx_tasks_status_new ON app.tasks(status, created_at)
    WHERE status = 'NEW';