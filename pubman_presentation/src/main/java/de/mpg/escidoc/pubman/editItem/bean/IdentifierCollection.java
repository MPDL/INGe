package de.mpg.escidoc.pubman.editItem.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.pubman.appbase.DataModelManager;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;

/**
 * Bean to handle the IdentifierCollection on a single jsp.
 * A IdentifierCollection is represented by a List<IdentifierVO>.
 *
 * @author Mario Wagner
 */
public class IdentifierCollection
{
    private List<IdentifierVO> parentVO;
    private IdentifierManager identifierManager;

    public IdentifierCollection()
    {
        // ensure the parentVO is never null;
        this(new ArrayList<IdentifierVO>());
    }

    public IdentifierCollection(List<IdentifierVO> parentVO)
    {
        setParentVO(parentVO);
    }

    public List<IdentifierVO> getParentVO()
    {
        return parentVO;
    }

    public void setParentVO(List<IdentifierVO> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        identifierManager = new IdentifierManager(parentVO);
    }

    /**
     * localized creation of SelectItems for the identifier types available
     * @return SelectItem[] with Strings representing identifier types
     */
    public SelectItem[] getIdentifierTypes()
    {
        InternationalizationHelper i18nHelper = (InternationalizationHelper)FacesContext.getCurrentInstance().getApplication().getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(), InternationalizationHelper.BEAN_NAME);
        ResourceBundle labelBundle = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());

        // constants for comboBoxes
        SelectItem NO_ITEM_SET = new SelectItem("", labelBundle.getString("EditItem_NO_ITEM_SET"));
        SelectItem IDENTIFIERTYPE_URI = new SelectItem(IdentifierVO.IdType.URI.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_URI"));
        SelectItem IDENTIFIERTYPE_ISBN = new SelectItem(IdentifierVO.IdType.ISBN.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ISBN"));
        SelectItem IDENTIFIERTYPE_ISSN = new SelectItem(IdentifierVO.IdType.ISSN.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ISSN"));
        SelectItem IDENTIFIERTYPE_DOI = new SelectItem(IdentifierVO.IdType.DOI.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_DOI"));
        SelectItem IDENTIFIERTYPE_URN = new SelectItem(IdentifierVO.IdType.URN.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_URN"));
        SelectItem IDENTIFIERTYPE_EDOC = new SelectItem(IdentifierVO.IdType.EDOC.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_EDOC"));
        SelectItem IDENTIFIERTYPE_ESCIDOC = new SelectItem(IdentifierVO.IdType.ESCIDOC.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ESCIDOC"));
        SelectItem IDENTIFIERTYPE_ISI = new SelectItem(IdentifierVO.IdType.ISI.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_ISI"));
        SelectItem IDENTIFIERTYPE_PND = new SelectItem(IdentifierVO.IdType.PND.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_PND"));
        SelectItem IDENTIFIERTYPE_OTHER = new SelectItem(IdentifierVO.IdType.OTHER.toString(), labelBundle.getString("ENUM_IDENTIFIERTYPE_OTHER"));
        return new SelectItem[] {NO_ITEM_SET, IDENTIFIERTYPE_URI, IDENTIFIERTYPE_ISBN, IDENTIFIERTYPE_ISSN, IDENTIFIERTYPE_DOI, IDENTIFIERTYPE_URN, IDENTIFIERTYPE_URN, IDENTIFIERTYPE_EDOC, IDENTIFIERTYPE_ESCIDOC, IDENTIFIERTYPE_ISI, IDENTIFIERTYPE_PND, IDENTIFIERTYPE_OTHER};
    }

    /**
     * Specialized DataModelManager to deal with objects of type IdentifierVO
     * @author Mario Wagner
     */
    public class IdentifierManager extends DataModelManager<IdentifierVO>
    {
        List<IdentifierVO> parentVO;
        
        public IdentifierManager(List<IdentifierVO> parentVO)
        {
            setParentVO(parentVO);
        }
        
        public IdentifierVO createNewObject()
        {
            IdentifierVO newIdentifier = new IdentifierVO();
            return newIdentifier;
        }
        
        public List<IdentifierVO> getDataListFromVO()
        {
            return parentVO;
        }

        public void setParentVO(List<IdentifierVO> parentVO)
        {
            this.parentVO = parentVO;
            setObjectList(parentVO);
        }
        
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
    }

    public IdentifierManager getIdentifierManager()
    {
        return identifierManager;
    }

    public void setIdentifierManager(IdentifierManager identifierManager)
    {
        this.identifierManager = identifierManager;
    }

}
