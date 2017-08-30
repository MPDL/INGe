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

ALTER TABLE ONLY public.context_basic_organization_basic DROP CONSTRAINT fkt475p4o0lw6x5sh7u6piwxk0b;
ALTER TABLE ONLY public.context_basic_organization_basic DROP CONSTRAINT fkng7egtqcpq9beeeg3dqymibwc;
DROP TABLE public.context_basic_organization_basic;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: context_basic_organization_basic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context_basic_organization_basic (
    contextvo_objectid character varying(255) NOT NULL,
    responsibleaffiliations_objectid character varying(255) NOT NULL
);


ALTER TABLE context_basic_organization_basic OWNER TO postgres;

--
-- Data for Name: context_basic_organization_basic; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: fkng7egtqcpq9beeeg3dqymibwc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context_basic_organization_basic
    ADD CONSTRAINT fkng7egtqcpq9beeeg3dqymibwc FOREIGN KEY (contextvo_objectid) REFERENCES context_basic(objectid);


--
-- Name: fkt475p4o0lw6x5sh7u6piwxk0b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context_basic_organization_basic
    ADD CONSTRAINT fkt475p4o0lw6x5sh7u6piwxk0b FOREIGN KEY (responsibleaffiliations_objectid) REFERENCES organization_basic(objectid);


--
-- PostgreSQL database dump complete
--

