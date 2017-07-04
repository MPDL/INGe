package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import de.mpg.mpdl.inge.util.PropertyReader;

public class DbTools {
  private static DataSource DS;

  static {
    try {
      Context initialContext = new InitialContext();
      DS =
          (DataSource) initialContext
              .lookup(PropertyReader.getProperty("inge.database.datasource"));
    } catch (NamingException e) {
      throw new RuntimeException("Error getting datasource", e);
    }
  }

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
      Connection connection = DbTools.DS.getConnection();

      if (connection != null && !connection.isClosed()) {
        return connection;
      } else {
        throw new RuntimeException("Error creating database connection");
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error creating database connection", e);
    }
  }
}
