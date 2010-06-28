--
-- PostgreSQL database dump
--

-- Started on 2010-06-28 14:34:20

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = aa, pg_catalog;

--
-- TOC entry 2013 (class 0 OID 138033)
-- Dependencies: 1677
-- Data for Name: scope_def; Type: TABLE DATA; Schema: aa; Owner: postgres
--

INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-administrator', 'escidoc:role-administrator', 'context', 'info:escidoc/names:aa:1.0:resource:context-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-administrator-2', 'escidoc:role-administrator', 'item', 'info:escidoc/names:aa:1.0:resource:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-administrator-3', 'escidoc:role-administrator', 'container', 'info:escidoc/names:aa:1.0:resource:container:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-administrator-5', 'escidoc:role-administrator', 'grant', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:rlc-scope-def-administrator-user-account', 'escidoc:role-administrator', 'user-account', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:rlc-scope-def-administrator-user-group', 'escidoc:role-administrator', 'user-group', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-audience', 'escidoc:role-audience', 'component', 'info:escidoc/names:aa:1.0:resource:component-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator', 'escidoc:role-collaborator', 'component', 'info:escidoc/names:aa:1.0:resource:component-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-2', 'escidoc:role-collaborator', 'component', 'info:escidoc/names:aa:1.0:resource:component:item');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-4', 'escidoc:role-collaborator', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-5', 'escidoc:role-collaborator', 'item', 'info:escidoc/names:aa:1.0:resource:item:component');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-6', 'escidoc:role-collaborator', 'item', 'info:escidoc/names:aa:1.0:resource:item-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-8', 'escidoc:role-collaborator', 'item', 'info:escidoc/names:aa:1.0:resource:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-9', 'escidoc:role-collaborator', 'container', 'info:escidoc/names:aa:1.0:resource:container-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-11', 'escidoc:role-collaborator', 'container', 'info:escidoc/names:aa:1.0:resource:container:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier', 'escidoc:role-collaborator-modifier', 'component', 'info:escidoc/names:aa:1.0:resource:component:item');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-2', 'escidoc:role-collaborator-modifier', 'item', 'info:escidoc/names:aa:1.0:resource:item:component');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-3', 'escidoc:role-collaborator-modifier', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-4', 'escidoc:role-collaborator-modifier', 'item', 'info:escidoc/names:aa:1.0:resource:item-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-6', 'escidoc:role-collaborator-modifier', 'item', 'info:escidoc/names:aa:1.0:resource:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-7', 'escidoc:role-collaborator-modifier', 'container', 'info:escidoc/names:aa:1.0:resource:container-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-9', 'escidoc:role-collaborator-modifier', 'container', 'info:escidoc/names:aa:1.0:resource:container:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-add-remove-any-members', 'escidoc:role-collaborator-modifier-container-add-remove-any-members', 'item', 'info:escidoc/names:aa:1.0:resource:item:hierarchical-containers');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-add-remove-any-members-2', 'escidoc:role-collaborator-modifier-container-add-remove-any-members', 'container', 'info:escidoc/names:aa:1.0:resource:container-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-add-remove-any-members-3', 'escidoc:role-collaborator-modifier-container-add-remove-any-members', 'container', 'info:escidoc/names:aa:1.0:resource:container:hierarchical-containers');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-add-remove-any-members-4', 'escidoc:role-collaborator-modifier-container-add-remove-any-members', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:hierarchical-containers');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-add-remove-members-1', 'escidoc:role-collaborator-modifier-container-add-remove-members', 'container', 'info:escidoc/names:aa:1.0:resource:container-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members', 'escidoc:role-collaborator-modifier-container-update-any-members', 'item', 'info:escidoc/names:aa:1.0:resource:item:hierarchical-containers');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members-2', 'escidoc:role-collaborator-modifier-container-update-any-members', 'container', 'info:escidoc/names:aa:1.0:resource:container-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members-3', 'escidoc:role-collaborator-modifier-container-update-any-members', 'container', 'info:escidoc/names:aa:1.0:resource:container:hierarchical-containers');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-any-members-4', 'escidoc:role-collaborator-modifier-container-update-any-members', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:hierarchical-containers');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members', 'escidoc:role-collaborator-modifier-container-update-direct-members', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:container');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members-4', 'escidoc:role-collaborator-modifier-container-update-direct-members', 'item', 'info:escidoc/names:aa:1.0:resource:item:container');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members-6', 'escidoc:role-collaborator-modifier-container-update-direct-members', 'container', 'info:escidoc/names:aa:1.0:resource:container-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-collaborator-modifier-container-update-direct-members-7', 'escidoc:role-collaborator-modifier-container-update-direct-members', 'container', 'info:escidoc/names:aa:1.0:resource:container:container');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-content-relation-modifier', 'escidoc:role-content-relation-modifier', 'content-relation', 'info:escidoc/names:aa:1.0:resource:content-relation-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-depositor', 'escidoc:role-depositor', 'context', 'info:escidoc/names:aa:1.0:resource:context-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-depositior-2', 'escidoc:role-depositor', 'item', 'info:escidoc/names:aa:1.0:resource:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-depositior-3', 'escidoc:role-depositor', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-depositior-4', 'escidoc:role-depositor', 'container', 'info:escidoc/names:aa:1.0:resource:container:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:rlc33', 'escidoc:role-depositor', 'staging-file', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-md-editor', 'escidoc:role-md-editor', 'context', 'info:escidoc/names:aa:1.0:resource:context-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-md-editor-2', 'escidoc:role-md-editor', 'item', 'info:escidoc/names:aa:1.0:resource:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-md-editor-3', 'escidoc:role-md-editor', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-md-editor-4', 'escidoc:role-md-editor', 'container', 'info:escidoc/names:aa:1.0:resource:container:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-moderator', 'escidoc:role-moderator', 'context', 'info:escidoc/names:aa:1.0:resource:context-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:rlc-scope-def-moderator-2', 'escidoc:role-moderator', 'item', 'info:escidoc/names:aa:1.0:resource:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:rlc-scope-def-moderator-3', 'escidoc:role-moderator', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:rlc-scope-def-moderator-4', 'escidoc:role-moderator', 'container', 'info:escidoc/names:aa:1.0:resource:container:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-moderator-5', 'escidoc:role-moderator', 'grant', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-moderator-6', 'escidoc:role-moderator', 'role', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-moderator-8', 'escidoc:role-moderator', 'user-group', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-privileged-viewer', 'escidoc:role-privileged-viewer', 'component', 'info:escidoc/names:aa:1.0:resource:component:item:context');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-statistics-editor', 'escidoc:role-statistics-editor', 'aggregation-definition', 'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-statistics-editor-2', 'escidoc:role-statistics-editor', 'report-definition', 'info:escidoc/names:aa:1.0:resource:report-definition:scope');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-statistics-editor-3', 'escidoc:role-statistics-editor', 'statistic-data', 'info:escidoc/names:aa:1.0:resource:statistic-data:scope');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-statistics-editor-4', 'escidoc:role-statistics-editor', 'scope', 'info:escidoc/names:aa:1.0:resource:scope-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-statistics-editor-5', 'escidoc:role-statistics-editor', 'preprocessing', 'info:escidoc/names:aa:1.0:resource:aggregation-definition:scope');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-statistics-reader', 'escidoc:role-statistics-reader', 'report', 'info:escidoc/names:aa:1.0:resource:report:scope');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-user-group-inspector', 'escidoc:role-user-group-inspector', 'user-group', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-user-account-inspector', 'escidoc:role-user-account-inspector', 'user-account', 'urn:oasis:names:tc:xacml:1.0:resource:resource-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-context-modifier', 'escidoc:role-context-modifier', 'context', 'info:escidoc/names:aa:1.0:resource:context-id');
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-context-modifier-6', 'escidoc:role-context-modifier', 'role', NULL);
INSERT INTO scope_def (id, role_id, object_type, attribute_id) VALUES ('escidoc:scope-def-role-context-modifier-5', 'escidoc:role-context-modifier', 'user-account', NULL);


-- Completed on 2010-06-28 14:34:22

--
-- PostgreSQL database dump complete
--

