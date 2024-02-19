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

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ValidationPoint;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
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
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.standard.IdentifierSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.service.util.PubItemUtil;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

public class ImportProcess extends Thread {
  private static final Logger logger = LogManager.getLogger(ImportProcess.class);

  public enum DuplicateStrategy
  {
    NO_CHECK,
    CHECK,
    ROLLBACK
  }

  private Principal user;
  private ContextDbRO escidocContext;
  private DuplicateStrategy duplicateStrategy;
  private TransformerFactory.FORMAT format;
  private File file;
  private FormatProcessor formatProcessor;
  private final ImportLog importLog;
  private Map<String, String> configuration = null;

  private final String authenticationToken;

  private boolean failed = false;
  private boolean rollback;

  private Connection connection = null;

  private final ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();
  private final ItemValidatingService itemValidatingService = new ItemValidatingService();

  public ImportProcess(String name, String fileName, File file, TransformerFactory.FORMAT format, ContextDbRO escidocContext,
      Principal user, boolean rollback, int duplicateStrategy, Map<String, String> configuration, String authenticationToken,
      Connection connection) {

    this.authenticationToken = authenticationToken;
    this.connection = connection;

    this.importLog = new ImportLog(user.getUserAccount().getObjectId(), format, connection);
    this.importLog.startItem("import_process_started", connection);
    this.importLog.finishItem(connection);

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

    this.initialize(name, fileName, file, format, escidocContext, user, rollback, strategy, configuration);

    if (this.importLog.isDone()) {
      DbTools.closeConnection(connection);
      return;
    }

    if (!this.validateFormat(file, format)) {
      DbTools.closeConnection(connection);
      return;
    }

    this.importLog.setPercentage(BaseImportLog.PERCENTAGE_IMPORT_START, connection);
  }

