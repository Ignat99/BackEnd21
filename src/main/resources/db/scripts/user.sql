CREATE TABLE IF NOT EXISTS user (
  id varchar(255) not null,
  username varchar(40) not null,
	email varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	name varchar(100) NOT NULL,
	surname varchar(100) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;