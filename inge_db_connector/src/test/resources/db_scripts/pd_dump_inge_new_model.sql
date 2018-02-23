--
-- PostgreSQL database dump
--

-- pg_dump --column-inserts --schema-only --clean inge_new_mpdel

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

ALTER TABLE ONLY public.item_object DROP CONSTRAINT fktdnnngv88h3l913lylbcylyqf;
ALTER TABLE ONLY public.organization_predecessor DROP CONSTRAINT fksmkncjbsor49iuvnkvgud3ms2;
ALTER TABLE ONLY public.item_version DROP CONSTRAINT fkr3jlfblcdwytk5p4bcbwlnltd;
ALTER TABLE ONLY public.context_organization DROP CONSTRAINT fkq77txeng0o7k1d32pa2bk8upd;
ALTER TABLE ONLY public.context_organization DROP CONSTRAINT fkq0gb0ly9mgt3eajribctu1r2u;
ALTER TABLE ONLY public.organization DROP CONSTRAINT fkl2u8rk7ip5ghjxb43l491ym6k;
ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT fkfwpsg5jl7ahtgc3sdfxc28w8q;
ALTER TABLE ONLY public.yearbook DROP CONSTRAINT fkfjpnwigunnp2grtn4bh2et98y;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT fkblh9cvrc5ocgnfw6sav842qpu;
ALTER TABLE ONLY public.yearbook_item DROP CONSTRAINT fkbb84i4hokbu1mt95d0an92p8i;
ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT fkahefe4tpvp7uiqtp0pleuvv1k;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT fk9yvnsv4vysj35qwjmvxx7eala;
ALTER TABLE ONLY public.audit_log DROP CONSTRAINT fk751lpy01kv1wgjnwlgcgn9o74;
ALTER TABLE ONLY public.organization_predecessor DROP CONSTRAINT fk6olo333815g1k20qcf7kkrsfa;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT fk18evwh3n73g7i7stv90nnn69c;
ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT fk14k00cpi4gg01mtt8qxrgifoe;
DROP INDEX public.import_log_item_idx_parent;
DROP INDEX public.import_log_item_idx_item_id;
DROP INDEX public.import_log_item_detail_idx_parent;
DROP INDEX public.import_log_idx_userid;
ALTER TABLE ONLY public.yearbook DROP CONSTRAINT yearbook_pkey;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT user_account_pkey;
ALTER TABLE ONLY public.yearbook DROP CONSTRAINT ukfmcofdqp6ylrxi2ry431oq2ch;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT uk_pjo26o2ngxbi23s0sw7nrnrsf;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT uk_hl02wv5hym99ys465woijmfib;
ALTER TABLE ONLY public.user_account DROP CONSTRAINT uk_cnacf4jcff11b69f19b4uvpiv;
ALTER TABLE ONLY public.user_login DROP CONSTRAINT user_login_loginname_fkey2;
ALTER TABLE ONLY public.user_login DROP CONSTRAINT user_login_pkey;
ALTER TABLE ONLY public.staged_file DROP CONSTRAINT staged_file_pkey;
ALTER TABLE ONLY public.organization DROP CONSTRAINT organization_pkey;
ALTER TABLE ONLY public.item_version DROP CONSTRAINT item_version_pkey;
ALTER TABLE ONLY public.item_version_file DROP CONSTRAINT item_version_file_pkey;
ALTER TABLE ONLY public.item_object DROP CONSTRAINT item_object_pkey;
ALTER TABLE ONLY public.import_log DROP CONSTRAINT import_log_pkey;
ALTER TABLE ONLY public.import_log_item DROP CONSTRAINT import_log_item_pkey;
ALTER TABLE ONLY public.import_log_item_detail DROP CONSTRAINT import_log_item_detail_pkey;
ALTER TABLE ONLY public.id_provider DROP CONSTRAINT id_provider_pkey;
ALTER TABLE ONLY public.file DROP CONSTRAINT file_pkey;
ALTER TABLE ONLY public.context DROP CONSTRAINT context_pkey;
ALTER TABLE ONLY public.audit_log DROP CONSTRAINT audit_log_pkey;
DROP TABLE public.yearbook_item;
DROP TABLE public.yearbook;
DROP TABLE public.user_login;
DROP TABLE public.user_account;
DROP TABLE public.staged_file;
DROP TABLE public.organization_predecessor;
DROP TABLE public.organization;
DROP TABLE public.item_version_file;
DROP TABLE public.item_version;
DROP TABLE public.item_object;
DROP TABLE public.import_log_item_detail;
DROP TABLE public.import_log_item;
DROP TABLE public.import_log;
DROP SEQUENCE public.import_log_id_seq;
DROP TABLE public.id_provider;
DROP SEQUENCE public.hibernate_sequence;
DROP TABLE public.file;
DROP TABLE public.context_organization;
DROP TABLE public.context;
DROP TABLE public.audit_log;
DROP EXTENSION plpgsql;
DROP SCHEMA public;
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA public;


