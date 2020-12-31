CREATE TABLE IF NOT EXISTS user_alert (
  id varchar(255) not null,
  project_id varchar(255) not null,
  user_id varchar(255) not null,
  alias varchar(100) NOT NULL,
  extravars varchar(600) NOT NULL,
  created_at DATETIME default CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;