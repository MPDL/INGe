package de.mpg.escidoc.pubman.util;

import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;

public class VersionHistoryVOPresentation extends VersionHistoryEntryVO
{
    
    public VersionHistoryVOPresentation(VersionHistoryEntryVO versionHistoryEntryVO)
    {
        this.setEvents(versionHistoryEntryVO.getEvents());
        this.setModificationDate(versionHistoryEntryVO.getModificationDate());
        this.setReference(versionHistoryEntryVO.getReference());
        this.setState(versionHistoryEntryVO.getState());
        
    }
    
    
    public String getFormattedModificationDate()
    {
        return CommonUtils.format(getModificationDate());
    }
}
