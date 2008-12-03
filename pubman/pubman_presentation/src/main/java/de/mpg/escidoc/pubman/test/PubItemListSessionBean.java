package de.mpg.escidoc.pubman.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

public class PubItemListSessionBean extends BasePaginatorListSessionBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA> implements PhaseListener
{
    private static Logger logger = Logger.getLogger(PubItemListSessionBean.class);
    public static String BEAN_NAME = "PubItemListSessionBean";
    
    /*
    public static String[] sortCriteriaNames = new String[]{"TITLE", "EVENT_TITLE", "GENRE",
          "PUBLISHING_INFO", "REVIEW_METHOD", "MODIFICATION_DATE", "CONTEXT", "STATE", "OWNER"};
    
    public static String[] sortCriteriaFilters = new String[]{
        "/md-records/md-record/publication/title",
        "/md-records/md-record/publication/event/title",
        "/md-records/md-record/publication/type",
        "/md-records/md-record/publication/source/publishing-info/publisher",
        "/md-records/md-record/publication/review-method",
        "/properties/version/date",
        "/properties/context/title",
        "/properties/version/status",
        "/properties/created-by/title" 
    };
    
    */
    public static enum SORT_CRITERIA
    {
        TITLE ("escidoc.title", "/md-records/md-record/publication/title"),
        EVENT_TITLE ("escidoc.any-event", "/md-records/md-record/publication/event/title"),
        SOURCE_TITLE ("escidoc.any-source", ""),
        GENRE ("escidoc.genre", "/md-records/md-record/publication/type"),
        DATE ("escidoc.any-dates", ""),
        CREATOR ("escidoc.complete-name", ""),
        PUBLISHING_INFO ("escidoc.publisher", "/md-records/md-record/publication/source/publishing-info/publisher"),
        MODIFICATION_DATE ("escidoc.last-modification-date", "/last-modification-date"),
        STATE("escidoc.version.status", "/properties/version/status"),
        FILE("","");
        
        private String index;
        private String sortPath;
        private String sortOrder;
        
        SORT_CRITERIA(String index, String sortPath)
        {
            this.setIndex(index);
            this.setSortPath(sortPath);
            this.sortOrder="";
        }

        public void setIndex(String index)
        {
            this.index = index;
        }

        public String getIndex()
        {
            return index;
        }

        public void setSortPath(String sortPath)
        {
            this.sortPath = sortPath;
        }

        public String getSortPath()
        {
            return sortPath;
        }

        public void setSortOrder(String sortOrder)
        {
            this.sortOrder = sortOrder;
        }

        public String getSortOrder()
        {
            return sortOrder;
        }
        
        
        
    }
    
    private static String parameterSelectedSortBy = "sortBy";
    
    private static String parameterSelectedSortOrder = "sortOrder";

    private List<SelectItem> sortBySelectItems;
    
    private String selectedSortBy;
    
    private String selectedSortOrder;
    
    private String subMenu = "VIEW";
    
    private String listType = "BIB";

    private Map<String, ItemRO> selectedItemRefs;
    
    


    public PubItemListSessionBean()
    {
        super();
        
        sortBySelectItems = new ArrayList<SelectItem>();
        selectedItemRefs = new HashMap<String, ItemRO>();
        
        for (SORT_CRITERIA sc : SORT_CRITERIA.values())
        {
            sortBySelectItems.add(new SelectItem(sc.name(), getLabel("ENUM_CRITERIA_"+sc.name())));
        }
        
        //sortBySelectItems = Arrays.asList(this.i18nHelper.getSelectItemsItemListSortBy());

        
        
        /*
        for (int i = 0; i < ItemVO.State.values().length; i++)
        {
            itemStateSelectItems.add(new SelectItem(getModifiedLink(parameterSelectedItemState, ItemVO.State.values()[i].toString()), ItemVO.State.values()[i].toString()));
        }
        */
       
    }
    
  
    /*
    public void changeSortBy(ValueChangeEvent event)
    {
        if (event.getOldValue() != null && !event.getOldValue().equals(event.getNewValue()))
        {
            try
            {
                setSelectedSortBy(event.getNewValue().toString());
                resetFilters();
                redirect();
            }
            catch (Exception e)
            {
               error("Could not redirect");
            }
        }
    }
    */
    
