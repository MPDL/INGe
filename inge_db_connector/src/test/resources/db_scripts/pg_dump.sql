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

ALTER TABLE ONLY public.context DROP CONSTRAINT fkhqq7922xmad4sl6r9hsn4m6iq;
ALTER TABLE ONLY public.context DROP CONSTRAINT context_pkey;
DROP TABLE public.context;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: context; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context (
    admindescriptor jsonb,
    description text,
    state character varying(255),
    type character varying(255),
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    allowedgenres jsonb,
    allowedsubjectclassifications jsonb,
    contactemail character varying(255),
    workflow character varying(255)
);


ALTER TABLE context OWNER TO postgres;

--
-- Name: context_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY context
    ADD CONSTRAINT context_pkey PRIMARY KEY (objectid);


--
-- Name: fkhqq7922xmad4sl6r9hsn4m6iq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context
    ADD CONSTRAINT fkhqq7922xmad4sl6r9hsn4m6iq FOREIGN KEY (objectid) REFERENCES context_basic(objectid);


--
-- PostgreSQL database dump complete
--

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

ALTER TABLE ONLY public.context_basic DROP CONSTRAINT context_basic_pkey;
DROP TABLE public.context_basic;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: context_basic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context_basic (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    admindescriptor jsonb,
    description text,
    state character varying(255),
    type character varying(255)
);


ALTER TABLE context_basic OWNER TO postgres;

--
-- Name: context_basic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY context_basic
    ADD CONSTRAINT context_basic_pkey PRIMARY KEY (objectid);


--
-- PostgreSQL database dump complete
--

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
    visibility character varying(255),
    localfileidentifier character varying(255),
    creator_name character varying(255),
    creator_objectid character varying(255)
);


ALTER TABLE file OWNER TO postgres;

--
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (objectid);


--
-- PostgreSQL database dump complete
--

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
-- Name: id_provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY id_provider
    ADD CONSTRAINT id_provider_pkey PRIMARY KEY (type);


--
-- PostgreSQL database dump complete
--

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
ALTER TABLE ONLY public.item_object DROP CONSTRAINT fk3ys7ixo6yd7oaivqea11rqrf9;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT fk18evwh3n73g7i7stv90nnn69c;
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
    latestversion_versionnumber integer,
    context_versionnumber integer,
    item_object character varying(255),
    creator_name character varying(255),
    creator_objectid character varying(255),
    publicstate character varying(255)
);


ALTER TABLE item_object OWNER TO postgres;

--
-- Name: item_object_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT item_object_pkey PRIMARY KEY (objectid);


--
-- Name: fk18evwh3n73g7i7stv90nnn69c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fk18evwh3n73g7i7stv90nnn69c FOREIGN KEY (context_objectid) REFERENCES context(objectid);


--
-- Name: fk3ys7ixo6yd7oaivqea11rqrf9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fk3ys7ixo6yd7oaivqea11rqrf9 FOREIGN KEY (context_objectid, context_versionnumber) REFERENCES item_version(objectid, versionnumber);


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
    metadata jsonb,
    versionstate character varying(255),
    message text
);


ALTER TABLE item_version OWNER TO postgres;

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

ALTER TABLE ONLY public.organization DROP CONSTRAINT fkl2u8rk7ip5ghjxb43l491ym6k;
ALTER TABLE ONLY public.organization DROP CONSTRAINT fkgbtkmc0vxvtnnhgf7yey0ep26;
ALTER TABLE ONLY public.organization DROP CONSTRAINT fk3djncc5hpoko2xshwt1rbvvsk;
ALTER TABLE ONLY public.organization DROP CONSTRAINT organization_pkey;
DROP TABLE public.organization;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: organization; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization (
    metadata jsonb,
    publicstatus character varying(255),
    objectid character varying(255) NOT NULL,
    parentaffiliation_objectid character varying(255),
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text
);


ALTER TABLE organization OWNER TO postgres;

--
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (objectid);


--
-- Name: fk3djncc5hpoko2xshwt1rbvvsk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fk3djncc5hpoko2xshwt1rbvvsk FOREIGN KEY (parentaffiliation_objectid) REFERENCES organization_basic(objectid);


