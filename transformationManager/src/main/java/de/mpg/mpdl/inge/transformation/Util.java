/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.aa.TanStore;
import de.mpg.mpdl.inge.util.PropertyReader;
import net.sf.saxon.dom.DocumentBuilderFactoryImpl;

/**
 * Helper methods for the transformation service.
 * 
 * @author kleinfe1 (initial creation)
 * @author $Author: MWalter $ (last modification)
 * @version $Revision: 5445 $ $LastChangedDate: 2015-02-19 14:13:07 +0100 (Thu, 19 Feb 2015) $
 * 
 */
public class Util {
  private static final Logger logger = Logger.getLogger(Util.class);
  //  private static long coneSessionTimestamp = 0;
  //  private static String coneSession = null;

  /**
   * Hide constructor of static class
   */
  private Util() {

  }

  // Jasper styles enum
  public static enum Styles
  {
    APA,
    AJP,
    Default
  };


  /**
   * Normalizes a given mimetype.
   * 
   * @param mimetype
   * @return
   */
  public static String normalizeMimeType(String mimetype) {
    String thisMimetype = mimetype;
    if (mimetype.toLowerCase().equals("text/xml")) {
      thisMimetype = "application/xml";
    }
    if (mimetype.toLowerCase().equals("text/rtf")) {
      thisMimetype = "application/rtf";
    }
    if (mimetype.toLowerCase().equals("text/richtext")) {
      thisMimetype = "application/rtf";
    }
    return thisMimetype;
  }

  //  private static String getAdminUrl() {
  //    return PropertyReader.getProperty(PropertyReader.INGE_AA_INSTANCE_URL) + "adminLogin" + "?username="
  //        + PropertyReader.getProperty(PropertyReader.INGE_AA_ADMIN_USERNAME) + "&password="
  //        + PropertyReader.getProperty(PropertyReader.INGE_AA_ADMIN_PASSWORD);
  //  }

