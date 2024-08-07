INSERT INTO question (question) VALUES
('다른 사람에게서 들은 나의 매력은 무엇인가요?'),
('적당하다고 생각하는 썸의 기간이 있나요?'),
('선호하는 성격이 있나요?'),
('선호하는 상대의 옷 스타일이 있나요?'),
('선호하는 연락 빈도가 있나요?'),
('기념일을 어떻게 챙기는 편인가요?'),
('챙겨주는 스타일 vs 챙김을 당하는 스타일,\n어느 쪽인가요?'),
('집 vs 밖, 어떤 데이트를 선호하나요?'),
('나를 한 가지 단어로 표현하고\n그 이유를 이야기해 주세요.'),
('스트레스를 푸는 나만의 방식이 있나요?'),
('다가가고 싶은 상대가 생기면\n어떻게 하는 편인가요?'),
('다툼 후 시간이 필요한 스타일\nvs 바로 풀어야 하는 스타일, 어느 쪽인가요?'),
('내가 생각하는 나의 장점을\n3가지 뽑아본다면?'),
('연애할 때 중요하게 생각하는 가치를\n한 가지 뽑아본다면 무엇인가요?'),
('여사친(남사친)과 어느 정도 사이까지\n괜찮다고 생각하나요?'),
('이것만은 터치 안 했으면 좋겠다\n싶은 게 있나요?'),
('스트레스를 푸는 나만의 방식이 있나요?'),
('연락 빈도는 어느 정도가\n적당하다 생각하나요?'),
('연애할 때 서로의 핸드폰\n비밀번호를 공유할 수 있나요?'),
('연애할 때 티를 내는 스타일\nvs 조용히 사귀는 스타일, 어느 쪽인가요?'),
('다가가고 싶은 상대가 생기면\n어떻게 하는 편인가요?'),
('나를 한 가지 단어로 표현하고\n그 이유를 이야기해 주세요.'),
('나를 좋아하는 사람\nvs 내가 좋아하는 사람, 이유는 무엇인가요?');

-- 새로운 데이터 삽입 쿼리
INSERT INTO user (
    id,
    name,
    birthdate,
    kakao_id,
    phone_number,
    gender,
    sign_up_type,
    deleted,
    deleted_at,
    created_at,
    updated_at
)
VALUES
(
    1,               -- id
    'DIO',           -- name
    '1999-03-01',    -- birthdate
    'chaenu123',     -- kakao_id
    '01012345678',   -- phone_number
    'MALE',          -- gender
    'KAKAO',         -- sign_up_type
    0,               -- deleted
    NULL,            -- deleted_at
    '2024-08-05 22:44:19', -- created_at
    '2024-08-05 22:59:28'  -- updated_at
),
(
    2,               -- id
    'carina',        -- name
    '1999-03-01',    -- birthdate
    'carina123',     -- kakao_id
    '01011112222',   -- phone_number
    'FEMALE',        -- gender
    'KAKAO',         -- sign_up_type
    0,               -- deleted
    NULL,            -- deleted_at
    '2024-08-05 22:44:19', -- created_at
    '2024-08-05 22:59:37'  -- updated_at
);


INSERT INTO user_profile (
    id,
    user_id,
    profile_select,
    introduction,
    image_url,
    blurred_image_url,
    created_at,
    updated_at
)
VALUES
(
    1, -- id
    1, -- user_id
    '{"job": "직장인", "mbti": "intj", "height": 175, "region": {"city": "서울특별시", "state": "강남구"}, "alcohol": "때에 따라 적당히 즐겨요", "keyword": ["다정한", "적극적인", "신중한"], "smoking": "가끔 피워요", "interest": {"etc": [], "sports": ["헬스", "러닝"], "culture": ["전시회 방문", "공연 관람"], "entertainment": ["독서"]}, "religion": "무교"}',
    '[{"answer": "호기심 많고 새로운 경험을 즐깁니다.\n주말엔 책을 읽거나 맛집을 찾아 다니며 여유를 즐기고, 친구들과 소소한 모임으로 충전해요. 일상에서 소소한 행복을 찾아요.", "question": "보틀에 담을 소개를 작성해 주세요"}]',
    'https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1',
    'https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/blurred_%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1',
    '2024-08-05 22:51:22',
    '2024-08-05 22:51:22'
),
(
    2, -- id
    2, -- user_id
    '{"job": "직장인", "mbti": "intj", "height": 163, "region": {"city": "서울특별시", "state": "강남구"}, "alcohol": "때에 따라 적당히 즐겨요", "keyword": ["다정한", "적극적인", "신중한"], "smoking": "가끔 피워요", "interest": {"etc": [], "sports": ["헬스", "러닝"], "culture": ["전시회 방문", "공연 관람"], "entertainment": ["독서"]}, "religion": "무교"}',
    '[{"answer": "호기심 많고 새로운 경험을 즐깁니다.\n주말엔 책을 읽거나 맛집을 찾아 다니며 여유를 즐기고, 친구들과 소소한 모임으로 충전해요. 일상에서 소소한 행복을 찾아요.", "question": "보틀에 담을 소개를 작성해 주세요"}]',
    'https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1',
    'https://bottles-bucket.s3.ap-northeast-2.amazonaws.com/blurred_%E1%84%80%E1%85%A9%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8B%E1%85%B5%E1%86%BC.jpeg_20240730233759_1',
    '2024-08-05 22:51:22',
    '2024-08-05 23:01:17'
);
