DROP DATABASE IF EXISTS events;
CREATE DATABASE events;

USE events;

CREATE TABLE users (
	id bigint(20) PRIMARY KEY AUTO_INCREMENT,
	username varchar(255) UNIQUE NOT NULL,
	password varchar(255) NOT NULL,
	enabled tinyint(1) DEFAULT '1',
	account_non_expired tinyint(1) DEFAULT '1',
	credentials_non_expired tinyint(1) DEFAULT '1',
	account_non_locked tinyint(1) DEFAULT '1'
);

CREATE TABLE IF NOT EXISTS events (
	id bigint(20) AUTO_INCREMENT,
	organizerid bigint(20) DEFAULT NULL,
	name varchar(255) NOT NULL,
	date datetime(6) NOT NULL,
	location varchar(255) NOT NULL,
	description varchar(255),

	PRIMARY KEY (id),
	FOREIGN KEY (organizerid) REFERENCES users(id)
);

INSERT INTO events (id, name, date, location, organizerid, description) VALUES
(1, 'Opening Event', '2025-10-06', 'Las Vegas, NV, USA', null, 'No gambling.'),
(2, 'Grand Opening', '2025-10-08', 'Phoenix, AZ, USA', null, 'Free food! Bring a friend.'),
(3, 'Birthday', '2024-08-08', 'Scottsdale, AZ, USA', null, 'Bring a gift.'),
(4, 'Funeral', '2024-07-08', 'Salt Lake City, UT, USA', null, 'They died.'),
(5, 'Birth', '2024-07-02', 'San Diego, CA, USA', null, 'They alived.'),
(6, 'Birth', '2024-07-02', 'San Diego, CA, USA', null, 'They also alived.'),
(7, 'Funeral', '2024-07-08', 'Salt Lake City, UT, USA', null, 'They also died.');

CREATE TABLE roles (
	user_id bigint(20),
	role varchar(255) DEFAULT NULL,

	FOREIGN KEY (user_id) REFERENCES users(id)
);
