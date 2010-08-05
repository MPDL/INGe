--NOTE: THIS SCRIPT MUST BE RUN AFTER MIGRATION AND BEFORE RECACHING
drop index list.id_path_position;
DROP INDEX list.local_path_public_status_value_index;
DROP INDEX list.local_path_version_status_value_index;
DROP INDEX list.path_contentmodeltitle_value_index;
DROP INDEX list.path_contextid_value_index;
DROP INDEX list.path_createdby_value_index;
DROP INDEX list.path_id_position;
DROP INDEX list.path_parents_value_index;
DROP INDEX list.path_structmap_container_index;
DROP INDEX list.path_structmap_index;

--AFTER THESE INDEXES ARE DROPPED CHECK IF THERE IS SOME INDEX LEFT; DROP IT MANUALLY IF NEEDED
vacuum analyze list.property;

CREATE INDEX group_role_date_object_role_grant_idx
  ON aa.role_grant
  USING btree
  (group_id, role_id, revocation_date, object_id)
TABLESPACE tbl_escidoc_core_normal_index;

CREATE INDEX object_group_role_grant_idx
  ON aa.role_grant
  USING btree
  (object_id, group_id, role_id)
TABLESPACE tbl_escidoc_core_normal_index;

CREATE INDEX object_user_role_grant_idx
  ON aa.role_grant
  USING btree
  (object_id, user_id, role_id)
TABLESPACE tbl_escidoc_core_normal_index;

CREATE INDEX user_role_date_object_role_grant_idx
  ON aa.role_grant
  USING btree
  (user_id, role_id, revocation_date, object_id)
TABLESPACE tbl_escidoc_core_normal_index;

vacuum analyze aa.role_grant;

CREATE INDEX role_type_index
  ON list.filter
  USING btree
  (role_id, type)
TABLESPACE tbl_escidoc_core_normal_index;

CREATE INDEX type_role_index
  ON list.filter
  USING btree
  (type, role_id)
TABLESPACE tbl_escidoc_core_normal_index;
vacuum analyze list.filter;


