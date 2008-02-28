package de.mpg.escidoc.pubman.util;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.EditItemPage;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.editItem.EditItemSessionBean;
import de.mpg.escidoc.services.common.valueobjects.PubFileVO;

public class PubFileVOPresentation extends FacesBean {

	private int index;
	private PubFileVO file;
	private HtmlCommandButton removeButton = new HtmlCommandButton();
	
	public PubFileVOPresentation()
	{
		this.file = new PubFileVO();
	}
	
	public PubFileVOPresentation(int fileIndex, PubFileVO file)
	{
		this.index = fileIndex; 
		this.file = file;
		this.removeButton.setTitle("btnRemove_" + fileIndex);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public PubFileVO getFile() {
		return file;
	}

	public void setFile(PubFileVO file) {
		this.file = file;
	}

	public HtmlCommandButton getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(HtmlCommandButton removeButton) {
		this.removeButton = removeButton;
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
				editItem.getPubItem().getFiles().add(new PubFileVO());
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
		//editItem.init();
		return "loadEditItem";
		
	}
}