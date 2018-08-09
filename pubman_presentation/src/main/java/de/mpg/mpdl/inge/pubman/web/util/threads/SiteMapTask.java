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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Storage;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO.Visibility;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.XmlUtilities;

/**
 * Thread that creates Sitemap files.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */

public class SiteMapTask {
  private static final Logger logger = Logger.getLogger(SiteMapTask.class);

  public static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";

  //  private ArrayList<String> contentModels;

  private FileWriter fileWriter = null;

  private final List<File> files = new ArrayList<File>();

  private SimpleDateFormat dateFormat;

  //  private String contentModel;
  private String contextPath;
  private String instanceUrl;
  private String itemPattern;

  //  private boolean signal = false;

  private int maxItemsPerFile;
  private int maxItemsPerRetrieve;



  @Autowired
  private PubItemService pubItemService;

  private String componentPattern;


  //  @Autowired
  //  private OrganizationService ouService;


  /**
   * {@inheritDoc}
   */

  public void run() {
    try {
      SiteMapTask.logger.info("CRON: Starting to create Sitemap.");
      this.instanceUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_URL);
      this.contextPath = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
      this.itemPattern = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_ITEM_PATTERN);
      this.componentPattern = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_COMPONENT_PATTERN);
      this.maxItemsPerFile = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_SITEMAP_MAX_ITEMS));
      this.maxItemsPerRetrieve = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_SITEMAP_RETRIEVE_ITEMS));


      this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      this.changeFile();

      this.addViewItemPages();



      this.finishSitemap();

      new File(SiteMapTask.SITEMAP_PATH).mkdir();

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
    do {


      try {

        logger.debug("SiteMapTask: Querying items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve));

        if (resp == null) {
          SearchSourceBuilder ssb = new SearchSourceBuilder();
          ssb.fetchSource(new String[] {PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER,
              PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, PubItemServiceDbImpl.INDEX_FILE_OBJECT_ID,
              PubItemServiceDbImpl.INDEX_FILE_VISIBILITY, PubItemServiceDbImpl.INDEX_FILE_STORAGE, PubItemServiceDbImpl.INDEX_FILE_NAME},
              null).query(qb).size(this.maxItemsPerRetrieve);
          resp = pubItemService.searchDetailed(ssb, 120000, null);
        } else {
          resp = pubItemService.scrollOn(resp.getScrollId(), 120000);
        }


        totalRecords = resp.getHits().getTotalHits();

        for (final SearchHit result : resp.getHits().getHits()) {

          Map<String, Object> sourceMap = result.getSourceAsMap();
          try {

            String itemId = sourceMap.get(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID).toString();
            String lmd = sourceMap.get(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE).toString().substring(0, 10);
            String loc = this.instanceUrl + this.contextPath + this.itemPattern.replace("$1", itemId);
            String version = sourceMap.get(PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER).toString();

            writeEntry(this.fileWriter, loc, lmd);
            writtenInThisFile++;

            if (sourceMap.containsKey("files")) {
              List<Map<String, Object>> fileList = (List<Map<String, Object>>) sourceMap.get("files");

              for (Map<String, Object> fileMap : fileList) {
                String storage = fileMap.get("storage").toString();
                if (Storage.INTERNAL_MANAGED.name().equals(storage)) {
                  String visibility = fileMap.get("visibility").toString();
                  if (Visibility.PUBLIC.name().equals(visibility)) {
                    String fileId = fileMap.get("objectId").toString();
                    String fileName = fileMap.get("name").toString();
                    String fileLoc = this.instanceUrl + this.contextPath + this.componentPattern.replace("$1", itemId + "_" + version)
                        .replace("$2", fileId).replace("$3", URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
                    fileLoc = XmlUtilities.escape(fileLoc);
                    writeEntry(this.fileWriter, fileLoc, lmd);
                    writtenInThisFile++;
                  }
                }
              }
            }


            if (writtenInThisFile == maxItemsPerFile) {
              changeFile();
              writtenInThisFile = 0;
            }

          } catch (final Exception e) {
            SiteMapTask.logger.error("Error", e);
          }

        }

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

  private void finishSitemap() {
    try {
      this.fileWriter.write("</urlset>");
      this.fileWriter.flush();
      this.fileWriter.close();
    } catch (final Exception e) {
      SiteMapTask.logger.error("Error", e);
    }
  }


  public static void writeEntry(FileWriter fw, String loc, String lmd) throws IOException {
    fw.write("\t<url>\n\t\t<loc>");
    fw.write(loc);
    fw.write("</loc>\n\t\t<lastmod>");
    fw.write(lmd);
    fw.write("</lastmod>\n\t</url>\n");

  }



}
