CREATE TABLE user
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) DEFAULT NULL,
    birthdate    DATE         DEFAULT NULL,
    kakao_id     VARCHAR(255) DEFAULT NULL,
    phone_number VARCHAR(255) DEFAULT NULL comment 'ex) 01012345678',
    gender       VARCHAR(10)  DEFAULT 'MALE',
    sign_up_type VARCHAR(20)  DEFAULT 'NORMAL'                                      NOT NULL,
    deleted      BOOLEAN      DEFAULT FALSE                                         NOT NULL,
    deleted_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
) AUTO_INCREMENT = 10;

CREATE TABLE user_profile
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT                                                         NOT NULL,
    profile_select    JSON,
    introduction      JSON,
    image_url         VARCHAR(2048),
    blurred_image_url VARCHAR(2048),
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bottle
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_user_id       BIGINT                                                            NOT NULL,
    source_user_id       BIGINT                                                            NOT NULL,
    like_message         VARCHAR(255),
    expired_at           DATETIME                                                          NOT NULL,
    stopped_user_id      BIGINT,
    first_select_user_id BIGINT comment '최종 선택을 먼저한 user id',
    bottle_status        VARCHAR(20) DEFAULT 'RANDOM'                                      NOT NULL comment 'RANDOM - 랜덤으로 받은 상태, SENT - 호감을 표시하여 받은 상태',
    ping_pong_status     VARCHAR(20) DEFAULT 'NONE'                                        NOT NULL
        comment 'NONE - 핑퐁 시작 전, ACTIVE - 양쪽 모두 수락하여 핑퐁을 진행중인 상태, REfUSED - 핑퐁 시작 전 한쪽이 거절한 상태, STOPPED - 핑퐁 진행 중 한쪽이 중단한 상태, MATCHED - 양쪽 모두 최종 선택을 수락하여 서로 매칭이 된 상태',
    created_at           DATETIME    DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at           DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE letter
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    bottle_id             BIGINT                                                         NOT NULL,
    user_id               BIGINT                                                         NOT NULL,
    letters               JSON                                                           NOT NULL,
    is_share_image        BOOLEAN  DEFAULT FALSE comment '사진 공유 여부',
    is_share_contact      BOOLEAN  DEFAULT FALSE comment '연락처 공유 여부',
    is_read_by_other_user BOOLEAN  DEFAULT FALSE                                         NOT NULL comment '상대방이 해당 핑퐁의 변경된 내용을 읽었는지 여부',
    created_at            DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
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
    user_id     BIGINT                                                         NOT NULL,
    token       VARCHAR(2048)                                                  NOT NULL,
    expiry_date DATETIME                                                       NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE auth_sms
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(255)                                                   NOT NULL,
    auth_code    VARCHAR(255)                                                   NOT NULL,
    expired_at   DATETIME                                                       NOT NULL,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE bottle_history
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT                                                         NOT NULL,
    matched_user_id BIGINT,
    refused_user_id BIGINT,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);


CREATE TABLE black_list
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    expired_access_token VARCHAR(2048)                                                  NOT NULL,
    created_at           DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE fcm_token
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT                                                         NOT NULL,
    token      VARCHAR(255)                                                   NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP                             NOT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_phone_number ON auth_sms (phone_number);