ALTER SCHEMA public OWNER TO postgres;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'standard public schema';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


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
-- Name: context; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context (
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
    description text,
    state character varying(255),
    workflow character varying(255)
);


ALTER TABLE context OWNER TO postgres;

--
-- Name: context_organization; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE context_organization (
    contextdbvo_objectid character varying(255) NOT NULL,
    responsibleaffiliations_objectid character varying(255) NOT NULL
);


ALTER TABLE context_organization OWNER TO postgres;

--
-- Name: file; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE file (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    checksum character varying(255),
    checksumalgorithm character varying(255),
    content character varying(255),
    localfileidentifier character varying(255),
    metadata jsonb,
    mimetype character varying(255),
    pid character varying(255),
    size bigint NOT NULL,
    storage character varying(255),
    visibility character varying(255),
    allowedaudienceids jsonb
);


ALTER TABLE file OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO postgres;

--
-- Name: id_provider; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE id_provider (
    type character varying(255) NOT NULL,
    current_id bigint
);


ALTER TABLE id_provider OWNER TO postgres;

--
-- Name: import_log_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE import_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 10;


ALTER TABLE import_log_id_seq OWNER TO postgres;

--
-- Name: import_log; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE import_log (
    id integer DEFAULT nextval('import_log_id_seq'::regclass) NOT NULL,
    status character varying NOT NULL,
    errorlevel character varying NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    userid character varying,
    name character varying,
    format character varying,
    context character varying,
    percentage integer
);


ALTER TABLE import_log OWNER TO postgres;

--
-- Name: import_log_item; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE import_log_item (
    id integer DEFAULT nextval('import_log_id_seq'::regclass) NOT NULL,
    status character varying NOT NULL,
    errorlevel character varying NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    parent integer NOT NULL,
    message character varying,
    item_id character varying
);


ALTER TABLE import_log_item OWNER TO postgres;

--
-- Name: import_log_item_detail; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE import_log_item_detail (
    id integer DEFAULT nextval('import_log_id_seq'::regclass) NOT NULL,
    status character varying NOT NULL,
    errorlevel character varying NOT NULL,
    startdate timestamp without time zone NOT NULL,
    parent integer NOT NULL,
    message character varying
);


ALTER TABLE import_log_item_detail OWNER TO postgres;

--
-- Name: item_object; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE item_object (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    localtags jsonb,
    objectpid character varying(255),
    publicstate character varying(255),
    context_objectid character varying(255),
    latestrelease_objectid character varying(255),
    latestrelease_versionnumber integer,
    latestversion_objectid character varying(255),
    latestversion_versionnumber integer
);


ALTER TABLE item_object OWNER TO postgres;

--
-- Name: item_version; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE item_version (
    objectid character varying(255) NOT NULL,
    versionnumber integer NOT NULL,
    modificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    versionpid character varying(255),
    versionstate character varying(255),
    message text,
    metadata jsonb
);


ALTER TABLE item_version OWNER TO postgres;

--
-- Name: item_version_file; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE item_version_file (
    itemversionvo_objectid character varying NOT NULL,
    itemversionvo_versionnumber integer NOT NULL,
    files_objectid character varying(255) NOT NULL,
    creationdate integer NOT NULL
);


ALTER TABLE item_version_file OWNER TO postgres;

--
-- Name: organization; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    metadata jsonb,
    publicstatus character varying(255),
    parentaffiliation_objectid character varying(255)
);


ALTER TABLE organization OWNER TO postgres;

