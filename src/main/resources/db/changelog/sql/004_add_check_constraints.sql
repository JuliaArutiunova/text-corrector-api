--liquibase formatted sql

--changeset Julia Arutiunova:1

ALTER TABLE app.tasks
    ADD CONSTRAINT check_task_status CHECK (status IN ('NEW', 'IN_PROGRESS', 'COMPLETED', 'ERROR')),
    ADD CONSTRAINT check_task_language CHECK (language IN ('RU', 'EN'));