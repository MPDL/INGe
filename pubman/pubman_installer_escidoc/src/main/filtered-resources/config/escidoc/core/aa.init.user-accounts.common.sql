/**
 * User initialization
 */
    /**
     * System Administrator user (Super user).
     */   
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
    VALUES
    ('escidoc:exuser1',
    ${SqlTrue},
    'System Administrator User',
    '${AdminUsername}',
    '${AdminPassword}',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
    /*
     * System Inspector user (Read only super user).
     */
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
    VALUES
    ('escidoc:exuser2',
    ${SqlTrue},
    'System Inspector User (Read Only Super User)',
    '${InspectorUsername}',
    '${InspectorPassword}',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
    
INSERT INTO aa.user_login_data
    (id, user_id, handle, expiryts)
     VALUES
    ('escidoc:exuser2LoginData', 
    'escidoc:exuser2',
    '${InspectorPassword}', 
    1999999999999);

    
    /**
     * Depositor user.
     */
INSERT INTO aa.user_account
    (id, active, name, loginName, password, creator_id, creation_date, modified_by_id, last_modification_date)
    VALUES
    ('escidoc:exuser4',
    ${SqlTrue},
    'Depositor User',
    '${DepositorUsername}',
    '${DepositorPassword}',
    'escidoc:exuser1',
    CURRENT_TIMESTAMP,
    'escidoc:exuser1',
    CURRENT_TIMESTAMP);
