--
-- PostgreSQL database dump
--

-- Started on 2010-06-28 14:06:42

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = aa, pg_catalog;

--
-- TOC entry 2016 (class 0 OID 138013)
-- Dependencies: 1676
-- Data for Name: escidoc_role; Type: TABLE DATA; Schema: aa; Owner: postgres
--

INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-cone-closed-vocabulary-editor', 'CoNE-Closed-Vocabulary-Editor', NULL, 'escidoc:user42', '2010-02-22 13:24:00', 'escidoc:user42', '2010-02-22 13:24:00');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-administrator', 'Administrator', NULL, 'escidoc:user42', '2010-05-28 15:04:28.883171', 'escidoc:user42', '2010-05-28 15:04:28.883171');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-audience', 'Audience', NULL, 'escidoc:user42', '2010-05-28 15:05:03.424238', 'escidoc:user42', '2010-05-28 15:05:03.424238');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-collaborator', 'Collaborator', NULL, 'escidoc:user42', '2010-05-28 15:05:32.8208', 'escidoc:user42', '2010-05-28 15:05:32.8208');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-collaborator-modifier', 'Collaborator-Modifier', NULL, 'escidoc:user42', '2010-05-28 15:05:59.008983', 'escidoc:user42', '2010-05-28 15:05:59.008983');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-collaborator-modifier-container-add-remove-any-members', 'Collaborator-Modifier-Container-Add-Remove-any-Members', NULL, 'escidoc:user42', '2010-05-28 15:06:39.13347', 'escidoc:user42', '2010-05-28 15:06:39.13347');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-collaborator-modifier-container-add-remove-members', 'Collaborator-Modifier-Container-Add-Remove-Members', NULL, 'escidoc:user42', '2010-05-28 15:07:03.960907', 'escidoc:user42', '2010-05-28 15:07:03.960907');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-collaborator-modifier-container-update-any-members', 'Collaborator-Modifier-Container-Update-any-Members', NULL, 'escidoc:user42', '2010-05-28 15:07:28.044128', 'escidoc:user42', '2010-05-28 15:07:28.044128');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-collaborator-modifier-container-update-direct-members', 'Collaborator-Modifier-Container-update-direct-members', NULL, 'escidoc:user42', '2010-05-28 15:07:51.072663', 'escidoc:user42', '2010-05-28 15:07:51.072663');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-content-relation-manager', 'ContentRelationManager', NULL, 'escidoc:user42', '2010-05-28 15:08:38.963827', 'escidoc:user42', '2010-05-28 15:08:38.963827');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-content-relation-modifier', 'ContentRelationModifier', NULL, 'escidoc:user42', '2010-05-28 15:09:03.244976', 'escidoc:user42', '2010-05-28 15:09:03.244976');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-default-user', 'Default-User', NULL, 'escidoc:user42', '2010-05-28 15:09:33.242687', 'escidoc:user42', '2010-05-28 15:09:33.242687');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-depositor', 'Depositor', NULL, 'escidoc:user42', '2010-05-28 15:10:07.526858', 'escidoc:user42', '2010-05-28 15:10:07.526858');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-ingester', 'Ingester', NULL, 'escidoc:user42', '2010-05-28 15:10:31.112673', 'escidoc:user42', '2010-05-28 15:10:31.112673');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-md-editor', 'MD-Editor', NULL, 'escidoc:user42', '2010-05-28 15:10:52.412451', 'escidoc:user42', '2010-05-28 15:10:52.412451');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-moderator', 'Moderator', NULL, 'escidoc:user42', '2010-05-28 15:11:35.260313', 'escidoc:user42', '2010-05-28 15:11:35.260313');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-privileged-viewer', 'Privileged-Viewer', NULL, 'escidoc:user42', '2010-05-28 15:11:58.749213', 'escidoc:user42', '2010-05-28 15:11:58.749213');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-statistics-editor', 'Statistics-Editor', NULL, 'escidoc:user42', '2010-05-28 15:12:19.328722', 'escidoc:user42', '2010-05-28 15:12:19.328722');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-statistics-reader', 'Statistics-Reader', NULL, 'escidoc:user42', '2010-05-28 15:12:40.217108', 'escidoc:user42', '2010-05-28 15:12:40.217108');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-system-administrator', 'System-Administrator', NULL, 'escidoc:user42', '2010-05-28 15:13:15.487705', 'escidoc:user42', '2010-05-28 15:13:15.487705');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-system-inspector', 'System-Inspector', NULL, 'escidoc:user42', '2010-05-28 15:13:36.374049', 'escidoc:user42', '2010-05-28 15:13:36.374049');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-user-group-administrator', 'User-Group-Administrator', NULL, 'escidoc:user42', '2010-05-28 15:14:01.594511', 'escidoc:user42', '2010-05-28 15:14:01.594511');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-user-group-inspector', 'User-Group-Inspector', NULL, 'escidoc:user42', '2010-05-28 15:14:25.969563', 'escidoc:user42', '2010-05-28 15:14:25.969563');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-cone-open-vocabulary-editor', 'CoNE-Open-Vocabulary-Editor', NULL, 'escidoc:user42', '2010-02-22 13:23:00', 'escidoc:user42', '2010-02-22 13:23:00');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-context-administrator', 'Context-Administrator', NULL, 'escidoc:exuser1', '2010-06-07 17:18:04.521191', 'escidoc:exuser1', '2010-06-07 17:18:04.521191');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-user-account-administrator', 'User-Account-Administrator', NULL, 'escidoc:user42', '2010-06-09 09:42:34.547395', 'escidoc:user42', '2010-06-09 09:42:34.547395');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-user-account-inspector', 'User-Account-Inspector', NULL, 'escidoc:user42', '2010-06-14 18:34:55.936116', 'escidoc:user42', '2010-06-14 18:34:55.936116');
INSERT INTO escidoc_role (id, role_name, description, creator_id, creation_date, modified_by_id, last_modification_date) VALUES ('escidoc:role-context-modifier', 'Context-Modifier', NULL, 'escidoc:user42', '2010-06-25 10:27:10.685354', 'escidoc:user42', '2010-06-25 10:27:10.685354');


-- Completed on 2010-06-28 14:06:43

--
-- PostgreSQL database dump complete
--

