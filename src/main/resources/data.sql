-- カテゴリーデータ
INSERT INTO categories (name) VALUES ('ドーナツ');

-- 商品データ（8種類のドーナツ）
-- ※ category_id は 1 を指定（自動採番された最初のID）
INSERT INTO items (category_id, name, description, price, stock_quantity, is_recommended, image_url) VALUES 
(1, 'ストロベリー', '甘酸っぱいいちごチョコがかかった人気ドーナツ', 240, 50, 1, '/images/strawberry_glazed_donut.png'),
(1, 'チョコレート', '濃厚なチョコレートをコーティングしました', 220, 50, 1, '/images/chocolate_glazed_donut.png'),
(1, 'オールドファッション', 'サクサク食感が楽しめる定番のドーナツ', 200, 50, 1, '/images/old_fashioned_donut.png'),
(1, '抹茶', '香り高い抹茶チョコをたっぷりかけた一品', 250, 50, 1, '/images/matcha_glazed_donut.png'),
(1, 'カスタード', 'なめらかなカスタードクリームがたっぷり', 260, 50, 1, '/images/caramel_custard_donut.png'),
(1, 'シュガー', 'シンプルな甘さがクセになる味わい', 210, 50, 1, '/images/sugar_glazed_bomboloni.png'),
(1, 'プレーン', '素材の味をそのまま楽しめるシンプルなドーナツ', 200, 50, 1, '/images/honey_glazed_donut.png'),
(1, 'キャラメル', 'ほろ苦いキャラメルソースが絶妙なドーナツ', 240, 50, 1, '/images/caramel_sugar_donut.png');