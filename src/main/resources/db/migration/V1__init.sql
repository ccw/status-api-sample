CREATE TABLE USER (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	user_name VARCHAR(255) not null,
  password VARCHAR(255) not null,
  salt BIGINT not null,
	roles VARCHAR(255) not null
);

ALTER TABLE USER ADD CONSTRAINT USER_NAME_UNIQUE UNIQUE(user_name);

CREATE TABLE STATUS (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	content VARCHAR(140) not null,
	user_id BIGINT not null
);