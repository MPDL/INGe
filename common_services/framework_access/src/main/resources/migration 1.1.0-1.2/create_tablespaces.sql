--creation of the tablespaces
CREATE TABLESPACE tbl_fedora_data
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/tables/fedora';

CREATE TABLESPACE tbl_fedora_index
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/indexes/fedora';

CREATE TABLESPACE tbl_triples_data
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/tables/triples';

CREATE TABLESPACE tbl_triples_index
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/indexes/triples';

CREATE TABLESPACE tbl_escidoc_core_data
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/tables/escidoc-core';

CREATE TABLESPACE tbl_escidoc_core_large_index
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/indexes/escidoc-core-large';

CREATE TABLESPACE tbl_escidoc_core_normal_index
  OWNER postgres
  LOCATION '/var/lib/pgsql/data/indexes/escidoc-core-normal';

