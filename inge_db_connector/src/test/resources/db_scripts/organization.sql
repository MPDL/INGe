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
    parentaffiliation_objectid character varying(255)
);


ALTER TABLE organization OWNER TO postgres;

--
-- Data for Name: organization; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO organization (metadata, publicstatus, objectid, parentaffiliation_objectid) VALUES ('{"city": "Hofgartenstraße 8, 80539 Munich", "name": "Max Planck Society", "type": "institute", "startDate": "1948", "coordinates": {"altitude": 0.0, "latitude": 10.0, "longitude": 20.0}, "countryCode": "DE", "identifiers": [{"id": "http://www.mpg.de/"}], "descriptions": ["Die Max-Planck-Gesellschaft zur Förderung der Wissenschaften e.V. ist eine unabhängige Forschungsorganisation, die Forschung vorrangig in eigenen Instituten fördert. Sie wurde am 26. Februar 1948 gegründet – in Nachfolge der bereits 1911 errichteten Kaiser-Wilhelm-Gesellschaft zur Förderung der Wissenschaften. Die derzeit 80 Max-Planck-Institute betreiben Grundlagenforschung in den Natur-, Bio-, Geistes- und Sozialwissenschaften im Dienste der Allgemeinheit.", "The Max Planck Society for the Advancement of Science is an independent, non-profit research organization that primarily promotes and supports research at its own institutes. It was founded on February 26, 1948, and is the successor organization to the Kaiser Wilhelm Society, which was established in 1911. The currently 80 Max Planck Institutes conduct basic research in the service of the general public in the natural sciences, life sciences, social sciences, and the humanities."], "alternativeNames": ["Max-Planck-Gesellschaft zur Förderung der Wissenschaften e.V.", "MPS", "MPG"]}', 'OPENED', 'ou_persistent13', NULL);
INSERT INTO organization (metadata, publicstatus, objectid, parentaffiliation_objectid) VALUES ('{"city": "Amalienstr. 33, 80799 Munich", "name": "Max Planck Digital Library", "type": "institute", "startDate": "2007", "countryCode": "DE", "identifiers": [{"id": ""}, {"id": "http://www.mpdl.mpg.de/"}, {"id": ""}], "descriptions": ["", "The Max Planck Digital Library (MPDL) is a scientific service unit within the Max Planck Society, established in January 2007.\n\nThe MPDL provides services to help the MPS researchers manage their scientific information workflow. Such services comprise the provision of actual content and of technical solutions, but also the support to users by acting as a centre of competence and community facilitator in the domain of scientific information management.\n\nThis is achieved through close collaboration with the Max Planck Institutes and their libraries. The core activities of the MPDL lie in building up infrastructures and tools for publications and research data.\n\nA substantial task of the MPDL is to provide most effective access to scientific information and fostering the Open Access policy of the Max Planck Society.", "Die Max Planck Digital Library (MPDL) ist eine wissenschaftliche Serviceeinheit innerhalb der\nMax Planck Gesellschaft. Sie hat ihre Arbeit am 1. Januar 2007 aufgenommen.\n\nDie MPDL bietet den Forschern der Max-Planck-Gesellschaft Dienste an, die ihnen helfen, den wissenschaftlichen Informationsablauf zu organisieren. Diese Dienste beinhalten u.a. die Bereitstellung von Forschungsdaten und technischen Lösungen. Die MPDL unterstützt die Wissenschaftler als Kompetenzzentrum und Ratgeber im Bereich wissenschaftliches Informationsmanagement.\n\nEine wesentliche Aufgabe der MPDL ist es, einen optimalen Zugang zu wissenschaftlichen Informationen zu ermöglichen und die Max-Planck-Gesellschaft in ihrer Open Access Politik zu unterstützen.", ""], "alternativeNames": ["", "", "MPDL", "plemplempdl"]}', 'CLOSED', 'ou_persistent25', 'ou_persistent13');

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
-- PostgreSQL database dump complete
--

