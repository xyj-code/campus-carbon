CREATE TABLE IF NOT EXISTS student_schedule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  weekday TINYINT NOT NULL COMMENT '1=Monday, 7=Sunday',
  course_name VARCHAR(100) NOT NULL,
  location VARCHAR(120) DEFAULT '',
  start_time VARCHAR(5) NOT NULL COMMENT 'HH:mm',
  end_time VARCHAR(5) NOT NULL COMMENT 'HH:mm',
  remark VARCHAR(255) DEFAULT '',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_schedule_user_weekday (username, weekday),
  INDEX idx_schedule_user_time (username, weekday, start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Student weekly course schedule';
