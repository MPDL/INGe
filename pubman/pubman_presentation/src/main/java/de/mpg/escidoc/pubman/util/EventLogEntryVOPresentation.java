package de.mpg.escidoc.pubman.util;

import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.services.common.valueobjects.EventLogEntryVO;
import de.mpg.escidoc.services.common.valueobjects.VersionHistoryEntryVO;

/**
 * Wrapper class for EventLogEntryVO used in presentation
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EventLogEntryVOPresentation extends EventLogEntryVO
{
    
    
    private VersionHistoryEntryVO versionHistoryVO;
    private InternationalizationHelper i18nHelper;
    private ResourceBundle labelBundle;
    
    
    public EventLogEntryVOPresentation(EventLogEntryVO eventLogVO, VersionHistoryEntryVO versionHistoryVO)
    {
        this.setComment(eventLogVO.getComment());
        this.setDate(eventLogVO.getDate());
        this.setType(eventLogVO.getType());
        
        this.versionHistoryVO = versionHistoryVO;
        
        i18nHelper  = (InternationalizationHelper)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(InternationalizationHelper.BEAN_NAME);
        labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
    }
    
    public String getCurrentTypeLabel()
    {
        
        switch (getType()){
        
            case CREATE : return labelBundle.getString("ViewItemLog_lblCreate");
            case RELEASE : return labelBundle.getString("ViewItemLog_lblRelease");
            case SUBMIT : return labelBundle.getString("ViewItemLog_lblSubmit");
            case UPDATE : return labelBundle.getString("ViewItemLog_lblUpdate");
            case WITHDRAW : return labelBundle.getString("ViewItemLog_lblWithdraw");
            
            
        }
        return "";
    }

    public VersionHistoryEntryVO getVersionHistoryVO()
    {
        return versionHistoryVO;
    }

    public void setVersionHistoryVO(VersionHistoryEntryVO versionHistoryVO)
    {
        this.versionHistoryVO = versionHistoryVO;
    }
    
    
    
}
