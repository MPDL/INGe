package de.mpg.escidoc.pubman.util;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.appbase.InternationalizedImpl;
import de.mpg.escidoc.pubman.easySubmission.EasySubmissionSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.pubman.util.statistics.PubItemSimpleStatistics;
import de.mpg.escidoc.pubman.util.statistics.SimpleStatistics;
import de.mpg.escidoc.services.common.valueobjects.FileVO;

public class PubFileVOPresentation extends FacesBean {

	public static final String FILE_TYPE_FILE = "FILE";
	public static final String FILE_TYPE_LOCATOR = "LOCATOR";
	private int index;
	private FileVO file;
	private HtmlCommandButton removeButton = new HtmlCommandButton();
	private boolean isLocator = false;
	private String fileType;

	
	public PubFileVOPresentation()
	{
		this.file = new FileVO();
	}
	
	public PubFileVOPresentation(int fileIndex, boolean isLocator)
	{
		this.file = new FileVO();
		this.index = fileIndex; 
		this.isLocator = isLocator;
	}
	
	public PubFileVOPresentation(int fileIndex, FileVO file)
	{
		this.index = fileIndex; 
		this.file = file;
		this.removeButton.setTitle("btnRemove_" + fileIndex);
	}

	public PubFileVOPresentation(int fileIndex, FileVO file, boolean isLocator)
	{
		this.index = fileIndex; 
		this.file = file;
		this.removeButton.setTitle("btnRemove_" + fileIndex);
		this.isLocator = isLocator;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public FileVO getFile() {
		return file;
	}

	public void setFile(FileVO file) {
		this.file = file;
	}

	public HtmlCommandButton getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(HtmlCommandButton removeButton) {
		this.removeButton = removeButton;
	}
	
	public boolean getIsLocator() {
		return isLocator;
	}

	public void setLocator(boolean isLocator) {
		this.isLocator = isLocator;
	}

	public String getFileType() {
		return fileType;
	}
	
	public String getContentCategory()
    {
    	String contentCategory = "";
    	InternationalizedImpl internationalized = new InternationalizedImpl();
    	if(this.file.getContentType() != null)
    	{
    		contentCategory = internationalized.getLabel(getApplicationBean().convertEnumToString(this.file.getContentType()));
    	}
    	return contentCategory;
    }
	
	public String getVisibility()
    {
    	String visibility = "";
    	InternationalizedImpl internationalized = new InternationalizedImpl();
    	if(this.file.getVisibility() != null)
    	{
    		visibility = internationalized.getLabel(getApplicationBean().convertEnumToString(this.file.getVisibility()));
    	}
    	return visibility;
    }

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String removeFile ()
	{
 		EditItem editItem = (EditItem)getSessionBean(EditItem.class);
 		EditItemSessionBean editItemSessionBean = (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
		if(index < editItem.getFiles().size())
		{
			editItem.getPubItem().getFiles().remove(index);
			editItemSessionBean.getFiles().remove(index);
			
			// ensure that at least one upload component is visible
			if(editItem.getPubItem().getFiles().size() == 0)
			{
				editItem.getPubItem().getFiles().add(new FileVO());
			}
			if(editItem.getFiles().size() == 0)
			{
				editItem.getFiles().add(new PubFileVOPresentation());
			}
			// clear the view root
			UIComponent component = FacesContext.getCurrentInstance().getViewRoot().findComponent("EditItem:fileUploads");
			if (component != null)
	        {
	        	component.getParent().getChildren().remove(component);
	        }
			//return "loadEditItemIntermediate";
		}
		editItem.reorganizeFileIndexes();
		editItem.init();
		return "loadEditItem";
		
	}
	
	public String removeLocatorEditItem ()
	{
		EditItem editItem = (EditItem)getSessionBean(EditItem.class);
		EditItemSessionBean editItemSessionBean = (EditItemSessionBean)getSessionBean(EditItemSessionBean.class);
 		
		editItemSessionBean.getLocators().remove(this.index);
		
		editItem.reorganizeLocatorIndexes();
		editItem.init();
		return "loadEditItem";		
	}
	
	public String removeFileEasySubmission ()
	{
 		EasySubmissionSessionBean easySubmissionSessionBean = this.getEasySubmissionSessionBean();
 		
 		easySubmissionSessionBean.getFiles().remove(this.index);
		return "loadNewEasySubmission";		
	}
	
	public String removeLocatorEasySubmission ()
	{
 		EasySubmissionSessionBean easySubmissionSessionBean = this.getEasySubmissionSessionBean();
 		
 		easySubmissionSessionBean.getLocators().remove(this.index);
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
        return (ApplicationBean) FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), ApplicationBean.BEAN_NAME);
        
    }


	
	public String getNumberOfFileDownloadsPerFileAllUsers() throws Exception
    {
        
        String fileID = file.getReference().getObjectId();
        PubItemSimpleStatistics stat = new SimpleStatistics();
        String result = stat.getSimpleStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ALL_USERS, fileID);
        return result;
    }
    
    public String getNumberOfFileDownloadsPerFileAnonymousUsers() throws Exception
    {
        String fileID = file.getReference().getObjectId();
        PubItemSimpleStatistics stat = new SimpleStatistics();
        String result = stat.getSimpleStatisticValue(PubItemSimpleStatistics.REPORTDEFINITION_FILE_DOWNLOADS_PER_FILE_ANONYMOUS, fileID);
        return result;
    }
}