--
-- Name: fkgbtkmc0vxvtnnhgf7yey0ep26; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fkgbtkmc0vxvtnnhgf7yey0ep26 FOREIGN KEY (objectid) REFERENCES organization_basic(objectid);


--
-- Name: fkl2u8rk7ip5ghjxb43l491ym6k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fkl2u8rk7ip5ghjxb43l491ym6k FOREIGN KEY (parentaffiliation_objectid) REFERENCES organization(objectid);


--
-- PostgreSQL database dump complete
--

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

ALTER TABLE ONLY public.organization_basic DROP CONSTRAINT organization_basic_pkey;
DROP TABLE public.organization_basic;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: organization_basic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization_basic (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name character varying(255)
);


ALTER TABLE organization_basic OWNER TO postgres;

--
-- Name: organization_basic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY organization_basic
    ADD CONSTRAINT organization_basic_pkey PRIMARY KEY (objectid);


--
-- PostgreSQL database dump complete
--

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
ALTER TABLE ONLY public.organization_predecessor DROP CONSTRAINT fk6olo333815g1k20qcf7kkrsfa;
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
-- Name: fk6celno57ccj38uikfqesnltrc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fk6celno57ccj38uikfqesnltrc FOREIGN KEY (predecessoraffiliations_objectid) REFERENCES organization_basic(objectid);


--
-- Name: fk6olo333815g1k20qcf7kkrsfa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fk6olo333815g1k20qcf7kkrsfa FOREIGN KEY (predecessoraffiliations_objectid) REFERENCES organization(objectid);


--
-- Name: fkl2yth2a87fobu9sgr5obexyqa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fkl2yth2a87fobu9sgr5obexyqa FOREIGN KEY (affiliationvo_objectid) REFERENCES organization(objectid);


--
-- PostgreSQL database dump complete
--

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

ALTER TABLE ONLY public.user_account DROP CONSTRAINT fkf7qviljpuun64colnwwd5fuw9;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT fk9yvnsv4vysj35qwjmvxx7eala;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT user_account_pkey;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT uk_pjo26o2ngxbi23s0sw7nrnrsf;
DROP TABLE public.user_account;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: user_account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_account (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    owner_name character varying(255),
    owner_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    active boolean NOT NULL,
    email character varying(255),
    grantlist jsonb,
    loginname character varying(255),
    password character varying(255),
    affiliation_objectid character varying(255),
    creator_name character varying(255),
    creator_objectid character varying(255)
);


ALTER TABLE user_account OWNER TO postgres;

--
-- Name: uk_pjo26o2ngxbi23s0sw7nrnrsf; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT uk_pjo26o2ngxbi23s0sw7nrnrsf UNIQUE (loginname);


--
-- Name: user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_account_pkey PRIMARY KEY (objectid);


--
-- Name: fk9yvnsv4vysj35qwjmvxx7eala; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fk9yvnsv4vysj35qwjmvxx7eala FOREIGN KEY (affiliation_objectid) REFERENCES organization(objectid);


--
-- Name: fkf7qviljpuun64colnwwd5fuw9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fkf7qviljpuun64colnwwd5fuw9 FOREIGN KEY (affiliation_objectid) REFERENCES organization_basic(objectid);


--
-- PostgreSQL database dump complete
--

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

ALTER TABLE ONLY public.user_login DROP CONSTRAINT user_login_loginname_fkey2;
ALTER TABLE ONLY public.user_login DROP CONSTRAINT user_login_pkey;
DROP TABLE public.user_login;
SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: user_login; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_login (
    loginname character varying(255) NOT NULL,
    password character varying(255)
);


ALTER TABLE user_login OWNER TO postgres;

--
-- Name: user_login_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_login
    ADD CONSTRAINT user_login_pkey PRIMARY KEY (loginname);


--
-- Name: user_login_loginname_fkey2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_login
    ADD CONSTRAINT user_login_loginname_fkey2 FOREIGN KEY (loginname) REFERENCES user_account(loginname) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

