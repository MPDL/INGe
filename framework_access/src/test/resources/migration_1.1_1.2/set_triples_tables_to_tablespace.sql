--TO MAKE SURE ALL TABLES ARE LISTED IN COMMANDS BELOW, YOU CAN RUN THE FOLLOWING SELECT ON FEDORA DATABASE 
--NOTE: CHECK IF DATABASE NAME IS "riTriples" OTHERWISE CHANGE THE table_catalog='riTriples' criteria
--to make sure all tables are included, run the SELECT query below, save the result into a .sql and run that as a script.
--triple tables are dependent on the data. This may change daily.

/*
select 'alter table '||table_schema||'.'||table_name||' set tablespace tbl_triples_data; ' 
from information_schema.tables 
where table_catalog='riTriples' and  table_schema not in ('pg_catalog', 'information_schema') and table_type='BASE TABLE'
order by table_schema, table_name
*/


alter table public.t1 set tablespace tbl_triples_data; 
alter table public.t10 set tablespace tbl_triples_data; 
alter table public.t11 set tablespace tbl_triples_data; 
alter table public.t12 set tablespace tbl_triples_data; 
alter table public.t13 set tablespace tbl_triples_data; 
alter table public.t14 set tablespace tbl_triples_data; 
alter table public.t15 set tablespace tbl_triples_data; 
alter table public.t16 set tablespace tbl_triples_data; 
alter table public.t17 set tablespace tbl_triples_data; 
alter table public.t18 set tablespace tbl_triples_data; 
alter table public.t19 set tablespace tbl_triples_data; 
alter table public.t2 set tablespace tbl_triples_data; 
alter table public.t20 set tablespace tbl_triples_data; 
alter table public.t21 set tablespace tbl_triples_data; 
alter table public.t22 set tablespace tbl_triples_data; 
alter table public.t23 set tablespace tbl_triples_data; 
alter table public.t24 set tablespace tbl_triples_data; 
alter table public.t25 set tablespace tbl_triples_data; 
alter table public.t26 set tablespace tbl_triples_data; 
alter table public.t27 set tablespace tbl_triples_data; 
alter table public.t28 set tablespace tbl_triples_data; 
alter table public.t29 set tablespace tbl_triples_data; 
alter table public.t3 set tablespace tbl_triples_data; 
alter table public.t30 set tablespace tbl_triples_data; 
alter table public.t31 set tablespace tbl_triples_data; 
alter table public.t32 set tablespace tbl_triples_data; 
alter table public.t33 set tablespace tbl_triples_data; 
alter table public.t34 set tablespace tbl_triples_data; 
alter table public.t35 set tablespace tbl_triples_data; 
alter table public.t36 set tablespace tbl_triples_data; 
alter table public.t37 set tablespace tbl_triples_data; 
alter table public.t38 set tablespace tbl_triples_data; 
alter table public.t39 set tablespace tbl_triples_data; 
alter table public.t4 set tablespace tbl_triples_data; 
alter table public.t40 set tablespace tbl_triples_data; 
alter table public.t41 set tablespace tbl_triples_data; 
alter table public.t42 set tablespace tbl_triples_data; 
alter table public.t43 set tablespace tbl_triples_data; 
alter table public.t44 set tablespace tbl_triples_data; 
alter table public.t45 set tablespace tbl_triples_data; 
alter table public.t46 set tablespace tbl_triples_data; 
alter table public.t47 set tablespace tbl_triples_data; 
alter table public.t48 set tablespace tbl_triples_data; 
alter table public.t49 set tablespace tbl_triples_data; 
alter table public.t5 set tablespace tbl_triples_data; 
alter table public.t50 set tablespace tbl_triples_data; 
alter table public.t51 set tablespace tbl_triples_data; 
alter table public.t52 set tablespace tbl_triples_data; 
alter table public.t53 set tablespace tbl_triples_data; 
alter table public.t54 set tablespace tbl_triples_data; 
alter table public.t55 set tablespace tbl_triples_data; 
alter table public.t56 set tablespace tbl_triples_data; 
alter table public.t57 set tablespace tbl_triples_data; 
alter table public.t6 set tablespace tbl_triples_data; 
alter table public.t7 set tablespace tbl_triples_data; 
alter table public.t8 set tablespace tbl_triples_data; 
alter table public.t9 set tablespace tbl_triples_data; 
alter table public.tmap set tablespace tbl_triples_data; 