    public String changeToSortByState()
    {
        
        try
        {
            setSelectedSortBy("STATE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeToSortByTitle()
    {
        
        try
        {
            setSelectedSortBy("TITLE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeToSortByGenre()
    {
        
        try
        {
            setSelectedSortBy("GENRE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeToSortByDate()
    {
        
        try
        {
            setSelectedSortBy("DATE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeToSortByCreator()
    {
        
        try
        {
            setSelectedSortBy("CREATOR");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
   
    public String changeToSortByFile()
    {
        
        try
        {
            setSelectedSortBy("FILE");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }



    public String changeSortOrder()
    {
        if (selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING))
        {
            setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
        }
        else
        {
            setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
        }
        try
        {
            setSelectedSortOrder(selectedSortOrder);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToView()
    {
        
        try
        {
            setSubMenu("VIEW");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToExport()
    {
        
        try
        {
            setSubMenu("EXPORT");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToFilter()
    {
        
        try
        {
            setSubMenu("FILTER");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeSubmenuToSorting()
    {
        
        try
        {
            setSubMenu("SORTING");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeListTypeToBib()
    {
        
        try
        {
            setListType("BIB");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public String changeListTypeToGrid()
    {
        
        try
        {
            setListType("GRID");
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    public boolean getIsAscending()
    {
        return selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING);
    }


    public void setSortBySelectItems(List<SelectItem> sortBySelectItems)
    {
        this.sortBySelectItems = sortBySelectItems;
    }


    public List<SelectItem> getSortBySelectItems()
    {
        return sortBySelectItems;
    }


    public void setSelectedSortBy(String selectedSortBy)
    {
        this.selectedSortBy = selectedSortBy;
        getParameterMap().put(parameterSelectedSortBy, selectedSortBy);
        
    }


    public String getSelectedSortBy()
    {
        return selectedSortBy;
    }
    
    public String getSelectedSortByLabel()
    {
        
        
        String returnString = "";
        
        
        if (!getSelectedSortBy().equals("all"))
        {
            returnString =  getLabel("ENUM_CRITERIA_"+getSelectedSortBy());
        }
        return returnString;
    }
    
    




    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }




    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
        getParameterMap().put(parameterSelectedSortOrder, selectedSortOrder);
    }



    @Override
    protected void readOutParameters()
    {
        String sortBy = getExternalContext().getRequestParameterMap().get(parameterSelectedSortBy);
        
        if (sortBy!=null)
        {
            setSelectedSortBy(sortBy);
        }
        else
        {
            setSelectedSortBy("TITLE");
        }
        
        String sortOrder = getExternalContext().getRequestParameterMap().get(parameterSelectedSortOrder);
        if (sortOrder!=null)
        {
            setSelectedSortOrder(sortOrder);
        }
        else
        {
            setSelectedSortOrder(OrderFilter.ORDER_ASCENDING);
        }
        
       
        
    }



    @Override
    public SORT_CRITERIA getAdditionalFilters()
    {
        SORT_CRITERIA sc = SORT_CRITERIA.valueOf(getSelectedSortBy());
        sc.setSortOrder(getSelectedSortOrder());
        return sc;
    }


    public void setSubMenu(String subMenu)
    {
        this.subMenu = subMenu;
    }


    public String getSubMenu()
    {
        return subMenu;
    }


    @Override
    protected void pageTypeChanged()
    {
       subMenu = "VIEW";
       listType = "BIB";
       getParameterMap().clear();
       getSelectedItemRefs().clear();
        
    }


    public void setListType(String listType)
    {
        this.listType = listType;
    }


    public String getListType()
    {
        return listType;
    }

    public List<PubItemVOPresentation> getSelectedItems()
    {
        List<PubItemVOPresentation> selectedPubItems = new ArrayList<PubItemVOPresentation>();
        for (PubItemVOPresentation pubItem : getCurrentPartList())
        {
            if (pubItem.getSelected())
            {
                selectedPubItems.add(pubItem);
            }
        }
        return selectedPubItems;
    }
    
    public String addSelectedToCart()
    {
        PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
        List<PubItemVOPresentation> selectedPubItems = getSelectedItems();
        
        int number = 0;
        for(PubItemVOPresentation pubItem : selectedPubItems)
        {
            if (!pubItemStorage.getStoredPubItems().containsKey(pubItem.getVersion().getObjectIdAndVersion()))
            {
                pubItemStorage.getStoredPubItems().put(pubItem.getVersion().getObjectIdAndVersion(), pubItem.getVersion());
                number++;
            }      
        }
        
     

            
        info(number + " items were added to the basket.");
        
        redirect();
       
        return "";
    }

    
    @Override
    protected void beforeRedirect()
    {
        saveSelections();
    }
    
    private void saveSelections()
    {
        
        
        for (PubItemVOPresentation pubItem : getCurrentPartList())
        {
            if (pubItem.getSelected())
            {
                getSelectedItemRefs().put(pubItem.getVersion().getObjectIdAndVersion(),pubItem.getVersion());
            }
            else
            {
                getSelectedItemRefs().remove(pubItem.getVersion().getObjectIdAndVersion());
            }
        }
    }
    
    private void updateSelections()
    {
        for (PubItemVOPresentation pubItem : getCurrentPartList())
        {
            if(getSelectedItemRefs().containsKey(pubItem.getVersion().getObjectIdAndVersion())){
                pubItem.setSelected(true);
            }
        }
        getSelectedItemRefs().clear();
        
    }
    
    @Override
    protected void saveState()
    {
       
        
    }
    
    @Override
    protected void listUpdated()
    {
        updateSelections();
    }
    
    
    /**
     * Returns the navigation string for loading the DisplayExportItemsPage.jsp .
     * 
     * @author: StG
     */
    public String showDisplayExportData()
    {
        
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        String displayExportData = getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        
        List<PubItemVOPresentation> selectedPubItems = getSelectedItems();
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        if (selectedPubItems.size() != 0)
        {
            // save selected file format on the web interface
            String selectedFileFormat = sb.getFileFormat();
            // for the display export data the file format should be always HTML
            sb.setFileFormat(FileFormatVO.HTML_NAME);
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            try
            {
                displayExportData = new String(icsb.retrieveExportData(curExportFormat, CommonUtils
                        .convertToPubItemVOList(selectedPubItems)));
            }
            catch (TechnicalException e)
            {
                ((ErrorPage)this.getSessionBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            if (curExportFormat.getFormatType() == ExportFormatVO.FormatType.STRUCTURED)
            {
                // replace the carriage returns by html breaks so that h:outputText can correctly display it
                displayExportData = displayExportData.replaceAll("\n", "<br/>");
            }
            sb.setExportDisplayData(displayExportData);
            // restore selected file format on the interface
            sb.setFileFormat(selectedFileFormat);
            return "showDisplayExportItemsPage";
        }
        else
        {
            error(getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
            sb.setExportDisplayData(displayExportData);
            redirect();
            return"";
        }
    }

    /**
     * Invokes the email service to send per email the the page with the selected items as attachment. This method is
     * called when the user selects one or more items and then clicks on the EMail-Button in the Export-Items Panel.
     * 
     * @author: StG
     */
    public String showExportEmailPage()
    {
        
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        List<PubItemVOPresentation> selectedPubItems = getSelectedItems();
        if (selectedPubItems.size() != 0)
        {
            // gets the export format VO that holds the data.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            byte[] exportFileData;
            try
            {
                exportFileData = icsb.retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(selectedPubItems));
            }
            catch (TechnicalException e)
            {
                ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            if ((exportFileData == null) || (new String(exportFileData)).trim().equals(""))
            {
                error(getMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED));
                redirect();
            }
            // YEAR + MONTH + DAY_OF_MONTH
            Calendar rightNow = Calendar.getInstance();
            String date = rightNow.get(Calendar.YEAR) + "-" + rightNow.get(Calendar.DAY_OF_MONTH) + "-"
                    + rightNow.get(Calendar.MONTH) + "_";
            // create an attachment temp file from the byte[] stream
            File exportAttFile;
            try
            {
                exportAttFile = File.createTempFile("eSciDoc_Export_" + curExportFormat.getName() + "_" + date, "."
                        + curExportFormat.getSelectedFileFormat().getName());
                FileOutputStream fos = new FileOutputStream(exportAttFile);
                fos.write(exportFileData);
                fos.close();
            }
            catch (IOException e1)
            {
                ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e1);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            sb.setExportEmailTxt(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_TEXT));
            sb.setAttExportFileName(exportAttFile.getName());
            sb.setAttExportFile(exportAttFile);
            sb.setExportEmailSubject(getMessage(ExportItems.MESSAGE_EXPORT_EMAIL_SUBJECT_TEXT) + ": "
                    + exportAttFile.getName());
            // hier call set the values on the exportEmailView - attachment file, subject, ....
            return "displayExportEmailPage";
        }
        else
        {
            error(getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
            return "";
        }
    }

    /**
     * Downloads the page with the selected items as export. This method is called when the user selects one or more
     * items and then clicks on the Download-Button in the Export-Items Panel.
     * 
     * @author: StG
     */
    public String downloadExportFile()
    {
        
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        List<PubItemVOPresentation> selectedPubItems = getSelectedItems();
        if (selectedPubItems.size() != 0)
        {
            // export format and file format.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            byte[] exportFileData = null;
            try
            {
                exportFileData = icsb.retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(selectedPubItems));
            }
            catch (TechnicalException e)
            {
            }
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
            String contentType = curExportFormat.getSelectedFileFormat().getMimeType();
            response.setContentType(contentType);
            try
            {
                response.setHeader("Content-disposition", "attachment; filename="
                        + URLEncoder.encode("ExportFile", "UTF-8"));
                OutputStream out = response.getOutputStream();
                out.write(exportFileData);
                out.flush();
                facesContext.responseComplete();
                out.close();
            }
            catch (IOException e1)
            {
            }
        }
        else
        {
            error(getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
            
        }
        
        redirect();
        return "";
    }


   


    public void setSelectedItemRefs(Map<String, ItemRO> selectedItemRefs)
    {
        this.selectedItemRefs = selectedItemRefs;
    }
    
    public Map<String, ItemRO> getSelectedItemRefs()
    {
        return selectedItemRefs;
    }
    
    public String selectAllOnPage()
    {
        for (PubItemVOPresentation pubItem : getCurrentPartList())
        {
            getSelectedItemRefs().put(pubItem.getVersion().getObjectIdAndVersion(), pubItem.getVersion());
        }
        redirect();
        return"";
        //getSelectedItemRefs().clear();
    }
    
    public String selectNone()
    {
        getSelectedItemRefs().clear();
        redirect();
        return "";
    }


    public void afterPhase(PhaseEvent pe)
    {
        logger.debug("After Phase: "+pe.getPhaseId());
        
    }


    public void beforePhase(PhaseEvent pe)
    {
        logger.debug("Before Phase: "+pe.getPhaseId());
        
    }


    public PhaseId getPhaseId()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    


    
   
}
