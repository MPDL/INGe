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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.model.SelectItem;

/**
 * Session bean to hold data needed for an import of multiple items.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@ManagedBean(name = "MultipleImport")
@SessionScoped
@SuppressWarnings("serial")
public class MultipleImport extends FacesBean {
  private static final Logger logger = LogManager.getLogger(MultipleImport.class);

  public static final String LOAD_MULTIPLE_IMPORT = "loadMultipleImport";
  public static final String LOAD_MULTIPLE_IMPORT_FORM = "loadMultipleImportForm";

  private ContextDbRO context;
  private TransformerFactory.FORMAT format;
  private File uploadedFile;
  private List<SelectItem> configParameters = null;
  private List<SelectItem> importFormats = new ArrayList<>();
  private Map<String, List<SelectItem>> parametersValues;
  private String fixedFileName;
  private String name;
  private UploadedFile uploadedImportFile;
  private boolean rollback = false;
  private int duplicateStrategy = 1;

  public MultipleImport() {
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.ENDNOTE_STRING, TransformerFactory.FORMAT.ENDNOTE_STRING.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.BIBTEX_STRING, TransformerFactory.FORMAT.BIBTEX_STRING.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.RIS_STRING, TransformerFactory.FORMAT.RIS_STRING.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.WOS_STRING, TransformerFactory.FORMAT.WOS_STRING.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.MAB_STRING, TransformerFactory.FORMAT.MAB_STRING.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.EDOC_XML, TransformerFactory.FORMAT.EDOC_XML.getName()));
    this.importFormats
        .add(new SelectItem(TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML, TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.ZFN_TEI_XML, TransformerFactory.FORMAT.ZFN_TEI_XML.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.MARC_21_STRING, TransformerFactory.FORMAT.MARC_21_STRING.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.MARC_XML, TransformerFactory.FORMAT.MARC_XML.getName()));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.BMC_XML, TransformerFactory.FORMAT.BMC_XML.getName()));
  }

  public String uploadFile() throws Exception {
    logger.info(this.uploadedImportFile);
    if (null == this.uploadedImportFile) {
      this.error(this.getMessage("UploadFileNotProvided"));
      return null;
    }

    this.initConfigParameters();

    return MultipleImport.LOAD_MULTIPLE_IMPORT_FORM;
  }

  public String getFileSize() {
    if (null != this.uploadedFile) {
      final long size = this.uploadedFile.length();
      logger.info("File uploaded of size <" + size + ">");
      if (1024 > size) {
        return size + "B";
      } else if (1024 * 1024 > size) {
        return Math.round((float) size / 1024) + "KB";
      } else {
        return Math.round((float) size / (1024 * 1024)) + "MB";
      }
    }

    return null;
  }

  public void startImport() throws Exception {
    if ("".equals(this.name)) {
      this.error(this.getMessage("ImportNameNotProvided"));
      return;
    }

    Map<String, String> configuration = null;

    if (!this.configParameters.isEmpty()) {
      configuration = new LinkedHashMap<>();
    }

    for (final SelectItem si : this.configParameters) {
      configuration.put(si.getLabel(), si.getValue().toString());
    }

    final Connection connection = DbTools.getNewConnection();
    ImportProcess importProcess = null;
    try {
      importProcess = new ImportProcess(this.name, this.uploadedImportFile.getFileName(), this.uploadedFile, this.format, this.context,
          this.getLoginHelper().getPrincipal(), this.rollback, this.duplicateStrategy, configuration,
          this.getLoginHelper().getAuthenticationToken(), connection);
      importProcess.start();
    } catch (final Exception e) {
      DbTools.closeConnection(connection);
      throw e;
    }

    FacesTools.getExternalContext().redirect("ImportWorkspace.jsp");
  }

  /**
   * JSF action that is triggered from the submission menu.
   *
   * @return Depending on the contexts the user is allowed to create items in, either createItemPage
   *         or multipleImport
   */
  public String newImport() {
    // clear the file
    this.uploadedImportFile = null;

    // deselect the selected context
    final ContextListSessionBean contextListSessionBean = FacesTools.findBean("ContextListSessionBean");
    if (null != contextListSessionBean.getDepositorContextList()) {
      for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++) {
        contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
      }
    }

    // set the current submission step to step2
    if (null != contextListSessionBean.getDepositorContextList() && 1 < contextListSessionBean.getDepositorContextList().size()) {
      final CreateItem createItem = FacesTools.findBean("CreateItem");
      createItem.setTarget(MultipleImport.LOAD_MULTIPLE_IMPORT);
      createItem.setMethod(CreateItem.SubmissionMethod.MULTIPLE_IMPORT);
      return CreateItem.LOAD_CREATEITEM;
    }
    // Skip Collection selection for Import & Easy Sub if only one Collection
    else if (null != contextListSessionBean.getDepositorContextList() && 1 == contextListSessionBean.getDepositorContextList().size()) {
      this.setContext(contextListSessionBean.getDepositorContextList().get(0));
      return MultipleImport.LOAD_MULTIPLE_IMPORT;
    } else {
      logger.warn("No context for this user, therefore no import mask");
      return null;
    }
  }

  public void initConfigParameters() throws Exception {
    Transformer transformer = null;
    Map<String, String> config = null;

    if (null != this.format) {
      transformer = TransformerFactory.newTransformer(this.format, TransformerFactory.getInternalFormat());
      logger.info("Get Default Configuration:");
      config = transformer.getConfiguration();
    }

    this.configParameters = new ArrayList<>();

    if (null != config) {
      this.parametersValues = new LinkedHashMap<>();

      for (final Map.Entry<String, String> entry : config.entrySet()) {
        final String key = entry.getKey();
        final List<String> values = transformer.getAllConfigurationValuesFor(key);
        final List<SelectItem> list = new ArrayList<>();
        if (null != values) {
          for (final String str : values) {
            list.add(new SelectItem(str, str));
          }
          this.parametersValues.put(key, list);
        }
        this.configParameters.add(new SelectItem(entry.getValue(), key));
      }
    }

  }

  public List<SelectItem> getConfigParameters() {
    return this.configParameters;
  }

  public Map<String, List<SelectItem>> getParametersValues() {
    return this.parametersValues;
  }

  public ContextDbRO getContext() {
    return this.context;
  }

  public void setContext(ContextDbVO context) {
    this.context = context;
  }

  public List<SelectItem> getImportFormats() {
    return this.importFormats;
  }

  public void setImportFormats(List<SelectItem> importFormats) {
    this.importFormats = importFormats;
  }

  public TransformerFactory.FORMAT getFormat() {
    return this.format;
  }

  public void setFormat(TransformerFactory.FORMAT format) {
    if (!format.equals(this.format)) {
      this.setName("");
    }
    this.format = format;
  }

  public UploadedFile getUploadedImportFile() {
    return this.uploadedImportFile;
  }

  public void setUploadedImportFile(UploadedFile uploadedImportFile) {
    this.uploadedImportFile = uploadedImportFile;
  }

  public boolean getRollback() {
    return this.rollback;
  }

  public void setRollback(boolean rollback) {
    this.rollback = rollback;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
    this.name = this.name.replace("ä", "ae").replace("Ä", "Ae").replace("ö", "oe").replace("Ö", "Oe").replace("ü", "ue").replace("Ü", "Ue")
        .replace("ß", "ss");
  }

  public int getDuplicateStrategy() {
    return this.duplicateStrategy;
  }

  public void setDuplicateStrategy(int duplicateStrategy) {
    this.duplicateStrategy = duplicateStrategy;
  }

  public void fileUploaded(FileUploadEvent evt) {
    try {
      this.uploadedImportFile = evt.getFile();
      this.fixedFileName = CommonUtils.fixURLEncoding(this.uploadedImportFile.getFileName());
      this.uploadedFile = File.createTempFile(this.uploadedImportFile.getFileName(), ".tmp");
      final FileOutputStream fos = new FileOutputStream(this.uploadedFile);
      final InputStream is = this.uploadedImportFile.getInputStream();
      IOUtils.copy(is, fos);
      fos.flush();
      fos.close();
      is.close();
    } catch (final Exception e) {
      logger.error("Error while uplaoding file", e);
    }
  }

  public String getFixedFileName() {
    return this.fixedFileName;
  }

  public void setFixedFileName(String fixedFileName) {
    this.fixedFileName = fixedFileName;
  }
}
