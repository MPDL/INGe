package de.mpg.mpdl.inge.pubman.web.util.vos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.EventLogEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.ItemControllerSessionBean;
import de.mpg.mpdl.inge.pubman.web.util.beans.LoginHelper;
import de.mpg.mpdl.inge.pubman.web.viewItem.ViewItemFull;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

@SuppressWarnings("serial")
public class VersionHistoryVOPresentation extends VersionHistoryEntryVO {
  private static final Logger logger = Logger.getLogger(VersionHistoryVOPresentation.class);

  private final List<EventLogEntryVOPresentation> eventLogEntries;



  public VersionHistoryVOPresentation(VersionHistoryEntryVO versionHistoryEntryVO) {
    this.eventLogEntries = new ArrayList<>();
    this.setEvents(versionHistoryEntryVO.getEvents());
    this.setModificationDate(versionHistoryEntryVO.getModificationDate());
    this.setReference(versionHistoryEntryVO.getReference());
    this.setState(versionHistoryEntryVO.getState());

    for (final EventLogEntryVO event : this.getEvents()) {
      this.eventLogEntries.add(new EventLogEntryVOPresentation(event, this));
    }
  }

  public List<EventLogEntryVOPresentation> getEventLogEntries() {
    return this.eventLogEntries;
  }



  /**
   * JSF action to rollback the item to this version.
   *
   * @return Nothing
   */
  public String rollback() throws Exception {
    VersionHistoryVOPresentation.logger.info("Rollback to version " + this.getReference().getVersionNumber());

    final LoginHelper loginHelper = FacesTools.findBean("LoginHelper");
    final PubItemService pubItemService = ApplicationBean.INSTANCE.getPubItemService();

    // Get the two versions
    final ItemVersionVO pubItemVOLatestVersion =
        pubItemService.get(this.getReference().getObjectId(), loginHelper.getAuthenticationToken());
    final ItemVersionVO pubItemVOThisVersion =
        pubItemService.get(this.getReference().getObjectIdAndVersion(), loginHelper.getAuthenticationToken());

    // Now copy the old stuff into the current item
    pubItemVOLatestVersion.setMetadata(pubItemVOThisVersion.getMetadata());

    // Do not forget the files and locators
    pubItemVOLatestVersion.getFiles().clear();
    for (final FileDbVO fileVO : pubItemVOThisVersion.getFiles()) {
      final FileDbVO clonedFile = new FileDbVO(fileVO);
      pubItemVOLatestVersion.getFiles().add(clonedFile);
    }

    // Then process it into the framework ...
    // TODO: An neuen Workflow anpassen (z.B. hat Owner im Standard-Workflow keine Berechtigung von
    // PENDING nach RELEASED)
    ItemVersionVO pubItemVONewVersion;
    try {
      pubItemVONewVersion = pubItemService.update(pubItemVOLatestVersion, loginHelper.getAuthenticationToken());
    } catch (Exception e) {
      logger.error("Error while updating", e);
      throw e;
    }

    /*
    if (ItemVersionRO.State.RELEASED.equals(pubItemVOLatestVersion.getVersionState())
        && !ItemVersionRO.State.RELEASED.equals(pubItemVONewVersion.getVersionState())) {
      pubItemVONewVersion = ApplicationBean.INSTANCE.getPubItemService().releasePubItem(pubItemVONewVersion.getObjectId(),
          pubItemVONewVersion.getModificationDate(), "Release after rollback to version " + this.getReference().getVersionNumber(),
          loginHelper.getAuthenticationToken());
    }
    */

    // ... and set the new version as current item in PubMan
    ((ItemControllerSessionBean) FacesTools.findBean("ItemControllerSessionBean"))
        .setCurrentPubItem(new PubItemVOPresentation(pubItemVONewVersion));

    final ViewItemFull viewItemFull = FacesTools.findBean("ViewItemFull");
    viewItemFull.setPubItem(new PubItemVOPresentation(pubItemVONewVersion));
    viewItemFull.init();

    return ViewItemFull.LOAD_VIEWITEM;
  }
}
