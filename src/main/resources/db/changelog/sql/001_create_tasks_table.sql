--liquibase formatted sql

--changeset Julia Arutiunova:1

CREATE TABLE app.tasks (
    id uuid NOT NULL PRIMARY KEY,
    original_text text NOT NULL,
    corrected_text text,
    status character varying (20) NOT NULL,
    language character varying (2) NOT NULL,
    error_message text,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    updated_at timestamp with time zone
);
