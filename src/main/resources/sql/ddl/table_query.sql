CREATE TABLE user
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) DEFAULT NULL,
    birthdate    DATE         DEFAULT NULL,
    kakao_id     VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL comment 'ex) 01012345678',
    gender       VARCHAR(10)  DEFAULT 'MALE',
    sign_up_type VARCHAR(20)  DEFAULT 'NORMAL'          NOT NULL,
    deleted      BOOLEAN      DEFAULT FALSE             NOT NULL,
    deleted_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE user_profile
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT                             NOT NULL,
    profile_select    JSON,
    introduction      JSON,
    image_url         VARCHAR(2048),
    blurred_image_url VARCHAR(2048),
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bottle
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id     BIGINT                                NOT NULL,
    target_user_select BOOLEAN     DEFAULT FALSE             NOT NULL,
    source_user_id     BIGINT                                NOT NULL,
    source_user_select BOOLEAN     DEFAULT FALSE             NOT NULL,
    like_message       VARCHAR(255),
    expired_at         DATETIME                              NOT NULL,
    stopped_user_id    BIGINT,
    bottles_status     VARCHAR(20) DEFAULT 'RANDOM'          NOT NULL,
    ping_pong_status   VARCHAR(20) DEFAULT 'NONE'            NOT NULL,
    created_at         DATETIME    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at         DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE letter
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    bottle_id             BIGINT                             NOT NULL,
    user_id               BIGINT                             NOT NULL,
    letters               JSON                               NOT NULL,
    is_show_image         BOOLEAN  DEFAULT FALSE             NOT NULL,
    is_read_by_other_user BOOLEAN  DEFAULT FALSE             NOT NULL,
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE question
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL
);

CREATE TABLE refresh_tokens
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT                             NOT NULL,
    token       VARCHAR(2048)                      NOT NULL,
    expiry_date DATETIME                           NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE auth_sms
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(255)                       NOT NULL,
    auth_code    VARCHAR(255)                       NOT NULL,
    expired_at   DATETIME                           NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_phone_number ON auth_sms (phone_number);
