--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

ALTER TABLE ONLY public.user_login DROP CONSTRAINT user_login_loginname_fkey2;
ALTER TABLE ONLY public.user_login DROP CONSTRAINT user_login_pkey;
DROP TABLE public.user_login;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: user_login; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_login (
    loginname character varying(255) NOT NULL,
    password character varying(255)
);


ALTER TABLE user_login OWNER TO postgres;

--
-- Data for Name: user_login; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO user_login (loginname, password) VALUES ('test_depositor', '$2a$10$o9SkKt3AS1sVfVyh/J81IuVPThsYoclC7sDKsU7fDt9hlBldiSjXO');
INSERT INTO user_login (loginname, password) VALUES ('test_moderator', '$2a$10$bl82eVlP4Z7g/w4dvFVjKeaoPdzP8ZKo5ag88JNgdXaBMghAHKqQm');
INSERT INTO user_login (loginname, password) VALUES ('testCreateUserWithGrant', '$2a$10$G5rsfixnZQFICdYaCFKqzerkWSkaZPemUh7kgKW5meapNC4DeHbFu');
INSERT INTO user_login (loginname, password) VALUES ('test_dep_mod', '$2a$10$o9SkKt3AS1sVfVyh/J81IuVPThsYoclC7sDKsU7fDt9hlBldiSjXO');


--
-- Name: user_login_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_login
    ADD CONSTRAINT user_login_pkey PRIMARY KEY (loginname);


--
-- Name: user_login_loginname_fkey2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_login
    ADD CONSTRAINT user_login_loginname_fkey2 FOREIGN KEY (loginname) REFERENCES user_account(loginname) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

