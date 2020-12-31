

create table if not exists api_log (
    id int not null auto_increment,
    user_id varchar(255) not null,
    alias varchar(100) NOT NULL,
    params varchar(600) NOT NULL,
    executed_at DATETIME default CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

