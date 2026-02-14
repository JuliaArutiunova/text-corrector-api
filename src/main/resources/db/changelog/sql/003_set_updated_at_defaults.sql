--liquibase formatted sql

--changeset Julia Arutiunova:1
UPDATE app.tasks SET updated_at = created_at WHERE updated_at IS NULL;

--changeset Julia Arutiunova:2
ALTER TABLE app.tasks ALTER COLUMN updated_at SET DEFAULT now();
ALTER TABLE app.tasks ALTER COLUMN updated_at SET NOT NULL;