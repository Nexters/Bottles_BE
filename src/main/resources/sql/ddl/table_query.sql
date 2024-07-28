CREATE TABLE user
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) DEFAULT NULL,
    birthdate    DATE         DEFAULT NULL,
    kakao_id     VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL,
    gender       VARCHAR(10)  DEFAULT 'MALE',
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
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
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id   BIGINT                                NOT NULL,
    source_user_id   BIGINT                                NOT NULL,
    expired_at       DATETIME                              NOT NULL,
    stopped_user_id  BIGINT,
    ping_pong_status VARCHAR(20) DEFAULT 'NONE'            NOT NULL,
    created_at       DATETIME    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at       DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE letter
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    bottle_id             BIGINT                             NOT NULL,
    user_id               BIGINT                             NOT NULL,
    letters               JSON                               NOT NULL,
    image_url             TEXT,
    is_read_by_other_user BOOLEAN  DEFAULT FALSE             NOT NULL,
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE question
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL
);

CREATE TABLE bottle_history
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT                             NOT NULL,
    matched_user_id BIGINT,
    refused_user_id BIGINT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);
