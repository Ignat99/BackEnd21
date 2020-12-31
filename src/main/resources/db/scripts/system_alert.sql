CREATE TABLE IF NOT EXISTS system_alert (
  id int not null,
  alias varchar(100) NOT NULL,
  extravars varchar(600) NOT NULL,
  created_at DATETIME default CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO system_alert (id, alias, extravars) VALUES (1,'num_posts', 'num=1000');
INSERT INTO system_alert (id, alias, extravars) VALUES (2,'num_posts', 'num=10000');
INSERT INTO system_alert (id, alias, extravars) VALUES (3,'num_posts', 'num=100000');
INSERT INTO system_alert (id, alias, extravars) VALUES (4,'num_posts', 'num=500000');
INSERT INTO system_alert (id, alias, extravars) VALUES (5,'num_posts', 'num=1000000');
INSERT INTO system_alert (id, alias, extravars) VALUES (6,'entity_hashtag_topic', 'ratio=0.1');
INSERT INTO system_alert (id, alias, extravars) VALUES (7,'entity_hashtag_topic', 'ratio=0.3');
INSERT INTO system_alert (id, alias, extravars) VALUES (8,'entity_hashtag_topic', 'ratio=0.5');
INSERT INTO system_alert (id, alias, extravars) VALUES (9,'entity_hashtag_topic', 'ratio=0.7');
INSERT INTO system_alert (id, alias, extravars) VALUES (10,'increase_num_posts', 'ratio=0.1');
INSERT INTO system_alert (id, alias, extravars) VALUES (11,'increase_num_posts', 'ratio=0.5');
INSERT INTO system_alert (id, alias, extravars) VALUES (12,'increase_num_posts', 'ratio=1');
INSERT INTO system_alert (id, alias, extravars) VALUES (13,'change_threat_level', '');
INSERT INTO system_alert (id, alias, extravars) VALUES (14,'suspicious_message', 'threat_level_min=4');
