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

ALTER TABLE ONLY public.organization_parent DROP CONSTRAINT fkkj03v2nbw4fbf1aebq1uw6qb2;
ALTER TABLE ONLY public.organization_parent DROP CONSTRAINT fk98tdxlm8wt59fxit2kh5qevan;
DROP TABLE public.organization_parent;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: organization_parent; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization_parent (
    affiliationvo_objectid character varying(255) NOT NULL,
    parentaffiliations_objectid character varying(255) NOT NULL
);


ALTER TABLE organization_parent OWNER TO postgres;

--
-- Data for Name: organization_parent; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: fk98tdxlm8wt59fxit2kh5qevan; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_parent
    ADD CONSTRAINT fk98tdxlm8wt59fxit2kh5qevan FOREIGN KEY (parentaffiliations_objectid) REFERENCES organization_basic(objectid);


--
-- Name: fkkj03v2nbw4fbf1aebq1uw6qb2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_parent
    ADD CONSTRAINT fkkj03v2nbw4fbf1aebq1uw6qb2 FOREIGN KEY (affiliationvo_objectid) REFERENCES organization(objectid);


--
-- PostgreSQL database dump complete
--