  /**
   * Queries CoNE service and returns the result as DOM node. The returned XML has the following
   * structure: <cone> <author> <familyname>Buxtehude-Mölln</familyname>
   * <givenname>Heribert</givenname> <prefix>von und zu</prefix> <title>König</title> </author>
   * <author> <familyname>Müller</familyname> <givenname>Peter</givenname> </author> </authors>
   * 
   * @param authors
   * @return
   */
  public static Node queryCone(String model, String query) {
    DocumentBuilder documentBuilder;
    String queryUrl = null;
    try {
      logger.info("queryCone: " + model + " query: " + query);

      documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

      Document document = documentBuilder.newDocument();
      Element element = document.createElement("cone");
      document.appendChild(element);

      queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&q="
          + URLEncoder.encode(query, "UTF-8");

      HttpClient client = new HttpClient();
      client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
      GetMethod method = new GetMethod(queryUrl);

      //      String coneSession = getConeSession();
      //
      //      if (coneSession != null) {
      //        method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
      //      }
      logger.info("CoNE query: " + queryUrl);
      client.executeMethod(method);

      if (method.getStatusCode() == 200) {
        String[] results = method.getResponseBodyAsString().split("\n");
        for (String result : results) {
          if (!"".equals(result.trim())) {
            String id = result.split("\\|")[1];
            // CONE Zugriff im LoggedIn Modus (obwohl evtl. nicht eingelogged)
            String tan = TanStore.getNewTan();
            GetMethod detailMethod = new GetMethod(id + "?format=rdf&tan4directLogin=" + URLEncoder.encode(tan, "UTF-8"));
            //            GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle="
            //                + Base64.getEncoder().encodeToString(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
            //detailMethod.setFollowRedirects(true);

            //            if (coneSession != null) {
            //              detailMethod.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
            //            }
            logger.info("CoNE query: " + id + "?format=rdf&tan4directLogin=loggedIn");
            client.getState().clearCookies();
            client.executeMethod(detailMethod);

            if (detailMethod.getStatusCode() == 200) {
              Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
              element.appendChild(document.importNode(details.getFirstChild(), true));
            } else {
              logger.error("Error querying CoNE: Status " + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
            }
          }
        }
      } else {
        logger.error("Error querying CoNE: Status " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
      }

      return document;
    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. (" + queryUrl
          + ") .Otherwise it should be clarified if any measures have to be taken.", e);
      logger.debug("Stacktrace", e);
      return null;
    }
  }

  public static List<String> queryConeForJava(String model, String query) {

    List<String> returnSet = new ArrayList<String>();
    String queryUrl = null;
    try {
      logger.info("queryCone: " + model + " query: " + query);

      queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&q="
          + URLEncoder.encode(query, "UTF-8");
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(queryUrl);

      //      String coneSession = getConeSession();
      //
      //      if (coneSession != null) {
      //        method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
      //      }
      client.executeMethod(method);

      if (method.getStatusCode() == 200) {
        String[] results = method.getResponseBodyAsString().split("\n");
        for (String result : results) {
          if (!"".equals(result.trim())) {
            String nextId = result.split("\\|")[1];
            if (!returnSet.contains(nextId)) {
              returnSet.add(nextId);
            }
          }
        }
      } else {
        logger.error("Error querying CoNE: Status " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
      }

      return returnSet;
    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. (" + queryUrl
          + ") .Otherwise it should be clarified if any measures have to be taken.", e);
      logger.debug("Stacktrace", e);
      return null;
    }
  }

  /**
   * queries the framework with the given request
   * 
   * @param request
   * @return XML document returned by the framework
   * @throws Exception
   */
  public static Document queryFramework(String request) throws Exception {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactoryImpl.newInstance();

    DocumentBuilder documentBuilder;

    String url = null;
    String frameworkUrl = null;
    Document document = null;
    try {
      frameworkUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
      url = frameworkUrl + request;

      documentBuilderFactory.setNamespaceAware(true);
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
      document = documentBuilder.newDocument();

      HttpClient client = new HttpClient();
      GetMethod getMethod = new GetMethod(url);
      int statusCode = client.executeMethod(getMethod);
      if (statusCode == 200) {
        InputStream input = getMethod.getResponseBodyAsStream();
        document = documentBuilder.parse(input);
      } else {
        throw new RuntimeException("Error requesting <" + url + ">");
      }
    } catch (IOException e) {
      logger.error("IOException when reading document from URL <" + url, e);
      throw e;
    } catch (ParserConfigurationException e) {
      logger.error("Parser configuration error", e);
      throw e;
    } catch (SAXException e) {
      logger.error("Could not parse returned XML", e);
      throw e;
    }

    return document;
  }

  //  public static String getConeSession() throws Exception {
  //    long now = new Date().getTime();
  //    if (coneSession == null || (now - coneSessionTimestamp) > 1000 * 60 * 30) {
  //      String queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
  //      HttpClient client = new HttpClient();
  //      GetMethod method = new GetMethod(queryUrl);
  //      try {
  //        client.executeMethod(method);
  //      } catch (Exception e) {
  //        logger.warn("Error while retrieving CoNE session", e);
  //        return null;
  //      }
  //      Header[] cookies = method.getResponseHeaders("Set-Cookie");
  //      if (cookies != null && cookies.length > 0) {
  //        for (Header cookie : cookies) {
  //          if (cookie.getValue().startsWith("JSESSIONID=")) {
  //            int end = cookie.getValue().indexOf(";", 11);
  //            if (end >= 0) {
  //              coneSession = cookie.getValue().substring(11, end);
  //              coneSessionTimestamp = now;
  //              logger.info("Refreshing CoNE session: " + coneSession);
  //              return coneSession;
  //            }
  //          }
  //        }
  //      }
  //      return null;
  //    } else {
  //      return coneSession;
  //    }
  //  }

  /**
   * Queries the CoNE service and transforms the result into a DOM node.
   * 
   * @param model The type of object (e.g. "persons")
   * @param name The query string.
   * @param ou Specialty for persons
   * @param coneSession A JSESSIONID to not produce a new session with each call.
   * @return A DOM node containing the results.
   */
  public static Node queryConeExact(String model, String name, String ou) {
    DocumentBuilder documentBuilder;

    try {
      logger.info("queryConeExact: " + model + " name: " + name + " ou: " + ou);

      documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

      Document document = documentBuilder.newDocument();
      Element element = document.createElement("cone");
      document.appendChild(element);

      String queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&"
          + URLEncoder.encode("dc:title", "UTF-8") + "=" + URLEncoder.encode("\"" + name + "\"", "UTF-8") + "&"
          + URLEncoder.encode("escidoc:position/eprints:affiliatedInstitution", "UTF-8") + "="
          + URLEncoder.encode("\"*" + ou + "*\"", "UTF-8");
      HttpClient client = new HttpClient();
      client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
      GetMethod method = new GetMethod(queryUrl);

      //      String coneSession = getConeSession();
      //
      //      if (coneSession != null) {
      //        method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
      //      }
      logger.info("CoNE query: " + queryUrl);
      client.executeMethod(method);
      if (method.getStatusCode() == 200) {
        ArrayList<String> results = new ArrayList<String>();
        results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
        queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&"
            + URLEncoder.encode("dcterms:alternative", "UTF-8") + "=" + URLEncoder.encode("\"" + name + "\"", "UTF-8") + "&"
            + URLEncoder.encode("escidoc:position/eprints:affiliatedInstitution", "UTF-8") + "="
            + URLEncoder.encode("\"*" + ou + "*\"", "UTF-8");
        client.getState().clearCookies();
        method = new GetMethod(queryUrl);
        //        if (coneSession != null) {
        //          method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
        //        }
        client.executeMethod(method);
        logger.info("CoNE query: " + queryUrl);
        if (method.getStatusCode() == 200) {
          results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
          Set<String> oldIds = new HashSet<String>();
          for (String result : results) {
            if (!"".equals(result.trim())) {
              String id = result.split("\\|")[1];
              if (!oldIds.contains(id)) {
                // CONE Zugriff im LoggedIn Modus (obwohl evtl. nicht eingelogged)
                String tan = TanStore.getNewTan();
                GetMethod detailMethod = new GetMethod(id + "?format=rdf&tan4directLogin=" + URLEncoder.encode(tan, "UTF-8"));
                //            GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle="
                //                + Base64.getEncoder().encodeToString(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
                //detailMethod.setFollowRedirects(true);
                logger
                    .info("CoNE query: " + id + "?format=rdf&tan4directLogin=loggedIn returned " + detailMethod.getResponseBodyAsString());
                client.getState().clearCookies();
                client.executeMethod(detailMethod);
                if (detailMethod.getStatusCode() == 200) {
                  Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
                  element.appendChild(document.importNode(details.getFirstChild(), true));
                } else {
                  logger
                      .error("Error querying CoNE: Status " + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
                }
                oldIds.add(id);
              }
            }
          }
        }
      } else {
        logger.error("Error querying CoNE: Status " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
      }
      return document;
    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. "
          + "Otherwise it should be clarified if any measures have to be taken.", e);
      return null;
    }
  }


  /**
   * Queries the CoNE service and transforms the result into a DOM node.
   * 
   * @param model The type of object (e.g. "persons")
   * @param name The query string.
   * @param ou Specialty for persons
   * @param coneSession A JSESSIONID to not produce a new session with each call.
   * @return A DOM node containing the results.
   */
  public static Node queryConeExactWithIdentifier(String model, String identifier, String ou) {
    DocumentBuilder documentBuilder;

    try {
      logger.info("queryConeExactWithIdentifier: " + model + " identifier: " + identifier + " ou: " + ou);

      documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

      Document document = documentBuilder.newDocument();
      Element element = document.createElement("cone");
      document.appendChild(element);

      String queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&dc:identifier/"
          + URLEncoder.encode("rdf:value", "UTF-8") + "=" + URLEncoder.encode("\"" + identifier + "\"", "UTF-8") + "&"
          + URLEncoder.encode("escidoc:position/eprints:affiliatedInstitution", "UTF-8") + "="
          + URLEncoder.encode("\"*" + ou + "*\"", "UTF-8");
      HttpClient client = new HttpClient();
      client.getParams().setParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
      GetMethod method = new GetMethod(queryUrl);

      //      String coneSession = getConeSession();
      //
      //      if (coneSession != null) {
      //        method.setRequestHeader("Cookie", "JSESSIONID=" + coneSession);
      //      }
      client.executeMethod(method);
      logger.info("CoNE query: " + queryUrl);
      if (method.getStatusCode() == 200) {
        ArrayList<String> results = new ArrayList<String>();
        results.addAll(Arrays.asList(method.getResponseBodyAsString().split("\n")));
        Set<String> oldIds = new HashSet<String>();
        for (String result : results) {
          if (!"".equals(result.trim())) {
            String id = result.split("\\|")[1];
            if (!oldIds.contains(id)) {
              // CONE Zugriff im LoggedIn Modus (obwohl evtl. nicht eingelogged)
              String tan = TanStore.getNewTan();
              GetMethod detailMethod = new GetMethod(id + "?format=rdf&tan4directLogin=" + URLEncoder.encode(tan, "UTF-8"));
              //            GetMethod detailMethod = new GetMethod(id + "?format=rdf&eSciDocUserHandle="
              //                + Base64.getEncoder().encodeToString(AdminHelper.getAdminUserHandle().getBytes("UTF-8")));
              // detailMethod.setFollowRedirects(true);
              client.getState().clearCookies();
              client.executeMethod(detailMethod);
              logger.info("CoNE query: " + id + "?format=rdf&tan4directLogin=loggedIn");
              if (detailMethod.getStatusCode() == 200) {
                Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
                element.appendChild(document.importNode(details.getFirstChild(), true));
              } else {
                logger.error("Error querying CoNE: Status " + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
              }
              oldIds.add(id);
            }
          }
        }
      } else {
        logger.error("Error querying CoNE: Status " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
      }
      return document;
    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. "
          + "Otherwise it should be clarified if any measures have to be taken.", e);
      return null;
    }
  }

  /**
   * Queries CoNE service and returns the result as DOM node. The returned XML has the following
   * structure: <cone> <author> <familyname>Buxtehude-Mölln</familyname>
   * <givenname>Heribert</givenname> <prefix>von und zu</prefix> <title>König</title> </author>
   * <author> <familyname>Müller</familyname> <givenname>Peter</givenname> </author> </authors>
   * 
   * @param Single instituteId for an institute without departments or list of Ids. Every department
   *        has his own Id.
   * @return
   */
  public static Node queryReportPersonCone(String model, String query) {
    DocumentBuilder documentBuilder;
    String queryUrl;
    List<String> childIds = new ArrayList<String>();
    // get the childOUs if any in the query
    if (query.contains(" ")) {
      String[] result = query.split("\\s+");
      for (String s : result) {
        childIds.add(s);
      }
    }

    try {
      documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      Element element = document.createElement("cone");
      document.appendChild(element);

      HttpClient client = new HttpClient();
      if (childIds.size() > 0) {
        // execute a method for every child ou
        for (String childId : childIds) {
          queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&"
              + URLEncoder.encode("escidoc:position/dc:identifier", "UTF-8") + "=" + URLEncoder.encode("\"" + childId + "\"", "UTF-8")
              + "&n=0";
          executeGetMethod(client, queryUrl, documentBuilder, document, element);
        }
      } else {
        // there are no child ous, method is called once
        queryUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL) + model + "/query?format=jquery&"
            + URLEncoder.encode("escidoc:position/dc:identifier", "UTF-8") + "=" + URLEncoder.encode("\"" + query + "\"", "UTF-8") + "&n=0";
        executeGetMethod(client, queryUrl, documentBuilder, document, element);
      }

      // LOGGING
      // javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
      // javax.xml.transform.Transformer t = tf.newTransformer();
      // StringWriter sw = new StringWriter();
      // t.transform(new DOMSource(document), new StreamResult(sw));
      // logger.info(sw.toString());

      return document;
    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. "
          + "Otherwise it should be clarified if any measures have to be taken.", e);

      return null;
    }
  }

  /**
   * Execute the GET-method.
   * 
   * @param client
   * @param queryUrl
   * @param documentBuilder
   * @param document
   * @param element
   * @return true if the array contains the format object, else false
   */
  private static void executeGetMethod(HttpClient client, String queryUrl, DocumentBuilder documentBuilder, Document document,
      Element element) {
    String previousUrl = null;
    try {
      logger.info("queryURL from executeGetMethod  " + queryUrl);
      GetMethod method = new GetMethod(queryUrl);
      client.executeMethod(method);

      if (method.getStatusCode() == 200) {
        String[] results = method.getResponseBodyAsString().split("\n");
        for (String result : results) {
          if (!"".equals(result.trim())) {
            String detailsUrl = result.split("\\|")[1];
            // if there is an alternative name, take only the first occurrence
            if (!detailsUrl.equalsIgnoreCase(previousUrl)) {
              GetMethod detailMethod = new GetMethod(detailsUrl + "?format=rdf");
              previousUrl = detailsUrl;
              client.executeMethod(detailMethod);

              if (detailMethod.getStatusCode() == 200) {
                Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
                element.appendChild(document.importNode(details.getFirstChild(), true));
              } else {
                logger.error("Error querying CoNE: Status " + detailMethod.getStatusCode() + "\n" + detailMethod.getPath() + "\n"
                    + detailMethod.getResponseBodyAsString());
              }
            }
          }
        }

      } else {
        logger.error("Error querying CoNE: Status " + method.getStatusCode() + "\n" + method.getResponseBodyAsString());
      }
    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. "
          + "Otherwise it should be clarified if any measures have to be taken.", e);

    }
  }

  public static Node querySSRNId(String conePersonUrl) {
    DocumentBuilder documentBuilder;
    HttpClient client = new HttpClient();

    logger.info("querySSRNId: " + conePersonUrl);

    try {
      documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();

      Document document = documentBuilder.newDocument();
      Element element = document.createElement("cone");
      document.appendChild(element);
      GetMethod detailMethod = new GetMethod(conePersonUrl + "?format=rdf");
      client.executeMethod(detailMethod);
      if (detailMethod.getStatusCode() == 200) {
        Document details = documentBuilder.parse(detailMethod.getResponseBodyAsStream());
        element.appendChild(document.importNode(details.getFirstChild(), true));
        return document;
      } else {
        logger.error("Error querying CoNE: Status " + detailMethod.getStatusCode() + "\n" + detailMethod.getResponseBodyAsString());
        return null;
      }

    } catch (Exception e) {
      logger.error("Error querying CoNE service. This is normal during unit tests. "
          + "Otherwise it should be clarified if any measures have to be taken.", e);
      return null;
    }

  }

  public static Node getSize(String url) {
    DocumentBuilder documentBuilder;

    HttpClient httpClient = new HttpClient();
    HeadMethod headMethod = new HeadMethod(url);

    try {
      logger.info("Getting size of " + url);

      httpClient.executeMethod(headMethod);

      if (headMethod.getStatusCode() != 200) {
        logger.warn("Wrong status code " + headMethod.getStatusCode() + " at " + url);
      }

      documentBuilder = DocumentBuilderFactoryImpl.newInstance().newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      Element element = document.createElement("size");
      document.appendChild(element);
      Header header = headMethod.getResponseHeader("Content-Length");
      logger.info("HEAD Request to " + url + " returned Content-Length: " + (header != null ? header.getValue() : null));
      if (header != null) {
        element.setTextContent(header.getValue());
        return document;
      } else {
        // did not get length via HEAD request, try to do a GET request
        // workaround for biomed central, where HEAD requests sometimes return Content-Length,
        // sometimes not

        logger.info("GET request to " + url + " did not return any Content-Length. Trying GET request.");
        httpClient.getState().clearCookies();
        GetMethod getMethod = new GetMethod(url);
        httpClient.executeMethod(getMethod);

        if (getMethod.getStatusCode() != 200) {
          logger.warn("Wrong status code " + getMethod.getStatusCode() + " at " + url);
        }

        InputStream is = getMethod.getResponseBodyAsStream();
        long size = 0;

        while (is.read() != -1) {
          size++;
        }
        is.close();

        logger.info("GET request to " + url + " returned a file with length: " + size);
        element.setTextContent(String.valueOf(size));
        return document;
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String getMimetype(String filename) {
    try {
      Tika tika = new Tika();
      String mimetype = tika.detect(filename);
      return mimetype;
    } catch (Exception e) {
      logger.error("Error while detecting mimetype of filename: " + filename, e);
    }

    // Error querying CoNE, return default mimetype
    return "application/octet-stream";
  }

  public static String stripHtml(String text) {
    if (text != null) {
      return new HtmlToPlainText().getPlainText(Jsoup.parse(text));
    }

    return "";
  }
}
