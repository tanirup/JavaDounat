-- カテゴリーデータ
INSERT INTO categories (id, name) VALUES (1, '野菜・果物'), (2, '肉・肉加工品');

-- アレルギーデータ
INSERT INTO allergens (id, name) VALUES (1, '卵'), (2, '乳'), (3, '小麦');

-- ユーザーデータ (初期管理者・一般ユーザー)
INSERT INTO users (email, password, name, role) 
VALUES ('admin@example.com', 'password', '管理者', 'ROLE_ADMIN'),
       ('user@example.com', 'password', 'テストユーザー', 'ROLE_USER');

-- 商品データ
INSERT INTO items (category_id, name, description, price, stock_quantity, is_recommended)
VALUES (1, '新鮮トマト', '甘くて美味しい甘熟トマトです', 200, 50, 1),
       (2, '国産豚肉ロース', 'しゃぶしゃぶ用の豚肉です', 800, 20, 0);