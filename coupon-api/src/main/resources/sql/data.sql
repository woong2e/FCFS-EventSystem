-- 1. 기존 데이터 초기화 (ID를 1번부터 다시 시작하기 위해 TRUNCATE 사용)
SET FOREIGN_KEY_CHECKS = 0; -- 혹시 모를 제약조건 잠시 해제
TRUNCATE TABLE issued_coupons;
TRUNCATE TABLE coupons;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 쿠폰 생성 (선착순 100명)
INSERT INTO coupons (title, total_quantity, issued_quantity)
VALUES ('선착순 100명 치킨', 100, 0);

-- 3. 유저 10,000명 생성 (Recursive CTE 활용)
-- MySQL 8.0의 재귀 제한(기본 1000)을 늘려줍니다.
SET SESSION cte_max_recursion_depth = 1000000;

INSERT INTO users (name)
WITH RECURSIVE sequence AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM sequence WHERE n < 10000
)
SELECT CONCAT('User_', n) FROM sequence;