-- =================================================================
-- 1. users (ユーザー管理)
-- =================================================================
CREATE TABLE users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    name          VARCHAR(100) NOT NULL,
    birthday      DATE,
    tel_no        VARCHAR(20),
    post_code     VARCHAR(10),
    prefecture    VARCHAR(50),
    address       VARCHAR(255),
    building      VARCHAR(255),
    role          VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME,
    withdrawn_at  DATETIME,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 2. categories (商品カテゴリー)
-- =================================================================
CREATE TABLE categories (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 3. allergens (アレルギー品目マスター)
-- =================================================================
CREATE TABLE allergens (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 4. items (商品)
-- =================================================================
CREATE TABLE items (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id    BIGINT,
    name           VARCHAR(100) NOT NULL,
    description    TEXT,
    price          INT NOT NULL,
    tax_rate       INT NOT NULL DEFAULT 8,
    stock_quantity INT NOT NULL DEFAULT 0,
    is_recommended TINYINT(1) NOT NULL DEFAULT 0,
    is_active      TINYINT(1) NOT NULL DEFAULT 1,
    image_url      VARCHAR(255),
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_category FOREIGN KEY (category_id) 
        REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 5. item_allergens (商品×アレルギー中間テーブル)
-- =================================================================
CREATE TABLE item_allergens (
    item_id     BIGINT NOT NULL,
    allergen_id BIGINT NOT NULL,
    PRIMARY KEY (item_id, allergen_id),
    CONSTRAINT fk_item_allergens_item FOREIGN KEY (item_id) 
        REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_allergens_allergen FOREIGN KEY (allergen_id) 
        REFERENCES allergens(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 6. cart_items (カート情報)
-- =================================================================
CREATE TABLE cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    item_id    BIGINT NOT NULL,
    quantity   INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_item UNIQUE (user_id, item_id),
    CONSTRAINT fk_cart_items_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_item FOREIGN KEY (item_id) 
        REFERENCES items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 7. orders (注文管理)
-- =================================================================
CREATE TABLE orders (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    order_number        VARCHAR(50) NOT NULL UNIQUE,
    total_amount        INT NOT NULL,
    shipping_fee        INT NOT NULL DEFAULT 0,
    payment_method      VARCHAR(30) NOT NULL,
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    shipping_name       VARCHAR(100) NOT NULL,
    shipping_post_code  VARCHAR(10) NOT NULL,
    shipping_prefecture VARCHAR(50) NOT NULL,
    shipping_address    VARCHAR(255) NOT NULL,
    shipping_building   VARCHAR(255),
    shipping_tel_no     VARCHAR(20) NOT NULL,
    ordered_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 8. order_items (注文明細)
-- =================================================================
CREATE TABLE order_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    item_id    BIGINT,
    item_name  VARCHAR(100) NOT NULL,
    price      INT NOT NULL,
    tax_rate   INT NOT NULL DEFAULT 8,
    quantity   INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id)
        REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_item FOREIGN KEY (item_id)
        REFERENCES items(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 9. shipping_addresses (お届け先住所録)
-- =================================================================
CREATE TABLE shipping_addresses (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    post_code      VARCHAR(10) NOT NULL,
    prefecture     VARCHAR(50) NOT NULL,
    address        VARCHAR(255) NOT NULL,
    building       VARCHAR(255),
    tel_no         VARCHAR(20) NOT NULL,
    is_default     TINYINT(1) NOT NULL DEFAULT 0,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_shipping_addresses_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- =================================================================
-- 10. payments (決済情報)
-- =================================================================
CREATE TABLE payments (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT NOT NULL UNIQUE,
    payment_method VARCHAR(30) NOT NULL,
    amount         INT NOT NULL,
    status         VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(255),
    paid_at        DATETIME,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id)
        REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;