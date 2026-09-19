CREATE TABLE project (
    id          UUID PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL
);

CREATE TABLE owner (
    id    UUID PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(56)  NOT NULL UNIQUE
);

CREATE TABLE tag (
    id    UUID PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    color VARCHAR(7)   NOT NULL
);

CREATE TABLE task (
    id           UUID PRIMARY KEY,
    title        VARCHAR(120) NOT NULL,
    description  TEXT,
    status       VARCHAR(20)  NOT NULL,
    priority     VARCHAR(10)  NOT NULL,
    due_date     DATE,
    created_at   TIMESTAMP    NOT NULL,
    completed_at TIMESTAMP,
    project_id   UUID         NOT NULL REFERENCES project (id),
    owner_id     UUID         REFERENCES owner (id)
);

CREATE TABLE task_tag (
    id      UUID PRIMARY KEY,
    task_id UUID NOT NULL REFERENCES task (id),
    tag_id  UUID NOT NULL REFERENCES tag (id),
    UNIQUE (task_id, tag_id)
);

CREATE TABLE comment (
    id         UUID PRIMARY KEY,
    text       VARCHAR(500) NOT NULL,
    author     VARCHAR(100) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    task_id    UUID         NOT NULL REFERENCES task (id)
);
