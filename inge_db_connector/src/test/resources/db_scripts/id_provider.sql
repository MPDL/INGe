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

ALTER TABLE ONLY public.id_provider DROP CONSTRAINT id_provider_pkey;
DROP TABLE public.id_provider;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: id_provider; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE id_provider (
    type character varying(255) NOT NULL,
    current_id bigint
);


ALTER TABLE id_provider OWNER TO postgres;

--
-- Data for Name: id_provider; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- Name: id_provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY id_provider
    ADD CONSTRAINT id_provider_pkey PRIMARY KEY (type);


--
-- PostgreSQL database dump complete
--

