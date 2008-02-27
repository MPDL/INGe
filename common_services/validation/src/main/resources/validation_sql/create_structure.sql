
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
CREATE TABLE escidoc_validation_schema_SNIPPETS (
	id_context_ref VARCHAR(255),
	id_content_type_ref VARCHAR(255),
    	id_validation_point VARCHAR(255),
	id_metadata_version_ref VARCHAR(255),
    	snippet_content LONGVARCHAR
);

ALTER TABLE escidoc_validation_schema_SNIPPETS
    ADD CONSTRAINT pk_escidoc_validation_schema_snippets PRIMARY KEY (id_context_ref, id_content_type_ref, id_validation_point);

CREATE TABLE escidoc_validation_schema (
    id_content_type_ref VARCHAR(255) NOT NULL,
    id_context_ref VARCHAR(255) NOT NULL,
	context_name VARCHAR(255),
    id_metadata_version_ref VARCHAR(255),
    creator_ref VARCHAR(255),
    date_created date,
    date_last_modified date,
    date_last_refreshed date,
    schema_content LONGVARCHAR,
    current_version VARCHAR(255)
);

ALTER TABLE escidoc_validation_schema
    ADD CONSTRAINT pk_escidoc_validation_schema PRIMARY KEY (id_context_ref, id_content_type_ref);
