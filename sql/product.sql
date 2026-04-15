SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_code` varchar(64) NOT NULL,
  `category_code` varchar(32) NOT NULL,
  `point` int NOT NULL,
  `stock` int NOT NULL DEFAULT 0,
  `status` tinyint NOT NULL DEFAULT 1,
  `sort_order` int NOT NULL DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`product_code`),
  KEY `idx_product_category` (`category_code`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `product` (`product_code`, `category_code`, `point`, `stock`, `status`, `sort_order`) VALUES
('DINING_MEAL_COUPON', 'DINING', 180, 80, 1, 10),
('DINING_DRINK_COUPON', 'DINING', 120, 120, 1, 20),
('STATIONERY_PEN', 'STATIONERY', 260, 50, 1, 30),
('STATIONERY_PENCIL', 'STATIONERY', 320, 36, 1, 40),
('CERT_GREEN_PIONEER', 'CERT', 420, 999, 1, 50),
('CERT_LOW_CARBON_STAR', 'CERT', 560, 999, 1, 60);

SET FOREIGN_KEY_CHECKS = 1;
