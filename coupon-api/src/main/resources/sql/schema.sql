DROP TABLE IF EXISTS issued_coupons;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS users;

-- 1. 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 쿠폰 정책 테이블
CREATE TABLE coupons (
    id BINARY(16) PRIMARY KEY COMMENT 'ULID (UUID)',
    title VARCHAR(100) NOT NULL,
    total_quantity INT NOT NULL,
    issued_quantity INT NOT NULL DEFAULT 0,
    created_at DATETIME(6),
    updated_at DATETIME(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 발급 이력
CREATE TABLE issued_coupons (
    id BINARY(16) PRIMARY KEY COMMENT 'ULID',
    coupon_id BINARY(16) NOT NULL COMMENT 'UUID',
    user_id BIGINT NOT NULL COMMENT 'Long (숫자)',
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',
    issued_at DATETIME(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- CREATE UNIQUE KEY uk_coupon_user (coupon_id, user_id);