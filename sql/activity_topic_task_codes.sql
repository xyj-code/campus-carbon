ALTER TABLE `activity_topic`
ADD COLUMN `task_codes` varchar(255) NOT NULL DEFAULT '' AFTER `target_text`;

UPDATE `activity_topic`
SET `task_codes` = 'DAILY_STEP_6000,DAILY_SPORT_2KM,WEEKLY_REDEEM_1'
WHERE `activity_code` = 'EARTH_DAY_2026' AND (`task_codes` = '' OR `task_codes` IS NULL);

UPDATE `activity_topic`
SET `task_codes` = 'DAILY_STEP_6000,DAILY_CHECKIN_1,WEEKLY_STEP_50000'
WHERE `activity_code` = 'GREEN_COMMUTE_MONTH' AND (`task_codes` = '' OR `task_codes` IS NULL);
