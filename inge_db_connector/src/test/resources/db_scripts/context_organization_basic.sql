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

ALTER TABLE ONLY public.context_organization_basic DROP CONSTRAINT fkjovdt5wluydmf1unncmoapkwl;
ALTER TABLE ONLY public.context_organization_basic DROP CONSTRAINT fk3ke0xcpa25rcoxxig1kfl2vnt;
DROP TABLE public.context_organization_basic;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: context_organization_basic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context_organization_basic (
    contextvo_objectid character varying(255) NOT NULL,
    responsibleaffiliations_objectid character varying(255) NOT NULL
);


ALTER TABLE context_organization_basic OWNER TO postgres;

--
-- Data for Name: context_organization_basic; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO context_organization_basic (contextvo_objectid, responsibleaffiliations_objectid) VALUES ('ctx_persistent3', 'ou_persistent25');
INSERT INTO context_organization_basic (contextvo_objectid, responsibleaffiliations_objectid) VALUES ('ctx_2322554', 'ou_persistent13');


--
-- Name: fk3ke0xcpa25rcoxxig1kfl2vnt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context_organization_basic
    ADD CONSTRAINT fk3ke0xcpa25rcoxxig1kfl2vnt FOREIGN KEY (responsibleaffiliations_objectid) REFERENCES organization_basic(objectid);


--
-- Name: fkjovdt5wluydmf1unncmoapkwl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context_organization_basic
    ADD CONSTRAINT fkjovdt5wluydmf1unncmoapkwl FOREIGN KEY (contextvo_objectid) REFERENCES context(objectid);


--
-- PostgreSQL database dump complete
--

