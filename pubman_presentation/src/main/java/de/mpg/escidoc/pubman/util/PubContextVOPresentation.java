package de.mpg.escidoc.pubman.util;

import javax.faces.context.FacesContext;

import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.createItem.CreateItem;
import de.mpg.escidoc.pubman.createItem.CreateItem.SubmissionMethod;
import de.mpg.escidoc.pubman.easySubmission.EasySubmission;
import de.mpg.escidoc.pubman.easySubmission.EasySubmissionSessionBean;
import de.mpg.escidoc.pubman.editItem.EditItem;
import de.mpg.escidoc.pubman.multipleimport.MultipleImport;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;

/**
 * Wrapper class for contexts to be used in the presentation.
 * @author franke
 * @author $Author$
 * @version: $Revision$ $LastChangedDate: 2007-12-04 16:52:04 +0100 (Di, 04 Dez 2007)$
 */
public class PubContextVOPresentation extends ContextVO
{

    private boolean selected = false;
    private boolean details = false;
    
    public PubContextVOPresentation(ContextVO item)
    {
        super(item);
    }
    
    public boolean getSelected()
    {
        return selected;
    }
    
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
    
    public boolean getDetails()
    {
        return details;
    }
    
    public void setDetails(boolean details)
    {
        this.details = details;
    }
    
    private ContextListSessionBean getContextListSessionBean()
    {
        return ((ContextListSessionBean) getSessionBean(ContextListSessionBean.class));
    }
    
    public void showDetails()
    {
        details = true;
    }
    
    public void hideDetails()
    {
        details = false;
    }
    
    public String select()
    {
        selected = true;
        CreateItem createItem = (CreateItem) getSessionBean(CreateItem.class);
        
        if (createItem.getMethod() == SubmissionMethod.FULL_SUBMISSION)
        {
            ((ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class))
                    .getCurrentPubItem()
                    .setContext(this.getReference());
            return EditItem.LOAD_EDITITEM;
        }
        else if (createItem.getMethod() == SubmissionMethod.MULTIPLE_IMPORT)
        {
            MultipleImport multipleImport = (MultipleImport) getSessionBean(MultipleImport.class);
            multipleImport.setContext(this);
            return MultipleImport.LOAD_MULTIPLE_IMPORT;
        }
        else
        {
            throw new RuntimeException("Submission method not set or unknown");
        }
    }
    
    public String selectForEasySubmission()
    {
        // deselect all other contexts
        if (this.getContextListSessionBean().getDepositorContextList() != null)
        {
            for (int i = 0; i < this.getContextListSessionBean().getDepositorContextList().size(); i++)
            {
                this.getContextListSessionBean().getDepositorContextList().get(i).setSelected(false);
            }
        }
        selected = true;
        EasySubmissionSessionBean easySubmissionSessionBean 
                = (EasySubmissionSessionBean)getSessionBean(EasySubmissionSessionBean.class);
        
        ((ItemControllerSessionBean) getSessionBean(ItemControllerSessionBean.class))
                .createNewPubItem(EasySubmission.LOAD_EASYSUBMISSION, getReference());
        easySubmissionSessionBean.setCurrentSubmissionStep(EasySubmissionSessionBean.ES_STEP3);
        return EasySubmission.LOAD_EASYSUBMISSION;
    }
    
    /**
     * Return any bean stored in session scope under the specified name.
     * @param cls The bean class.
     * @return the actual or new bean instance
     */
    public static synchronized Object getSessionBean(final Class<?> cls)
    {

        String name = null;

        try
        {
            name = (String) cls.getField("BEAN_NAME").get(new String());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error getting bean name of " + cls, e);
        }
        Object result = FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getSessionMap()
                .get(name);
        if (result == null)
        {
            try
            {
                Object newBean = cls.newInstance();
                FacesContext
                        .getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .put(name, newBean);
                return newBean;
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating new bean of type " + cls, e);
            }
        }
        else
        {
            return result;
        }
    }
}
