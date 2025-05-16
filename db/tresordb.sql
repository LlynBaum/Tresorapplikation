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
    password longtext NOT NULL,
    PRIMARY KEY (id)
);

--
-- table user content
--

INSERT INTO `user` (`id`, `first_name`, `last_name`, `email`, `password`) VALUES
(1, 'Hans', 'Muster', 'hans.muster@bbw.ch', 'abcd'),
(2, 'Paula', 'Kuster', 'paula.kuster@bbw.ch', 'efgh'),
(3, 'Andrea', 'Oester', 'andrea.oester@bbw.ch', 'ijkl');

--
-- table secret
--

CREATE TABLE secret (
    id int NOT NULL AUTO_INCREMENT,
    user_id int NOT NULL,
    content TEXT NOT NULL,
    PRIMARY KEY (id)
);