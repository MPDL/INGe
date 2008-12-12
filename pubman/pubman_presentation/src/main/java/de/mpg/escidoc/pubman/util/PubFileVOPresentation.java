package de.mpg.escidoc.pubman.util;

import java.util.List;

import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.easySubmission.EasySubmission;
import de.mpg.escidoc.pubman.easySubmission.EasySubmissionSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.FormatVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.MdsFileVO;
import de.mpg.escidoc.services.pubman.PubItemSimpleStatistics;
import de.mpg.escidoc.services.pubman.util.AdminHelper;

/**
 * Presentation wrapper for {@link FileVO}.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PubFileVOPresentation extends FacesBean
{

    public static final String FILE_TYPE_FILE = "FILE";
    public static final String FILE_TYPE_LOCATOR = "LOCATOR";
    private int index;
    private FileVO file;
    private HtmlCommandButton removeButton = new HtmlCommandButton();
    private boolean isLocator = false;
    private String fileType;
    private PubItemSimpleStatistics pubItemStatistics;
    private static final Logger logger = Logger.getLogger(PubFileVOPresentation.class);
    private LoginHelper loginHelper;


    /**
     * The possible content types of a file.
     * @updated 21-Nov-2007 12:05:47
     */
    public enum ContentCategory
    {
        ANY_FULLTEXT, PRE_PRINT, POST_PRINT, PUBLISHER_VERSION, ABSTRACT, TABLE_OF_CONTENTS,
        SUPPLEMENTARY_MATERIAL, CORRESPONDENCE, COPYRIGHT_TRANSFER_AGREEMENT;
        
        public String toString()
        {
            return super.toString().toLowerCase().replace("_", "-");
        }

    }

    /**
     * Default constructor.
     */
    public PubFileVOPresentation()
    {
        this.file = new FileVO();
        
        file.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        init();
    }
    
    public PubFileVOPresentation(int fileIndex, boolean isLocator)
    {
        this.file = new FileVO();
        this.index = fileIndex; 
        this.isLocator = isLocator;
        if (isLocator)
        {
            file.setStorage(FileVO.Storage.EXTERNAL_URL);
        }
        else
        {
            file.setStorage(FileVO.Storage.INTERNAL_MANAGED);
        }
        init();
    }
    
    public PubFileVOPresentation(int fileIndex, FileVO file)
    {
        this.index = fileIndex; 
        this.file = file;
        this.removeButton.setTitle("btnRemove_" + fileIndex);
        init();
    }

    public PubFileVOPresentation(int fileIndex, FileVO file, boolean isLocator)
    {
        this.index = fileIndex; 
        this.file = file;
        this.removeButton.setTitle("btnRemove_" + fileIndex);
        this.isLocator = isLocator;
        init();
    }
    
    
    public void init()
    {
        this.loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        setVisibility();
    }
    
    private void initStatisticService()
    {
        if (pubItemStatistics == null)
        {
            try
            {
                InitialContext initialContext = new InitialContext();
                pubItemStatistics = (PubItemSimpleStatistics)
                        initialContext.lookup(PubItemSimpleStatistics.SERVICE_NAME);
            }
            catch (NamingException e)
            {
                logger.debug("Couldn't find PubItemSimpleStatistics Service");
            }
        }
        
    }
    
    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public FileVO getFile()
    {
        return file;
    }

    public void setFile(FileVO file)
    {
        this.file = file;
    }

    public HtmlCommandButton getRemoveButton()
    {
        return removeButton;
    }

    public void setRemoveButton(HtmlCommandButton removeButton)
    {
        this.removeButton = removeButton;
    }
    
    public boolean getIsLocator()
    {
        return isLocator;
    }

    public void setLocator(boolean isLocator)
    {
        this.isLocator = isLocator;
    }

    public String getFileType()
    {
        return fileType;
    }
    
    /**
     * Returns an internationalized String for the file's content category.
     * 
     * @return The internationalized content-category.
     */
    public String getContentCategory()
    {
        String contentCategory = "";
        InternationalizedImpl internationalized = new InternationalizedImpl();
        if (this.file.getContentCategory() != null)
        {
            contentCategory = internationalized
                .getLabel(
                        this
                            .i18nHelper
                            .convertEnumToString(
                                    PubFileVOPresentation.ContentCategory.valueOf(
                                            CommonUtils.convertToEnumString(
                                                    this.file.getContentCategory()))));
        }
        return contentCategory;
    }
    
    public int getSize()
    {
        int size = 0;
        if (this.file.getDefaultMetadata() != null)
        {
            size = this.file.getDefaultMetadata().getSize();
        }
        return size;
    }
    
    public String getDescription()
    {
        String description = "";
        if (this.file.getDefaultMetadata() != null)
        {
            description = this.file.getDefaultMetadata().getDescription();
        }
        return description;
    }
    
    public void setDescription(String description)
    {
        if (this.file.getDefaultMetadata() != null)
        {
            this.file.getDefaultMetadata().setDescription(description);
        }
        else
        {
            this.file.getMetadataSets().add(new MdsFileVO());
            this.file.getDefaultMetadata().setDescription(description);
        }
    }
    
    public String getVisibility()
    {
        String visibility = "";
        InternationalizedImpl internationalized = new InternationalizedImpl();
        if (this.file.getVisibility() != null)
        {
            visibility = internationalized.getLabel(this.i18nHelper.convertEnumToString(this.file.getVisibility()));
        }
        else
        {
            this.file.setVisibility(FileVO.Visibility.PUBLIC);
            visibility = internationalized.getLabel(this.i18nHelper.convertEnumToString(this.file.getVisibility()));
        }
        return visibility;
    }
    
    private void setVisibility()
    {
        if (this.file.getVisibility() == null)
        {
            this.file.setVisibility(FileVO.Visibility.PUBLIC);
        }
    }
    
    public void setMimeType(String mimeType)
    {
        if (this.file.getDefaultMetadata() == null)
        {
            this.file.getMetadataSets().add(new MdsFileVO());
        }
        List<FormatVO> formats = this.file.getDefaultMetadata().getFormats();
        boolean found = false;
        for (FormatVO formatVO : formats)
        {
            if ("dcterms:IMT".equals(formatVO.getType()))
            {
                formatVO.setValue(mimeType);
                found = true;
                break;
            }
        }
        if (!found)
        {
            FormatVO formatVO = new FormatVO();
            formatVO.setType("dcterms:IMT");
            formatVO.setValue(mimeType);
            formats.add(formatVO);
        }
    }
    
    public String getMimeType()
    {
        if (this.file.getDefaultMetadata() == null)
        {
            return null;
        }
        else
        {
            List<FormatVO> formats = this.file.getDefaultMetadata().getFormats();
            boolean found = false;
            for (FormatVO formatVO : formats)
            {
                if ("dcterms:IMT".equals(formatVO.getType()))
                {
                    return formatVO.getValue();
                }
            }
        }
        return null;
    }
    
    public String getLocator()
    {
        String locator = "";
        if (getIsLocator())
        {
            locator = this.file.getContent();
        }
        return locator;
    }
    
    public void setLocator(String locator)
    {
        this.file.setContent(locator);
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }
    
    
    public String removeFile()
    {
        EditItemSessionBean editItemSessionBean = (EditItemSessionBean)
                getSessionBean(EditItemSessionBean.class);
         
        editItemSessionBean.getFiles().remove(this.index);
        
        // ensure that at least one file component is visible
        if (editItemSessionBean.getFiles().size() == 0)
        {
            FileVO newFile = new FileVO();
            newFile.getMetadataSets().add(new MdsFileVO());
            newFile.setStorage(FileVO.Storage.INTERNAL_MANAGED);
            editItemSessionBean.getFiles().add(0, new PubFileVOPresentation(0, newFile, false));
        }
        
        editItemSessionBean.reorganizeFileIndexes();
        return null;        
    }
    
    public String removeLocatorEditItem ()
    {
        EditItemSessionBean editItemSessionBean = (EditItemSessionBean)
                getSessionBean(EditItemSessionBean.class);
         
        editItemSessionBean.getLocators().remove(this.index);
        
        // ensure that at least one locator component is visible
        if (editItemSessionBean.getLocators().size() == 0)
        {
            FileVO newLocator = new FileVO();
            newLocator.getMetadataSets().add(new MdsFileVO());
            newLocator.setStorage(FileVO.Storage.EXTERNAL_URL);
            editItemSessionBean.getLocators().add(0, new PubFileVOPresentation(0, newLocator, true));
        }
        
        editItemSessionBean.reorganizeLocatorIndexes();
        return "loadEditItem";        
    }
    
    public String removeFileEasySubmission()
    {
        EasySubmission easySubmission = (EasySubmission) getSessionBean(EasySubmission.class); 
        EasySubmissionSessionBean easySubmissionSessionBean = this.getEasySubmissionSessionBean();
         
        easySubmissionSessionBean.getFiles().remove(this.index);
        easySubmission.reorganizeFileIndexes();
        easySubmission.setFileIterator(new UIXIterator());
        easySubmission.init();
        return "loadNewEasySubmission";        
    }
    
    public String removeLocatorEasySubmission()
    {
        EasySubmission easySubmission = (EasySubmission) getSessionBean(EasySubmission.class); 
        EasySubmissionSessionBean easySubmissionSessionBean = this.getEasySubmissionSessionBean();
         
        easySubmissionSessionBean.getLocators().remove(this.index);
        easySubmission.reorganizeLocatorIndexes();
        easySubmission.setLocatorIterator(new UIXIterator());
        easySubmission.init();
        return "loadNewEasySubmission";        
    }
    
    /**
     * Returns the EasySubmissionSessionBean.
     *
     * @return a reference to the scoped data bean (EasySubmissionSessionBean)
     */
    protected EasySubmissionSessionBean getEasySubmissionSessionBean()
    {
        return (EasySubmissionSessionBean) getSessionBean(EasySubmissionSessionBean.class);
    }
    
    /**
     * Returns the ApplicationBean.
     * 
     * @return a reference to the scoped data bean (ApplicationBean)
     */
    protected ApplicationBean getApplicationBean()
    {
        return (ApplicationBean) getApplicationBean(ApplicationBean.class);
        
    }


    
    public String getNumberOfFileDownloadsPerFileAllUsers() throws Exception
    {
        initStatisticService();
        String fileID = file.getReference().getObjectId();
        
        String result = pubItemStatistics.getNumberOfItemOrFileRequests(
                PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS,
                fileID,
                loginHelper.getAccountUser());
        return result;
    }
    
    public String getNumberOfFileDownloadsPerFileAnonymousUsers() throws Exception
    {
        initStatisticService();
        String fileID = file.getReference().getObjectId();
        String result = pubItemStatistics.getNumberOfItemOrFileRequests(
                PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS, 
                fileID,
                loginHelper.getAccountUser());
        return result;
    }
}