--
-- Name: organization_predecessor; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE organization_predecessor (
    affiliationdbvo_objectid character varying(255) NOT NULL,
    predecessoraffiliations_objectid character varying(255) NOT NULL
);


ALTER TABLE organization_predecessor OWNER TO postgres;

--
-- Name: staged_file; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE staged_file (
    id integer NOT NULL,
    creationdate timestamp without time zone,
    creatorid character varying(255),
    filename character varying(255),
    path character varying(255)
);


ALTER TABLE staged_file OWNER TO postgres;

--
-- Name: user_account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE user_account (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    active boolean NOT NULL,
    email character varying(255),
    grantlist jsonb,
    loginname character varying(255),
    affiliation_objectid character varying(255)
);


ALTER TABLE user_account OWNER TO postgres;


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
-- Name: yearbook; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE yearbook (
    objectid character varying(255) NOT NULL,
    creationdate timestamp without time zone,
    creator_name character varying(255),
    creator_objectid character varying(255),
    lastmodificationdate timestamp without time zone,
    modifier_name character varying(255),
    modifier_objectid character varying(255),
    name text,
    contextids jsonb,
    state character varying(255),
    year integer NOT NULL,
    organization character varying(255)
);


ALTER TABLE yearbook OWNER TO postgres;

--
-- Name: yearbook_item; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE yearbook_item (
    yearbookdbvo_objectid character varying(255) NOT NULL,
    itemids character varying(255)
);


ALTER TABLE yearbook_item OWNER TO postgres;

--
-- Name: audit_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY audit_log
    ADD CONSTRAINT audit_log_pkey PRIMARY KEY (id);


--
-- Name: context_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY context
    ADD CONSTRAINT context_pkey PRIMARY KEY (objectid);


--
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (objectid);


--
-- Name: id_provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY id_provider
    ADD CONSTRAINT id_provider_pkey PRIMARY KEY (type);


--
-- Name: import_log_item_detail_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY import_log_item_detail
    ADD CONSTRAINT import_log_item_detail_pkey PRIMARY KEY (id);


--
-- Name: import_log_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY import_log_item
    ADD CONSTRAINT import_log_item_pkey PRIMARY KEY (id);


--
-- Name: import_log_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY import_log
    ADD CONSTRAINT import_log_pkey PRIMARY KEY (id);


--
-- Name: item_object_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT item_object_pkey PRIMARY KEY (objectid);


--
-- Name: item_version_file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT item_version_file_pkey PRIMARY KEY (itemversionvo_objectid, itemversionvo_versionnumber, creationdate);


--
-- Name: item_version_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY item_version
    ADD CONSTRAINT item_version_pkey PRIMARY KEY (objectid, versionnumber);


--
-- Name: organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT organization_pkey PRIMARY KEY (objectid);


--
-- Name: staged_file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY staged_file
    ADD CONSTRAINT staged_file_pkey PRIMARY KEY (id);


--
-- Name: uk_cnacf4jcff11b69f19b4uvpiv; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT uk_cnacf4jcff11b69f19b4uvpiv UNIQUE (loginname);


--
-- Name: uk_hl02wv5hym99ys465woijmfib; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT uk_hl02wv5hym99ys465woijmfib UNIQUE (email);


--
-- Name: uk_pjo26o2ngxbi23s0sw7nrnrsf; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT uk_pjo26o2ngxbi23s0sw7nrnrsf UNIQUE (loginname);


--
-- Name: ukfmcofdqp6ylrxi2ry431oq2ch; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY yearbook
    ADD CONSTRAINT ukfmcofdqp6ylrxi2ry431oq2ch UNIQUE (organization, year);


--
-- Name: user_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT user_account_pkey PRIMARY KEY (objectid);


--
-- Name: yearbook_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY yearbook
    ADD CONSTRAINT yearbook_pkey PRIMARY KEY (objectid);


--
-- Name: import_log_idx_userid; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX import_log_idx_userid ON import_log USING btree (userid);


--
-- Name: import_log_item_detail_idx_parent; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX import_log_item_detail_idx_parent ON import_log_item_detail USING btree (parent);


--
-- Name: import_log_item_idx_item_id; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX import_log_item_idx_item_id ON import_log_item USING btree (item_id);


