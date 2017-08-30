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

ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT fkahefe4tpvp7uiqtp0pleuvv1k;
ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT fk14k00cpi4gg01mtt8qxrgifoe;
ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT item_version_file_pkey;
DROP TABLE public.item_version_file;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: item_version_file; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE item_version_file (
    pubitemversionvo_objectid character varying(255) NOT NULL,
    pubitemversionvo_versionnumber integer NOT NULL,
    files_objectid character varying(255) NOT NULL,
    creationdate integer NOT NULL
);


ALTER TABLE item_version_file OWNER TO postgres;

--
-- Data for Name: item_version_file; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: item_version_file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT item_version_file_pkey PRIMARY KEY (pubitemversionvo_objectid, pubitemversionvo_versionnumber, creationdate);


--
-- Name: fk14k00cpi4gg01mtt8qxrgifoe; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT fk14k00cpi4gg01mtt8qxrgifoe FOREIGN KEY (files_objectid) REFERENCES file(objectid);


--
-- Name: fkahefe4tpvp7uiqtp0pleuvv1k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT fkahefe4tpvp7uiqtp0pleuvv1k FOREIGN KEY (pubitemversionvo_objectid, pubitemversionvo_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- PostgreSQL database dump complete
--

