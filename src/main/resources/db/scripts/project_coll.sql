CREATE TABLE IF NOT EXISTS project (
  id varchar(255) not null,
  name varchar(255)  character set utf8 collate utf8_general_ci not null,
  keywords varchar(2000)  character set utf8 collate utf8_general_ci not null,
  excluded_keywords varchar(2000) character set utf8 collate utf8_general_ci,
  sources varchar(1000)  character set utf8 collate utf8_general_ci not null,
  team_members varchar(2000) character set utf8 collate utf8_general_ci,
  description varchar(1000) character set utf8 collate utf8_general_ci,
  created DATETIME default CURRENT_TIMESTAMP,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci;


