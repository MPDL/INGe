-- Table: public.matches

-- DROP TABLE public.matches;

CREATE TABLE public.matches
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    value character varying COLLATE pg_catalog."default" NOT NULL,
    lang character varying COLLATE pg_catalog."default",
    model character varying COLLATE pg_catalog."default" NOT NULL
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.matches
    OWNER to inge_rw;
-- Index: pk_matches

-- DROP INDEX public.pk_matches;

CREATE UNIQUE INDEX pk_matches
    ON public.matches USING btree
    (id COLLATE pg_catalog."default" ASC NULLS LAST, value COLLATE pg_catalog."default" ASC NULLS LAST, lang COLLATE pg_catalog."default" ASC NULLS LAST, model COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
--------------------------------------------------------------------------------------------------------
-- Table: public.properties

-- DROP TABLE public.properties;

CREATE TABLE public.properties
(
    name character varying COLLATE pg_catalog."default" NOT NULL,
    value character varying COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.properties
    OWNER to inge_rw;
--------------------------------------------------------------------------------------------------------
-- Table: public.results

-- DROP TABLE public.results;

CREATE TABLE public.results
(
    id character varying COLLATE pg_catalog."default" NOT NULL,
    value character varying COLLATE pg_catalog."default" NOT NULL,
    lang character varying COLLATE pg_catalog."default",
    type character varying COLLATE pg_catalog."default",
    sort character varying COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.results
    OWNER to inge_rw;
-- Index: pk

-- DROP INDEX public.pk;

CREATE UNIQUE INDEX pk
    ON public.results USING btree
    (id COLLATE pg_catalog."default" ASC NULLS LAST, value COLLATE pg_catalog."default" ASC NULLS LAST, lang COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
--------------------------------------------------------------------------------------------------------
-- Table: public.triples

-- DROP TABLE public.triples;

CREATE TABLE public.triples
(
    subject character varying COLLATE pg_catalog."default",
    predicate character varying COLLATE pg_catalog."default",
    object character varying COLLATE pg_catalog."default",
    lang character varying COLLATE pg_catalog."default",
    model character varying COLLATE pg_catalog."default"
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.triples
    OWNER to inge_rw;
-- Index: ix_lang

-- DROP INDEX public.ix_lang;

CREATE INDEX ix_lang
    ON public.triples USING btree
    (lang COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: ix_model

-- DROP INDEX public.ix_model;

CREATE INDEX ix_model
    ON public.triples USING btree
    (model COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: ix_object

-- DROP INDEX public.ix_object;

CREATE INDEX ix_object
    ON public.triples USING btree
    ("left"(object::text, 600) COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: ix_predicate

-- DROP INDEX public.ix_predicate;

CREATE INDEX ix_predicate
    ON public.triples USING btree
    (predicate COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: ix_subject

-- DROP INDEX public.ix_subject;

CREATE INDEX ix_subject
    ON public.triples USING btree
    (subject COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: pk_all

-- DROP INDEX public.pk_all;

CREATE UNIQUE INDEX pk_all
    ON public.triples USING btree
    (model COLLATE pg_catalog."default" ASC NULLS LAST, subject COLLATE pg_catalog."default" ASC NULLS LAST, predicate COLLATE pg_catalog."default" ASC NULLS LAST, "left"(object::text, 600) COLLATE pg_catalog."default" ASC NULLS LAST, lang COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
