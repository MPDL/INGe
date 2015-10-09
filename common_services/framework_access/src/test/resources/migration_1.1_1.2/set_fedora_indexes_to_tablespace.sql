--TO MAKE SURE ALL INDEXES ARE LISTED IN COMMANDS BELOW, YOU CAN RUN THE FOLLOWING SELECT ON FEDORA DATABASE 

/*
select 'alter index '||n.nspname||'.'||c.relname||' set tablespace tbl_fedora_index; '
from 
pg_catalog.pg_namespace n, pg_catalog.pg_index i, pg_catalog.pg_class c
where c.relnamespace=n.oid
and i.indexrelid=c.oid
and n.nspname NOT IN ('pg_catalog', 'pg_toast')
AND i.indisprimary=false
*/

alter index public.objectpaths_token_key set tablespace tbl_fedora_index; 
alter index public.datastreampaths_token_key set tablespace tbl_fedora_index; 
alter index public.dofields_pid set tablespace tbl_fedora_index; 
alter index public.dcdates_pid set tablespace tbl_fedora_index; 
