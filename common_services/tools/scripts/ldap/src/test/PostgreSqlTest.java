package test;

import static org.junit.Assert.assertTrue;
import ldap.PostgreSql;

import org.junit.Test;

public class PostgreSqlTest {
	
	@Test
	public void writeTest() {
		PostgreSql db = new PostgreSql("jdbc:postgresql://localhost:5432/escidoc-core", "escidoc", "escidoc");
		db.insertUserAccount("mpiwg:mwalter", "Matthias Walter", "mwalter", "abcdefg");
		boolean userExists = db.duplicateUserCheck("mwalter");
		db.insertUserAccount("exuser1", "DER ADMIN", "admin", "1337");
		boolean adminExists = db.duplicateUserCheck("mwalter");
		db.addUserRole("meineID", "mpiwg:mwalter", "Diener", "contextXXX", "DER TestContext");
		boolean roleExists = db.duplicateRoleCheck("mpiwg:mwalter", "Diener", "contextXXX");
		if (roleExists) {
			System.out.println("Removing Role");
			db.removeUserRole("meineId");
		}
		if (userExists) {
			System.out.println("Removing User");
			db.removeUserAccount("mwalter");
		}
		if (adminExists) {
			System.out.println("Removing Admin");
			db.removeUserAccount("admin");
		}
		db.closeConnection();
		assertTrue(userExists);
		assertTrue(roleExists);
	}
}
