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

package de.mpg.mpdl.inge.pubman.web.util.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Thread that creates Sitemap files.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Component
public class SiteMapTask {
  private static final Logger logger = Logger.getLogger(SiteMapTask.class);

  public static final String SITEMAP_PATH = System.getProperty("jboss.home.dir") + "/standalone/data/sitemap/";

  private ArrayList<String> contentModels;

  private FileWriter fileWriter = null;

  private final List<File> files = new ArrayList<File>();

  private SimpleDateFormat dateFormat;

  private String contentModel;
  private String contextPath;
  private String instanceUrl;
  private String itemPattern;

  private boolean signal = false;

  private int maxItemsPerFile;
  private int maxItemsPerRetrieve;



  @Autowired
  private PubItemService pubItemService;


  @Autowired
  private OrganizationService ouService;


  /**
   * {@inheritDoc}
   */
  @Scheduled(cron = "${inge.cron.pubman.sitemap}")
  public void run() {
    try {
      SiteMapTask.logger.info("CRON: Starting to create Sitemap.");
      this.instanceUrl = PropertyReader.getProperty("inge.pubman.instance.url");
      this.contextPath = PropertyReader.getProperty("inge.pubman.instance.context.path");
      this.itemPattern = PropertyReader.getProperty("inge.pubman.item.pattern");

      /*
       * this.interval =
       * Integer.parseInt(PropertyReader.getProperty("inge.pubman.sitemap.task.interval"));
       */
      this.maxItemsPerFile = Integer.parseInt(PropertyReader.getProperty("inge.pubman.sitemap.max.items"));
      this.maxItemsPerRetrieve = Integer.parseInt(PropertyReader.getProperty("inge.pubman.sitemap.retrieve.items"));



      this.contentModel = PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");

      this.contentModels = new ArrayList<String>();
      this.contentModels.add(this.contentModel);

      this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      this.changeFile();

      final long alreadyWritten = this.addViewItemPages();

      // this.addOUSearchResultPages(alreadyWritten);

      this.finishSitemap();

      // String appPath = System.getProperty("jboss.home.dir") + "/modules/pubman/main/sitemap/";
      new File(SiteMapTask.SITEMAP_PATH).mkdir();
      /*
       * try { appPath = ResourceUtil.getResourceAsFile("EditItemPage.jsp",
       * SiteMapTask.class.getClassLoader()).getAbsolutePath(); } catch (Exception e) {
       * logger.error("EditItemPage.jsp was not found in web root, terminating sitemap task", e);
       * return; } appPath = appPath.substring(0,
       * appPath.lastIndexOf(System.getProperty("file.separator")) + 1);
       */
      if (this.files.size() == 1) {
        final File finalFile = new File(SiteMapTask.SITEMAP_PATH + "sitemap.xml");
        try {
          finalFile.delete();
        } catch (final Exception e) {
          // Unable to delete file, it probably didn't exist
        }
        this.fileWriter = new FileWriter(SiteMapTask.SITEMAP_PATH + "sitemap.xml");

        // File newSiteMap = new File(SITEMAP_PATH + "sitemap.xml");
        this.copySiteMap(this.files.get(0), finalFile, (int) this.files.get(0).length(), true);
      } else {
        final String currentDate = this.dateFormat.format(new Date());

        final File indexFile = File.createTempFile("sitemap", ".xml");
        final FileWriter indexFileWriter = new FileWriter(indexFile);

        indexFileWriter
            .write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 "
                + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");

        for (int i = 0; i < this.files.size(); i++) {
          final File finalFile = new File(SiteMapTask.SITEMAP_PATH + "sitemap" + (i + 1) + ".xml");
          try {
            finalFile.delete();
          } catch (final Exception e) {
            // Unable to delete file, it probably didn't exist
          }
          this.copySiteMap(this.files.get(i), finalFile, (int) this.files.get(i).length(), true);

          indexFileWriter.write("\t<sitemap>\n\t\t<loc>" + this.instanceUrl + this.contextPath + "/sitemap" + (i + 1)
              + ".xml</loc>\n\t\t<lastmod>" + currentDate + "</lastmod>\n\t</sitemap>\n");

        }

        indexFileWriter.write("</sitemapindex>\n");
        indexFileWriter.flush();
        indexFileWriter.close();

        final File finalFile = new File(SiteMapTask.SITEMAP_PATH + "sitemap.xml");
        SiteMapTask.logger.info("Sitemap file: " + finalFile.getAbsolutePath());
        try {
          finalFile.delete();
        } catch (final Exception e) {
          // Unable to delete file, it probably didn't exist
        }
        final boolean success = this.copySiteMap(indexFile, finalFile, (int) indexFile.length(), true);
        SiteMapTask.logger.debug("Renaming succeeded: " + success);
      }

      SiteMapTask.logger.info("CRON: Finished creating Sitemap.");
    } catch (final Exception e) {
      SiteMapTask.logger.error("Error creating Sitemap", e);
    }



  }

