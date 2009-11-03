
SET client_encoding = 'UTF8';

SET default_tablespace = '';

--
-- TOC entry 1490 (class 1259 OID 29579)
-- Dependencies: 6
-- Name: matches; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--
DROP TABLE IF EXISTS matches CASCADE;

DROP TABLE IF EXISTS properties CASCADE;

DROP TABLE IF EXISTS results CASCADE;

DROP TABLE IF EXISTS triples CASCADE;

DROP VIEW IF EXISTS triples CASCADE;

CREATE TABLE matches (
    id character varying NOT NULL,
    value character varying NOT NULL,
    lang character varying,
    model character varying NOT NULL
);

--
-- TOC entry 1491 (class 1259 OID 29585)
-- Dependencies: 6
-- Name: properties; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--


CREATE TABLE properties (
    name character varying NOT NULL,
    value character varying
);

--
-- TOC entry 1492 (class 1259 OID 29591)
-- Dependencies: 6
-- Name: results; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--


CREATE TABLE results (
    id character varying NOT NULL,
    value character varying NOT NULL,
    lang character varying
);

--
--
-- TOC entry 1493 (class 1259 OID 29597)
-- Dependencies: 6
-- Name: triples; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--


CREATE TABLE triples (
    subject character varying,
    predicate character varying,
    object character varying,
    lang character varying,
    model character varying
);

--
-- TOC entry 1494 (class 1259 OID 29603)
-- Dependencies: 1570 6
-- Name: vw_search; Type: VIEW; Schema: public; Owner: postgres
--


CREATE VIEW vw_search AS
    SELECT triples.subject, triples.predicate, triples.object, triples.lang, triples.model FROM triples WHERE (((((((triples.predicate)::text = 'http://purl.org/dc/elements/1.1/identifier'::text) OR ((triples.predicate)::text = 'http://purl.org/dc/elements/1.1/title'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/terms/alternative'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/terms/identifier'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/terms/publisher'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/elements/1.1/publisher'::text));

--
-- TOC entry 1771 (class 0 OID 29585)
-- Dependencies: 1491
-- Data for Name: properties; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO properties (name, value) VALUES ('max_id', '1069');
INSERT INTO properties (name, value) VALUES ('initialize', 'true');
