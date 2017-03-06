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
package de.mpg.mpdl.inge.pubman.web.multipleimport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.referenceobjects.ContextRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.PubItemService;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.ErrorLevel;
import de.mpg.mpdl.inge.pubman.web.multipleimport.ImportLog.Status;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.ArxivProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.BibtexProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.BmcProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.EdocProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.EndnoteProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.EscidocProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.FormatProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.MabProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.Marc21Processor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.MarcXmlProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.RisProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.WosProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.ZfNProcessor;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.transformation.Configurable;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.TransformationService;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;
import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ProxyHelper;
import de.mpg.mpdl.inge.util.ResourceUtil;

/**
 * TODO Description
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImportProcess extends Thread {
  private static final Logger logger = Logger.getLogger(ImportProcess.class);

  public enum DuplicateStrategy {
    NO_CHECK, CHECK, ROLLBACK
  }

  private static final Format ARXIV_FORMAT = new Format("arxiv", "application/xml", "utf-8");
  private static final Format BIBTEX_FORMAT = new Format("bibtex", "text/plain", "utf-8");
  private static final Format BMC_FORMAT = new Format("bmc_editura", "application/xml", "UTF-8");
  private static final Format EDOC_FORMAT = new Format("edoc", "application/xml", "utf-8");
  private static final Format EDOC_FORMAT_AEI = new Format("eDoc-AEI", "application/xml", "utf-8");
  private static final Format ENDNOTE_FORMAT = new Format("endnote", "text/plain", "utf-8");
  private static final Format ENDNOTE_ICE_FORMAT = new Format("endnote-ice", "text/plain", "utf-8");
  private static final Format ESCIDOC_FORMAT = new Format("eSciDoc-publication-item",
      "application/xml", "utf-8");
  private static final Format MAB_FORMAT = new Format("mab", "text/plain", "UTF-8");
  private static final Format MARC21_FORMAT = new Format("marc21viaxml", "application/marc",
      "UTF-8");
  private static final Format MARCXML_FORMAT = new Format("marcxml", "application/marcxml+xml",
      "UTF-8");
  private static final Format RIS_FORMAT = new Format("ris", "text/plain", "utf-8");
  private static final Format WOS_FORMAT = new Format("wos", "text/plain", "utf-8");
  private static final Format ZFN_FORMAT = new Format("zfn_tei", "application/xml", "UTF-8");

  private AccountUserVO user;
  private ContextRO escidocContext;
  private DuplicateStrategy duplicateStrategy;
  private File file;
  private Format format;
  private FormatProcessor formatProcessor;
  private ImportLog log;
  private Map<String, String> configuration = null;
  private String fileName;
  private String itemContentModel;
  private String name;
  private String publicationContentModel;
  private Transformation transformation;
  private boolean failed = false;
  private boolean rollback;
  private long lastBeat = 0;

  public ImportProcess(String name, String fileName, File file, Format format,
      ContextRO escidocContext, AccountUserVO user, boolean rollback, int duplicateStrategy,
      Map<String, String> configuration) {
    try {
      this.publicationContentModel =
          PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
    } catch (Exception e) {
      throw new RuntimeException(
          "Error getting property 'escidoc.framework_access.content-model.id.publication'", e);
    }

    this.log = new ImportLog("import", user.getReference().getObjectId(), format.getName());
    this.log.setUserHandle(user.getHandle());
    this.log.setPercentage(5);
    this.log.startItem("import_process_started");
    this.log.finishItem();

    DuplicateStrategy strategy;
    if (duplicateStrategy == 1) {
      strategy = DuplicateStrategy.NO_CHECK;
    } else if (duplicateStrategy == 2) {
      strategy = DuplicateStrategy.CHECK;
    } else if (duplicateStrategy == 3) {
      strategy = DuplicateStrategy.ROLLBACK;
    } else {
      throw new RuntimeException("Invalid value " + duplicateStrategy + " for DuplicateStrategy");
    }

    initialize(name, fileName, file, format, escidocContext, user, rollback, strategy,
        configuration);

    this.log.setPercentage(7);
    if (this.log.isDone()) {
      return;
    }

    if (!validate(file, format)) {
      return;
    }

    this.log.setPercentage(10);
  }

  /**
   * @param inputStream
   * @param format
   */
  private void initialize(String name, String fileName, File file, Format format,
      ContextRO escidocContext, AccountUserVO user, boolean rollback,
      DuplicateStrategy duplicateStrategy, Map<String, String> configuration) {
    this.log.startItem("import_process_initialize");

    try {
      this.log.setMessage(name);
      this.log.setContext(escidocContext.getObjectId());
      this.log.setFormat(format.getName());

      this.configuration = configuration;
      this.duplicateStrategy = duplicateStrategy;
      this.escidocContext = escidocContext;
      this.file = file;
      this.fileName = fileName;
      this.format = format;
      this.itemContentModel =
          PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
      this.name = name;
      this.rollback = rollback;
      this.transformation = new TransformationService();
      this.user = user;
    } catch (Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_initialization_failed");
      this.log.addDetail(ErrorLevel.FATAL, e);
      fail();
    }

    this.log.finishItem();
  }

  /**
   * @param inputStream
   * @param format
   */
  private boolean validate(File file, Format format) {
    this.log.startItem("import_process_validate");

    if (file == null) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_inputstream_unavailable");
      fail();
      return false;
    } else {
      this.log.addDetail(ErrorLevel.FINE, "import_process_inputstream_available");
    }

    if (format == null) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_format_unavailable");
      fail();
      return false;
    } else {
      this.log.addDetail(ErrorLevel.FINE, "import_process_format_available");
    }

    Format[] allSourceFormats = transformation.getSourceFormats(ESCIDOC_FORMAT);

    boolean found = false;
    for (Format sourceFormat : allSourceFormats) {
      if (format.matches(sourceFormat)) {
        found = true;
        if (setProcessor(format)) {
          this.log.addDetail(ErrorLevel.FINE, "import_process_format_valid");
        } else {
          this.log.addDetail(ErrorLevel.FATAL, "import_process_format_not_supported");
          fail();
        }
        break;
      }
    }

    if (!found) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_format_invalid");
      fail();
      return false;
    }
    this.log.finishItem();
    return true;
  }

  private boolean setProcessor(Format format) {
    try {
      if (format == null) {
        return false;
      } else if (ENDNOTE_FORMAT.matches(format) || ENDNOTE_ICE_FORMAT.matches(format)) {
        this.formatProcessor = new EndnoteProcessor();
      } else if (RIS_FORMAT.matches(format)) {
        this.formatProcessor = new RisProcessor();
      } else if (BIBTEX_FORMAT.matches(format)) {
        this.formatProcessor = new BibtexProcessor();
      } else if (ARXIV_FORMAT.matches(format)) {
        this.formatProcessor = new ArxivProcessor();
      } else if (WOS_FORMAT.matches(format)) {
        this.formatProcessor = new WosProcessor();
      } else if (ESCIDOC_FORMAT.matches(format)) {
        this.formatProcessor = new EscidocProcessor();
      } else if (EDOC_FORMAT.matches(format) || EDOC_FORMAT_AEI.matches(format)) {
        this.formatProcessor = new EdocProcessor();
      } else if (MAB_FORMAT.matches(format)) {
        this.formatProcessor = new MabProcessor();
      } else if (ZFN_FORMAT.matches(format)) {
        this.formatProcessor = new ZfNProcessor();
      } else if (BMC_FORMAT.matches(format)) {
        this.formatProcessor = new BmcProcessor();
      } else if (MARCXML_FORMAT.matches(format)) {
        this.formatProcessor = new MarcXmlProcessor();
      } else if (MARC21_FORMAT.matches(format)) {
        this.formatProcessor = new Marc21Processor();
      } else {
        return false;
      }
    } catch (Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_format_error");
      this.log.addDetail(ErrorLevel.FATAL, e);
      fail();
    }
    this.formatProcessor.setEncoding(format.getEncoding());
    return true;
  }

  private void fail() {
    this.failed = true;
    this.log.finishItem();
    this.log.startItem(ErrorLevel.FATAL, "import_process_failed");
    this.log.finishItem();
    if (this.rollback) {
      this.log.setStatus(Status.ROLLBACK);
      rollback();
    }
    this.log.close();
  }

  private void rollback() {
    this.log.startItem(ErrorLevel.FINE, "import_process_rollback");
    this.log.finishItem();
    this.log.close();
    DeleteProcess deleteProcess = new DeleteProcess(log);
    deleteProcess.start();
  }

  /**
   * {@inheritDoc}
   */
  public void run() {
    int counter = 0;
    int itemCount = 1;
    if (!failed) {
      try {
        this.log.startItem("import_process_start_import");
        this.formatProcessor.setSourceFile(file);
        if (this.formatProcessor.hasNext()) {
          itemCount = this.formatProcessor.getLength();
        }
        this.log.finishItem();
        while (this.formatProcessor.hasNext() && !failed) {
          try {
            if (this.log.getCurrentItem() == null) {
              this.log.startItem("import_process_import_item");
            }
            String singleItem = this.formatProcessor.next();
            if (failed) {
              return;
            }
            if (singleItem != null && !"".equals(singleItem.trim())) {
              prepareItem(singleItem);
            }
            counter++;
            this.log.setPercentage(30 * counter / itemCount + 10);
          } catch (Exception e) {
            logger.error("Error during import", e);
            this.log.addDetail(ErrorLevel.ERROR, e);
            this.log.finishItem();
            if (this.rollback) {
              fail();
              break;
            }
          }
          heartBeat();
        }
      } catch (Exception e) {
        logger.error("Error during import", e);
        this.log.addDetail(ErrorLevel.ERROR, e);
        this.log.finishItem();
        if (this.rollback) {
          fail();
        }
      }
      if (failed) {
        return;
      }
      this.log.finishItem();
      this.log.startItem("import_process_preparation_finished");
      this.log.addDetail(ErrorLevel.FINE, "import_process_no_more_items");
      this.log.finishItem();
      this.log.setPercentage(40);
      counter = 0;
      for (int i = 0; i < this.log.getItems().size(); i++) {
        ImportLogItem item = this.log.getItems().get(i);
        if (item.getStatus() == Status.SUSPENDED && item.getItemVO() != null && !failed) {
          try {
            this.log.activateItem(item);

            // Fetch files for zfn import
            if (this.format.getName().equalsIgnoreCase("zfn_tei")) {
              try {
                // Set file
                FileVO file =
                    ((ZfNProcessor) this.formatProcessor).getFileforImport(this.configuration,
                        this.user);
                item.getItemVO().getFiles().add(file);
              } catch (Exception e) {
                this.log.addDetail(ErrorLevel.WARNING, "Could not fetch file for import");
              }
            }

            this.log.addDetail(ErrorLevel.FINE, "import_process_save_item");

            PubItemVO savedPubItem = PubItemService.savePubItem(item.getItemVO(), user);
            String objid = savedPubItem.getVersion().getObjectId();
            this.log.setItemId(objid);
            this.log.addDetail(ErrorLevel.FINE, "import_process_item_imported");
            this.log.finishItem();
            counter++;
            this.log.setPercentage(55 * counter / itemCount + 40);
          } catch (Exception e) {
            logger.error("Error during import", e);
            this.log.addDetail(ErrorLevel.ERROR, e);
            this.log.finishItem();
            if (this.rollback) {
              fail();
              break;
            }
          }
        }
      }

      if (!failed) {
        this.log.startItem("import_process_finished");
        this.log.addDetail(ErrorLevel.FINE, "import_process_import_finished");
        this.log.finishItem();
        try {
          this.log.startItem("import_process_archive_log");
          this.log.addDetail(ErrorLevel.FINE, "import_process_build_task_item");
          // Store log in repository
          String taskItemXml = createTaskItemXml();
          ItemHandler itemHandler = ServiceLocator.getItemHandler(this.user.getHandle());
          String savedTaskItemXml = itemHandler.create(taskItemXml);
          Pattern pattern = Pattern.compile("objid=\"([^\"]+)\"");
          Matcher matcher = pattern.matcher(savedTaskItemXml);
          if (matcher.find()) {
            String taskId = matcher.group(1);
            logger.info("Imported task item: " + taskId);
          }
          this.log.setPercentage(100);
        } catch (Exception e) {
          logger.error("Error during import", e);
          this.log.finishItem();
          this.log.startItem(ErrorLevel.ERROR, "import_process_error");
          this.log.addDetail(ErrorLevel.ERROR, e);
          fail();
        }
      }
    }

    // Close connection if no rollback is done. Otherwise, Connection is still required for delete
    // process
    if (!this.rollback) {
      this.log.closeConnection();
    }

    file.delete();
  }

  /**
   * Send a request to the framework every 30 minutes to make sure the user handle will not expire.
   */
  private void heartBeat() {
    long now = new Date().getTime();
    if ((now - lastBeat) > 1000 * 60 * 30) {
      logger.info("Refreshing " + this.log.getUserHandle());
      lastBeat = now;
      try {
        ServiceLocator.getContextHandler(this.log.getUserHandle()).retrieve(this.log.getContext());
      } catch (Exception e) {
        logger.warn("Heartbeat error", e);
      }
    }
  }

  private String createTaskItemXml() {
    try {
      String fwUrl = PropertyReader.getFrameworkUrl();
      HttpClient client = new HttpClient();
      ProxyHelper.setProxy(client, fwUrl);

      StringBuilder sb =
          new StringBuilder(ResourceUtil.getResourceAsString(
              "multipleImport/ImportTaskTemplate.xml", ImportProcess.class.getClassLoader()));
      replace("$01", escape(this.escidocContext.getObjectId()), sb);
      replace("$02", escape(PropertyReader.getProperty("escidoc.import.task.content-model")), sb);
      replace("$03", escape("Import Task Item for import " + name + " "), sb);

      // Upload original data
      PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
      method.setRequestHeader("Content-Type", this.format.toString());
      method.setRequestHeader("Cookie", "escidocCookie=" + this.user.getHandle());
      InputStream is = new FileInputStream(this.formatProcessor.getSourceFile());
      method.setRequestEntity(new InputStreamRequestEntity(is));
      client.executeMethod(method);
      is.close();
      String response = method.getResponseBodyAsString();
      URL originalDataUrl = XmlTransformingService.transformUploadResponseToFileURL(response);

      replace("$04", escape(this.name), sb);
      replace("$05", escape(this.fileName), sb);
      replace("$06", escape(originalDataUrl.toExternalForm()), sb);
      replace("$07", escape(this.log.getStoredId() + ""), sb);
      replace("$08", escape(this.format.toString()), sb);
      replace("$09", escape(String.valueOf(this.formatProcessor.getLength())), sb);

      // Upload and create task item xml
      File tempLogXml = File.createTempFile("multipleImportLogXml", "xml");
      Writer fw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempLogXml), "UTF-8"));
      this.log.toXML(fw);
      fw.flush();
      fw.close();

      PutMethod method2 = new PutMethod(fwUrl + "/st/staging-file");
      method2.setRequestHeader("Content-Type", "text/xml");
      method2.setRequestHeader("Cookie", "escidocCookie=" + this.user.getHandle());
      is = new FileInputStream(tempLogXml);
      method2.setRequestEntity(new InputStreamRequestEntity(is));
      client.executeMethod(method2);
      is.close();

      response = method2.getResponseBodyAsString();
      URL logXmlUrl = XmlTransformingService.transformUploadResponseToFileURL(response);

      replace("$10", escape(this.name), sb);
      replace("$11", "importthis.log.xml", sb);
      replace("$12", escape(logXmlUrl.toExternalForm()), sb);
      replace("$13", escape(this.log.getStoredId() + ""), sb);
      replace("$14", escape(String.valueOf(tempLogXml.length())), sb);

      tempLogXml.delete();

      this.log.finishItem();
      this.log.close();
      return sb.toString();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void replace(String target, String replacement, StringBuilder builder) {
    int indexOfTarget = -1;
    while ((indexOfTarget = builder.indexOf(target)) >= 0) {
      builder.replace(indexOfTarget, indexOfTarget + target.length(), replacement);
    }
  }

  private String escape(String string) {
    if (string != null) {
      return string.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;")
          .replace(">", "&gt;");
    }

    return null;
  }

  /**
   * @param writer
   * @param singleItem
   * @return
   */
  private void prepareItem(String singleItem) {
    this.log.addDetail(ErrorLevel.FINE, "import_process_source_data_found");
    this.log.addDetail(ErrorLevel.FINE, singleItem);
    this.log.addDetail(ErrorLevel.FINE, "import_process_start_transformation");
    String esidocXml = null;

    try {
      if (configuration != null && transformation instanceof Configurable) {
        esidocXml =
            new String(((Configurable) transformation).transform(
                singleItem.getBytes(this.format.getEncoding()), this.format, ESCIDOC_FORMAT,
                "escidoc", configuration), ESCIDOC_FORMAT.getEncoding());
      } else {
        esidocXml =
            new String(transformation.transform(singleItem.getBytes(this.format.getEncoding()),
                this.format, ESCIDOC_FORMAT, "escidoc"), ESCIDOC_FORMAT.getEncoding());
      }

      this.log.addDetail(ErrorLevel.FINE, esidocXml);
      this.log.addDetail(ErrorLevel.FINE, "import_process_transformation_done");
      PubItemVO pubItemVO = XmlTransformingService.transformToPubItem(esidocXml);
      pubItemVO.setContext(escidocContext);
      pubItemVO.setContentModel(publicationContentModel);
      pubItemVO.getVersion().setObjectId(null);
      pubItemVO.getLocalTags().add("multiple_import");
      pubItemVO.getLocalTags().add(this.log.getMessage() + " " + this.log.getStartDateFormatted());

      // Simple Validation
      this.log.addDetail(ErrorLevel.FINE, "import_process_default_validation");
      try {
        ItemValidatingService.validateItemObject(pubItemVO, ValidationPoint.SIMPLE);
        this.log.addDetail(ErrorLevel.FINE, "import_process_default_validation_successful");

        // Release Validation
        this.log.addDetail(ErrorLevel.FINE, "import_process_release_validation");
        try {
          ItemValidatingService.validateItemObject(pubItemVO, ValidationPoint.STANDARD);
          this.log.addDetail(ErrorLevel.FINE, "import_process_release_validation_successful");

          this.log.addDetail(ErrorLevel.FINE, "import_process_generate_item");
          this.log.setItemVO(pubItemVO);
          if (this.duplicateStrategy != DuplicateStrategy.NO_CHECK) {
            this.log.addDetail(ErrorLevel.FINE, "import_process_check_duplicates_by_identifier");
            boolean duplicatesDetected = checkDuplicatesByIdentifier(pubItemVO);
            if (duplicatesDetected && this.duplicateStrategy == DuplicateStrategy.ROLLBACK) {
              this.rollback = true;
              fail();
            } else if (duplicatesDetected) {
              this.log.addDetail(ErrorLevel.WARNING, "import_process_no_import");
              this.log.finishItem();
            } else {
              this.log.suspendItem();
            }
          } else {
            this.log.suspendItem();
          }
        } catch (ItemInvalidException e2) { // Release Validation
          this.log.addDetail(ErrorLevel.WARNING, "import_process_release_validation_failed");
          for (ValidationReportItemVO item : e2.getReport().getItems()) {
            this.log.addDetail(ErrorLevel.WARNING, item.getContent());
          }
        }
      } catch (ItemInvalidException e) { // Default Validation
        this.log.addDetail(ErrorLevel.PROBLEM, "import_process_default_validation_failed");
        for (ValidationReportItemVO item : e.getReport().getItems()) {
          this.log.addDetail(ErrorLevel.PROBLEM, item.getContent());
        }
        this.log.addDetail(ErrorLevel.PROBLEM, "import_process_item_not_imported");
        this.log.finishItem();
      }
    } catch (Exception e) {
      logger.error("Error while multiple import", e);
      this.log.addDetail(ErrorLevel.ERROR, e);
      this.log.addDetail(ErrorLevel.ERROR, "import_process_item_not_imported");
      if (this.rollback) {
        fail();
      }
      this.log.finishItem();
    }
  }

  private boolean checkDuplicatesByIdentifier(PubItemVO itemVO) {
    try {
      if (itemVO.getMetadata().getIdentifiers().size() > 0) {
        ArrayList<String> contentModels = new ArrayList<String>();
        contentModels.add(this.itemContentModel);
        ArrayList<MetadataSearchCriterion> criteria = new ArrayList<MetadataSearchCriterion>();
        boolean first = true;
        for (IdentifierVO identifierVO : itemVO.getMetadata().getIdentifiers()) {
          MetadataSearchCriterion criterion =
              new MetadataSearchCriterion(CriterionType.IDENTIFIER, identifierVO.getId(),
                  (first ? LogicalOperator.AND : LogicalOperator.OR));
          first = false;
          criteria.add(criterion);
        }
        MetadataSearchQuery query = new MetadataSearchQuery(contentModels, criteria);
        ItemContainerSearchResult searchResult = SearchService.searchForItemContainer(query);
        if (searchResult.getTotalNumberOfResults().equals(NonNegativeInteger.ZERO)) {
          this.log.addDetail(ErrorLevel.FINE, "import_process_no_duplicate_detected");
          return false;
        } else {
          this.log.addDetail(ErrorLevel.FINE, "import_process_duplicates_detected");
          for (ItemVO duplicate : searchResult.extractItemsOfSearchResult()) {
            if (this.itemContentModel.equals(duplicate.getContentModel())) {
              PubItemVO duplicatePubItemVO = new PubItemVO(duplicate);
              if (this.duplicateStrategy == DuplicateStrategy.ROLLBACK) {
                this.log.addDetail(ErrorLevel.PROBLEM, "import_process_duplicate_detected");
                this.log.addDetail(ErrorLevel.PROBLEM, duplicatePubItemVO.getVersion()
                    .getObjectId() + " \"" + duplicatePubItemVO.getMetadata().getTitle() + "\"",
                    duplicatePubItemVO.getVersion().getObjectId());
                return true;
              } else {
                this.log.addDetail(ErrorLevel.WARNING, "import_process_duplicate_detected");
                this.log.addDetail(ErrorLevel.WARNING, duplicatePubItemVO.getVersion()
                    .getObjectId() + " \"" + duplicatePubItemVO.getMetadata().getTitle() + "\"",
                    duplicatePubItemVO.getVersion().getObjectId());
              }
            } else {
              this.log.addDetail(ErrorLevel.WARNING,
                  "import_process_detected_duplicate_no_publication");
            }
          }
        }
        return true;
      } else {
        this.log.addDetail(ErrorLevel.FINE, "import_process_no_identifier_for_duplicate_check");
        return false;
      }
    } catch (Exception e) {
      this.log.addDetail(ErrorLevel.WARNING, e);
      // An error while checking for duplicates should not cause the item not to be imported.
      // this.log.finishItem();
      return false;
    }
  }
}
