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
    objectid character varying(255) NOT NULL
);


ALTER TABLE context OWNER TO postgres;

--
-- Data for Name: context; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO context (admindescriptor, description, state, type, objectid) VALUES ('{"workflow": "STANDARD", "contactEmail": "pubman-support@gwdg.de", "templateItem": {"objectId": "", "versionNumber": 0}, "allowedGenres": ["ARTICLE", "BOOK", "BOOK_ITEM", "PROCEEDINGS", "CONFERENCE_PAPER", "TALK_AT_EVENT", "CONFERENCE_REPORT", "POSTER", "COURSEWARE_LECTURE", "THESIS", "REPORT", "JOURNAL", "ISSUE", "SERIES", "OTHER", "EDITORIAL", "CONTRIBUTION_TO_HANDBOOK", "CONTRIBUTION_TO_FESTSCHRIFT", "CONTRIBUTION_TO_COMMENTARY", "CONTRIBUTION_TO_COLLECTED_EDITION", "BOOK_REVIEW", "CASE_STUDY", "CASE_NOTE", "ENCYCLOPEDIA", "COMMENTARY", "HANDBOOK", "COLLECTED_EDITION", "FESTSCHRIFT", "PATENT", "NEWSPAPER_ARTICLE", "PAPER", "MANUSCRIPT", "MANUAL", "OPINION", "MONOGRAPH", "NEWSPAPER", "MULTI_VOLUME", "MEETING_ABSTRACT", "FILM"], "validationSchema": "publication", "visibilityOfReferences": null, "allowedSubjectClassifications": ["DDC", "MPIPKS", "ISO639_3"]}', 'Sandbox collection for test purposes within the productive server.
	Please do not release any of the items stored in this context!', 'OPENED', 'PubMan', 'ctx_persistent3');
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

