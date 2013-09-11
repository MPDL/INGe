/**
 * Role Grant initialization.
 */

        /**
         * The System Administrator user gets the role System-Administrator.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grant1', 'escidoc:exuser1', 'escidoc:role-system-administrator', 'escidoc:exuser1', CURRENT_TIMESTAMP);


        /**
         * The System Inspector user gets the role System-Inspector.
         */
INSERT INTO aa.role_grant 
    (id, user_id, role_id, creator_id, creation_date)
     VALUES
    ('escidoc:grant2', 'escidoc:exuser2', 'escidoc:role-system-inspector', 'escidoc:exuser1', CURRENT_TIMESTAMP);
