
ALTER TABLE user_alert ADD active TINYINT NOT NULL DEFAULT 1;
ALTER TABLE user_alert ADD email TINYINT NOT NULL DEFAULT 1;
ALTER TABLE user_alert ADD type varchar(40) NOT NULL;

