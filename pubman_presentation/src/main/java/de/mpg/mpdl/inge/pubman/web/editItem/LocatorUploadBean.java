package de.mpg.mpdl.inge.pubman.web.editItem;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.FormatVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.FileLocatorUploadBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.PubFileVOPresentation;

@SuppressWarnings("serial")
public class LocatorUploadBean extends FileLocatorUploadBean {
  private static final Logger logger = LogManager.getLogger(LocatorUploadBean.class);

  @Override
  public void locatorUploaded() {
    try {
      FileDbVO fileVO = new FileDbVO();
      fileVO.setMetadata(new MdsFileVO());
      fileVO.setSize(this.getSize());
      fileVO.getMetadata().setTitle(super.getFileName(this.getLocator()));
      fileVO.setMimeType(this.getType());
      fileVO.setName(super.getFileName(this.getLocator()));

      FormatVO formatVO = new FormatVO();
      formatVO.setType("dcterms:IMT");
      formatVO.setValue(this.getType());
      fileVO.getMetadata().getFormats().add(formatVO);
      fileVO.setContent(this.getLocator());
      fileVO.setStorage(FileDbVO.Storage.INTERNAL_MANAGED);
      fileVO.setVisibility(FileDbVO.Visibility.PUBLIC);
      fileVO.getAllowedAudienceIds().add(null);

      int index = this.getEditItemSessionBean().getFiles().size();

      List<PubFileVOPresentation> list = this.getEditItemSessionBean().getFiles();
      PubFileVOPresentation pubFile = new PubFileVOPresentation(index, fileVO, false);
      list.add(pubFile);
      this.getEditItemSessionBean().setFiles(list);
    } catch (Exception e) {
      logger.error(e);
      this.error = this.getMessage("errorLocatorUploadFW");
    }
  }

  @Override
  public void removeLocator() {
    List<PubFileVOPresentation> list = this.getEditItemSessionBean().getLocators();
    for (int i = 0; i < list.size(); i++) {
      PubFileVOPresentation locatorPres = list.get(i);
      if (locatorPres.getFile().getContent().equals(super.locator)) {
        List<PubFileVOPresentation> listClean = this.getEditItemSessionBean().getLocators();
        listClean.remove(i);
        this.getEditItemSessionBean().setLocators(listClean);
        this.getEditItemSessionBean().checkMinAnzLocators();
      }
    }
  }

  private EditItemSessionBean getEditItemSessionBean() {
    return FacesTools.findBean("EditItemSessionBean");
  }
}
