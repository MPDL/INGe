package ldap;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PreDestroy;

import org.junit.Test;

public class PostgreSql {
	
	final private String serverUrl;
	final private String username;
	final private String password;
	private Connection connection;
	
	public PostgreSql(String serverUrl, String username, String password) {
		this.serverUrl = serverUrl;
		this.username = username;
		this.password = password;
		try {
			this.connection = this.getConnection();
		} catch (SQLException e) {
			System.out.println("An error occoured, while setting up Database-Connection [PostgreSql()]");
			e.printStackTrace();
		}
	}
	
	private Connection getConnection () throws SQLException {
        return DriverManager.getConnection(this.serverUrl, this.username, this.password);
	}
	
	@PreDestroy
	public void closeConnection () {
		try {
			if (!this.connection.isClosed())
			{
				this.connection.close();
			}
		} catch (SQLException e) {
			System.out.println("An error occoured, while closing Database-Connection [closeConnection()]");
		}
		
		
	}
	
	public synchronized void insertUserAccount(String personId, String personName, String personLoginName, String personPassword) {
		Date date = new Date();
		long currentDate = date.getTime();
		try {
			PreparedStatement statement = this.connection.prepareStatement("insert into aa.user_account "
			        + "(id, active, name, loginname, password, creator_id, creation_date, modified_by_id, last_modification_date) "
			        + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			statement.setString(1, "escidoc:" + personId);
			statement.setBoolean(2, true);
			statement.setString(3, personName);
			statement.setString(4, personLoginName);
			statement.setString(5, personPassword);
			statement.setString(6, "escidoc:exuser1");
			statement.setTimestamp(7, new Timestamp(currentDate));
			statement.setString(8, "escidoc:exuser1");
			statement.setTimestamp(9, new Timestamp(currentDate));
			statement.execute();
		} catch (SQLException e) {
			System.out.println("ERROR: couldn't insert id '" + personId + "' into DB. Maybe this Entry already exists?");
			System.out.println("An error occoured, while inserting into Database [insertUserAccount()]");
			e.printStackTrace();
		}
	}
	
	public synchronized void addUserRole(String id, String personId, String escidocRole, String grantedToObjectId, String grantedToObjectTitle) {
		Date date = new Date();
		try {
			PreparedStatement statement = this.connection.prepareStatement("insert into aa.role_grant "
			        + "(id, user_id, role_id, object_id, object_title, object_href, creator_id, creation_date) "
			        + "values (?, ?, ?, ?, ?, ?, ?, ?)");
			
			statement.setString(1, "escidoc:" + id);
			statement.setString(2, "escidoc:" + personId);
			statement.setString(3, escidocRole);
			statement.setString(4, grantedToObjectId);
			statement.setString(5, grantedToObjectTitle);
			statement.setString(6, "/ir/context/" + grantedToObjectId);
			statement.setString(7, "escidoc:exuser1");
			statement.setTimestamp(8, new Timestamp(date.getTime()));
			statement.execute();
		} catch (SQLException e) {
			System.out.println("ERROR: couldn't set rolegrant for '" + personId + "' in the DB. Maybe this Entry already exists?");
			System.out.println("An error occoured, while inserting into Database [addUserRole()]");
			e.printStackTrace();
		}
	}
	
	public boolean duplicateUserCheck (String personLoginName) {
		ResultSet result = null; 
		try {
			Statement statement = this.connection.createStatement();
			result = statement.executeQuery("SELECT * FROM aa.user_account WHERE loginname ilike '" + personLoginName + "'");
			if (result.next()) {
				System.out.println("Found Duplicate: " + result.getString(1));
				return true;
			}
		} catch (SQLException e) {
			System.out.println("An error occoured, while querying Database [duplicateUserCheck()]");
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized void removeUserAccount(String personLoginName) {
		try {
			Statement statement = this.connection.createStatement();
			statement.execute("Delete FROM aa.user_account WHERE loginname ilike '" + personLoginName + "'");
			
		} catch (SQLException e) {
			System.out.println("An error occoured, while deleting userAccount from Database [removeUserAccount()]");
			e.printStackTrace();
		}
	}
	
	public boolean duplicateRoleCheck (String personId, String escidocRole, String objectId) {
		ResultSet result = null; 
		try {
			Statement statement = this.connection.createStatement();
			result = statement.executeQuery("SELECT * FROM aa.role_grant WHERE user_id ilike 'escidoc:" + personId + "' AND role_id ilike '" + escidocRole + "' AND object_id ilike '" + objectId + "'" );
			if (result.next()) {
				System.out.println("Found Duplicate: " + result.getString(1));
				return true;
			}
		} catch (SQLException e) {
			System.out.println("An error occoured, while querying Database [duplicateRoleCheck()]");
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized void removeUserRole(String roleId) {
		try {
			Statement statement = this.connection.createStatement();
			statement.execute("Delete FROM aa.role_grant WHERE id ilike 'escidoc:" + roleId + "'");
			
		} catch (SQLException e) {
			System.out.println("An error occoured, while deleting userRole from Database [removeUserRole()]");
			e.printStackTrace();
		}
	}
}
