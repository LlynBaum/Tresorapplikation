--
-- Datenbank: `tresordb`
--

DROP DATABASE IF EXISTS tresordb;
CREATE DATABASE tresordb;
USE tresordb;

-- --------------------------------------------------------

--
-- table user
--

CREATE TABLE user (
    id int NOT NULL AUTO_INCREMENT,
    first_name varchar(30) NOT NULL,
    last_name varchar(30) NOT NULL,
    email varchar(30) NOT NULL,
    role varchar(30) NOT NULL DEFAULT 'user',
    password longtext NOT NULL,
    PRIMARY KEY (id)
);

--
-- table secret
--

CREATE TABLE secret (
    id int NOT NULL AUTO_INCREMENT,
    user_id int NOT NULL,
    content TEXT NOT NULL,
    PRIMARY KEY (id)
);