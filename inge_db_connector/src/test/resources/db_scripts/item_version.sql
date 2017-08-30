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

ALTER TABLE ONLY public.item_version DROP CONSTRAINT fkr3jlfblcdwytk5p4bcbwlnltd;
ALTER TABLE ONLY public.item_version DROP CONSTRAINT item_version_pkey;
DROP TABLE public.item_version;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: item_version; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE item_version (
    objectid character varying(255) NOT NULL,
    versionnumber integer NOT NULL,
    lastmessage text,
    modificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    state character varying(255),
    versionpid character varying(255),
    metadata jsonb
);


ALTER TABLE item_version OWNER TO postgres;

--
-- Data for Name: item_version; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: item_version_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_version
    ADD CONSTRAINT item_version_pkey PRIMARY KEY (objectid, versionnumber);


--
-- Name: fkr3jlfblcdwytk5p4bcbwlnltd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version
    ADD CONSTRAINT fkr3jlfblcdwytk5p4bcbwlnltd FOREIGN KEY (objectid) REFERENCES item_object(objectid) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

