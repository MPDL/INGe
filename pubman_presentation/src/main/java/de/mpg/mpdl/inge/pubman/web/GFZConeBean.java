package de.mpg.mpdl.inge.pubman.web;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.mpg.mpdl.inge.cone.LocalizedTripleObject;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.util.PropertyReader;

@ManagedBean(name = "GFZConeBean")
@SuppressWarnings("serial")
public class GFZConeBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(GFZConeBean.class);

  //  private Querier querier;
  private String coneUri;
  private String hostName;

  public GFZConeBean() throws Exception {
    this.coneUri = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
    this.hostName = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getServerName();
    //    this.querier = QuerierFactory.newQuerier(true);
  }

  private static NamespaceContext nameSpaceCtx = new NamespaceContext() {
    public String getNamespaceURI(String prefix) {
      String uri;
      if (prefix.equals("rdf"))
        uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
      else if (prefix.equals("dc"))
        uri = "http://purl.org/dc/elements/1.1/";
      else if (prefix.equals("gfz"))
        uri = "http://www.gfz-potsdam.de/metadata/namespaces/cone-namespace/";
      else if (prefix.equals("dcterms"))
        uri = "http://purl.org/dc/terms/";
      else
        uri = null;

      return uri;
    }

    public Iterator<?> getPrefixes(String val) {
      return null;
    }

    public String getPrefix(String uri) {
      return null;
    }
  };

  public String getJournalMetaData(ItemVersionVO item) throws Exception {
    String metadataString = "";

    logger.debug("CoNE-Service: " + coneUri);
    logger.debug("This Host: " + hostName);

    //    String journalUrl = "", journalId = "";
    String journalUrl = "";

    if (item.getMetadata().getGenre().equals(MdsPublicationVO.Genre.ARTICLE) && item.getMetadata().getSources().size() > 0) {
      if (item.getMetadata().getSources().get(0) != null && item.getMetadata().getSources().get(0).getIdentifiers() != null
          && item.getMetadata().getSources().get(0).getIdentifiers().size() > 0) {

        for (SourceVO source : item.getMetadata().getSources()) {
          logger.debug("Sources found!");
          if (source.getGenre().equals(SourceVO.Genre.JOURNAL)) {
            logger.debug("Journal as Source found!");
            for (IdentifierVO id : source.getIdentifiers()) {
              if (id.getType().equals(IdType.CONE)) {
                logger.debug("CoNE-Identifier found!");
                logger.debug("Retrieving Journal-Metadata from CoNE");
                journalUrl = id.getId();
              }
            }
          }
        }

        if (!(journalUrl == null || journalUrl.isEmpty())) {
          //          journalId = journalUrl.replaceAll(coneUri, "");
          //
          //          logger.debug("journalUrl: " + journalUrl);
          //          logger.debug("journalId: " + journalId);
          //
          //          if (coneUri.contains(hostName)) {
          //            logger.debug("CoNE Service and PubMan are on the same Machine..");
          //
          //
          //            if (!journalUrl.equals("")) {
          //              TreeFragment tf = querier.details("journals", journalId);
          //              metadataString = appendAttributesToMDString(metadataString,
          //                  tf.get("http://www.gfz-potsdam.de/metadata/namespaces/cone-namespace/listedIn"));
          //              metadataString = appendAttributesToMDString(metadataString, tf.get("http://purl.org/dc/terms/type"));
          //              metadataString =
          //                  appendAttributesToMDString(metadataString, tf.get("http://www.gfz-potsdam.de/metadata/namespaces/cone-namespace/note"));
          //            }
          //          } else {
          logger.debug("CoNE-Service and PubMan are not on the same Machine..");

          GetMethod getMethod = new GetMethod(coneUri + journalUrl.substring(1) + "?format=rdf");
          HttpClient client = new HttpClient();
          client.executeMethod(getMethod);
          String response = getMethod.getResponseBodyAsString();

          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
          dbf.setNamespaceAware(true);
          DocumentBuilder db = dbf.newDocumentBuilder();

          Document xmlDocument = db.parse(new InputSource(new StringReader(response)));

          XPath xPath = XPathFactory.newInstance().newXPath();
          xPath.setNamespaceContext(nameSpaceCtx);

          XPathExpression xPathExpression = xPath.compile("//rdf:Description/*");

          NodeList journalMetadataList = (NodeList) xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);

          List<String> metadataList = new ArrayList<String>();
          for (int j = 0; j < journalMetadataList.getLength(); j++) {
            Node journalNode = journalMetadataList.item(j);
            if (journalNode.getNamespaceURI().equals(nameSpaceCtx.getNamespaceURI("gfz"))
                && journalNode.getLocalName().equals("listedIn")) {
              logger.debug("Attr: " + journalNode.getTextContent());
              metadataList.add(journalNode.getTextContent());
            }
          }

          Collections.sort(metadataList);

          for (String attr : metadataList)
            metadataString += ", " + attr;

          metadataList = new ArrayList<String>();
          for (int j = 0; j < journalMetadataList.getLength(); j++) {
            Node journalNode = journalMetadataList.item(j);
            if (journalNode.getNamespaceURI().equals(nameSpaceCtx.getNamespaceURI("dcterms"))
                && journalNode.getLocalName().equals("type")) {
              logger.debug("Attr: " + journalNode.getTextContent());
              metadataList.add(journalNode.getTextContent());
            }
          }

          Collections.sort(metadataList);

          for (String attr : metadataList)
            metadataString += ", " + attr;

          metadataList = new ArrayList<String>();
          for (int j = 0; j < journalMetadataList.getLength(); j++) {
            Node journalNode = journalMetadataList.item(j);
            if (journalNode.getNamespaceURI().equals(nameSpaceCtx.getNamespaceURI("gfz")) && journalNode.getLocalName().equals("note")) {
              logger.debug("Attr: " + journalNode.getTextContent());
              metadataList.add(journalNode.getTextContent());
            }
          }

          Collections.sort(metadataList);

          for (String attr : metadataList)
            metadataString += ", " + attr;
          //          }
        }
      }
    }

    return metadataString;
  }

  public String getJournalMetaData() throws Exception {
    ViewItemFull viewItemFull = (ViewItemFull) FacesTools.findBean("ViewItemFull");
    ItemVersionVO item = new ItemVersionVO(viewItemFull.getPubItem());

    return getJournalMetaData(item);
  }

  //  private String appendAttributesToMDString(String mdString, List<LocalizedTripleObject> ltoList) {
  //    if (!(ltoList == null || ltoList.isEmpty())) {
  //      Collections.sort(ltoList, new CompLocalizedTripleObject());
  //
  //      logger.debug("found " + ltoList.size() + " matches..");
  //
  //      for (LocalizedTripleObject lto : ltoList) {
  //        logger.debug("Attr: " + lto.toString());
  //        mdString += ", " + lto.toString();
  //      }
  //    }
  //
  //    return mdString;
  //  }

  public static class CompLocalizedTripleObject implements Comparator<LocalizedTripleObject> {
    @Override
    public int compare(LocalizedTripleObject o1, LocalizedTripleObject o2) {
      return o1.toString().compareTo(o2.toString());
    }
  }

}
