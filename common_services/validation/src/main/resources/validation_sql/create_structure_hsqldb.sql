CREATE TABLE VALIDATION_SCHEMA_SNIPPETS (
	id_context_ref VARCHAR(255),
	id_content_type_ref VARCHAR(255),
    	id_validation_point VARCHAR(255),
	id_metadata_version_ref VARCHAR(255),
    	snippet_content LONGVARCHAR
);

ALTER TABLE VALIDATION_SCHEMA_SNIPPETS
    ADD CONSTRAINT pk_validation_schema_snippets PRIMARY KEY (id_context_ref, id_content_type_ref, id_validation_point);

CREATE TABLE VALIDATION_SCHEMA (
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

ALTER TABLE VALIDATION_SCHEMA
    ADD CONSTRAINT pk_validation_schema PRIMARY KEY (id_context_ref, id_content_type_ref);
