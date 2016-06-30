--TO MAKE SURE ALL INDEXES ARE LISTED IN COMMANDS BELOW, YOU CAN RUN THE FOLLOWING SELECT ON RiTriples DATABASE 

/*
select 'alter index '||n.nspname||'.'||c.relname||' set tablespace tbl_triples_index; '
from 
pg_catalog.pg_namespace n, pg_catalog.pg_index i, pg_catalog.pg_class c
where c.relnamespace=n.oid
and i.indexrelid=c.oid
and n.nspname NOT IN ('pg_catalog', 'pg_toast')
AND i.indisprimary=false
*/

/*

put the result of the above query in an .sql file and execute it.
triple tables are dependent on the data. This may change daily.


*/

alter index public.tmap_pkey set tablespace tbl_triples_index; 
alter index public.tmap_p set tablespace tbl_triples_index; 
alter index public.t1_s set tablespace tbl_triples_index; 
alter index public.t1_o set tablespace tbl_triples_index; 
alter index public.t2_s set tablespace tbl_triples_index; 
alter index public.t2_o set tablespace tbl_triples_index; 
alter index public.t3_s set tablespace tbl_triples_index; 
alter index public.t3_o set tablespace tbl_triples_index; 
alter index public.t4_s set tablespace tbl_triples_index; 
alter index public.t4_o set tablespace tbl_triples_index; 
alter index public.t5_s set tablespace tbl_triples_index; 
alter index public.t5_o set tablespace tbl_triples_index; 
alter index public.t6_s set tablespace tbl_triples_index; 
alter index public.t6_o set tablespace tbl_triples_index; 
alter index public.t7_s set tablespace tbl_triples_index; 
alter index public.t7_o set tablespace tbl_triples_index; 
alter index public.t8_s set tablespace tbl_triples_index; 
alter index public.t8_o set tablespace tbl_triples_index; 
alter index public.t9_s set tablespace tbl_triples_index; 
alter index public.t9_o set tablespace tbl_triples_index; 
alter index public.t10_s set tablespace tbl_triples_index; 
alter index public.t10_o set tablespace tbl_triples_index; 
alter index public.t11_s set tablespace tbl_triples_index; 
alter index public.t11_o set tablespace tbl_triples_index; 
alter index public.t12_s set tablespace tbl_triples_index; 
alter index public.t12_o set tablespace tbl_triples_index; 
alter index public.t13_s set tablespace tbl_triples_index; 
alter index public.t13_o set tablespace tbl_triples_index; 
alter index public.t14_s set tablespace tbl_triples_index; 
alter index public.t14_o set tablespace tbl_triples_index; 
alter index public.t15_s set tablespace tbl_triples_index; 
alter index public.t15_o set tablespace tbl_triples_index; 
alter index public.t16_s set tablespace tbl_triples_index; 
alter index public.t16_o set tablespace tbl_triples_index; 
alter index public.t17_s set tablespace tbl_triples_index; 
alter index public.t17_o set tablespace tbl_triples_index; 
alter index public.t18_s set tablespace tbl_triples_index; 
alter index public.t18_o set tablespace tbl_triples_index; 
alter index public.t19_s set tablespace tbl_triples_index; 
alter index public.t19_o set tablespace tbl_triples_index; 
alter index public.t20_s set tablespace tbl_triples_index; 
alter index public.t20_o set tablespace tbl_triples_index; 
alter index public.t21_s set tablespace tbl_triples_index; 
alter index public.t21_o set tablespace tbl_triples_index; 
alter index public.t22_s set tablespace tbl_triples_index; 
alter index public.t22_o set tablespace tbl_triples_index; 
alter index public.t23_s set tablespace tbl_triples_index; 
alter index public.t23_o set tablespace tbl_triples_index; 
alter index public.t24_s set tablespace tbl_triples_index; 
alter index public.t24_o set tablespace tbl_triples_index; 
alter index public.t25_s set tablespace tbl_triples_index; 
alter index public.t25_o set tablespace tbl_triples_index; 
alter index public.t26_s set tablespace tbl_triples_index; 
alter index public.t26_o set tablespace tbl_triples_index; 
alter index public.t27_s set tablespace tbl_triples_index; 
alter index public.t27_o set tablespace tbl_triples_index; 
alter index public.t28_s set tablespace tbl_triples_index; 
alter index public.t28_o set tablespace tbl_triples_index; 
alter index public.t29_s set tablespace tbl_triples_index; 
alter index public.t29_o set tablespace tbl_triples_index; 
alter index public.t30_s set tablespace tbl_triples_index; 
alter index public.t30_o set tablespace tbl_triples_index; 
alter index public.t31_s set tablespace tbl_triples_index; 
alter index public.t31_o set tablespace tbl_triples_index; 
alter index public.t32_s set tablespace tbl_triples_index; 
alter index public.t32_o set tablespace tbl_triples_index; 
alter index public.t33_s set tablespace tbl_triples_index; 
alter index public.t33_o set tablespace tbl_triples_index; 
alter index public.t34_s set tablespace tbl_triples_index; 
alter index public.t34_o set tablespace tbl_triples_index; 
alter index public.t35_s set tablespace tbl_triples_index; 
alter index public.t35_o set tablespace tbl_triples_index; 
alter index public.t36_s set tablespace tbl_triples_index; 
alter index public.t36_o set tablespace tbl_triples_index; 
alter index public.t37_s set tablespace tbl_triples_index; 
alter index public.t37_o set tablespace tbl_triples_index; 
alter index public.t38_s set tablespace tbl_triples_index; 
alter index public.t38_o set tablespace tbl_triples_index; 
alter index public.t39_s set tablespace tbl_triples_index; 
alter index public.t39_o set tablespace tbl_triples_index; 
alter index public.t40_s set tablespace tbl_triples_index; 
alter index public.t40_o set tablespace tbl_triples_index; 
alter index public.t41_s set tablespace tbl_triples_index; 
alter index public.t41_o set tablespace tbl_triples_index; 
alter index public.t42_s set tablespace tbl_triples_index; 
alter index public.t42_o set tablespace tbl_triples_index; 
alter index public.t43_s set tablespace tbl_triples_index; 
alter index public.t43_o set tablespace tbl_triples_index; 
alter index public.t44_s set tablespace tbl_triples_index; 
alter index public.t44_o set tablespace tbl_triples_index; 
alter index public.t45_s set tablespace tbl_triples_index; 
alter index public.t45_o set tablespace tbl_triples_index; 
alter index public.t46_s set tablespace tbl_triples_index; 
alter index public.t46_o set tablespace tbl_triples_index; 
alter index public.t47_s set tablespace tbl_triples_index; 
alter index public.t47_o set tablespace tbl_triples_index; 
alter index public.t48_s set tablespace tbl_triples_index; 
alter index public.t48_o set tablespace tbl_triples_index; 
alter index public.t49_s set tablespace tbl_triples_index; 
alter index public.t49_o set tablespace tbl_triples_index; 
alter index public.t50_s set tablespace tbl_triples_index; 
alter index public.t50_o set tablespace tbl_triples_index; 
alter index public.t51_s set tablespace tbl_triples_index; 
alter index public.t51_o set tablespace tbl_triples_index; 
alter index public.t52_s set tablespace tbl_triples_index; 
alter index public.t52_o set tablespace tbl_triples_index; 
alter index public.t53_s set tablespace tbl_triples_index; 
alter index public.t53_o set tablespace tbl_triples_index; 
alter index public.t54_s set tablespace tbl_triples_index; 
alter index public.t54_o set tablespace tbl_triples_index; 
alter index public.t55_s set tablespace tbl_triples_index; 
alter index public.t55_o set tablespace tbl_triples_index; 
alter index public.t56_s set tablespace tbl_triples_index; 
alter index public.t56_o set tablespace tbl_triples_index; 
alter index public.t57_s set tablespace tbl_triples_index; 
alter index public.t57_o set tablespace tbl_triples_index; 
