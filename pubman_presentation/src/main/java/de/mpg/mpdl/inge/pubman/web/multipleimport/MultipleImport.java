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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.pubman.web.contextList.ContextListSessionBean;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem;
import de.mpg.mpdl.inge.pubman.web.createItem.CreateItem.SubmissionMethod;
import de.mpg.mpdl.inge.pubman.web.util.CommonUtils;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.transformation.ImportUsableTransformer;
import de.mpg.mpdl.inge.transformation.Transformer;
import de.mpg.mpdl.inge.transformation.TransformerCache;
import de.mpg.mpdl.inge.transformation.TransformerFactory;

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
  private static final Logger logger = Logger.getLogger(MultipleImport.class);

  public static final String LOAD_MULTIPLE_IMPORT = "loadMultipleImport";
  public static final String LOAD_MULTIPLE_IMPORT_FORM = "loadMultipleImportForm";

  private ContextVO context;
  private TransformerFactory.FORMAT format;
  private File uploadedFile;
  private List<SelectItem> configParameters = null;
  private List<SelectItem> importFormats = new ArrayList<SelectItem>();
  private Map<String, List<SelectItem>> parametersValues;
  private String fixedFileName;
  private String name;
  private UploadedFile uploadedImportFile;
  private boolean rollback = true;
  private int duplicateStrategy = 3;

  public MultipleImport() {
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.ENDNOTE_STRING, //
        this.getLabel("ENUM_IMPORT_FORMAT_ENDNOTE")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.BIBTEX_STRING, //
        this.getLabel("ENUM_IMPORT_FORMAT_BIBTEX")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.RIS_STRING, //
        this.getLabel("ENUM_IMPORT_FORMAT_RIS")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.WOS_STRING, //
        this.getLabel("ENUM_IMPORT_FORMAT_WOS")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.MAB_STRING, //
        this.getLabel("ENUM_IMPORT_FORMAT_MAB")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.EDOC_XML, //
        this.getLabel("ENUM_IMPORT_FORMAT_EDOC")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.ESCIDOC_ITEMLIST_V3_XML, //
        this.getLabel("ENUM_IMPORT_FORMAT_ESCIDOC")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.ZFN_TEI_XML, //
        this.getLabel("ENUM_IMPORT_FORMAT_ZFN")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.MARC_21_STRING, //
        this.getLabel("ENUM_IMPORT_FORMAT_MARC21")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.MARC_XML, //
        this.getLabel("ENUM_IMPORT_FORMAT_MARCXML")));
    this.importFormats.add(new SelectItem(TransformerFactory.FORMAT.BMC_XML, //
        this.getLabel("ENUM_IMPORT_FORMAT_BMC")));
  }

  public String uploadFile() {
    MultipleImport.logger.info(this.uploadedImportFile);
    if (this.uploadedImportFile == null) {
      FacesBean.error(this.getMessage("UploadFileNotProvided"));
      return null;
    }

    return MultipleImport.LOAD_MULTIPLE_IMPORT_FORM;
  }

  public String getFileSize() {
    if (this.uploadedFile != null) {
      final long size = this.uploadedFile.length();
      System.out.println(size);
      if (size < 1024) {
        return size + "B";
      } else if (size < 1024 * 1024) {
        return Math.round(size / 1024) + "KB";
      } else {
        return Math.round(size / (1024 * 1024)) + "MB";
      }
    }

    return null;
  }

  public void startImport() throws Exception {
    if ("".equals(this.name)) {
      FacesBean.error(this.getMessage("ImportNameNotProvided"));
      return;
    }

    Map<String, String> configuration = null;

    if (this.configParameters.size() > 0) {
      configuration = new LinkedHashMap<String, String>();
    }

    for (final SelectItem si : this.configParameters) {
      configuration.put(si.getLabel(), si.getValue().toString());
    }

    final ImportProcess importProcess =
        new ImportProcess(this.name, this.uploadedImportFile.getFileName(), this.uploadedFile,
            this.format, this.context.getReference(), this.getLoginHelper().getAccountUser(),
            this.rollback, this.duplicateStrategy, configuration, this.getLoginHelper()
                .getAuthenticationToken());
    importProcess.start();

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
    final ContextListSessionBean contextListSessionBean =
        (ContextListSessionBean) FacesTools.findBean("ContextListSessionBean");
    if (contextListSessionBean.getDepositorContextList() != null) {
      for (int i = 0; i < contextListSessionBean.getDepositorContextList().size(); i++) {
        contextListSessionBean.getDepositorContextList().get(i).setSelected(false);
      }
    }

    // set the current submission step to step2
    if (contextListSessionBean.getDepositorContextList() != null
        && contextListSessionBean.getDepositorContextList().size() > 1) {
      final CreateItem createItem = (CreateItem) FacesTools.findBean("CreateItem");
      createItem.setTarget(MultipleImport.LOAD_MULTIPLE_IMPORT);
      createItem.setMethod(SubmissionMethod.MULTIPLE_IMPORT);
      return CreateItem.LOAD_CREATEITEM;
    }
    // Skip Collection selection for Import & Easy Sub if only one Collection
    else if (contextListSessionBean.getDepositorContextList() != null
        && contextListSessionBean.getDepositorContextList().size() == 1) {
      this.setContext(contextListSessionBean.getDepositorContextList().get(0));
      return MultipleImport.LOAD_MULTIPLE_IMPORT;
    } else {
      MultipleImport.logger.warn("No context for this user, therefore no import mask");
      return null;
    }
  }

  public List<SelectItem> initConfigParameters() throws Exception {
    Transformer transformer = null;
    Map<String, String> config = null;

    if (this.format != null) {
      transformer =
          TransformerCache.getTransformer(this.format,
              TransformerFactory.FORMAT.ESCIDOC_ITEM_V3_XML);
      config = transformer.getConfiguration();
    }

    this.configParameters = new ArrayList<SelectItem>();

    if (config != null) {
      this.parametersValues = new LinkedHashMap<String, List<SelectItem>>();

      for (final String key : config.keySet()) {
        final List<String> values =
            ((ImportUsableTransformer) transformer).getConfigurationValuesFor(key);
        final List<SelectItem> list = new ArrayList<SelectItem>();
        if (values != null) {
          for (final String str : values) {
            list.add(new SelectItem(str, str));
          }
          this.parametersValues.put(key, list);
        }
        this.configParameters.add(new SelectItem(config.get(key), key));
      }
    }

    return this.configParameters;
  }

  public List<SelectItem> getConfigParameters() throws Exception {
    if (this.configParameters == null) {
      this.initConfigParameters();
    }

    return this.configParameters;
  }

  public void setConfigParameters(List<SelectItem> list) {
    this.configParameters = list;
  }

  public Map<String, List<SelectItem>> getParametersValues() {
    return this.parametersValues;
  }

  public void setParametersValues(Map<String, List<SelectItem>> parametersValues) {
    this.parametersValues = parametersValues;
  }

  /**
   * @return the context
   */
  public ContextVO getContext() {
    return this.context;
  }

  /**
   * @param context the context to set
   */
  public void setContext(ContextVO context) {
    this.context = context;
  }

  /**
   * @return the importFormats
   */
  public List<SelectItem> getImportFormats() {
    return this.importFormats;
  }

  /**
   * @param importFormats the importFormats to set
   */
  public void setImportFormats(List<SelectItem> importFormats) {
    this.importFormats = importFormats;
  }

  /**
   * @return the format
   */
  public TransformerFactory.FORMAT getFormat() {
    return this.format;
  }

  /**
   * @param format the format to set
   */
  public void setFormat(TransformerFactory.FORMAT format) {
    if (!format.equals(this.format)) {
      this.setName("");
    }
    this.format = format;
  }

  /**
   * @return the uploadedImportFile
   */
  public UploadedFile getUploadedImportFile() {
    return this.uploadedImportFile;
  }

  /**
   * @param uploadedImportFile the uploadedImportFile to set
   */
  public void setUploadedImportFile(UploadedFile uploadedImportFile) {
    this.uploadedImportFile = uploadedImportFile;
  }

  /**
   * @return the rollback
   */
  public boolean getRollback() {
    return this.rollback;
  }

  /**
   * @param rollback the rollback to set
   */
  public void setRollback(boolean rollback) {
    this.rollback = rollback;
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
    this.name =
        this.name.replace("ä", "ae").replace("Ä", "Ae").replace("ö", "oe").replace("Ö", "Oe")
            .replace("ü", "ue").replace("Ü", "Ue").replace("ß", "ss");
  }

  /**
   * @return the duplicateStrategy
   */
  public int getDuplicateStrategy() {
    return this.duplicateStrategy;
  }

  /**
   * @param duplicateStrategy the duplicateStrategy to set
   */
  public void setDuplicateStrategy(int duplicateStrategy) {
    this.duplicateStrategy = duplicateStrategy;
  }

  public void fileUploaded(FileUploadEvent evt) {
    try {
      this.uploadedImportFile = evt.getFile();
      this.fixedFileName = CommonUtils.fixURLEncoding(this.uploadedImportFile.getFileName());
      this.uploadedFile = File.createTempFile(this.uploadedImportFile.getFileName(), ".tmp");
      final FileOutputStream fos = new FileOutputStream(this.uploadedFile);
      final InputStream is = this.uploadedImportFile.getInputstream();
      IOUtils.copy(is, fos);
      fos.flush();
      fos.close();
      is.close();
    } catch (final Exception e) {
      MultipleImport.logger.error("Error while uplaoding file", e);
    }
  }

  public void clearImportFile(ActionEvent evt) {
    this.uploadedImportFile = null;
  }

  public String getFixedFileName() {
    return this.fixedFileName;
  }

  public void setFixedFileName(String fixedFileName) {
    this.fixedFileName = fixedFileName;
  }
}
