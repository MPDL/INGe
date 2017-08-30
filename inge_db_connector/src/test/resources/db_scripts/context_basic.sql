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

ALTER TABLE ONLY public.context_basic DROP CONSTRAINT context_basic_pkey;
DROP TABLE public.context_basic;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: context_basic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context_basic (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text
);


ALTER TABLE context_basic OWNER TO postgres;

--
-- Data for Name: context_basic; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO context_basic (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, admindescriptor, description, state, type) VALUES ('ctx_persistent3', '2007-01-16 12:23:24.359', NULL, 'user_user42', '2016-01-25 12:59:31.873', NULL, 'user_user42', 'PubMan Test Collection', NULL, NULL, NULL, NULL);
INSERT INTO context_basic (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, admindescriptor, description, state, type) VALUES ('ctx_2322554', '2016-07-25 10:54:42.848', NULL, 'user_user42', '2016-07-25 10:57:17.126', NULL, 'user_user42', 'Test_Context_Simple', NULL, NULL, NULL, NULL);



--
-- Name: context_basic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY context_basic
    ADD CONSTRAINT context_basic_pkey PRIMARY KEY (objectid);


--
-- PostgreSQL database dump complete
--

