package ldap;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class Processor {
	//Server information
	final static String SERVER_URL = "ldap://mpiwgldap.mpiwg-berlin.mpg.de:389/";
	final static String BASE_DN = "ou=people,dc=mpiwg-berlin,dc=mpg,dc=de";
	final static String USER = "cn=pubman,ou=people,dc=mpiwg-berlin,dc=mpg,dc=de";
	final static String PASSWORD = "XXAEqIoq";
	
	//PostgreSQL information
	final static String POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/escidoc-core";
	final static String POSTGRESQL_USERNAME = "escidoc";
	final static String POSTGRESQL_PASSWORD = "escidoc";
	final static String POSTGRESQL_ID_PREFIX = "mpiwg";
	final static String[] POSTGRESQL_CONTEXTS_IDS_FOR_SETTING_RIGHTS = {"escidoc:733167", "escidoc:733168", "escidoc:733169"};
	final static String[] POSTGRESQL_CONTEXTS_NAMES_FOR_SETTING_RIGHTS = {"General", "Institutsbibliography", "Preprints"};
	
	public static void main(String[] args) {

        insertPersonListIntoDatabase(getLdapUserList());
	}
	
	private static void insertPersonListIntoDatabase(List<Person> userList) {
		
		int personCount = 0;
		StringBuffer xml = new StringBuffer();
		PostgreSql postgreSql = new PostgreSql(POSTGRESQL_URL, POSTGRESQL_USERNAME, POSTGRESQL_PASSWORD);
		xml.append("<personList>");
		for (Person person : userList) {
			if (person.getMail() != null && !postgreSql.duplicateUserCheck(person.getMail())) {
				personCount++;
				postgreSql.insertUserAccount((POSTGRESQL_ID_PREFIX != null ? POSTGRESQL_ID_PREFIX + ":" + person.getUid() : person.getUid()), person.getGivenname() + " " + person.getSurname(), person.getMail(), person.getPassword());
				xml.append(person.toXmlString());
				for (int i = 0; i < POSTGRESQL_CONTEXTS_IDS_FOR_SETTING_RIGHTS.length; i++) {
					postgreSql.addUserRole((POSTGRESQL_ID_PREFIX != null ? POSTGRESQL_ID_PREFIX + ":" + personCount + i : Integer.toString(personCount + i)), (POSTGRESQL_ID_PREFIX != null ? POSTGRESQL_ID_PREFIX + ":" + person.getUid() : person.getUid()), "escidoc:role-depositor", POSTGRESQL_CONTEXTS_IDS_FOR_SETTING_RIGHTS[i], POSTGRESQL_CONTEXTS_NAMES_FOR_SETTING_RIGHTS[i]);
				}
				
			} else {
				System.out.println("ERROR: Person with UID (" + person.getUid() + ") couldn't be written (mail-adress is null or duplicate found)");
			}
		}
		xml.append("\n</personList>");
		Util.writeStringbufferToFile(xml.toString());
		postgreSql.closeConnection();
		
	}
	
	private static List <Person> getLdapUserList() {
		Ldap ldap = new Ldap(SERVER_URL, BASE_DN, USER, PASSWORD);
        List <Person> userList = ldap.getUserList();
        ldap.closeContext();
        return userList;
	}
	
}
