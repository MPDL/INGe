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

ALTER TABLE ONLY public.item_object DROP CONSTRAINT fktdnnngv88h3l913lylbcylyqf;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT fkcvl5cml7ubacyt3xs8871458h;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT fkblh9cvrc5ocgnfw6sav842qpu;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT item_object_pkey;
DROP TABLE public.item_object;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: item_object; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE item_object (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    lastmodificationdate timestamp without time zone,
    localtags jsonb,
    owner_name character varying(255),
    owner_objectid character varying(255),
    objectpid character varying(255),
    publicstatus character varying(255),
    publicstatuscomment text,
    context_objectid character varying(255),
    latestrelease_objectid character varying(255),
    latestrelease_versionnumber integer,
    latestversion_objectid character varying(255),
    latestversion_versionnumber integer
);


ALTER TABLE item_object OWNER TO postgres;

--
-- Data for Name: item_object; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: item_object_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT item_object_pkey PRIMARY KEY (objectid);


--
-- Name: fkblh9cvrc5ocgnfw6sav842qpu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fkblh9cvrc5ocgnfw6sav842qpu FOREIGN KEY (latestrelease_objectid, latestrelease_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- Name: fkcvl5cml7ubacyt3xs8871458h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fkcvl5cml7ubacyt3xs8871458h FOREIGN KEY (context_objectid) REFERENCES context_basic(objectid);


--
-- Name: fktdnnngv88h3l913lylbcylyqf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fktdnnngv88h3l913lylbcylyqf FOREIGN KEY (latestversion_objectid, latestversion_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- PostgreSQL database dump complete
--

