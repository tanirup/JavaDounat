-- カテゴリーデータ
INSERT INTO categories (name) VALUES ('ドーナツ');

-- 商品データ（8種類のドーナツ）
-- ※ category_id は 1 を指定（自動採番された最初のID）
INSERT INTO items (category_id, name, description, price, stock_quantity, is_recommended, image_url) VALUES 
(1, 'ストロベリー', '甘酸っぱいいちごチョコがかかった人気ドーナツ', 240, 50, 1, 'https://placehold.co/400x300/FCE4EC/9B5268?text=Strawberry'),
(1, 'チョコレート', '濃厚なチョコレートをコーティングしました', 220, 50, 1, 'https://placehold.co/400x300/F5DFC0/795438?text=Chocolate'),
(1, 'オールドファッション', 'サクサク食感が楽しめる定番のドーナツ', 200, 50, 1, 'https://placehold.co/400x300/EFEBE9/5D4037?text=Old+Fashioned'),
(1, '抹茶', '香り高い抹茶チョコをたっぷりかけた一品', 250, 50, 1, 'https://placehold.co/400x300/E8F5E9/2E7D32?text=Matcha'),
(1, 'カスタード', 'なめらかなカスタードクリームがたっぷり', 260, 50, 1, 'https://placehold.co/400x300/FFF8E1/F57F17?text=Custard'),
(1, 'シュガー', 'シンプルな甘さがクセになる味わい', 210, 50, 1, 'https://placehold.co/400x300/FAFAFA/616161?text=Sugar'),
(1, 'プレーン', '素材の味をそのまま楽しめるシンプルなドーナツ', 200, 50, 1, 'https://placehold.co/400x300/FFF3E0/E65100?text=Plain'),
(1, 'キャラメル', 'ほろ苦いキャラメルソースが絶妙なドーナツ', 240, 50, 1, 'https://placehold.co/400x300/EFEBE9/4E342E?text=Caramel');


INSERT INTO admins (username, password) VALUES ('admin', 'donut123');