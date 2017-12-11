-- Table: public.import_log

-- DROP TABLE public.import_log;

CREATE TABLE public.import_log
(
    id integer NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
    status character varying COLLATE pg_catalog."default" NOT NULL,
    errorlevel character varying COLLATE pg_catalog."default" NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    userid character varying COLLATE pg_catalog."default",
    name character varying COLLATE pg_catalog."default",
    format character varying COLLATE pg_catalog."default",
    context character varying COLLATE pg_catalog."default",
    percentage integer,
    CONSTRAINT import_log_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.import_log
    OWNER to postgres;

-- Index: import_log_idx_userid

-- DROP INDEX public.import_log_idx_userid;

CREATE INDEX import_log_idx_userid
    ON public.import_log USING btree
    (userid COLLATE pg_catalog."default")
    TABLESPACE pg_default;    
    
-- Table: public.import_log_item

-- DROP TABLE public.import_log_item;

CREATE TABLE public.import_log_item
(
    id integer NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
    status character varying COLLATE pg_catalog."default" NOT NULL,
    errorlevel character varying COLLATE pg_catalog."default" NOT NULL,
    startdate timestamp without time zone NOT NULL,
    enddate timestamp without time zone,
    parent integer NOT NULL,
    message character varying COLLATE pg_catalog."default",
    item_id character varying COLLATE pg_catalog."default",
    CONSTRAINT import_log_item_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.import_log_item
    OWNER to postgres;

-- Index: import_log_item_idx_item_id

-- DROP INDEX public.import_log_item_idx_item_id;

CREATE INDEX import_log_item_idx_item_id
    ON public.import_log_item USING btree
    (item_id COLLATE pg_catalog."default")
    TABLESPACE pg_default;

-- Index: import_log_item_idx_parent

-- DROP INDEX public.import_log_item_idx_parent;

CREATE INDEX import_log_item_idx_parent
    ON public.import_log_item USING btree
    (parent)
    TABLESPACE pg_default;
        
-- Table: public.import_log_item_detail

-- DROP TABLE public.import_log_item_detail;

CREATE TABLE public.import_log_item_detail
(
    id integer NOT NULL DEFAULT nextval('import_log_id_seq'::regclass),
    status character varying COLLATE pg_catalog."default" NOT NULL,
    errorlevel character varying COLLATE pg_catalog."default" NOT NULL,
    startdate timestamp without time zone NOT NULL,
    parent integer NOT NULL,
    message character varying COLLATE pg_catalog."default",
    CONSTRAINT import_log_item_detail_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.import_log_item_detail
    OWNER to postgres;

-- Index: import_log_item_detail_idx_parent

-- DROP INDEX public.import_log_item_detail_idx_parent;

CREATE INDEX import_log_item_detail_idx_parent
    ON public.import_log_item_detail USING btree
    (parent)
    TABLESPACE pg_default;
    