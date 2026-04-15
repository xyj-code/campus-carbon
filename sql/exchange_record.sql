SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `exchange_record`;
CREATE TABLE `exchange_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `product_id` bigint NOT NULL,
  `product_code` varchar(64) NOT NULL,
  `category_code` varchar(32) NOT NULL,
  `point` int NOT NULL,
  `status` varchar(32) NOT NULL DEFAULT 'PENDING_CLAIM',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_exchange_username` (`username`, `create_time`),
  KEY `idx_exchange_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;
