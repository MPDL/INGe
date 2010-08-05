--TO MAKE SURE ALL TABLES ARE LISTED IN COMMANDS BELOW, YOU CAN RUN THE FOLLOWING SELECT ON escidoc-coreDATABASE 
--NOTE: CHECK IF DATABASE NAME IS "escidoc-core" OTHERWISE CHANGE THE table_catalog='escidoc-core' criteria

/*
select 'alter table '||table_schema||'.'||table_name||' set tablespace tbl_escidoc_core_data; ' 
from information_schema.tables 
where table_catalog='escidoc-core' and table_schema not in ('pg_catalog', 'information_schema') and table_type='BASE TABLE'
order by table_schema, table_name
*/


alter table aa.actions set tablespace tbl_escidoc_core_data; 
alter table aa.escidoc_policies set tablespace tbl_escidoc_core_data; 
alter table aa.escidoc_role set tablespace tbl_escidoc_core_data; 
alter table aa.invocation_mappings set tablespace tbl_escidoc_core_data; 
alter table aa.method_mappings set tablespace tbl_escidoc_core_data; 
alter table aa.role_grant set tablespace tbl_escidoc_core_data; 
alter table aa.scope_def set tablespace tbl_escidoc_core_data; 
alter table aa.unsecured_action_list set tablespace tbl_escidoc_core_data; 
alter table aa.user_account set tablespace tbl_escidoc_core_data; 
alter table aa.user_attribute set tablespace tbl_escidoc_core_data; 
alter table aa.user_group set tablespace tbl_escidoc_core_data; 
alter table aa.user_group_member set tablespace tbl_escidoc_core_data; 
alter table aa.user_login_data set tablespace tbl_escidoc_core_data; 
alter table aa.user_preference set tablespace tbl_escidoc_core_data; 
alter table adm.version set tablespace tbl_escidoc_core_data; 
alter table jbpm.escidoc_startactors set tablespace tbl_escidoc_core_data; 
alter table jbpm.escidoc_workflowdefinitions set tablespace tbl_escidoc_core_data; 
alter table jbpm.escidoc_workflowtemplates set tablespace tbl_escidoc_core_data; 
alter table jbpm.escidoc_workflowtypes set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_action set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_bytearray set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_byteblock set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_comment set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_decisionconditions set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_delegation set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_event set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_exceptionhandler set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_id_group set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_id_membership set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_id_permissions set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_id_user set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_job set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_log set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_moduledefinition set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_moduleinstance set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_node set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_pooledactor set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_processdefinition set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_processinstance set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_runtimeaction set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_swimlane set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_swimlaneinstance set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_task set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_taskactorpool set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_taskcontroller set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_taskinstance set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_token set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_tokenvariablemap set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_transition set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_variableaccess set tablespace tbl_escidoc_core_data; 
alter table jbpm.jbpm_variableinstance set tablespace tbl_escidoc_core_data; 
alter table list.container set tablespace tbl_escidoc_core_data; 
alter table list.content_relation set tablespace tbl_escidoc_core_data; 
alter table list.context set tablespace tbl_escidoc_core_data; 
alter table list.filter set tablespace tbl_escidoc_core_data; 
alter table list.item set tablespace tbl_escidoc_core_data; 
alter table list.ou set tablespace tbl_escidoc_core_data; 
alter table list.property set tablespace tbl_escidoc_core_data; 
alter table oai.set_definition set tablespace tbl_escidoc_core_data; 
alter table om.lockstatus set tablespace tbl_escidoc_core_data; 
alter table public.scope_def_after_migration set tablespace tbl_escidoc_core_data; 
alter table sm.aggregation_definitions set tablespace tbl_escidoc_core_data; 
alter table sm._escidocaggdef1_object_statistics set tablespace tbl_escidoc_core_statistics; 
alter table sm._escidocaggdef1_request_statistics set tablespace tbl_escidoc_core_statistics; 
alter table sm._escidocaggdef2_error_statistics set tablespace tbl_escidoc_core_statistics; 
alter table sm.preprocessing_logs set tablespace tbl_escidoc_core_data; 
alter table sm.report_definitions set tablespace tbl_escidoc_core_data; 
alter table sm.scopes set tablespace tbl_escidoc_core_data; 
alter table sm.statistic_data set tablespace tbl_tbl_escidoc_core_statistics; 
alter table st.staging_file set tablespace tbl_escidoc_core_data; 
