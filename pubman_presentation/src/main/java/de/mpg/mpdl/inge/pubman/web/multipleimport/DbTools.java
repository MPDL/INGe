package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import de.mpg.mpdl.inge.util.PropertyReader;

public class DbTools {

  public static void closePreparedStatement(PreparedStatement ps) {
    try {
      if (ps != null && !ps.isClosed()) {
        ps.close();
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error closing prepared statement", e);
    }
  }

  public static void closeResultSet(ResultSet rs) {
    try {
      if (rs != null && !rs.isClosed()) {
        rs.close();
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error closing result set", e);
    }
  }

  public static void closeConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error closing database connection", e);
    }
  }

  public static Connection getNewConnection() {
    try {
      Class.forName(PropertyReader.getProperty("inge.database.driver.class"));
      final String connectionUrl = PropertyReader.getProperty("inge.database.jdbc.url");
      return DriverManager.getConnection(connectionUrl,
          PropertyReader.getProperty("inge.database.user.name"),
          PropertyReader.getProperty("inge.database.user.password"));
    } catch (final Exception e) {
      throw new RuntimeException("Error creating database connection", e);
    }
  }
}
