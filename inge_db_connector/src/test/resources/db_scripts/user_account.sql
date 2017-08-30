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

ALTER TABLE ONLY public.user_account DROP CONSTRAINT fkf7qviljpuun64colnwwd5fuw9;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT user_account_pkey;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT uk_pjo26o2ngxbi23s0sw7nrnrsf;
DROP TABLE public.user_account;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: user_account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_account (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    active boolean NOT NULL,
    email character varying(255),
    grantlist jsonb,
    loginname character varying(255),
    password character varying(255),
    affiliation_objectid character varying(255)
);


ALTER TABLE user_account OWNER TO postgres;

--
-- Data for Name: user_account; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO user_account (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, password, affiliation_objectid) VALUES ('user_3000056', '2017-05-31 13:36:45.703', 'roland', 'user_user42', '2017-05-31 13:36:45.703', 'roland', 'user_user42', 'Test Depositor', true, 'a@b.de', '[{"role": "DEPOSITOR", "objectRef": "ctx_2322554"}, {"role": "DEPOSITOR", "objectRef": "ctx_persistent3"}]', 'test_depositor', NULL, 'ou_persistent25');
INSERT INTO user_account (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, password, affiliation_objectid) VALUES ('user_3000057', '2017-05-31 13:38:09.611', 'roland', 'user_user42', '2017-05-31 13:38:09.611', 'roland', 'user_user42', 'Test Moderator', true, 'a@b.de', '[{"role": "MODERATOR", "objectRef": "ctx_2322554"}, {"role": "MODERATOR", "objectRef": "ctx_persistent3"}]', 'test_moderator', NULL, 'ou_persistent25');

INSERT INTO user_account (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, password, affiliation_objectid) VALUES ('user_3000165', '2017-06-02 09:56:08.234', 'roland', 'user_user42', '2017-06-02 09:56:08.816', 'roland', 'user_user42', 'Test Moderator', true, 'a@b.de', '[{"role": "MODERATOR", "objectRef": "ctx_2322554"}]', 'testCreateUserWithGrant', NULL, 'ou_persistent25');

--
-- Name: uk_pjo26o2ngxbi23s0sw7nrnrsf; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT uk_pjo26o2ngxbi23s0sw7nrnrsf UNIQUE (loginname);


--
-- Name: user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_account_pkey PRIMARY KEY (objectid);


--
-- Name: fkf7qviljpuun64colnwwd5fuw9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fkf7qviljpuun64colnwwd5fuw9 FOREIGN KEY (affiliation_objectid) REFERENCES organization_basic(objectid);


--
-- PostgreSQL database dump complete
--

