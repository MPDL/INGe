package de.mpg.mpdl.inge.cone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import de.mpg.mpdl.inge.util.PropertyReader;

public class GfzTools {

  private static Logger logger = Logger.getLogger(GfzTools.class);
  private Connection connection;

  public Document ousXmlDocument;

  public GfzTools() throws Exception {
    Class.forName(PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_DRIVER_CLASS));
    connection = DriverManager.getConnection(
        "jdbc:postgresql://" + PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_SERVER_NAME) + ":"
            + PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_SERVER_PORT) + "/"
            + PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_NAME),
        PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_USER_NAME),
        PropertyReader.getProperty(PropertyReader.INGE_CONE_DATABASE_USER_PASSWORD));
  }

  public void updateAffiliatedInstitutionsByPerson(String id, Querier querier) throws Exception {
    String query, ouID;
    TreeFragment details = querier.details("persons", id, "*");

    if (details.containsKey("http://purl.org/escidoc/metadata/terms/0.1/position")) {

      for (LocalizedTripleObject lto : details.get("http://purl.org/escidoc/metadata/terms/0.1/position")) {
        logger.info("Found predicate position! subject: " + lto.toString());

        query = "select * from triples where subject = ? and predicate = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, lto.toString());
        statement.setString(2, "http://purl.org/dc/elements/1.1/identifier");
        ResultSet rset = statement.executeQuery();

        int rowCount = 0;

        while (rset.next()) {
          rowCount++;
          ouID = rset.getString("object");

          logger.info("found ouID: " + ouID);

          logger.info("Trying to update OU-Path..");
          query = "update triples set object= ? where subject = ? and predicate = ?";
          PreparedStatement statement2 = connection.prepareStatement(query);
          statement2.setString(1, getOUPath(ouID));
          statement2.setString(2, lto.toString());
          statement2.setString(3, "http://purl.org/eprint/terms/affiliatedInstitution");
          int hitCount = statement2.executeUpdate();
          statement2.close();


          if (hitCount > 0) {
            logger.info("Success.. " + hitCount + " rows updated...");
          } else if (hitCount == 0) {
            logger.info("No rows updated... Trying to insert..");

            query = "insert into triples values (?, ?, ?, null, null );";
            statement2 = connection.prepareStatement(query);
            statement2.setString(1, lto.toString());
            statement2.setString(2, "http://purl.org/eprint/terms/affiliatedInstitution");
            statement2.setString(3, getOUPath(ouID));
            hitCount = statement2.executeUpdate();

            if (hitCount > 0) {
              logger.info("Success.. " + hitCount + " rows inserted...");
            }

            statement2.close();

          }

          logger.info("----");

        }

        if (rowCount == 0) {
          ouID = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_ID);
          logger.info("Relation to affiliation but no affiliations found, selecting " + ouID + " and updating existing relation..");

          query = "insert into triples values (?, ?, ?, null, null ), (?, ?, ?, null, null);";
          PreparedStatement statement2 = connection.prepareStatement(query);
          statement2.setString(1, lto.toString());
          statement2.setString(2, "http://purl.org/eprint/terms/affiliatedInstitution");
          statement2.setString(3, getOUPath(ouID));
          statement2.setString(4, lto.toString());
          statement2.setString(5, "http://purl.org/dc/elements/1.1/identifier");
          statement2.setString(6, ouID);

          int hitCount = statement2.executeUpdate();

          if (hitCount > 0) {
            logger.info("Success.. " + hitCount + " rows inserted...");
          }

          statement2.close();
        }

        statement.close();
      }
    } else {
      ouID = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ROOT_ORGANIZATION_ID);
      logger.info("No affiliations found, selecting " + ouID + " and inserting relation..");
      String relationId = "gfzPeopleID:".concat(id.substring(id.lastIndexOf('/') + 1, id.length()));

      query = "insert into triples values (?, ?, ?, null, 'persons' ), (?, ?, ?, null, null ), (?, ?, ?, null, null);";
      PreparedStatement statement2 = connection.prepareStatement(query);
      statement2.setString(1, id);
      statement2.setString(2, "http://purl.org/escidoc/metadata/terms/0.1/position");
      statement2.setString(3, relationId);
      statement2.setString(4, relationId);
      statement2.setString(5, "http://purl.org/eprint/terms/affiliatedInstitution");
      statement2.setString(6, getOUPath(ouID));
      statement2.setString(7, relationId);
      statement2.setString(8, "http://purl.org/dc/elements/1.1/identifier");
      statement2.setString(9, ouID);

      int hitCount = statement2.executeUpdate();

      if (hitCount > 0) {
        logger.info("Success.. " + hitCount + " rows inserted...");
      }

      statement2.close();
    }
  }

  public String getOUPath(String ouID) throws Exception {
    String URL = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL) + "/rest/ous/" + ouID + "/ouPath";

    logger.info("Sending GET Request..." + URL);
    HttpClient client = new HttpClient();
    GetMethod getMethod = new GetMethod(URL);
    client.executeMethod(getMethod);
    String response = getMethod.getResponseBodyAsString();
    logger.info("Response Body: " + response);

    if (getMethod.getStatusCode() != 200) {
      throw new RuntimeException(response);
    }
    return response;
  }
}
