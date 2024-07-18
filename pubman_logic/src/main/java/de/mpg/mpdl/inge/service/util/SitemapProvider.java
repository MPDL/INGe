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

  public static final String SITEMAP_PATH = System.getProperty(PropertyReader.JBOSS_HOME_DIR) + "/standalone/data/sitemap/";

  private FileWriter fileWriter = null;

  private final List<File> files = new ArrayList<>();

  private int maxItemsPerFile;
  private int maxItemsPerRetrieve;

  private int writtenInCurrentFile = 0;

  @Autowired
  private PubItemService pubItemService;

  public SitemapProvider() {}

  @Scheduled(cron = "${inge.cron.pubman.sitemap}")
  public void run() {
    try {
      logger.info("CRON: Starting to create Sitemap.");
      this.maxItemsPerFile = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_SITEMAP_MAX_ITEMS));
      this.maxItemsPerRetrieve = Integer.parseInt(PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_SITEMAP_RETRIEVE_ITEMS));
      String restUrl = PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL);
      String contextPath = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_INSTANCE_CONTEXT_PATH);
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      this.changeFile();
      this.addViewItemPages();
      this.finishSitemap();

      new File(SitemapProvider.SITEMAP_PATH).mkdir();

      if (1 == this.files.size()) {
        File finalFile = new File(SitemapProvider.SITEMAP_PATH + "sitemap.xml");
        try {
          finalFile.delete();
        } catch (Exception e) {
          // Unable to delete file, it probably didn't exist
        }
        this.fileWriter = new FileWriter(SitemapProvider.SITEMAP_PATH + "sitemap.xml");

        // File newSiteMap = new File(SITEMAP_PATH + "sitemap.xml");
        this.copySiteMap(this.files.get(0), finalFile, (int) this.files.get(0).length(), true);
      } else {
        String currentDate = dateFormat.format(new Date());

        File indexFile = File.createTempFile("sitemap", ".xml");
        FileWriter indexFileWriter = new FileWriter(indexFile);

        indexFileWriter
            .write("""
                <?xml version="1.0" encoding="UTF-8"?>
                <sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd">
                """);

        for (int i = 0; i < this.files.size(); i++) {
          File finalFile = new File(SitemapProvider.SITEMAP_PATH + "sitemap" + (i + 1) + ".xml");
          try {
            finalFile.delete();
          } catch (Exception e) {
            // Unable to delete file, it probably didn't exist
          }
          this.copySiteMap(this.files.get(i), finalFile, (int) this.files.get(i).length(), true);

          indexFileWriter.write("\t<sitemap>\n\t\t<loc>" + restUrl + "/sitemap/sitemap" + (i + 1) + ".xml</loc>\n\t\t<lastmod>"
              + currentDate + "</lastmod>\n\t</sitemap>\n");
        }

        indexFileWriter.write("</sitemapindex>\n");
        indexFileWriter.flush();
        indexFileWriter.close();

        File finalFile = new File(SitemapProvider.SITEMAP_PATH + "sitemap.xml");
        logger.info("Sitemap file: " + finalFile.getAbsolutePath());
        try {
          finalFile.delete();
        } catch (Exception e) {
          // Unable to delete file, it probably didn't exist
        }
        boolean success = this.copySiteMap(indexFile, finalFile, (int) indexFile.length(), true);
        logger.debug("Renaming succeeded: " + success);
      }

      logger.info("CRON: Finished creating Sitemap.");
    } catch (Exception e) {
      logger.error("Error creating Sitemap", e);
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
        successful = true;
      }
    } finally {
      if (null != in) {
        try {
          in.close();
          successful = successful || src.delete();
        } finally {
          if (null != out) {
            out.close();
          }
        }
      }
    }

    return successful;
  }

  private void addViewItemPages() {
    int firstRecord = 0;
    long totalRecords = 0;
    this.writtenInCurrentFile = 0;

    Query qb = BoolQuery.of(b -> b.must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_PUBLIC_STATE).value("RELEASED"))._toQuery())
        .must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_VERSION_STATE).value("RELEASED"))._toQuery()))._toQuery();

    ResponseBody<ObjectNode> resp = null;
    do {
      try {
        logger.debug("SiteMapTask: Querying items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve));

        if (null == resp) {
          //SearchSourceBuilder ssb = new SearchSourceBuilder();
          SearchRequest.Builder sr = new SearchRequest.Builder();

          String[] includes = {PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, PubItemServiceDbImpl.INDEX_VERSION_VERSIONNUMBER,
              PubItemServiceDbImpl.INDEX_MODIFICATION_DATE, PubItemServiceDbImpl.INDEX_FILE_OBJECT_ID,
              PubItemServiceDbImpl.INDEX_FILE_VISIBILITY, PubItemServiceDbImpl.INDEX_FILE_STORAGE, PubItemServiceDbImpl.INDEX_FILE_NAME};

          SourceFilter sf = SourceFilter.of(s -> s.includes(Arrays.asList(includes)));
          sr.source(SourceConfig.of(sc -> sc.filter(sf))).query(qb).size(this.maxItemsPerRetrieve);

          resp = this.pubItemService.searchDetailed(sr.build(), 120000, null);
        } else {
          resp = this.pubItemService.scrollOn(resp.scrollId(), 120000);
        }

        totalRecords = resp.hits().total().value();

        for (Hit<ObjectNode> result : resp.hits().hits()) {

          //Map<String, Object> sourceMap = result.getSourceAsMap();
          ObjectNode root = result.source();
          try {
            String itemId = root.get(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID).asText();
            String lmd = root.get(PubItemServiceDbImpl.INDEX_MODIFICATION_DATE).asText().substring(0, 10);
            String loc = UriBuilder.getItemObjectLink(itemId).toString();
            //String loc = this.instanceUrl + this.contextPath + this.itemPattern.replace("$1", itemId);
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
                    String fileLoc = UriBuilder.getItemComponentLink(itemId, Integer.parseInt(version), fileId, fileName).toString();
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

        logger.debug("SiteMapTask: finished with items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve));
        firstRecord += this.maxItemsPerRetrieve;
      } catch (Exception e) {
        logger.error(
            "Error while creating sitemap part for items from offset " + firstRecord + " to " + (firstRecord + this.maxItemsPerRetrieve),
            e);
      }
    } while (!resp.hits().hits().isEmpty());
  }

  private void changeFile() {
    try {
      if (null != this.fileWriter) {
        this.finishSitemap();
      }

      File file = File.createTempFile("sitemap", ".xml");
      this.fileWriter = new FileWriter(file);
      this.files.add(file);

      this.startSitemap();
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
    if (this.writtenInCurrentFile >= this.maxItemsPerFile) {
      changeFile();
      this.writtenInCurrentFile = 0;
    }
  }
}
