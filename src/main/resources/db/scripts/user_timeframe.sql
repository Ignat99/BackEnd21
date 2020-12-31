CREATE TABLE IF NOT EXISTS user_timeframe (
  id varchar(255) not null,
  from_date DATETIME NOT NULL,
  to_date DATETIME NOT NULL,
  realtime TINYINT NOT NULL DEFAULT 0,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;