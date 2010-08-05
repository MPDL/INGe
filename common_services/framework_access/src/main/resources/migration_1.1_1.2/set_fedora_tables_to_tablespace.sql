--TO MAKE SURE ALL TABLES ARE LISTED IN COMMANDS BELOW, YOU CAN RUN THE FOLLOWING SELECT ON FEDORA DATABASE 
--NOTE: CHECK IF DATABASE NAME IS "fedora3" OTHERWISE CHANGE THE table_catalog='fedora3' criteria
/*
select 'alter table '||table_schema||'.'||table_name||' set tablespace tbl_fedora_data; ' 
from information_schema.tables 
where table_catalog='fedora3' and  table_schema not in ('pg_catalog', 'information_schema') and table_type='BASE TABLE'
order by table_schema, table_name
*/

alter table public.datastreampaths set tablespace tbl_fedora_data; 
alter table public.dcdates set tablespace tbl_fedora_data; 
alter table public.dofields set tablespace tbl_fedora_data; 
alter table public.doregistry set tablespace tbl_fedora_data; 
alter table public.modeldeploymentmap set tablespace tbl_fedora_data; 
alter table public.objectpaths set tablespace tbl_fedora_data; 
alter table public.pidgen set tablespace tbl_fedora_data; 

