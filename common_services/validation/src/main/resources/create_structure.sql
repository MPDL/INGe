
-- CDDL HEADER START

-- The contents of this file are subject to the terms of the
-- Common Development and Distribution License, Version 1.0 only
-- (the "License"). You may not use this file except in compliance
-- with the License.

-- You can obtain a copy of the license at license/ESCIDOC.LICENSE
-- or http://www.escidoc.de/license.
-- See the License for the specific language governing permissions
-- and limitations under the License.

-- When distributing Covered Code, include this CDDL HEADER in each
-- file and include the License file at license/ESCIDOC.LICENSE.
-- If applicable, add the following below this CDDL HEADER, with the
-- fields enclosed by brackets "[]" replaced with your own identifying
-- information: Portions Copyright [yyyy] [name of copyright owner]

-- CDDL HEADER END


-- Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
-- für wissenschaftlich-technische Information mbH and Max-Planck-
-- Gesellschaft zur Förderung der Wissenschaft e.V.
-- All rights reserved. Use is subject to license terms.

--
-- PostgreSQL database dump
--

-- TOC entry 1633 (class 1262 OID 16831)
-- Name: validation; Type: DATABASE; Schema: -; Owner: validator
--

-- SET client_encoding = 'UTF8';
-- SET standard_conforming_strings = off;
-- SET check_function_bodies = false;
-- SET client_min_messages = warning;
-- SET escape_string_warning = off;

--
-- TOC entry 1288 (class 1259 OID 16877)
-- Dependencies: 1623 4
-- Name: escidoc_validation_schema_snippets; Type: TABLE; Schema: public; Owner: validator; Tablespace: 
--

CREATE TABLE escidoc_validation_schema_snippets (
	id_context_ref character varying(255),
	id_content_type_ref character varying(255),
    id_validation_point character varying(255),
	id_metadata_version_ref character varying(255),
    snippet_content text
);


ALTER TABLE public.escidoc_validation_schema_snippets OWNER TO "validator";


ALTER TABLE ONLY "escidoc_validation_schema_snippets"
    ADD CONSTRAINT pk_escidoc_validation_schema_snippets PRIMARY KEY (id_context_ref, id_content_type_ref, id_validation_point);

--
-- TOC entry 1287 (class 1259 OID 16832)
-- Dependencies: 1622 4
-- Name: escidoc_validation_schema; Type: TABLE; Schema: public; Owner: validator; Tablespace: 
--

CREATE TABLE "escidoc_validation_schema" (
    id_content_type_ref character varying(255) NOT NULL,
    id_context_ref character varying(255) NOT NULL,
    context_name character varying(255),
    id_metadata_version_ref character varying(255),
    creator_ref character varying(255),
    date_created date,
    date_last_modified date,
    date_last_refreshed date,
    schema_content text,
    current_version character varying(255)
);


ALTER TABLE public."escidoc_validation_schema" OWNER TO "validator";

--
-- TOC entry 1625 (class 2606 OID 16944)
-- Dependencies: 1287 1287
-- Name: pk_escidoc_validation_schema; Type: CONSTRAINT; Schema: public; Owner: validator; Tablespace: 
--

ALTER TABLE ONLY "escidoc_validation_schema"
    ADD CONSTRAINT pk_escidoc_validation_schema PRIMARY KEY (id_context_ref, id_content_type_ref);


--
-- TOC entry 1635 (class 0 OID 0)
-- Dependencies: 4
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;