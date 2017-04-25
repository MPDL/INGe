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
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.RisProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.WosProcessor;
import de.mpg.mpdl.inge.pubman.web.multipleimport.processor.ZfNProcessor;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.CriterionType;
import de.mpg.mpdl.inge.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.mpdl.inge.search.query.MetadataSearchQuery;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.transformation.TransformerFactory.FORMAT;
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

  private AccountUserVO user;
  private ContextRO escidocContext;
  private DuplicateStrategy duplicateStrategy;
  private FORMAT format;
  private File file;
  private FormatProcessor formatProcessor;
  private ImportLog log;
  private Map<String, String> configuration = null;

  private String authenticationToken;
  private String fileName;
  private String itemContentModel;
  private String name;
  private String publicationContentModel;

  private boolean failed = false;
  private boolean rollback;

  private final ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();
  private long lastBeat = 0;

  public ImportProcess(String name, String fileName, File file, FORMAT format,
      ContextRO escidocContext, AccountUserVO user, boolean rollback, int duplicateStrategy,
      Map<String, String> configuration, String authenticationToken) {
    try {
      this.publicationContentModel =
          PropertyReader.getProperty("escidoc.framework_access.content-model.id.publication");
    } catch (final Exception e) {
      throw new RuntimeException(
          "Error getting property 'escidoc.framework_access.content-model.id.publication'", e);
    }

    this.authenticationToken = authenticationToken;

    this.log =
        new ImportLog("import", user.getReference().getObjectId(), format.name(),
            this.authenticationToken);
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

    this.initialize(name, fileName, file, format, escidocContext, user, rollback, strategy,
        configuration);

    this.log.setPercentage(7);

    if (this.log.isDone()) {
      return;
    }

    if (!this.validate(file, format)) {
      return;
    }

    this.log.setPercentage(10);
  }

  /**
   * @param inputStream
   * @param format
   */
  private void initialize(String name, String fileName, File file, FORMAT format,
      ContextRO escidocContext, AccountUserVO user, boolean rollback,
      DuplicateStrategy duplicateStrategy, Map<String, String> configuration) {
    this.log.startItem("import_process_initialize");

    try {
      this.log.setMessage(name);
      this.log.setContext(escidocContext.getObjectId());
      this.log.setFormat(format.name());

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
      this.user = user;
    } catch (final Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_initialization_failed");
      this.log.addDetail(ErrorLevel.FATAL, e);
      this.fail();
    }

    this.log.finishItem();
  }

  /**
   * @param inputStream
   * @param format
   */
  private boolean validate(File file, FORMAT format) {
    this.log.startItem("import_process_validate");

    if (file == null) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_inputstream_unavailable");
      this.fail();
      return false;
    } else {
      this.log.addDetail(ErrorLevel.FINE, "import_process_inputstream_available");
    }

    if (format == null) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_format_unavailable");
      this.fail();
      return false;
    } else {
      this.log.addDetail(ErrorLevel.FINE, "import_process_format_available");
    }

    final FORMAT[] allSourceFormats =
        this.itemTransformingService.getAllSourceFormatsFor(FORMAT.ESCIDOC_ITEMLIST_V3_XML);
    boolean found = false;
    for (final FORMAT sourceFormat : allSourceFormats) {
      if (format.equals(sourceFormat)) {
        found = true;
        if (this.setProcessor(format)) {
          this.log.addDetail(ErrorLevel.FINE, "import_process_format_valid");
        } else {
          this.log.addDetail(ErrorLevel.FATAL, "import_process_format_not_supported");
          this.fail();
        }
        break;
      }
    }

    if (!found) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_format_invalid");
      this.fail();
      return false;
    }

    this.log.finishItem();

    return true;
  }

  private boolean setProcessor(FORMAT format) {
    try {
      if (format == null) {
        return false;
      }

      switch (format) {
        case ARXIV_OAIPMH_XML:
          this.formatProcessor = new ArxivProcessor();
          break;
        case BIBTEX_STRING:
          this.formatProcessor = new BibtexProcessor();
          break;
        case BMC_XML:
          this.formatProcessor = new BmcProcessor();
          break;
        case EDOC_XML:
          this.formatProcessor = new EdocProcessor();
          break;
        case ENDNOTE_STRING:
        case ENDNOTE_XML:
          this.formatProcessor = new EndnoteProcessor();
          break;
        case ESCIDOC_ITEMLIST_V1_XML:
        case ESCIDOC_ITEMLIST_V2_XML:
        case ESCIDOC_ITEMLIST_V3_XML:
          this.formatProcessor = new EscidocProcessor();
          break;
        case MAB_STRING:
        case MAB_XML:
          this.formatProcessor = new MabProcessor();
          break;
        case MARC_21_STRING:
        case MARC_XML:
          this.formatProcessor = new Marc21Processor();
          break;
        case RIS_STRING:
        case RIS_XML:
          this.formatProcessor = new RisProcessor();
          break;
        case WOS_STRING:
        case WOS_XML:
          this.formatProcessor = new WosProcessor();
          break;
        case ZFN_TEI_XML:
          this.formatProcessor = new ZfNProcessor();
          break;
        default:
          return false;
      }
    } catch (final Exception e) {
      this.log.addDetail(ErrorLevel.FATAL, "import_process_format_error");
      this.log.addDetail(ErrorLevel.FATAL, e);
      this.fail();
    }

    this.formatProcessor.setEncoding("UTF-8");

    return true;
  }

  private void fail() {
    this.failed = true;
    this.log.finishItem();
    this.log.startItem(ErrorLevel.FATAL, "import_process_failed");
    this.log.finishItem();

    if (this.rollback) {
      this.log.setStatus(Status.ROLLBACK);
      this.rollback();
    }

    this.log.close();
  }

  private void rollback() {
    this.log.startItem(ErrorLevel.FINE, "import_process_rollback");
    this.log.finishItem();
    this.log.close();

    final DeleteProcess deleteProcess = new DeleteProcess(this.log, this.authenticationToken);
    deleteProcess.start();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    int counter = 0;
    int itemCount = 1;

    if (!this.failed) {
      try {
        this.log.startItem("import_process_start_import");
        this.formatProcessor.setSourceFile(this.file);
        if (this.formatProcessor.hasNext()) {
          itemCount = this.formatProcessor.getLength();
        }
        this.log.finishItem();
        while (this.formatProcessor.hasNext() && !this.failed) {
          try {
            if (this.log.getCurrentItem() == null) {
              this.log.startItem("import_process_import_item");
            }
            final String singleItem = this.formatProcessor.next();
            if (this.failed) {
              return;
            }
            if (singleItem != null && !"".equals(singleItem.trim())) {
              this.prepareItem(singleItem);
            }
            counter++;
            this.log.setPercentage(30 * counter / itemCount + 10);
          } catch (final Exception e) {
            ImportProcess.logger.error("Error during import", e);
            this.log.addDetail(ErrorLevel.ERROR, e);
            this.log.finishItem();
            if (this.rollback) {
              this.fail();
              break;
            }
          }
          this.heartBeat();
        }
      } catch (final Exception e) {
        ImportProcess.logger.error("Error during import", e);
        this.log.addDetail(ErrorLevel.ERROR, e);
        this.log.finishItem();
        if (this.rollback) {
          this.fail();
        }
      }

      if (this.failed) {
        return;
      }

      this.log.finishItem();
      this.log.startItem("import_process_preparation_finished");
      this.log.addDetail(ErrorLevel.FINE, "import_process_no_more_items");
      this.log.finishItem();
      this.log.setPercentage(40);
      counter = 0;

      for (int i = 0; i < this.log.getItems().size(); i++) {
        final ImportLogItem item = this.log.getItems().get(i);
        if (item.getStatus() == Status.SUSPENDED && item.getItemVO() != null && !this.failed) {
          try {
            this.log.activateItem(item);

            // Fetch files for zfn import
            if (this.format.equals(FORMAT.ZFN_TEI_XML)) {
              try {
                // Set file
                final FileVO file =
                    ((ZfNProcessor) this.formatProcessor).getFileforImport(this.configuration,
                        this.user);
                item.getItemVO().getFiles().add(file);
              } catch (final Exception e) {
                this.log.addDetail(ErrorLevel.WARNING, "Could not fetch file for import");
              }
            }

            this.log.addDetail(ErrorLevel.FINE, "import_process_save_item");

            final PubItemVO savedPubItem =
                ApplicationBean.INSTANCE.getPubItemService().create(item.getItemVO(),
                    this.authenticationToken);

            final String objid = savedPubItem.getVersion().getObjectId();
            this.log.setItemId(objid);
            this.log.addDetail(ErrorLevel.FINE, "import_process_item_imported");
            this.log.finishItem();
            counter++;
            this.log.setPercentage(55 * counter / itemCount + 40);
          } catch (final Exception e) {
            ImportProcess.logger.error("Error during import", e);
            this.log.addDetail(ErrorLevel.ERROR, e);
            this.log.finishItem();
            if (this.rollback) {
              this.fail();
              break;
            }
          }
        }
      }

      if (!this.failed) {
        this.log.startItem("import_process_finished");
        this.log.addDetail(ErrorLevel.FINE, "import_process_import_finished");
        this.log.finishItem();

        try {
          this.log.startItem("import_process_archive_log");
          this.log.addDetail(ErrorLevel.FINE, "import_process_build_task_item");
          final String taskItemXml = this.createTaskItemXml();
          final ItemHandler itemHandler = ServiceLocator.getItemHandler(this.user.getHandle());
          final String savedTaskItemXml = itemHandler.create(taskItemXml);
          final Pattern pattern = Pattern.compile("objid=\"([^\"]+)\"");
          final Matcher matcher = pattern.matcher(savedTaskItemXml);

          if (matcher.find()) {
            final String taskId = matcher.group(1);
            ImportProcess.logger.info("Imported task item: " + taskId);
          }
          this.log.setPercentage(100);
        } catch (final Exception e) {
          ImportProcess.logger.error("Error during import", e);
          this.log.finishItem();
          this.log.startItem(ErrorLevel.ERROR, "import_process_error");
          this.log.addDetail(ErrorLevel.ERROR, e);
          this.fail();
        }
      }
    }

    // Close connection if no rollback is done. Otherwise, Connection is still required for delete
    // process
    if (!this.rollback) {
      this.log.closeConnection();
    }

    this.file.delete();
  }

  /**
   * Send a request to the framework every 30 minutes to make sure the user handle will not expire.
   */
  private void heartBeat() {
    final long now = new Date().getTime();
    if ((now - this.lastBeat) > 1000 * 60 * 30) {
      ImportProcess.logger.info("Refreshing " + this.log.getUserHandle());
      this.lastBeat = now;
      try {
        ServiceLocator.getContextHandler(this.log.getUserHandle()).retrieve(this.log.getContext());
      } catch (final Exception e) {
        ImportProcess.logger.warn("Heartbeat error", e);
      }
    }
  }

  private String createTaskItemXml() {
    try {
      final String fwUrl = PropertyReader.getFrameworkUrl();
      final HttpClient client = new HttpClient();
      ProxyHelper.setProxy(client, fwUrl);

      final StringBuilder sb =
          new StringBuilder(ResourceUtil.getResourceAsString(
              "multipleImport/ImportTaskTemplate.xml", ImportProcess.class.getClassLoader()));
      ImportProcess.replace("$01", this.escape(this.escidocContext.getObjectId()), sb);
      ImportProcess.replace("$02",
          this.escape(PropertyReader.getProperty("escidoc.import.task.content-model")), sb);
      ImportProcess.replace("$03", this.escape("Import Task Item for import " + this.name + " "),
          sb);

      // Upload original data
      final PutMethod method = new PutMethod(fwUrl + "/st/staging-file");
      method.setRequestHeader("Content-Type", this.format.toString());
      method.setRequestHeader("Cookie", "escidocCookie=" + this.user.getHandle());
      InputStream is = new FileInputStream(this.formatProcessor.getSourceFile());
      method.setRequestEntity(new InputStreamRequestEntity(is));
      client.executeMethod(method);
      is.close();
      String response = method.getResponseBodyAsString();
      final URL originalDataUrl = XmlTransformingService.transformUploadResponseToFileURL(response);

      ImportProcess.replace("$04", this.escape(this.name), sb);
      ImportProcess.replace("$05", this.escape(this.fileName), sb);
      ImportProcess.replace("$06", this.escape(originalDataUrl.toExternalForm()), sb);
      ImportProcess.replace("$07", this.escape(this.log.getStoredId() + ""), sb);
      ImportProcess.replace("$08", this.escape(this.format.toString()), sb);
      ImportProcess.replace("$09", this.escape(String.valueOf(this.formatProcessor.getLength())),
          sb);

      // Upload and create task item xml
      final File tempLogXml = File.createTempFile("multipleImportLogXml", "xml");
      final Writer fw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempLogXml), "UTF-8"));
      this.log.toXML(fw);
      fw.flush();
      fw.close();

      final PutMethod method2 = new PutMethod(fwUrl + "/st/staging-file");
      method2.setRequestHeader("Content-Type", "text/xml");
      method2.setRequestHeader("Cookie", "escidocCookie=" + this.user.getHandle());
      is = new FileInputStream(tempLogXml);
      method2.setRequestEntity(new InputStreamRequestEntity(is));
      client.executeMethod(method2);
      is.close();

      response = method2.getResponseBodyAsString();
      final URL logXmlUrl = XmlTransformingService.transformUploadResponseToFileURL(response);

      ImportProcess.replace("$10", this.escape(this.name), sb);
      ImportProcess.replace("$11", "importthis.log.xml", sb);
      ImportProcess.replace("$12", this.escape(logXmlUrl.toExternalForm()), sb);
      ImportProcess.replace("$13", this.escape(this.log.getStoredId() + ""), sb);
      ImportProcess.replace("$14", this.escape(String.valueOf(tempLogXml.length())), sb);

      tempLogXml.delete();

      this.log.finishItem();
      this.log.close();
      return sb.toString();
    } catch (final Exception e) {
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
    String escidocXml = null;

    try {
      escidocXml =
          this.itemTransformingService.transformFromTo(this.format, FORMAT.ESCIDOC_ITEM_V3_XML,
              singleItem);

      this.log.addDetail(ErrorLevel.FINE, escidocXml);
      this.log.addDetail(ErrorLevel.FINE, "import_process_transformation_done");
      final PubItemVO pubItemVO = XmlTransformingService.transformToPubItem(escidocXml);
      pubItemVO.setContext(this.escidocContext);
      pubItemVO.setContentModel(this.publicationContentModel);
      pubItemVO.getVersion().setObjectId(null);
      pubItemVO.getLocalTags().add("multiple_import");
      pubItemVO.getLocalTags().add(this.log.getMessage() + " " + this.log.getStartDateFormatted());

      // Simple Validation
      this.log.addDetail(ErrorLevel.FINE, "import_process_default_validation");
      try {
        ItemValidatingService.validate(pubItemVO, ValidationPoint.SIMPLE);
        this.log.addDetail(ErrorLevel.FINE, "import_process_default_validation_successful");

        // Standard Validation
        this.log.addDetail(ErrorLevel.FINE, "import_process_release_validation");
        try {
          ItemValidatingService.validate(pubItemVO, ValidationPoint.STANDARD);
          this.log.addDetail(ErrorLevel.FINE, "import_process_release_validation_successful");
          this.log.addDetail(ErrorLevel.FINE, "import_process_generate_item");
          this.log.setItemVO(pubItemVO);
          if (this.duplicateStrategy != DuplicateStrategy.NO_CHECK) {
            this.log.addDetail(ErrorLevel.FINE, "import_process_check_duplicates_by_identifier");
            final boolean duplicatesDetected = this.checkDuplicatesByIdentifier(pubItemVO);
            if (duplicatesDetected && this.duplicateStrategy == DuplicateStrategy.ROLLBACK) {
              this.rollback = true;
              this.fail();
            } else if (duplicatesDetected) {
              this.log.addDetail(ErrorLevel.WARNING, "import_process_no_import");
              this.log.finishItem();
            } else {
              this.log.suspendItem();
            }
          } else {
            this.log.suspendItem();
          }
        } catch (final ItemInvalidException e2) { // Standard Validation
          this.log.addDetail(ErrorLevel.WARNING, "import_process_release_validation_failed");
          for (final ValidationReportItemVO item : e2.getReport().getItems()) {
            this.log.addDetail(ErrorLevel.WARNING, item.getContent());
          }
        }
      } catch (final ItemInvalidException e) { // Simple Validation
        this.log.addDetail(ErrorLevel.PROBLEM, "import_process_default_validation_failed");
        for (final ValidationReportItemVO item : e.getReport().getItems()) {
          this.log.addDetail(ErrorLevel.PROBLEM, item.getContent());
        }
        this.log.addDetail(ErrorLevel.PROBLEM, "import_process_item_not_imported");
        this.log.finishItem();
      }
    } catch (final Exception e) {
      ImportProcess.logger.error("Error while multiple import", e);
      this.log.addDetail(ErrorLevel.ERROR, e);
      this.log.addDetail(ErrorLevel.ERROR, "import_process_item_not_imported");
      if (this.rollback) {
        this.fail();
      }
      this.log.finishItem();
    }
  }

  private boolean checkDuplicatesByIdentifier(PubItemVO itemVO) {
    try {
      if (itemVO.getMetadata().getIdentifiers().size() > 0) {
        final ArrayList<String> contentModels = new ArrayList<String>();
        contentModels.add(this.itemContentModel);
        final ArrayList<MetadataSearchCriterion> criteria =
            new ArrayList<MetadataSearchCriterion>();
        boolean first = true;
        for (final IdentifierVO identifierVO : itemVO.getMetadata().getIdentifiers()) {
          final MetadataSearchCriterion criterion =
              new MetadataSearchCriterion(CriterionType.IDENTIFIER, identifierVO.getId(),
                  (first ? LogicalOperator.AND : LogicalOperator.OR));
          first = false;
          criteria.add(criterion);
        }
        final MetadataSearchQuery query = new MetadataSearchQuery(contentModels, criteria);
        final ItemContainerSearchResult searchResult = SearchService.searchForItemContainer(query);
        if (searchResult.getTotalNumberOfResults().equals(BigInteger.ZERO)) {
          this.log.addDetail(ErrorLevel.FINE, "import_process_no_duplicate_detected");
          return false;
        } else {
          this.log.addDetail(ErrorLevel.FINE, "import_process_duplicates_detected");
          for (final ItemVO duplicate : searchResult.extractItemsOfSearchResult()) {
            if (this.itemContentModel.equals(duplicate.getContentModel())) {
              final PubItemVO duplicatePubItemVO = new PubItemVO(duplicate);
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
      }
    } catch (final Exception e) {
      this.log.addDetail(ErrorLevel.WARNING, e);
      // An error while checking for duplicates should not cause the item not to be imported.
      // this.log.finishItem();
    }

    return false;
  }
}
