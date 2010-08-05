--NOTE: This script creates the list.property indexes 
--and runs vacuum analyze on list.property

CREATE INDEX cmodel_resourceid_value_index
  ON list.property
  USING btree
  (resource_id, value)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/content-model/id'::text;

-- Index: list.cmodel_value_resourceid_index

-- DROP INDEX list.cmodel_value_resourceid_index;

CREATE INDEX cmodel_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/content-model/id'::text;

-- Index: list.context_resourceid_value_index

-- DROP INDEX list.context_resourceid_value_index;

CREATE INDEX context_resourceid_value_index
  ON list.property
  USING btree
  (resource_id, value)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/context/id'::text;

-- Index: list.context_value_resourceid_index

-- DROP INDEX list.context_value_resourceid_index;

CREATE INDEX context_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/context/id'::text;

-- Index: list.createdby_resourceid_value_index

-- DROP INDEX list.createdby_resourceid_value_index;

CREATE INDEX createdby_resourceid_value_index
  ON list.property
  USING btree
  (resource_id, value)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/created-by/id'::text;

-- Index: list.createdby_value_resourceid_index

-- DROP INDEX list.createdby_value_resourceid_index;

CREATE INDEX createdby_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/created-by/id'::text;

-- Index: list.modifiedby_value_resourceid_index

-- DROP INDEX list.modifiedby_value_resourceid_index;

CREATE INDEX modifiedby_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/version/modified-by/id'::text;

-- Index: list.parents_path_value_resourceid_index

-- DROP INDEX list.parents_path_value_resourceid_index;

CREATE INDEX parents_path_value_resourceid_index
  ON list.property
  USING btree
  (local_path, value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/parents/parent/id'::text;

-- Index: list.propertyany_path_value_resourceid_index

-- DROP INDEX list.propertyany_path_value_resourceid_index;

CREATE INDEX propertyany_path_value_resourceid_index
  ON list.property
  USING btree
  (local_path, value, resource_id)
TABLESPACE tbl_escidoc_core_large_index
  WHERE local_path <> ALL (ARRAY['/parents/parent/id'::text, '/properties/context/id'::text, '/properties/content-model/id'::text, '/properties/public-status'::text, '/properties/version/status'::text, '/struct-map/item/id'::text, '/struct-map/container/id'::text, '/properties/created-by/id'::text, '/id'::text, '/properties/version/modified-by/id'::text]);

-- Index: list.propertyid_path_value_resourceid_index

-- DROP INDEX list.propertyid_path_value_resourceid_index;

CREATE INDEX propertyid_path_value_resourceid_index
  ON list.property
  USING btree
  (local_path, value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/id'::text;

-- Index: list.publicstatus_createdopenedclosed_path_value_resourceid_index

-- DROP INDEX list.publicstatus_createdopenedclosed_path_value_resourceid_index;

CREATE INDEX publicstatus_createdopenedclosed_path_value_resourceid_index
  ON list.property
  USING btree
  (local_path, value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/public-status'::text AND (value::text = ANY (ARRAY['created'::text, 'opened'::text, 'closed'::text]));

-- Index: list.publicstatus_createdopenedclosed_resourceid_value_index

-- DROP INDEX list.publicstatus_createdopenedclosed_resourceid_value_index;

CREATE INDEX publicstatus_createdopenedclosed_resourceid_value_index
  ON list.property
  USING btree
  (resource_id, value)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/public-status'::text AND (value::text = ANY (ARRAY['created'::text, 'opened'::text, 'closed'::text]));

-- Index: list.publicstatus_notreleased_resourceid_value_index

-- DROP INDEX list.publicstatus_notreleased_resourceid_value_index;

CREATE INDEX publicstatus_notreleased_resourceid_value_index
  ON list.property
  USING btree
  (resource_id, value)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/public-status'::text AND (value::text = ANY (ARRAY['pending'::text, 'withdrawn'::text, 'submitted'::text, 'in-revision'::text]));

-- Index: list.publicstatus_notreleased_value_resourceid_index

-- DROP INDEX list.publicstatus_notreleased_value_resourceid_index;

CREATE INDEX publicstatus_notreleased_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/public-status'::text AND (value::text = ANY (ARRAY['pending'::text, 'submitted'::text, 'withdrawn'::text, 'in-revision'::text]));

-- Index: list.publicstatus_released_resourceid_value_index

-- DROP INDEX list.publicstatus_released_resourceid_value_index;

CREATE INDEX publicstatus_released_resourceid_value_index
  ON list.property
  USING btree
  (resource_id, value)
TABLESPACE tbl_escidoc_core_large_index
  WHERE local_path = '/properties/public-status'::text AND value::text = 'released'::text;

-- Index: list.publicstatus_released_value_resourceid_index

-- DROP INDEX list.publicstatus_released_value_resourceid_index;

CREATE INDEX publicstatus_released_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_large_index
  WHERE local_path = '/properties/public-status'::text AND value::text = 'released'::text;

-- Index: list.sorting_resourceid_path_position_index

-- DROP INDEX list.sorting_resourceid_path_position_index;

CREATE INDEX sorting_resourceid_path_position_index
  ON list.property
  USING btree
  (resource_id, local_path, "position")
TABLESPACE tbl_escidoc_core_large_index;

-- Index: list.structmapcontainer_path_resourceid_index

-- DROP INDEX list.structmapcontainer_path_resourceid_index;

CREATE INDEX structmapcontainer_path_resourceid_index
  ON list.property
  USING btree
  (local_path, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/struct-map/container/id'::text;

-- Index: list.structmapitem_path_resourceid_index

-- DROP INDEX list.structmapitem_path_resourceid_index;

CREATE INDEX structmapitem_path_resourceid_index
  ON list.property
  USING btree
  (local_path, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/struct-map/item/id'::text;

-- Index: list.versionstatus_notreleased_value_resourceid_index

-- DROP INDEX list.versionstatus_notreleased_value_resourceid_index;

CREATE INDEX versionstatus_notreleased_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_normal_index
  WHERE local_path = '/properties/version/status'::text AND (value::text = ANY (ARRAY['pending'::text, 'submitted'::text, 'in-revision'::text]));

-- Index: list.versionstatus_released_value_resourceid_index

-- DROP INDEX list.versionstatus_released_value_resourceid_index;

CREATE INDEX versionstatus_released_value_resourceid_index
  ON list.property
  USING btree
  (value, resource_id)
TABLESPACE tbl_escidoc_core_large_index
  WHERE local_path = '/properties/version/status'::text AND value::text = 'released'::text;

vacuum analyze list.property;
ALTER TABLE list.property ALTER COLUMN resource_id SET STATISTICS 1000;
ALTER TABLE list.property ALTER COLUMN local_path SET STATISTICS 1000;
ALTER TABLE list.property ALTER COLUMN "value" SET STATISTICS 1000;
ALTER TABLE list.property ALTER COLUMN "position" SET STATISTICS 1000;
analyze list.property;