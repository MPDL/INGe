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

ALTER TABLE ONLY public.organization_predecessor DROP CONSTRAINT fkl2yth2a87fobu9sgr5obexyqa;
ALTER TABLE ONLY public.organization_predecessor DROP CONSTRAINT fk6celno57ccj38uikfqesnltrc;
DROP TABLE public.organization_predecessor;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: organization_predecessor; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization_predecessor (
    affiliationvo_objectid character varying(255) NOT NULL,
    predecessoraffiliations_objectid character varying(255) NOT NULL
);


ALTER TABLE organization_predecessor OWNER TO postgres;

--
-- Data for Name: organization_predecessor; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- Name: fk6celno57ccj38uikfqesnltrc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fk6celno57ccj38uikfqesnltrc FOREIGN KEY (predecessoraffiliations_objectid) REFERENCES organization_basic(objectid);


--
-- Name: fkl2yth2a87fobu9sgr5obexyqa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fkl2yth2a87fobu9sgr5obexyqa FOREIGN KEY (affiliationvo_objectid) REFERENCES organization(objectid);


--
-- PostgreSQL database dump complete
--

