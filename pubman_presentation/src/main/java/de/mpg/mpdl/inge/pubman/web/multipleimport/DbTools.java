package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;

import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;

public class DbTools {
  private DbTools() {}
  // public static void closePreparedStatement(PreparedStatement ps) {
  // try {
  // if (ps != null && !ps.isClosed()) {
  // ps.close();
  // }
  // } catch (final Exception e) {
  // throw new RuntimeException("Error closing prepared statement", e);
  // }
  // }

  // public static void closeResultSet(ResultSet rs) {
  // try {
  // if (rs != null && !rs.isClosed()) {
  // rs.close();
  // }
  // } catch (final Exception e) {
  // throw new RuntimeException("Error closing result set", e);
  // }
  // }

  public static void closeConnection(Connection connection) {
    try {
      if (null != connection && !connection.isClosed()) {
        connection.close();
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error closing database connection", e);
    }
  }

  public static Connection getNewConnection() {
    try {
      Connection connection = ApplicationBean.INSTANCE.getDataSource().getConnection();

      if (null != connection && !connection.isClosed()) {
        return connection;
      } else {
        throw new RuntimeException("Error creating database connection");
      }
    } catch (final Exception e) {
      throw new RuntimeException("Error creating database connection", e);
    }
  }
}
