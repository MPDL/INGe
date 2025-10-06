package de.mpg.mpdl.inge.service.util;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.UriBuilder;
import de.mpg.mpdl.inge.util.XmlUtilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SitemapProvider {
  private static final Logger logger = LogManager.getLogger(SitemapProvider.class);

  public static final int MAX_ITEMS_PER_FILE = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_SITEMAP_MAX_ITEMS));
  public static final int MAX_ITEMS_PER_RETRIEVE =
      Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_SITEMAP_RETRIEVE_ITEMS));
  public static final String REST_URL = PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL);
  public static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";

  private List<File> files;
  private FileWriter fileWriter;
  private int writtenInCurrentFile;

  @Autowired
  private PubItemService pubItemService;

  public SitemapProvider() {}

  @Scheduled(cron = "${inge.cron.pubman.sitemap}")
  public void run() {
    try {
      logger.info("*** CRON (" + PropertyReader.getProperty(PropertyReader.INGE_CRON_PUBMAN_SITEMAP) + "): Starting to create Sitemap.");

      this.files = new ArrayList<>();
      this.fileWriter = null;
      writtenInCurrentFile = 0;

      changeFile();
      addViewItemPages();
      finishSitemap();

      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      writeSitemapFiles(dateFormat, SitemapProvider.REST_URL);

      cleanupTmpFiles();

      logger.info("*** CRON: Finished creating Sitemap.");
    } catch (Exception e) {
      logger.error("*** CRON: Error creating Sitemap", e);
    }
  }

  private void cleanupTmpFiles() {
    for (File file : this.files) {
      logger.info("Try to delete file " + file.getName());
      if (file.delete()) {
        logger.info("Done.");
      } else {
        logger.error("File does not exist or could not be deleted.");
      }
    }
  }

  private void writeSitemapFiles(SimpleDateFormat dateFormat, String restUrl) throws IOException {
    new File(SitemapProvider.SITEMAP_PATH).mkdir();

    logger.info("Number of files: " + this.files.size());
    if (1 == this.files.size()) {
      File finalFile = new File(SitemapProvider.SITEMAP_PATH + "sitemap.xml");
      this.fileWriter = new FileWriter(SitemapProvider.SITEMAP_PATH + "sitemap.xml");
      copySiteMap(this.files.get(0), finalFile, (int) this.files.get(0).length());
    } else {
      String currentDate = dateFormat.format(new Date());

      logger.info("Try to create temp indexfile:");
      File indexFile = File.createTempFile("sitemap", ".xml");
      logger.info("Done: " + indexFile.getName());

      FileWriter indexFileWriter = new FileWriter(indexFile);
      indexFileWriter.write("""
          <?xml version="1.0" encoding="UTF-8"?>
          <sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
          """);

      for (int i = 0; i < this.files.size(); i++) {
        File finalFile = new File(SitemapProvider.SITEMAP_PATH + "sitemap" + (i + 1) + ".xml");
        copySiteMap(this.files.get(i), finalFile, (int) this.files.get(i).length());

        indexFileWriter.write(
            "\t<sitemap>\n\t\t<loc>" + restUrl + "/miscellaneous/sitemap" + (i + 1) + ".xml</loc>\n\t\t<lastmod>" + currentDate + "</lastmod>\n\t</sitemap>\n");
      }

      indexFileWriter.write("</sitemapindex>\n");
      indexFileWriter.flush();
      indexFileWriter.close();

      File finalFile = new File(SitemapProvider.SITEMAP_PATH + "sitemap.xml");
      logger.info("Sitemap file: " + finalFile.getName());

      copySiteMap(indexFile, finalFile, (int) indexFile.length());

      logger.info("Try to delete indexFile " + indexFile.getName());
      if (indexFile.delete()) {
        logger.info("Done.");
      } else {
        logger.error("File does not exist or could not be deleted.");
      }
    }
  }

  private void copySiteMap(File src, File dest, int bufSize) throws IOException {
    if (dest.exists()) {
      logger.info("Try to delete dest " + dest.getName());
      if (dest.delete()) {
        logger.info("Done.");
      } else {
        throw new IOException("Cannot overwrite existing file: " + dest.getName());
      }
    }

    byte[] buffer = new byte[bufSize];
    int read = 0;
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(src);
      out = new FileOutputStream(dest);
      while (true) {
        read = in.read(buffer);
        if (-1 == read) {
          break;
        }
        out.write(buffer, 0, read);
      }
    } finally {
      if (null != in) {
        try {
          in.close();
        } finally {
          if (null != out) {
            out.close();
          }
        }
      }
    }
  }

  private void addViewItemPages() {
    int firstRecord = 0;
    this.writtenInCurrentFile = 0;

    Query qb = BoolQuery.of(b -> b.must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_PUBLIC_STATE).value("RELEASED"))._toQuery())
        .must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_VERSION_STATE).value("RELEASED"))._toQuery()))._toQuery();

    ResponseBody<ObjectNode> resp = null;
    do {
      try {
        if (null == resp) {
          SearchRequest.Builder sr = new SearchRequest.Builder();

          String[] includes = {PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER,
              PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, PubItemServiceDbImpl.INDEX_FILE_OBJECT_ID,
              PubItemServiceDbImpl.INDEX_FILE_VISIBILITY, PubItemServiceDbImpl.INDEX_FILE_STORAGE, PubItemServiceDbImpl.INDEX_FILE_NAME};

          SourceFilter sf = SourceFilter.of(s -> s.includes(Arrays.asList(includes)));
          sr.source(SourceConfig.of(sc -> sc.filter(sf))).query(qb).size(SitemapProvider.MAX_ITEMS_PER_RETRIEVE);

          resp = this.pubItemService.searchDetailed(sr.build(), 120000, null);
        } else {
          resp = this.pubItemService.scrollOn(resp.scrollId(), 120000);
        }

        for (Hit<ObjectNode> result : resp.hits().hits()) {

          ObjectNode root = result.source();
          try {
            String itemId = root.get(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID).asText();
            String lmd = root.get(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE).asText().substring(0, 10);
            String loc = UriBuilder.getItemObjectLink(itemId).toString();
            String version = root.get(PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER).toString();

            writeEntry(this.fileWriter, loc, lmd);

            if (null != root.get("files")) {
              ArrayNode fileList = (ArrayNode) root.get("files");

              for (JsonNode fileMap : fileList) {
                ObjectNode file = (ObjectNode) fileMap;
                String storage = file.get("storage").asText();
                if (FileDbVO.Storage.INTERNAL_MANAGED.name().equals(storage)) {
                  String visibility = file.get("visibility").asText();
                  if (FileDbVO.Visibility.PUBLIC.name().equals(visibility)) {
                    String fileId = file.get("objectId").asText();
                    String fileName = file.get("name").asText();
                    String fileLoc = UriBuilder.getItemComponentLink(itemId, Integer.parseInt(version), fileId).toString();
                    fileLoc = XmlUtilities.escape(fileLoc);
                    writeEntry(this.fileWriter, fileLoc, lmd);
                  }
                }
              }
            }
          } catch (Exception e) {
            logger.error("Error", e);
          }
        }
        firstRecord += SitemapProvider.MAX_ITEMS_PER_RETRIEVE;
      } catch (Exception e) {
        logger.error("Error while creating sitemap part for items from offset " + firstRecord + " to "
            + (firstRecord + SitemapProvider.MAX_ITEMS_PER_RETRIEVE), e);
      }
    } while (!resp.hits().hits().isEmpty());
  }

  private void changeFile() {
    try {
      if (null != this.fileWriter) {
        finishSitemap();
      }

      logger.info("ChangeFile: Try to create temp file:");
      File file = File.createTempFile("sitemap", ".xml");
      logger.info("Done: " + file.getName());
      this.fileWriter = new FileWriter(file);
      this.files.add(file);
      logger.info("Added to files[]");

      startSitemap();
    } catch (Exception e) {
      logger.error("Error creating sitemap file.", e);
    }
  }

  private void startSitemap() {
    try {
      this.fileWriter.write("""
          <?xml version="1.0" encoding="UTF-8"?>
          <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
          """);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void finishSitemap() {
    try {
      this.fileWriter.write("</urlset>");
      this.fileWriter.flush();
      this.fileWriter.close();
    } catch (Exception e) {
      logger.error("Error", e);
    }
  }

  private void writeEntry(FileWriter fw, String loc, String lmd) throws IOException {
    fw.write("\t<url>\n\t\t<loc>");
    fw.write(loc);
    fw.write("</loc>\n\t\t<lastmod>");
    fw.write(lmd);
    fw.write("</lastmod>\n\t</url>\n");

    this.writtenInCurrentFile++;
    if (this.writtenInCurrentFile >= SitemapProvider.MAX_ITEMS_PER_FILE) {
      changeFile();
      this.writtenInCurrentFile = 0;
    }
  }
}