  private boolean copySiteMap(File src, File dest, int bufSize, boolean force) throws IOException {
    boolean successful = false;
    if (dest.exists()) {
      if (force) {
        dest.delete();
      } else {
        throw new IOException("Cannot overwrite existing file: " + dest.getName());
      }
    }
    final byte[] buffer = new byte[bufSize];
    int read = 0;
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(src);
      out = new FileOutputStream(dest);
      while (true) {
        read = in.read(buffer);
        if (read == -1) {
          break;
        }
        out.write(buffer, 0, read);
        successful = true;
      }
    } finally {
      if (in != null) {
        try {
          in.close();
          successful |= src.delete();
        } finally {
          if (out != null) {
            out.close();
          }
        }
      }
    }

    return successful;
  }

  private long addViewItemPages() {
    int firstRecord = 0;
    long totalRecords = 0;
    int writtenInThisFile = 0;


    QueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "RELEASED"))
        .must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "RELEASED"));

    SearchResponse resp = null;
    // fileWriter.write("<ul>");
    do {


      try {
        // logger.info("Trying to creatie sitemap part for items from offset " + firstRecord +
        // " to " + (firstRecord+maxItemsPerRetrieve));

        logger.debug("SiteMapTask: Querying items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve));

        if (resp == null) {
          SearchSourceBuilder ssb = new SearchSourceBuilder();
          ssb.docValueField(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID).docValueField(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE)
              .query(qb).size(this.maxItemsPerRetrieve);
          resp = pubItemService.searchDetailed(ssb, new Scroll(new TimeValue(120000)), null);
        } else {
          resp = pubItemService.scrollOn(resp.getScrollId(), new Scroll(new TimeValue(120000)));
        }


        totalRecords = resp.getHits().getTotalHits();

        for (final SearchHit result : resp.getHits().getHits()) {

          // final ItemVersionVO pubItemVO = new ItemVersionVO(result.getData());
          try {
            this.fileWriter.write("\t<url>\n\t\t<loc>");
            this.fileWriter.write(this.instanceUrl);
            this.fileWriter.write(this.contextPath);
            this.fileWriter.write(this.itemPattern.replace("$1", result.field(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID).getValue()));
            this.fileWriter.write("</loc>\n\t\t<lastmod>");
            Long lmd = result.field(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE).getValue();
            this.fileWriter.write(dateFormat.format(new Date(lmd)));
            this.fileWriter.write("</lastmod>\n\t</url>\n");
            writtenInThisFile++;

            if (writtenInThisFile == maxItemsPerFile) {
              changeFile();
              writtenInThisFile = 0;
            }

          } catch (final Exception e) {
            SiteMapTask.logger.error("Error", e);
          }

        }

        /*
         * final ItemContainerSearchResult itemSearchResult = this.getItems(firstRecord);
         * totalRecords = itemSearchResult.getTotalNumberOfResults().intValue();
         * this.addItemsToSitemap(itemSearchResult);
         */

        logger.debug("SiteMapTask: finished with items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve));
        firstRecord += this.maxItemsPerRetrieve;



      } catch (final Exception e) {
        SiteMapTask.logger.error(
            "Error while creating sitemap part for items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve),
            e);
      }


    } while (resp.getHits().getHits().length != 0);

    return totalRecords;
  }

  private void addOUSearchResultPages(int alreadyWritten) {
    this.changeFile();

    int firstRecord = 0;
    int totalRecords = 0;

    // fileWriter.write("<ul>");
    do {
      try {
        final SearchRetrieveResponseVO<AffiliationDbVO> ouSearchResult = this.getOUs(firstRecord);
        totalRecords = ouSearchResult.getNumberOfRecords();
        this.addOUsToSitemap(ouSearchResult);

        firstRecord += this.maxItemsPerRetrieve;

        if (firstRecord <= totalRecords && firstRecord % this.maxItemsPerFile == 0) {
          this.changeFile();
        }
      } catch (final Exception e) {
        SiteMapTask.logger.error(
            "Error while creating sitemap part for ous from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve));
      }


    } while (firstRecord <= totalRecords);
  }

  private void changeFile() {
    try {
      if (this.fileWriter != null) {
        this.finishSitemap();
      }

      final File file = File.createTempFile("sitemap", ".xml");
      this.fileWriter = new FileWriter(file);
      this.files.add(file);

      this.startSitemap();
    } catch (final Exception e) {
      SiteMapTask.logger.error("Error creating sitemap file.", e);
    }
  }



  /**
   * @param contentModels
   * @param orgUnit
   * @return
   * @throws TechnicalException
   * @throws Exception
   */
  private SearchRetrieveResponseVO<AffiliationDbVO> getOUs(int firstRecord) throws Exception {
    // SearchQuery ouQuery = new PlainCqlQuery("(escidoc.any-identifier=e*)");

    SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(null, firstRecord, this.maxItemsPerRetrieve);
    SearchRetrieveResponseVO<AffiliationDbVO> resp = ouService.search(srr, null);


    return resp;
    /*
     * final SearchQuery ouQuery = new
     * PlainCqlQuery("(escidoc.public-status=opened or escidoc.public-status=closed)");
     * ouQuery.setStartRecord(firstRecord + ""); ouQuery.setMaximumRecords(this.maxItemsPerRetrieve
     * + ""); try { final OrgUnitsSearchResult ouSearchResult =
     * SearchService.searchForOrganizationalUnits(ouQuery); return ouSearchResult; } catch (final
     * Exception e) { SiteMapTask.logger.error("Error getting ous", e); }
     * 
     * return null;
     */
  }

  private void startSitemap() {
    try {
      this.fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
          + "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 "
          + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">\n");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }



  private void addOUsToSitemap(SearchRetrieveResponseVO<AffiliationDbVO> searchResult) {

    for (final SearchRetrieveRecordVO<AffiliationDbVO> result : searchResult.getRecords()) {

      try {
        this.fileWriter.write("\t<url>\n\t\t<loc>");
        this.fileWriter.write(this.instanceUrl);
        this.fileWriter.write(this.contextPath);
        this.fileWriter.write("/faces/SearchResultListPage.jsp?cql=((escidoc.any-organization-pids%3D%22");
        this.fileWriter.write(result.getData().getReference().getObjectId());
        this.fileWriter.write("%22)+and+(escidoc.objecttype%3D%22item%22))+and+(escidoc.content-model.objid%3D%22");
        this.fileWriter.write(this.contentModel);
        this.fileWriter.write("%22)&amp;searchType=org");
        this.fileWriter.write("</loc>\n\t</url>\n");
      } catch (final Exception e) {
        SiteMapTask.logger.error("Error", e);
      }
    }
  }

  private void finishSitemap() {
    try {
      this.fileWriter.write("</urlset>");
      this.fileWriter.flush();
      this.fileWriter.close();
    } catch (final Exception e) {
      SiteMapTask.logger.error("Error", e);
    }
  }



}
