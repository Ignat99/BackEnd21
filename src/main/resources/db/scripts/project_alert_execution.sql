

create table if not exists project_alert_execution (
    id int not null auto_increment,
    project_id varchar(255) not null,
    alias varchar(100) NOT NULL,
    extravars varchar(600) NOT NULL,
    show_alert TINYINT NOT NULL,
    executed_at DATETIME default CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

