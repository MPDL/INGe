package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.common.valueobjects.EventLogEntryVO;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;

public class VersionHistoryVOPresentation extends VersionHistoryEntryVO
{
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
}
