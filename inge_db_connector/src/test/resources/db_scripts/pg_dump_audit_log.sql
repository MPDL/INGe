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

ALTER TABLE ONLY public.audit_log DROP CONSTRAINT fk751lpy01kv1wgjnwlgcgn9o74;
ALTER TABLE ONLY public.audit_log DROP CONSTRAINT audit_log_pkey;
DROP TABLE public.audit_log;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: audit_log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE audit_log (
    id integer NOT NULL,
    comment text,
    event character varying(255),
    modificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    pubitem_objectid character varying(255),
    pubitem_versionnumber integer
);


ALTER TABLE audit_log OWNER TO postgres;

--
-- Data for Name: audit_log; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);


--
-- Name: fk751lpy01kv1wgjnwlgcgn9o74; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audit_log
    ADD CONSTRAINT fk751lpy01kv1wgjnwlgcgn9o74 FOREIGN KEY (pubitem_objectid, pubitem_versionnumber) REFERENCES item_version(objectid, versionnumber) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

