/*
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

/**
 * Feed class for eSciDoc syndication manager.
 * 
 * @author Vlad Makarenko (initial creation)
 * @author $Author$ (last modification) $Revision$ $LastChangedDate$
 */

package de.mpg.mpdl.inge.syndication;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.SyndPersonImpl;
import com.sun.syndication.io.FeedException;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.AbstractVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;
import de.mpg.mpdl.inge.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.xmltransforming.util.HtmlUtils;
import de.mpg.mpdl.inge.xmltransforming.xmltransforming.XmlTransformingBean;


public class Feed extends SyndFeedImpl {

  private static final long serialVersionUID = 1L;
  private static final String FEEDS_CONTENT_MODEL =
      "escidoc.framework_access.content-model.id.publication";

  private static final Logger logger = Logger.getLogger(Feed.class);

  // Search CQL query
  // see: http://www.escidoc-project.de/documentation/Soap_api_doc_SB_Search.pdf
  private String query;

  // Sort keys
  private String sortKeys;

  // Records limit
  private String maximumRecords;

  // List of the all available types for the feed
  private String feedTypes;

  // template for html rel link generation
  private String relLink;

  // uriMatcher is RegExp for URI matching
  private String uriMatcher;

  // use caching if "true", do not use, otherwise
  private String cachingStatus;

  // TTL for until the channel, it will be recached after
  private String cachingTtl = "0";

  // List of the parameters generated according to the URI
  private List<String> paramList = new ArrayList<String>();

  // Hash of the parameters/values
  private Map<String, String> paramHash = new HashMap<String, String>();

  // XML transformation bean
  private static XmlTransforming xt = new XmlTransformingBean();

  /**
   * Query getter.
   * 
   * @return <code>query</code>
   */
  public String getQuery() {
    return query;
  }


  /**
   * Query setter.
   * 
   * @param query
   */
  public void setQuery(String query) {
    try {
      if (query != null && query.contains("${content_model}")) {
        String contentModel = PropertyReader.getProperty(FEEDS_CONTENT_MODEL);
        if (contentModel != null) {
          query = query.replaceAll("\\$\\{content_model\\}", contentModel);
        }
      }
    } catch (IOException e) {
      System.out.println("Problem reading property(" + FEEDS_CONTENT_MODEL + ")");
      e.printStackTrace();
    } catch (URISyntaxException e) {
      System.out
          .println("Problem replacing ${content_model} with specific content-model in setQuery");
      e.printStackTrace();
    }
    this.query = query;
  }

  /**
   * SortKeys getter
   * 
   * @return <code>sortKeys</code>
   */
  public String getSortKeys() {
    return sortKeys;
  }


  /**
   * SortKeys setter.
   * 
   * @param sortKeys
   */
  public void setSortKeys(String sortKeys) {
    this.sortKeys = sortKeys;
  }


  /**
   * MaximumRecords getter.
   * 
   * @return <code>MaximumRecords</code>
   */
  public String getMaximumRecords() {
    return maximumRecords;
  }


  /**
   * MaximumRecords setter.
   * 
   * @param maximumRecords
   */
  public void setMaximumRecords(String maximumRecords) {
    this.maximumRecords = maximumRecords;
  }


  /**
   * FeedTypes getter.
   * 
   * @return <code>FeedTypes</code>
   */
  public String getFeedTypes() {
    return feedTypes;
  }

  /**
   * FeedTypes setter.
   * 
   * @param feedTypes
   */
  public void setFeedTypes(String feedTypes) {
    this.feedTypes = feedTypes;
  }

  /**
   * UriMatcher getter.
   * 
   * @return <code>UriMatcher</code>
   */
  public String getUriMatcher() {
    return uriMatcher;
  }


  /**
   * UriMatcher setter.
   * 
   * @param uriMatcher
   */
  public void setUriMatcher(String uriMatcher) {
    this.uriMatcher = uriMatcher;
  }



  public String getRelLink() {
    return relLink;
  }


  public void setRelLink(String relLink) {
    this.relLink = relLink;
  }


  public String generateRelLink(String uri) throws SyndicationException {

    populateParamsFromUri(uri);

    String feedLink =
        "<link href=\"" + StringEscapeUtils.escapeXml(uri) + "\"" + " rel=\"alternate\" type=\""
            + getFeedMimeType((String) paramHash.get("${feedType}")) + "\" title=\""
            + StringEscapeUtils.escapeXml(populateFieldWithParams("relLink", getRelLink()))
            + "\" />\n";

    return feedLink;
  }


  private String getFeedMimeType(String feedType) throws SyndicationException {
    Utils.checkName(feedType, "feedType is empty");
    return "application/" + (feedType.toLowerCase().contains("atom") ? "atom" : "rss") + "+xml";
  }