  private void initialize(String name, String fileName, File file, TransformerFactory.FORMAT format, ContextDbRO escidocContext,
      Principal user, boolean rollback, DuplicateStrategy duplicateStrategy, Map<String, String> configuration) {
    this.importLog.startItem("import_process_initialize", this.connection);

    try {
      this.importLog.setMessage(name);
      this.importLog.setContext(escidocContext.getObjectId());
      this.importLog.setFormat(format);

      this.configuration = configuration;
      this.duplicateStrategy = duplicateStrategy;
      this.escidocContext = escidocContext;
      this.file = file;
      // this.fileName = fileName;
      this.format = format;
      // this.name = name;
      this.rollback = rollback;
      this.user = user;
    } catch (final Exception e) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_initialization_failed", this.connection);
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, e, this.connection);
      this.fail();
    }

    this.importLog.finishItem(this.connection);
  }

  private boolean validateFormat(File file, TransformerFactory.FORMAT format) {
    this.importLog.startItem("import_process_validate", this.connection);

    if (file == null) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_inputstream_unavailable", this.connection);
      this.fail();
      return false;
    } else {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_inputstream_available", this.connection);
    }

    if (format == null) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_format_unavailable", this.connection);
      this.fail();
      return false;
    } else {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_format_available", this.connection);
    }

    if (!itemTransformingService.isTransformationExisting(format, TransformerFactory.getInternalFormat())) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_format_invalid", this.connection);
      this.fail();
      return false;
    }

    if (this.setProcessor(format)) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_format_valid", this.connection);
    } else {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_format_not_supported", this.connection);
      this.fail();
    }

    this.importLog.finishItem(this.connection);

    return true;
  }

  private boolean setProcessor(TransformerFactory.FORMAT format) {
    try {
      if (format == null) {
        return false;
      }

      switch (format) {
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
          this.formatProcessor = new EndnoteProcessor();
          break;
        case ESCIDOC_ITEM_V3_XML:
          this.formatProcessor = new EscidocProcessor();
          break;
        case MAB_STRING:
          this.formatProcessor = new MabProcessor();
          break;
        case MARC_21_STRING:
          this.formatProcessor = new Marc21Processor();
          break;
        case MARC_XML:
          this.formatProcessor = new MarcXmlProcessor();
          break;
        case RIS_STRING:
          this.formatProcessor = new RisProcessor();
          break;
        case WOS_STRING:
          this.formatProcessor = new WosProcessor();
          break;
        case ZFN_TEI_XML:
          this.formatProcessor = new ZfNProcessor();
          break;
        default:
          return false;
      }
    } catch (final Exception e) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, "import_process_format_error", this.connection);
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FATAL, e, this.connection);
      this.fail();
    }

    this.formatProcessor.setEncoding("UTF-8");

    return true;
  }

  private void fail() {
    this.failed = true;
    this.importLog.finishItem(this.connection);
    this.importLog.startItem(BaseImportLog.ErrorLevel.FATAL, "import_process_failed", this.connection);
    this.importLog.finishItem(this.connection);

    if (this.rollback) {
      this.importLog.setStatus(BaseImportLog.Status.ROLLBACK);
      this.rollback();
    }

    this.importLog.close(this.connection);
  }

  private void rollback() {
    this.importLog.startItem(BaseImportLog.ErrorLevel.FINE, "import_process_rollback", this.connection);
    this.importLog.finishItem(this.connection);
    this.importLog.close(this.connection);

    final Connection connection = DbTools.getNewConnection();
    final DeleteProcess deleteProcess;
    try {
      deleteProcess = new DeleteProcess(this.importLog, this.authenticationToken, connection);
      deleteProcess.start();
    } catch (final Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }
  }

  @Override
  public void run() {
    try {
      int counter = 0;
      int itemCount = 1;

      if (!this.failed) {
        try {
          this.importLog.startItem("import_process_start_import", this.connection);
          this.formatProcessor.setSourceFile(this.file);
          if (this.formatProcessor.hasNext()) {
            itemCount = this.formatProcessor.getLength();
          }
          this.importLog.finishItem(this.connection);
          while (this.formatProcessor.hasNext() && !this.failed) {
            try {
              if (this.importLog.getCurrentItem() == null) {
                this.importLog.startItem("import_process_import_item", this.connection);
              }
              final String singleItem = this.formatProcessor.next();
              if (this.failed) {
                return;
              }
              if (singleItem != null && !singleItem.trim().isEmpty()) {
                this.prepareItem(singleItem);
              }
              counter++;
              this.importLog.setPercentage(
                  BaseImportLog.PERCENTAGE_IMPORT_PREPARE * counter / itemCount + BaseImportLog.PERCENTAGE_IMPORT_START, this.connection);
            } catch (final Exception e) {
              ImportProcess.logger.error("Error during import", e);
              this.importLog.addDetail(BaseImportLog.ErrorLevel.ERROR, e, this.connection);
              this.importLog.finishItem(this.connection);
              if (this.rollback) {
                this.fail();
                break;
              }
            }
            // this.heartBeat();
          }
        } catch (final Exception e) {
          ImportProcess.logger.error("Error during import", e);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.ERROR, e, this.connection);
          this.importLog.finishItem(this.connection);
          if (this.rollback) {
            this.fail();
          }
        }

        if (this.failed) {
          return;
        }

        this.importLog.finishItem(this.connection);
        this.importLog.startItem("import_process_preparation_finished", this.connection);
        this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_no_more_items", this.connection);
        this.importLog.finishItem(this.connection);
        counter = 0;

        String message = this.importLog.getMessage();
        String startDateFormatted = this.importLog.getStartDateFormatted();
        StringBuilder localTag = new StringBuilder();
        localTag.append(message);
        localTag.append(" ");
        localTag.append(startDateFormatted);

        for (int i = 0; i < this.importLog.getItems().size(); i++) {
          final ImportLogItem item = this.importLog.getItems().get(i);
          if (item.getStatus() == BaseImportLog.Status.SUSPENDED && item.getItemVO() != null && !this.failed) {
            try {
              this.importLog.activateItem(item);

              // Fetch files for zfn import
              if (this.format.equals(TransformerFactory.FORMAT.ZFN_TEI_XML)) {
                try {
                  // Set file
                  final FileDbVO file = ((ZfNProcessor) this.formatProcessor).getFileforImport(this.configuration, this.user);
                  item.getItemVO().getFiles().add(file);
                } catch (final Exception e) {
                  this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "Could not fetch file for import", this.connection);
                  logger.info("Could not fetch file for import", e);
                }
              }

              item.getItemVO().getObject().getLocalTags().add("multiple_import");
              item.getItemVO().getObject().getLocalTags().add(localTag.toString());
              this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "Local Tag: " + localTag, this.connection);

              this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_save_item", this.connection);

              final ItemVersionVO savedPubItem =
                  ApplicationBean.INSTANCE.getPubItemService().create(item.getItemVO(), this.authenticationToken);

              final String objid = savedPubItem.getObjectId();
              this.importLog.setItemId(objid, this.connection);
              this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_item_imported", this.connection);
              this.importLog.finishItem(this.connection);
              counter++;
              this.importLog.setPercentage(BaseImportLog.PERCENTAGE_IMPORT_END * counter / itemCount + BaseImportLog.PERCENTAGE_IMPORT_START
                  + BaseImportLog.PERCENTAGE_IMPORT_PREPARE, this.connection);
            } catch (final Exception e) {
              ImportProcess.logger.error("Error during import", e);
              this.importLog.addDetail(BaseImportLog.ErrorLevel.ERROR, e, this.connection);
              this.importLog.finishItem(this.connection);
              if (this.rollback) {
                this.fail();
                break;
              }
            }
          }
        }

        if (!this.failed) {
          this.importLog.startItem("import_process_finished", this.connection);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_import_finished", this.connection);
          this.importLog.finishItem(this.connection);
          this.importLog.close(this.connection);
        }
      }

      this.file.delete();

    } finally {
      DbTools.closeConnection(this.connection);
    }
  }

  private void prepareItem(String singleItem) {
    this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_source_data_found", this.connection);
    this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, singleItem, this.connection);
    this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_start_transformation", this.connection);
    String escidocXml = null;

    try {
      escidocXml =
          this.itemTransformingService.transformFromTo(this.format, TransformerFactory.getInternalFormat(), singleItem, this.configuration);
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, escidocXml, this.connection);

      final ItemVersionVO itemVersionVO = EntityTransformer.transformToNew(XmlTransformingService.transformToPubItem(escidocXml));
      itemVersionVO.getObject().setContext(this.escidocContext);
      itemVersionVO.setObjectId(null);
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_transformation_done", this.connection);

      // Simple Validation
      this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_default_validation", this.connection);
      try {
        PubItemUtil.cleanUpItem(itemVersionVO);
        this.itemValidatingService.validate(itemVersionVO, ValidationPoint.SIMPLE);
        this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_default_validation_successful", this.connection);

        // Standard Validation
        this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_release_validation", this.connection);
        try {
          this.itemValidatingService.validate(itemVersionVO, ValidationPoint.STANDARD);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_release_validation_successful", this.connection);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_generate_item", this.connection);
          this.importLog.setItemVO(itemVersionVO);
          if (this.duplicateStrategy != DuplicateStrategy.NO_CHECK) {
            this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_check_duplicates_by_identifier", this.connection);
            final boolean duplicatesDetected = this.checkDuplicatesByIdentifier(itemVersionVO);
            if (duplicatesDetected && this.duplicateStrategy == DuplicateStrategy.ROLLBACK) {
              this.rollback = true;
              this.fail();
            } else if (duplicatesDetected) {
              this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_no_import", this.connection);
              this.importLog.finishItem(this.connection);
            } else {
              this.importLog.suspendItem(this.connection);
            }
          } else {
            this.importLog.suspendItem(this.connection);
          }
        } catch (final ValidationException e) { // Standard Validation
          this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_release_validation_failed", this.connection);
          for (final ValidationReportItemVO item : e.getReport().getItems()) {
            this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, item.getContent(), this.connection);
          }
          this.importLog.setItemVO(itemVersionVO);
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_generate_item", this.connection);
          this.importLog.suspendItem(this.connection);
        }
      } catch (final ValidationException e) { // Simple Validation
        this.importLog.addDetail(BaseImportLog.ErrorLevel.PROBLEM, "import_process_default_validation_failed", this.connection);
        for (final ValidationReportItemVO item : e.getReport().getItems()) {
          this.importLog.addDetail(BaseImportLog.ErrorLevel.PROBLEM, item.getContent(), this.connection);
        }
        this.importLog.addDetail(BaseImportLog.ErrorLevel.PROBLEM, "import_process_item_not_imported", this.connection);
        this.importLog.finishItem(this.connection);
      }
    } catch (final Exception e) {
      ImportProcess.logger.error("Error while multiple import", e);
      this.importLog.addDetail(BaseImportLog.ErrorLevel.ERROR, e, this.connection);
      this.importLog.addDetail(BaseImportLog.ErrorLevel.ERROR, "import_process_item_not_imported", this.connection);
      if (this.rollback) {
        this.fail();
      }
      this.importLog.finishItem(this.connection);
    }
  }

  private boolean checkDuplicatesByIdentifier(ItemVersionVO itemVO) {
    try {
      if (!itemVO.getMetadata().getIdentifiers().isEmpty()) {



        List<SearchCriterionBase> scList = new ArrayList<>();

        for (final IdentifierVO identifierVO : itemVO.getMetadata().getIdentifiers()) {
          if (!scList.isEmpty()) {
            scList.add(new de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator(SearchCriterion.OR_OPERATOR));
          }
          IdentifierSearchCriterion sc = new IdentifierSearchCriterion();
          sc.setSearchString(identifierVO.getId());
          scList.add(sc);

        }


        Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
        SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
        SearchRetrieveResponseVO<ItemVersionVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, authenticationToken);



        if (resp.getNumberOfRecords() == 0) {
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_no_duplicate_detected", this.connection);
          return false;
        } else {
          this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_duplicates_detected", this.connection);



          for (final ItemVersionVO duplicate : resp.getRecords().stream().map(SearchRetrieveRecordVO::getData).toList()) {
            final ItemVersionVO duplicatePubItemVO = new ItemVersionVO(duplicate);
            if (this.duplicateStrategy == DuplicateStrategy.ROLLBACK) {
              this.importLog.addDetail(BaseImportLog.ErrorLevel.PROBLEM, "import_process_duplicate_detected", this.connection);
              this.importLog.addDetail(BaseImportLog.ErrorLevel.PROBLEM,
                  duplicatePubItemVO.getObjectId() + " \"" + duplicatePubItemVO.getMetadata().getTitle() + "\"",
                  duplicatePubItemVO.getObjectId(), this.connection);
              return true;
            } else {
              this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, "import_process_duplicate_detected", this.connection);
              this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING,
                  duplicatePubItemVO.getObjectId() + " \"" + duplicatePubItemVO.getMetadata().getTitle() + "\"",
                  duplicatePubItemVO.getObjectId(), this.connection);
            }
          }
        }
        return true;
      } else {
        this.importLog.addDetail(BaseImportLog.ErrorLevel.FINE, "import_process_no_identifier_for_duplicate_check", this.connection);
      }
    } catch (final Exception e) {
      this.importLog.addDetail(BaseImportLog.ErrorLevel.WARNING, e, this.connection);
      // An error while checking for duplicates should not cause the item not to be imported.
      // this.log.finishItem();
    }

    return false;
  }
}
