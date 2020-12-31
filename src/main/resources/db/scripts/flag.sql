CREATE TABLE IF NOT EXISTS flag (
  id varchar(255) not null,
  name varchar(1000) NOT NULL,
  project_id varchar(255) not null,
  lower_name varchar(1000) NOT NULL UNIQUE,
  active TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;