--
-- Name: import_log_item_idx_parent; Type: INDEX; Schema: public; Owner: postgres; Tablespace: 
--

CREATE INDEX import_log_item_idx_parent ON import_log_item USING btree (parent);


--
-- Name: fk14k00cpi4gg01mtt8qxrgifoe; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT fk14k00cpi4gg01mtt8qxrgifoe FOREIGN KEY (files_objectid) REFERENCES file(objectid);


--
-- Name: fk18evwh3n73g7i7stv90nnn69c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fk18evwh3n73g7i7stv90nnn69c FOREIGN KEY (context_objectid) REFERENCES context(objectid);


--
-- Name: fk6olo333815g1k20qcf7kkrsfa; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fk6olo333815g1k20qcf7kkrsfa FOREIGN KEY (predecessoraffiliations_objectid) REFERENCES organization(objectid);


--
-- Name: fk751lpy01kv1wgjnwlgcgn9o74; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audit_log
    ADD CONSTRAINT fk751lpy01kv1wgjnwlgcgn9o74 FOREIGN KEY (pubitem_objectid, pubitem_versionnumber) REFERENCES item_version(objectid, versionnumber) ON DELETE CASCADE;


--
-- Name: fk9yvnsv4vysj35qwjmvxx7eala; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_account
    ADD CONSTRAINT fk9yvnsv4vysj35qwjmvxx7eala FOREIGN KEY (affiliation_objectid) REFERENCES organization(objectid);


--
-- Name: fkahefe4tpvp7uiqtp0pleuvv1k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT fkahefe4tpvp7uiqtp0pleuvv1k FOREIGN KEY (itemversionvo_objectid, itemversionvo_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- Name: fkbb84i4hokbu1mt95d0an92p8i; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY yearbook_item
    ADD CONSTRAINT fkbb84i4hokbu1mt95d0an92p8i FOREIGN KEY (yearbookdbvo_objectid) REFERENCES yearbook(objectid);


--
-- Name: fkblh9cvrc5ocgnfw6sav842qpu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fkblh9cvrc5ocgnfw6sav842qpu FOREIGN KEY (latestrelease_objectid, latestrelease_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- Name: fkfjpnwigunnp2grtn4bh2et98y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY yearbook
    ADD CONSTRAINT fkfjpnwigunnp2grtn4bh2et98y FOREIGN KEY (organization) REFERENCES organization(objectid);


--
-- Name: fkfwpsg5jl7ahtgc3sdfxc28w8q; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version_file
    ADD CONSTRAINT fkfwpsg5jl7ahtgc3sdfxc28w8q FOREIGN KEY (itemversionvo_objectid, itemversionvo_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- Name: fkl2u8rk7ip5ghjxb43l491ym6k; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization
    ADD CONSTRAINT fkl2u8rk7ip5ghjxb43l491ym6k FOREIGN KEY (parentaffiliation_objectid) REFERENCES organization(objectid);


--
-- Name: fkq0gb0ly9mgt3eajribctu1r2u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context_organization
    ADD CONSTRAINT fkq0gb0ly9mgt3eajribctu1r2u FOREIGN KEY (contextdbvo_objectid) REFERENCES context(objectid);


--
-- Name: fkq77txeng0o7k1d32pa2bk8upd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY context_organization
    ADD CONSTRAINT fkq77txeng0o7k1d32pa2bk8upd FOREIGN KEY (responsibleaffiliations_objectid) REFERENCES organization(objectid);


--
-- Name: fkr3jlfblcdwytk5p4bcbwlnltd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_version
    ADD CONSTRAINT fkr3jlfblcdwytk5p4bcbwlnltd FOREIGN KEY (objectid) REFERENCES item_object(objectid) ON DELETE CASCADE;


--
-- Name: fksmkncjbsor49iuvnkvgud3ms2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY organization_predecessor
    ADD CONSTRAINT fksmkncjbsor49iuvnkvgud3ms2 FOREIGN KEY (affiliationdbvo_objectid) REFERENCES organization(objectid);


--
-- Name: fktdnnngv88h3l913lylbcylyqf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY item_object
    ADD CONSTRAINT fktdnnngv88h3l913lylbcylyqf FOREIGN KEY (latestversion_objectid, latestversion_versionnumber) REFERENCES item_version(objectid, versionnumber);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

