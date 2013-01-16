
SET client_encoding = 'UTF8';

SET default_tablespace = '';

DROP TABLE IF EXISTS matches CASCADE;

DROP TABLE IF EXISTS properties CASCADE;

DROP TABLE IF EXISTS results CASCADE;

DROP TABLE IF EXISTS triples CASCADE;

CREATE TABLE matches (
    id character varying NOT NULL,
    value character varying NOT NULL,
    lang character varying,
    model character varying NOT NULL
);


CREATE TABLE properties (
    name character varying NOT NULL,
    value character varying
);


CREATE TABLE results (
    id character varying NOT NULL,
    value character varying NOT NULL,
    lang character varying
);


CREATE TABLE triples (
    subject character varying,
    predicate character varying,
    object character varying,
    lang character varying,
    model character varying
);

CREATE VIEW vw_search AS
    SELECT triples.subject, triples.predicate, triples.object, triples.lang, triples.model FROM triples WHERE (((((((triples.predicate)::text = 'http://purl.org/dc/elements/1.1/identifier'::text) OR ((triples.predicate)::text = 'http://purl.org/dc/elements/1.1/title'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/terms/alternative'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/terms/identifier'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/terms/publisher'::text)) OR ((triples.predicate)::text = 'http://purl.org/dc/elements/1.1/publisher'::text));


INSERT INTO properties (name, value) VALUES ('max_id', '1');
INSERT INTO properties (name, value) VALUES ('initialize', 'true');
