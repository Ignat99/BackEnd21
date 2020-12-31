CREATE TABLE alerts (
  id INT auto_increment PRIMARY KEY,
  project_id VARCHAR(256) DEFAULT NULL,
  type_alert INT(1),
  keywords VARCHAR(256) DEFAULT NULL,
  type_keyword VARCHAR(256) DEFAULT NULL,
  username VARCHAR(256) DEFAULT NULL,
  threat_or_more INT(7),
  active BOOLEAN,
  mail BOOLEAN);
