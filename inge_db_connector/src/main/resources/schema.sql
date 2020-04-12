-- Database: cone
 
-- DROP DATABASE cone;
 
CREATE DATABASE cone
    WITH 
    OWNER = inge_rw
    ENCODING = 'UTF8'
    LC_COLLATE = 'de_DE.UTF-8'
    LC_CTYPE = 'de_DE.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
 
GRANT TEMPORARY, CONNECT ON DATABASE cone TO PUBLIC;
 
GRANT ALL ON DATABASE cone TO inge_rw;
 
-- Database: inge
 
-- DROP DATABASE inge;
 
CREATE DATABASE inge
    WITH 
    OWNER = inge_rw
    ENCODING = 'UTF8'
    LC_COLLATE = 'de_DE.UTF-8'
    LC_CTYPE = 'de_DE.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;
 
GRANT TEMPORARY, CONNECT ON DATABASE inge TO PUBLIC;
 
GRANT ALL ON DATABASE inge TO inge_rw;
