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

ALTER TABLE ONLY public.file DROP CONSTRAINT file_pkey;
DROP TABLE public.file;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: file; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE file (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    checksum character varying(255),
    checksumalgorithm character varying(255),
    content character varying(255),
    contentcategory character varying(255),
    description text,
    metadata jsonb,
    mimetype character varying(255),
    pid character varying(255),
    storage character varying(255),
    visibility character varying(255)
);


ALTER TABLE file OWNER TO postgres;

--
-- Data for Name: file; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (objectid);


--
-- PostgreSQL database dump complete
--

