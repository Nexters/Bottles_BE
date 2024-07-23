CREATE TABLE User (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) DEFAULT NULL,
    birthdate DATE DEFAULT NULL,
    kakao_id VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    gender VARCHAR(10) DEFAULT 'MALE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE user_profile
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT                             NOT NULL,
    profile_select JSON,
    introduction   JSON,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bottle
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id BIGINT                             NOT NULL,
    source_user_id BIGINT                             NOT NULL,
    expired_at     DATETIME                           NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
)
