-- Categories
INSERT INTO categories (id, name, english_name, created_at, updated_at) VALUES
(1, '카페/디저트', 'cafe_dessert', NOW(), NOW()),
(2, '패스트푸드', 'fast_food', NOW(), NOW()),
(3, '아시안', 'asian', NOW(), NOW()),
(4, '한식', 'korean', NOW(), NOW()),
(5, '일식', 'japanese', NOW(), NOW()),
(6, '분식', 'bunsik', NOW(), NOW()),
(7, '치킨', 'chicken', NOW(), NOW()),
(8, '중식', 'chinese', NOW(), NOW()),
(9, '찜·탕·전골', 'steamed_soup_hotpot', NOW(), NOW());

-- SubCategories for 카페/디저트 (Category ID: 1)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(1, '커피', NOW(), NOW()),
(1, '음료', NOW(), NOW()),
(1, '차', NOW(), NOW()),
(1, '케이크', NOW(), NOW()),
(1, '베이커리', NOW(), NOW()),
(1, '아이스크림', NOW(), NOW());

-- SubCategories for 패스트푸드 (Category ID: 2)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(2, '햄버거', NOW(), NOW()),
(2, '피자', NOW(), NOW()),
(2, '샌드위치', NOW(), NOW()),
(2, '토스트', NOW(), NOW());

-- SubCategories for 아시안 (Category ID: 3)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(3, '베트남 음식', NOW(), NOW()),
(3, '태국 음식', NOW(), NOW()),
(3, '인도 음식', NOW(), NOW());

-- SubCategories for 한식 (Category ID: 4)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(4, '백반', NOW(), NOW()),
(4, '죽', NOW(), NOW()),
(4, '국수', NOW(), NOW()),
(4, '고기', NOW(), NOW()),
(4, '구이', NOW(), NOW()),
(4, '찌개', NOW(), NOW()),
(4, '국밥', NOW(), NOW()),
(4, '족발', NOW(), NOW());

-- SubCategories for 일식 (Category ID: 5)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(5, '돈까스', NOW(), NOW()),
(5, '회', NOW(), NOW()),
(5, '초밥', NOW(), NOW()),
(5, '우동', NOW(), NOW()),
(5, '소바', NOW(), NOW());

-- SubCategories for 분식 (Category ID: 6)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(6, '떡볶이', NOW(), NOW()),
(6, '김밥', NOW(), NOW()),
(6, '라면', NOW(), NOW()),
(6, '튀김', NOW(), NOW());

-- SubCategories for 치킨 (Category ID: 7)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(7, '후라이드', NOW(), NOW()),
(7, '양념치킨', NOW(), NOW()),
(7, '오븐구이', NOW(), NOW());

-- SubCategories for 중식 (Category ID: 8)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(8, '짜장면', NOW(), NOW()),
(8, '짬뽕', NOW(), NOW()),
(8, '탕수육', NOW(), NOW()),
(8, '마라탕', NOW(), NOW());

-- SubCategories for 찜·탕·전골 (Category ID: 9)
INSERT INTO sub_categories (category_id, name, created_at, updated_at) VALUES
(9, '찜', NOW(), NOW()),
(9, '탕', NOW(), NOW()),
(9, '전골', NOW(), NOW()); 