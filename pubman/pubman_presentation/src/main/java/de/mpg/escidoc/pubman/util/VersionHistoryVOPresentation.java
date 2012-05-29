package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.viewItem.ViewItemFull;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.EventLogEntryVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.pubman.PubItemDepositing;

public class VersionHistoryVOPresentation extends VersionHistoryEntryVO
{
    Logger logger = Logger.getLogger(VersionHistoryVOPresentation.class);
    private List<EventLogEntryVOPresentation> eventLogEntries;
    
    public VersionHistoryVOPresentation(VersionHistoryEntryVO versionHistoryEntryVO)
    {
        eventLogEntries = new ArrayList<EventLogEntryVOPresentation>();
     
        this.setEvents(versionHistoryEntryVO.getEvents());
        this.setModificationDate(versionHistoryEntryVO.getModificationDate());
        this.setReference(versionHistoryEntryVO.getReference());
        this.setState(versionHistoryEntryVO.getState());
        
        for (EventLogEntryVO event : getEvents())
        {
            eventLogEntries.add(new EventLogEntryVOPresentation(event, this));
        }
        
    }
    
    
    public String getFormattedModificationDate()
    {
        return CommonUtils.format(getModificationDate());
    }
    
    
    public List<EventLogEntryVOPresentation> getEventLogEntries()
    {
        return eventLogEntries;
    }
    
    /**
     * JSF action to rollback the item to this version.
     * 
     * @return Nothing
     */
    public String rollback() throws Exception
    {
        logger.info("Rollback to version " + this.getReference().getVersionNumber());
        
        LoginHelper loginHelper = (LoginHelper) FacesContext
            .getCurrentInstance()
            .getExternalContext()
            .getSessionMap()
            .get(LoginHelper.BEAN_NAME);
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        PubItemDepositing pubItemDepositingBean = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
        ItemHandler itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
        
        // Get the two versions
        String xmlItemLatestVersion = itemHandler.retrieve(this.getReference().getObjectId());
        String xmlItemThisVersion = itemHandler.retrieve(this.getReference().getObjectIdAndVersion());
        PubItemVO pubItemVOLatestVersion = xmlTransforming.transformToPubItem(xmlItemLatestVersion);
        PubItemVO pubItemVOThisVersion = xmlTransforming.transformToPubItem(xmlItemThisVersion);
        
        // Now copy the old stuff into the current item
        pubItemVOLatestVersion.getMetadataSets().set(0, pubItemVOThisVersion.getMetadata());
        pubItemVOLatestVersion.getLocalTags().clear();
        pubItemVOLatestVersion.getLocalTags().addAll(pubItemVOThisVersion.getLocalTags());
        
        // Do not forget the files
        pubItemVOLatestVersion.getFiles().clear();
        for (FileVO fileVO : pubItemVOThisVersion.getFiles())
        {
            pubItemVOLatestVersion.getFiles().add(fileVO);
        }
        
        // Then process it into the framework ...
        String xmlItemNewVersion = xmlTransforming.transformToItem(pubItemVOLatestVersion);
        xmlItemNewVersion = itemHandler.update(this.getReference().getObjectId(), xmlItemNewVersion);
        PubItemVO pubItemVONewVersion = xmlTransforming.transformToPubItem(xmlItemNewVersion);
        if (pubItemVOLatestVersion.getVersion().getState() == State.RELEASED && pubItemVONewVersion.getVersion().getState() == State.PENDING)
        {
            pubItemDepositingBean.submitAndReleasePubItem(pubItemVONewVersion, "Submit and release after rollback", loginHelper.getAccountUser());
            xmlItemNewVersion = itemHandler.retrieve(this.getReference().getObjectId());
            pubItemVONewVersion = xmlTransforming.transformToPubItem(xmlItemNewVersion);
        }

        // ... and set the new version as current item in PubMan
        ItemControllerSessionBean itemControllerSessionBean = (ItemControllerSessionBean) FacesContext
            .getCurrentInstance()
            .getExternalContext()
            .getSessionMap()
            .get(ItemControllerSessionBean.BEAN_NAME);
        itemControllerSessionBean.setCurrentPubItem(new PubItemVOPresentation(pubItemVONewVersion));
        
        ViewItemFull viewItemFull = (ViewItemFull) FacesContext
            .getCurrentInstance()
            .getExternalContext()
            .getRequestMap()
            .get(ViewItemFull.BEAN_NAME);
        viewItemFull.setPubItem(new PubItemVOPresentation(pubItemVONewVersion));
        viewItemFull.init();
        
        return ViewItemFull.LOAD_VIEWITEM;
    }
}
