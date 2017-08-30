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

ALTER TABLE ONLY public.organization_basic DROP CONSTRAINT organization_basic_pkey;
DROP TABLE public.organization_basic;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: organization_basic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization_basic (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name character varying(255)
);


ALTER TABLE organization_basic OWNER TO postgres;

--
-- Data for Name: organization_basic; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO organization_basic (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name) VALUES ('ou_persistent13', '2007-03-22 09:23:35.562', NULL, 'user_user42', '2011-06-08 10:08:11.522', NULL, 'user_user42', 'Max Planck Society');
INSERT INTO organization_basic (objectid, creationdate, owner_name, owner_objectid, lastmodificationdate, modifier_name, modifier_objectid, name) VALUES ('ou_persistent25', '2007-03-22 09:23:35.562', NULL, 'user_user42', '2017-06-19 09:10:39.493', 'roland', 'user_user42', 'Max Planck Digital Library');


--
-- Name: organization_basic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY organization_basic
    ADD CONSTRAINT organization_basic_pkey PRIMARY KEY (objectid);


--
-- PostgreSQL database dump complete
--

