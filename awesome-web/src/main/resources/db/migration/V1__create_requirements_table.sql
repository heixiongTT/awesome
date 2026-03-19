CREATE TABLE requirements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(1024),
    priority VARCHAR(32),
    status VARCHAR(32) NOT NULL,
    creator VARCHAR(64),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_requirements_status ON requirements (status);
CREATE INDEX idx_requirements_creator ON requirements (creator);
CREATE INDEX idx_requirements_created_at ON requirements (created_at);