  /**
   * CachingStatus getter.
   * 
   * @return <code>CachingStatus</code>
   */
  public String getCachingStatus() {
    return cachingStatus;
  }


  /**
   * CachingStatus setter.
   * 
   * @param status
   */
  public void setCachingStatus(String status) {
    this.cachingStatus = status;
  }


  /**
   * CachingTtl getter.
   * 
   * @return <code>CachingTtl</code>
   */
  public String getCachingTtl() {
    return cachingTtl;
  }


  /**
   * CachingTtl setter.
   * 
   * @param cachingTtl
   */
  public void setCachingTtl(String cachingTtl) {
    this.cachingTtl = cachingTtl;
  }


  /**
   * ParamList getter
   * 
   * @return <code>ParamList</code>
   */
  public List<String> getParamList() {
    return paramList;
  }



  /**
   * ParamList setter.
   * 
   * @param paramList
   */
  public void setParamList(List<String> paramList) {
    this.paramList = paramList;
  }



  /**
   * Add new SyndCategory.
   * 
   * @param sc
   */
  public void addCategory(SyndCategory sc) {
    getCategories().add(sc);
  }


  /**
   * Add new SyndPerson as Author.
   * 
   * @param sp
   */
  public void addAuthor(SyndPerson sp) {
    getAuthors().add(sp);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sun.syndication.feed.synd.SyndFeedImpl#setUri(java.lang.String)
   */
  @Override
  public void setUri(String uri) {
    super.setUri(uri);
    generateUriMatcher(uri);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.sun.syndication.feed.synd.SyndFeedImpl#clone()
   */
  @Override
  public Object clone() {
    Object clone = null;
    try {
      clone = super.clone();
    } catch (CloneNotSupportedException e) {
      // should never happen
    }
    return clone;
  }



  /**
   * Generate <code>UriMatcher</code> for the feed and list of the parameters <code>paramList</code>
   * according to the <code>uri</code>.
   * 
   * @param uri
   */
  public void generateUriMatcher(final String uri) {
    String result = new String(uri);

    result = escapeUri(result);

    // property regexp in uri
    String regexp = "(\\$\\{[\\w.]+?\\})";

    Matcher m = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(uri);
    while (m.find()) {
      String param = m.group(1);
      paramList.add(param);
      result = result.replaceFirst(Utils.quoteReplacement(param), "\\(.+\\)?");
    }
    setUriMatcher(result);
  }


  private String escapeUri(String uri) {
    return uri.replaceAll("\\?", "\\\\?");
  }

  /**
   * Populate parameters with the values taken from the certain <code>uri</code> and populate
   * <code>paramHash</code> with the parameter/value paars.
   * 
   * @param uri
   * @throws SyndicationException
   */
  private void populateParamsFromUri(String uri) throws SyndicationException {
    Utils.checkName(uri, "Uri is empty");

    String um = getUriMatcher();

    Utils.checkName(um, "Uri matcher is empty");

    Matcher m = Pattern.compile(um, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(uri);
    if (m.find()) {
      for (int i = 0; i < m.groupCount(); i++)
        paramHash.put((String) paramList.get(i), m.group(i + 1));
    }

    // special handling of Organizational Unit Feed
    // TODO: should be resolved other way!
    if (getUriMatcher().equals("(.+)?/syndication/feed/(.+)?/publications/organization/(.+)?")) {
      TreeMap<String, String> outm = Utils.getOrganizationUnitTree();
      String oid = (String) paramHash.get("${organizationId}");
      for (Map.Entry<String, String> entry : outm.entrySet()) {
        if (entry.getValue().equals(oid)) {
          paramHash.put("${organizationName}", entry.getKey());
        }
      }
    }

    logger.info("parameters: " + paramHash);

  }

  /**
   * Populate feed/channel element with the parameters from the <code>paramHash</code>
   * 
   * @param name - the name of the element, is only needed for check <code>value</code> message
   * @param value is value of the the element to be populated
   * @return populated <code>value</code>
   * @throws SyndicationException
   */
  public String populateFieldWithParams(String name, String value) throws SyndicationException {
    Utils.checkName(value, "Field <" + name + "> is empty");

    Utils.checkCondition(paramHash.keySet().size() == 0, "No parameters for Uri matcher");
    for (String key : (Set<String>) paramHash.keySet()) {
      value = value.replaceAll(Utils.quoteReplacement(key), (String) paramHash.get(key));
    }
    return value;
  }


  /**
   * List of feed/channel elements to be populated by @see #populateFieldWithParams(String, String)
   * 
   * @throws SyndicationException
   */
  private void populateFeedElementsWithParams() throws SyndicationException {

    setTitle(populateFieldWithParams("title", getTitle()));
    setDescription(populateFieldWithParams("description", getDescription()));

    setLink(populateFieldWithParams("link", getLink()));
    setUri(populateFieldWithParams("uri", getUri()));

    setRelLink(populateFieldWithParams("relLink", getRelLink()));

    setQuery(populateFieldWithParams("query", getQuery()));

    SyndImage si = getImage();
    si.setLink(populateFieldWithParams("image/link", si.getLink()));
    si.setUrl(populateFieldWithParams("image/url", si.getUrl()));
    setImage(si);

  }

  public String toString() {

    String str =
        "[" + "query: " + getQuery() + "\n" + "feedTypes: " + getFeedTypes() + "\n"
            + "uriMatcher: " + getUriMatcher() + "\n" + "paramList: " + paramList + "\n"
            + "cachingStatus: " + cachingStatus + "\n" + "cachingTtl: " + cachingTtl + "\n"
            + super.toString() + "]";

    return str;
  }


  /**
   * Main method for entries population of the feed/channel according to the <code>uri</code>
   * 
   * @param uri is URI for feed parameters
   * @throws SyndicationException
   */
  public void populateEntries(String uri) throws SyndicationException {

    logger.info("uri:" + uri);

    populateParamsFromUri(uri);

    // set feedType
    String ft = (String) paramHash.get("${feedType}");
    if (!Utils.findInList(getFeedTypes().split(","), ft)) {
      throw new SyndicationException("Requested feed type: " + ft + " is not supported");
    }
    setFeedType(ft);

    populateFeedElementsWithParams();

    setChannelLimitations();

    // search for itemList

    String itemListXml = null;
    /*
     * //hack to test Faces if ( getQuery().equals(
     * "escidoc.content-model.objid=escidoc:faces40 and escidoc.property.public-status=released") )
     * { try { itemListXml = Utils.getResourceAsString("src/test/resources/FacesExport.xml"); }
     * catch (IOException e1) { // TODO Auto-generated catch block e1.printStackTrace(); } } else {
     */
    itemListXml = performSearch(getQuery(), getMaximumRecords(), getSortKeys());
    /* } */


    // populate entires
    setEntries(transformToEntryList(itemListXml));


    // Set PublishedDate of the feed to the PublishedDate of the latest Entry
    // or, if no entires presented, to the current date
    List<?> e = getEntries();
    setPublishedDate(Utils.checkList(e) ? ((SyndEntry) e.get(0)).getPublishedDate() : new Date());


  }



  /**
   * Search for the items for feed entries population. The HTTP request to the SearchAndExport WEB
   * interface is used
   * 
   * @param query is CQL query.
   * @param maximumRecords is the limit of the search
   * @param sortKeys
   * @return item list XML
   * @throws SyndicationException
   */
  private String performSearch(String query, String maximumRecords, String sortKeys)
      throws SyndicationException {
    URL url;
    try {
      url =
          new URL(paramHash.get("${baseUrl}") + "/search/SearchAndExport?" + "cqlQuery="
              + URLEncoder.encode(query, "UTF-8") + "&maximumRecords="
              + URLEncoder.encode(maximumRecords, "UTF-8") + "&sortKeys="
              + URLEncoder.encode(sortKeys, "UTF-8") + "&exportFormat=ESCIDOC_XML_V13"
              + "&sortOrder=descending" + "&language=all");
    } catch (Exception e) {
      throw new SyndicationException("Wrong URL:", e);
    }

    Object content;
    URLConnection uconn;

    try {
      uconn = ProxyHelper.openConnection(url);
      if (!(uconn instanceof HttpURLConnection))
        throw new IllegalArgumentException("URL protocol must be HTTP.");
      HttpURLConnection conn = (HttpURLConnection) uconn;

      InputStream stream = conn.getErrorStream();
      if (stream != null) {
        conn.disconnect();
        throw new SyndicationException(Utils.getInputStreamAsString(stream));
      } else if ((content = conn.getContent()) != null && content instanceof InputStream)
        content = Utils.getInputStreamAsString((InputStream) content);
      else {
        conn.disconnect();
        throw new SyndicationException("Cannot retrieve content from the HTTP response");
      }
      conn.disconnect();

      return (String) content;
    } catch (Exception e) {
      throw new SyndicationException(e);
    }

  }

  /**
   * Transformation method takes ItemList XML and transforms it to the list of syndication entries (
   * <code><List>SyndEntry</code>)
   * 
   * @param itemListXml
   * @return <List>SyndEntry
   * @throws SyndicationException
   */
  private List<SyndEntry> transformToEntryList(String itemListXml) throws SyndicationException {

    List<SyndEntry> entries = new ArrayList();

    List<ItemVO> itemListVO = null;
    try {
      itemListVO = (List<ItemVO>) xt.transformToItemList(itemListXml);
    } catch (Exception e) {
      throw new SyndicationException("Cannot transform item list XML to List<ItemVO>:", e);
    }

    for (ItemVO item : itemListVO) {

      SyndEntry se = new SyndEntryImpl();

      PubItemVO pi = (PubItemVO) item;
      MdsPublicationVO md = pi.getMetadata();

      // Content

      /*
       * SyndContent scont = new SyndContentImpl(); scont.setType("application/xml"); try { String
       * itemXml = replaceXmlHeader(xt.transformToItem( pi ));
       * 
       * scont.setValue( itemXml ); if ( "atom_0.3".equals(getFeedType()) )
       * scont.setMode(Content.XML); } catch (TechnicalException e) { throw new RuntimeException(
       * "Cannot transform to XML: ", e); };
       * 
       * se.setContents(Arrays.asList(scont));
       */
      //
      se.setTitle(HtmlUtils.removeSubSupIfBalanced(md.getTitle()));

      // Description ??? optional
      List<?> abs = md.getAbstracts();
      SyndContent sc = new SyndContentImpl();
      sc.setValue(Utils.checkList(abs) ? ((AbstractVO) abs.get(0)).getValue() : null);
      se.setDescription(sc);

      // Category
      String subj = md.getFreeKeywords();
      if (subj != null && Utils.checkVal(subj)) {
        List<SyndCategory> categories = new ArrayList<SyndCategory>();
        SyndCategory scat = new SyndCategoryImpl();
        scat.setName(subj);
        categories.add(scat);
        se.setCategories(categories);
      }


      if (Utils.checkList(md.getCreators())) {
        List<SyndPerson> authors = new ArrayList<SyndPerson>();
        SyndPerson sp;
        StringBuffer allCrs = new StringBuffer();
        int counter = 0;
        for (CreatorVO creator : md.getCreators()) {

          String crs =
              creator.getPerson() != null ? Utils.join(Arrays.asList(creator.getPerson()
                  .getFamilyName(), creator.getPerson().getGivenName()), ", ") : creator
                  .getOrganization().getName();

          sp = new SyndPersonImpl();
          sp.setName(crs);
          authors.add(sp);


          allCrs.append(crs);
          if (counter + 1 != md.getCreators().size()) {
            allCrs.append("; ");
          }

          counter++;

        }

        se.setAuthor(allCrs.toString());
        se.setAuthors(authors);
        // se.setContributors(contributors);
      }

      // Contents ???
      // se.setContents(contents)

      se.setLink(paramHash.get("${baseUrl}") + "/pubman/item/"
          + pi.getLatestRelease().getObjectIdAndVersion());

      // Uri ????
      se.setUri(se.getLink());

      // Entry UpdatedDate ???
      se.setUpdatedDate(pi.getModificationDate());

      // Entry PublishedDate ???
      se.setPublishedDate(pi.getLatestRelease().getModificationDate());

      setEntryLimitations(se);

      populateModules(se, md);

      entries.add(se);

    }

    return entries;


  }

  /**
   * @param se
   * @param md
   * @throws FeedException
   */
  private void populateModules(SyndEntry se, MdsPublicationVO md) throws SyndicationException {
    // populateMediaRss(se, md);

  }

  /**
   * set channel limitations
   */
  private void setChannelLimitations() {

    // set setMaximumRecords to 15 for RSS 0.9, 0.91N, 091U
    if (isRSS_09_or_091N_or_091U() && Integer.parseInt(getMaximumRecords()) > 15)
      setMaximumRecords("15");

    // length of the channel/image title of RSS 0.9 <= 40
    if (getFeedType().equals("rss_0.9")) {
      setTitle(Utils.cutString(getTitle(), 40, "..."));
      getImage().setTitle(Utils.cutString(getImage().getTitle(), 40, "..."));
    }

  }


  /**
   * set entry limitations
   */
  private void setEntryLimitations(SyndEntry se) {
    if (isRSS_09_or_091N_or_091U()) {
      se.setTitle(Utils.cutString(se.getTitle(), 100, "..."));
      se.getDescription().setValue(Utils.cutString(se.getDescription().getValue(), 500, "..."));
    }

  }


  /**
   * <code>True</code> for RSS 0.9, 0.91*
   * 
   * @return
   */
  private boolean isRSS_09_or_091N_or_091U() {
    return "rss_0.9".equals(getFeedType()) || getFeedType().contains("rss_0.91");
  }



}