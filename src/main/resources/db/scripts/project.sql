CREATE TABLE IF NOT EXISTS project (
  id varchar(255) not null,
  name varchar(255) not null,
  keywords varchar(2000) not null,
  excluded_keywords varchar(2000),
  sources varchar(1000) not null,
  team_members varchar(2000),
  description varchar(1000),
  created DATETIME default CURRENT_TIMESTAMP,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;