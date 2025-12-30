-- Pet Shop Database Setup Script for Ubuntu EC2
-- Compatible with MySQL 8.0+

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- Create database
CREATE DATABASE IF NOT EXISTS `pet_shop` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;

USE `pet_shop`;

-- =============================================
-- CORE TABLES (no dependencies)
-- =============================================

-- Role table
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
                        `name` varchar(255) NOT NULL,
                        `description` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Permission table
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
                              `name` varchar(255) NOT NULL,
                              `description` varchar(255) DEFAULT NULL,
                              PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Role permissions junction table
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
                                    `role_name` varchar(255) NOT NULL,
                                    `permissions_name` varchar(255) NOT NULL,
                                    PRIMARY KEY (`role_name`,`permissions_name`),
                                    KEY `FKf5aljih4mxtdgalvr7xvngfn1` (`permissions_name`),
                                    CONSTRAINT `FKcppvu8fk24eqqn6q4hws7ajux` FOREIGN KEY (`role_name`) REFERENCES `role` (`name`),
                                    CONSTRAINT `FKf5aljih4mxtdgalvr7xvngfn1` FOREIGN KEY (`permissions_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Users table
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(100) NOT NULL,
                         `username` varchar(100) NOT NULL,
                         `full_name` varchar(100) NOT NULL,
                         `phone` varchar(100) NOT NULL,
                         `role_name` varchar(255) DEFAULT NULL,
                         `is_deleted` varchar(1) DEFAULT NULL,
                         `created_date` datetime(6) DEFAULT NULL,
                         `updated_date` datetime(6) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_users_email` (`email`),
                         UNIQUE KEY `uk_users_username` (`username`),
                         KEY `idx_users_role` (`role_name`),
                         KEY `idx_users_deleted` (`is_deleted`),
                         CONSTRAINT `fk_users_role` FOREIGN KEY (`role_name`) REFERENCES `role` (`name`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Categories table
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `name` varchar(255) NOT NULL,
                              `description` varchar(255) DEFAULT NULL,
                              `is_featured` varchar(1) DEFAULT NULL,
                              `is_deleted` varchar(1) DEFAULT NULL,
                              `created_date` datetime(6) DEFAULT NULL,
                              `updated_date` datetime(6) DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `idx_categories_featured` (`is_featured`),
                              KEY `idx_categories_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Services table
DROP TABLE IF EXISTS `services`;
CREATE TABLE `services` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `create_date` datetime(6) DEFAULT NULL,
                            `description` text NOT NULL,
                            `duration_minutes` int NOT NULL,
                            `is_active` varchar(1) DEFAULT NULL,
                            `name` text NOT NULL,
                            `price` decimal(12,2) DEFAULT NULL,
                            `title` text NOT NULL,
                            `update_date` datetime(6) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Pets table
DROP TABLE IF EXISTS `pets`;
CREATE TABLE `pets` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `name` varchar(100) NOT NULL,
                        `animal` varchar(100) NOT NULL,
                        `breed` varchar(100) DEFAULT NULL,
                        `age` int NOT NULL,
                        `age_group` varchar(100) DEFAULT NULL,
                        `size` varchar(100) DEFAULT NULL,
                        `gender` varchar(100) DEFAULT NULL,
                        `description` text,
                        `health_status` varchar(100) DEFAULT NULL,
                        `vaccinated` varchar(1) DEFAULT '0',
                        `neutered` varchar(1) DEFAULT '0',
                        `created_date` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
                        `updated_date` datetime(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
                        `is_deleted` varchar(1) DEFAULT NULL,
                        `status` varchar(45) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =============================================
-- DEPENDENT TABLES (Level 1)
-- =============================================

-- Addresses table
DROP TABLE IF EXISTS `addresses`;
CREATE TABLE `addresses` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `user_id` int NOT NULL,
                             `contact_name` varchar(255) DEFAULT NULL,
                             `phone` varchar(255) NOT NULL,
                             `detail_address` varchar(255) NOT NULL,
                             `ward` varchar(255) NOT NULL,
                             `city` varchar(255) NOT NULL,
                             `state` varchar(255) NOT NULL,
                             `is_default` varchar(1) DEFAULT NULL,
                             `is_deleted` varchar(1) DEFAULT NULL,
                             `created_date` datetime(6) DEFAULT NULL,
                             `updated_date` datetime(6) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `idx_addresses_user` (`user_id`),
                             KEY `idx_addresses_deleted` (`is_deleted`),
                             CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Cart table
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
                        `id` int NOT NULL AUTO_INCREMENT,
                        `user_id` int NOT NULL,
                        `created_date` datetime(6) DEFAULT NULL,
                        `updated_date` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_cart_user` (`user_id`),
                        KEY `idx_cart_user` (`user_id`),
                        CONSTRAINT `FKg5uhi8vpsuy0lgloxk2h4w5o6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Products table
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `category_id` int DEFAULT NULL,
                            `name` varchar(255) COLLATE utf8mb4_vietnamese_ci NOT NULL,
                            `short_description` text COLLATE utf8mb4_vietnamese_ci,
                            `description` text COLLATE utf8mb4_vietnamese_ci,
                            `sold_quantity` int NOT NULL DEFAULT '0',
                            `stock_quantity` int NOT NULL DEFAULT '0',
                            `is_featured` varchar(1) COLLATE utf8mb4_vietnamese_ci DEFAULT NULL,
                            `is_deleted` varchar(1) COLLATE utf8mb4_vietnamese_ci DEFAULT NULL,
                            `created_date` datetime(6) DEFAULT NULL,
                            `updated_date` datetime(6) DEFAULT NULL,
                            `animal` varchar(255) COLLATE utf8mb4_vietnamese_ci DEFAULT NULL,
                            `brand` varchar(255) COLLATE utf8mb4_vietnamese_ci DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `idx_products_category` (`category_id`),
                            KEY `idx_products_featured` (`is_featured`),
                            KEY `idx_products_deleted` (`is_deleted`),
                            CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

-- Orders table
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `user_id` int NOT NULL,
                          `order_code` varchar(255) NOT NULL,
                          `status` varchar(255) NOT NULL,
                          `total_amount` decimal(38,2) NOT NULL,
                          `shipping_amount` decimal(38,2) NOT NULL,
                          `discount_amount` float DEFAULT NULL,
                          `shipping_address` varchar(500) NOT NULL,
                          `note` varchar(500) DEFAULT NULL,
                          `is_deleted` varchar(1) DEFAULT NULL,
                          `created_date` datetime(6) DEFAULT NULL,
                          `updated_date` datetime(6) DEFAULT NULL,
                          `payment_method` varchar(100) DEFAULT NULL,
                          `payment_expired_at` datetime(6) DEFAULT NULL,
                          `final_amount` decimal(15,2) GENERATED ALWAYS AS ((`total_amount` - coalesce(`discount_amount`,0))) STORED,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_orders_code` (`order_code`),
                          KEY `idx_orders_user` (`user_id`),
                          CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Booking time table
DROP TABLE IF EXISTS `booking_time`;
CREATE TABLE `booking_time` (
                                `id` int NOT NULL AUTO_INCREMENT,
                                `service_id` int NOT NULL,
                                `slot_date` date NOT NULL,
                                `start_time` time NOT NULL,
                                `end_time` time NOT NULL,
                                `max_capacity` int NOT NULL,
                                `booked_count` int NOT NULL DEFAULT '0',
                                `available_count` int NOT NULL,
                                `is_active` char(1) DEFAULT '1',
                                `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                `is_deleted` tinyint(1) DEFAULT '0',
                                PRIMARY KEY (`id`),
                                KEY `idx_booking_service_date` (`service_id`,`slot_date`),
                                KEY `idx_booking_slot_date` (`slot_date`),
                                CONSTRAINT `fk_booking_time_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Pet images table
DROP TABLE IF EXISTS `pet_images`;
CREATE TABLE `pet_images` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `pet_id` int NOT NULL,
                              `public_id` varchar(255) NOT NULL,
                              `image_url` varchar(500) NOT NULL,
                              `image_position` int DEFAULT NULL,
                              `is_primary` int DEFAULT '0',
                              `is_deleted` varchar(1) DEFAULT '0',
                              `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
                              `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`),
                              KEY `fk_pet_images_pet` (`pet_id`),
                              CONSTRAINT `fk_pet_images_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Product images table
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images` (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `product_id` int NOT NULL,
                                  `image_url` varchar(255) NOT NULL,
                                  `public_id` varchar(255) DEFAULT NULL,
                                  `position` int DEFAULT '0',
                                  `is_primary` int DEFAULT NULL,
                                  `is_deleted` varchar(1) DEFAULT NULL,
                                  `created_date` datetime(6) DEFAULT NULL,
                                  `updated_date` datetime(6) DEFAULT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `idx_images_product` (`product_id`),
                                  CONSTRAINT `fk_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =============================================
-- DEPENDENT TABLES (Level 2)
-- =============================================

-- Adopt table
DROP TABLE IF EXISTS `adopt`;
CREATE TABLE `adopt` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `user_id` int NOT NULL,
                         `pet_id` int NOT NULL,
                         `address_id` int DEFAULT NULL,
                         `status` varchar(50) NOT NULL,
                         `note` text,
                         `is_deleted` varchar(1) DEFAULT '0',
                         `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
                         `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         `job` varchar(45) DEFAULT NULL,
                         `income` varchar(45) DEFAULT NULL,
                         `is_own_pet` varchar(1) DEFAULT NULL,
                         `live_condition` varchar(255) DEFAULT NULL,
                         `code` varchar(45) DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `fk_adopt_user` (`user_id`),
                         KEY `fk_adopt_pet` (`pet_id`),
                         KEY `fk_adopt_address` (`address_id`),
                         CONSTRAINT `fk_adopt_address` FOREIGN KEY (`address_id`) REFERENCES `addresses` (`id`),
                         CONSTRAINT `fk_adopt_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`),
                         CONSTRAINT `fk_adopt_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Service appointments table
DROP TABLE IF EXISTS `service_appointments`;
CREATE TABLE `service_appointments` (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `appointment_end` datetime(6) NOT NULL,
                                        `appointment_start` datetime(6) NOT NULL,
                                        `created_date` datetime(6) DEFAULT NULL,
                                        `name_pet` varchar(255) NOT NULL,
                                        `notes` text,
                                        `specie_pet` varchar(255) NOT NULL,
                                        `status` enum('CANCELED','COMPLETED','NO_SHOW','SCHEDULED') NOT NULL,
                                        `updated_date` datetime(6) DEFAULT NULL,
                                        `service_id` int DEFAULT NULL,
                                        `user_id` int DEFAULT NULL,
                                        `booking_time_id` int NOT NULL,
                                        PRIMARY KEY (`id`),
                                        KEY `FK3sapkf6fwbsgff0b69dk9pd70` (`service_id`),
                                        KEY `FKt72jjpl5qj108goru7rn85ytu` (`user_id`),
                                        CONSTRAINT `FK3sapkf6fwbsgff0b69dk9pd70` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
                                        CONSTRAINT `FKt72jjpl5qj108goru7rn85ytu` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Payments table
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `order_id` int NOT NULL,
                            `provider` tinytext NOT NULL,
                            `amount` decimal(18,2) NOT NULL,
                            `currency` varchar(10) DEFAULT 'VND',
                            `status` tinytext NOT NULL,
                            `response_code` varchar(10) DEFAULT NULL,
                            `transaction_no` varchar(50) DEFAULT NULL,
                            `bank_code` varchar(20) DEFAULT NULL,
                            `provider_ref` varchar(100) DEFAULT NULL,
                            `paid_at` datetime DEFAULT NULL,
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            KEY `fk_payment_order` (`order_id`),
                            CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Product variants table
DROP TABLE IF EXISTS `product_variants`;
CREATE TABLE `product_variants` (
                                    `id` int NOT NULL AUTO_INCREMENT,
                                    `product_id` int NOT NULL,
                                    `variant_name` varchar(255) NOT NULL,
                                    `weight` float DEFAULT NULL,
                                    `price` float DEFAULT NULL,
                                    `stock_quantity` int NOT NULL DEFAULT '0',
                                    `sold_quantity` int NOT NULL DEFAULT '0',
                                    `product_image_id` int DEFAULT NULL,
                                    `is_deleted` varchar(1) DEFAULT NULL,
                                    `created_date` datetime(6) DEFAULT NULL,
                                    `updated_date` datetime(6) DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    KEY `idx_variants_product` (`product_id`),
                                    KEY `fk_variants_thumbnail` (`product_image_id`),
                                    CONSTRAINT `fk_variants_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
                                    CONSTRAINT `fk_variants_thumbnail` FOREIGN KEY (`product_image_id`) REFERENCES `product_images` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Order items table
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `order_id` int NOT NULL,
                               `product_variant_id` int NOT NULL,
                               `quantity` int NOT NULL,
                               `unit_price` decimal(38,2) NOT NULL,
                               `total_price` decimal(38,2) NOT NULL,
                               `is_deleted` varchar(1) DEFAULT NULL,
                               `created_date` datetime(6) DEFAULT NULL,
                               `updated_date` datetime(6) DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `fk_order_items_order` (`order_id`),
                               KEY `fk_order_items_variant` (`product_variant_id`),
                               CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
                               CONSTRAINT `fk_order_items_variant` FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Reviews table
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `comment` varchar(255) DEFAULT NULL,
                           `created_date` datetime(6) DEFAULT NULL,
                           `is_deleted` varchar(1) DEFAULT NULL,
                           `is_verified` smallint DEFAULT NULL,
                           `rating` smallint NOT NULL,
                           `updated_date` datetime(6) DEFAULT NULL,
                           `product_id` int DEFAULT NULL,
                           `user_id` int DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `FKpl51cejpw4gy5swfar8br9ngi` (`product_id`),
                           KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
                           CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
                           CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =============================================
-- DEPENDENT TABLES (Level 3)
-- =============================================

-- Cart items table
DROP TABLE IF EXISTS `cart_items`;
CREATE TABLE `cart_items` (
                              `id` int NOT NULL AUTO_INCREMENT,
                              `created_date` datetime(6) DEFAULT NULL,
                              `is_deleted` varchar(1) DEFAULT NULL,
                              `quantity` int NOT NULL,
                              `updated_date` datetime(6) DEFAULT NULL,
                              `cart_id` int DEFAULT NULL,
                              `product_variant_id` int DEFAULT NULL,
                              PRIMARY KEY (`id`),
                              KEY `FK99e0am9jpriwxcm6is7xfedy3` (`cart_id`),
                              KEY `FKn1s4l7h0vm4o259wpu7ft0y2y` (`product_variant_id`),
                              CONSTRAINT `FK99e0am9jpriwxcm6is7xfedy3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`),
                              CONSTRAINT `FKn1s4l7h0vm4o259wpu7ft0y2y` FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Product variant image junction table
DROP TABLE IF EXISTS `product_variant_image`;
CREATE TABLE `product_variant_image` (
                                         `id` int NOT NULL AUTO_INCREMENT,
                                         `variant_id` int NOT NULL,
                                         `image_id` int NOT NULL,
                                         `sort_order` int DEFAULT '0',
                                         `created_date` datetime(6) DEFAULT NULL,
                                         `updated_date` datetime(6) DEFAULT NULL,
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_variant_image` (`variant_id`,`image_id`),
                                         KEY `idx_variant_image_variant` (`variant_id`),
                                         KEY `fk_pvi_image` (`image_id`),
                                         CONSTRAINT `fk_pvi_image` FOREIGN KEY (`image_id`) REFERENCES `product_images` (`id`),
                                         CONSTRAINT `fk_pvi_variant` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =============================================
-- UTILITY TABLES
-- =============================================

-- Email OTP table
DROP TABLE IF EXISTS `email_otp`;
CREATE TABLE `email_otp` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `email` varchar(255) NOT NULL,
                             `otp` varchar(6) NOT NULL,
                             `expire_time` datetime NOT NULL,
                             `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Invalidated token table
DROP TABLE IF EXISTS `invalidated_token`;
CREATE TABLE `invalidated_token` (
                                     `id` varchar(255) NOT NULL,
                                     `expiry_time` datetime(6) DEFAULT NULL,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- =============================================
-- TRIGGERS
-- =============================================

DELIMITER $$

DROP TRIGGER IF EXISTS `trg_after_insert_variant`$$
CREATE TRIGGER `trg_after_insert_variant`
    AFTER INSERT ON `product_variants`
    FOR EACH ROW
BEGIN
    UPDATE products
    SET stock_quantity = (
        SELECT COALESCE(SUM(v.stock_quantity), 0)
        FROM product_variants v
        WHERE v.product_id = NEW.product_id AND v.is_deleted = '0'
    ),
        sold_quantity = (
            SELECT COALESCE(SUM(v.sold_quantity), 0)
            FROM product_variants v
            WHERE v.product_id = NEW.product_id AND v.is_deleted = '0'
        )
    WHERE id = NEW.product_id;
    END$$

    DROP TRIGGER IF EXISTS `trg_after_update_variant`$$
    CREATE TRIGGER `trg_after_update_variant`
        AFTER UPDATE ON `product_variants`
        FOR EACH ROW
    BEGIN
        UPDATE products
        SET stock_quantity = (
            SELECT COALESCE(SUM(v.stock_quantity), 0)
            FROM product_variants v
            WHERE v.product_id = NEW.product_id AND v.is_deleted = '0'
        ),
            sold_quantity = (
                SELECT COALESCE(SUM(v.sold_quantity), 0)
                FROM product_variants v
                WHERE v.product_id = NEW.product_id AND v.is_deleted = '0'
            )
        WHERE id = NEW.product_id;
        END$$

        DROP TRIGGER IF EXISTS `trg_after_delete_variant`$$
        CREATE TRIGGER `trg_after_delete_variant`
            AFTER DELETE ON `product_variants`
            FOR EACH ROW
        BEGIN
            UPDATE products
            SET stock_quantity = (
                SELECT COALESCE(SUM(v.stock_quantity), 0)
                FROM product_variants v
                WHERE v.product_id = OLD.product_id AND v.is_deleted = '0'
            ),
                sold_quantity = (
                    SELECT COALESCE(SUM(v.sold_quantity), 0)
                    FROM product_variants v
                    WHERE v.product_id = OLD.product_id AND v.is_deleted = '0'
                )
            WHERE id = OLD.product_id;
            END$$

            DELIMITER ;

-- =============================================
-- RESTORE SETTINGS
-- =============================================

SET FOREIGN_KEY_CHECKS = 1;

-- End of script