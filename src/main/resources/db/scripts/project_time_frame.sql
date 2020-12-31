
DROP TABLE IF EXISTS user_timeframe;
CREATE TABLE IF NOT EXISTS project_time_frame (
  id varchar(255) not null,
  from_date DATETIME NOT NULL,
  to_date DATETIME NOT NULL,
  realtime TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;