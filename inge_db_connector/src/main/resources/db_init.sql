-- This script is running during every startup and creates the tables which are not automatically created by Hibernate's automatic schema generation DDL

-- ID Provider
CREATE TABLE IF NOT EXISTS id_provider (
    type varchar(255),
    current_id bigint,
    PRIMARY KEY (type)
);
INSERT INTO id_provider (type, current_id) VALUES ('pure', 1) ON CONFLICT DO NOTHING;


-- User Login Table
CREATE TABLE IF NOT EXISTS user_login (
    loginname varchar(255),
    password varchar(255),
    last_password_change DATE,
    password_change_flag BOOLEAN,
    PRIMARY KEY (loginname),
	FOREIGN KEY (loginname) REFERENCES user_account(loginname) ON DELETE CASCADE ON UPDATE CASCADE
    );


-- Wird durch JPA generiert

--Import Tables
-- CREATE SEQUENCE IF NOT EXISTS import_log_id_seq
--     INCREMENT 1
--     START 1
--     MINVALUE 1
--     CACHE 10;

-- CREATE TABLE IF NOT EXISTS import_log
-- (
--     id         integer                     NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
--     status     varchar(255)                NOT NULL,
--     errorlevel varchar(255)                NOT NULL,
--     startdate  timestamp without time zone NOT NULL,
--     enddate    timestamp without time zone,
--     userid     varchar(255),
--     name       varchar,
--     format     varchar(255),
--     context    varchar(255),
--     percentage integer,
--     PRIMARY KEY (id)
-- );

-- CREATE INDEX IF NOT EXISTS import_log_idx_userid
--     ON import_log USING btree
--     (userid);

-- CREATE TABLE  IF NOT EXISTS import_log_item
-- (
--     id integer NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
--     status varchar(255)NOT NULL,
--     errorlevel varchar(255)NOT NULL,
--     startdate timestamp without time zone NOT NULL,
--     enddate timestamp without time zone,
--     parent integer NOT NULL,
--     message varchar,
--     item_id varchar(255),
--     PRIMARY KEY (id),
--     FOREIGN KEY (parent) REFERENCES import_log(id) ON DELETE CASCADE
-- );

-- CREATE INDEX IF NOT EXISTS import_log_item_idx_parent
--     ON import_log_item USING btree
--         (parent);

-- CREATE INDEX  IF NOT EXISTS import_log_item_idx_itemid
--     ON import_log_item USING btree
--     (item_id);

-- CREATE TABLE IF NOT EXISTS import_log_item_detail
-- (
--     id integer NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
--     status varchar(255) NOT NULL,
--     errorlevel varchar(255) NOT NULL,
--     startdate timestamp without time zone NOT NULL,
--     parent integer NOT NULL,
--     message varchar,
--     PRIMARY KEY (id),
--     FOREIGN KEY (parent) REFERENCES import_log_item(id) ON DELETE CASCADE
-- );

-- CREATE INDEX IF NOT EXISTS import_log_item_detail_idx_parent
--     ON import_log_item_detail USING btree
--         (parent);

