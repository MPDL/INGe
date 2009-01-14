package de.mpg.escidoc.pubman.itemList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.basket.PubItemStorageSessionBean;
import de.mpg.escidoc.pubman.common_presentation.BasePaginatorListSessionBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;

/**
 * This session bean implements the BasePaginatorListSessionBean for sortable lists of PubItems.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PubItemListSessionBean extends BasePaginatorListSessionBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA>
{
    private static Logger logger = Logger.getLogger(PubItemListSessionBean.class);
    
    public static String BEAN_NAME = "PubItemListSessionBean";
    
   
    /**
     * An enumeration that contains the index for the search service and the sorting filter for the eSciDoc ItemHandler for the offered sorting criterias.
     * TODO Description
     *
     * @author Markus Haarlaender (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     *
     */
    public static enum SORT_CRITERIA
    {
        TITLE ("escidoc.title", "/md-records/md-record/publication/title"),
        GENRE ("escidoc.genre", "/md-records/md-record/publication/type"),
        DATE ("escidoc.any-dates", ""),
        CREATOR ("escidoc.complete-name", ""),
        PUBLISHING_INFO ("escidoc.publisher", "/md-records/md-record/publication/source/publishing-info/publisher"),
        MODIFICATION_DATE ("escidoc.last-modification-date", "/last-modification-date"),
        EVENT_TITLE ("escidoc.any-event", "/md-records/md-record/publication/event/title"),
        SOURCE_TITLE ("escidoc.any-source", ""),
        SOURCE_CREATOR("", ""),
        REVIEW_METHOD("", "/md-records/md-record/publication/review-method"),
        FILE("",""),
        STATE("escidoc.version.status", "/properties/version/status"),
        OWNER("escidoc.created-by.name", "/properties/created-by/title"),
        COLLECTION("escidoc.context.name", "/properties/context/title");
        
        
        /**
         * The search sorting index
         */
        private String index;
        
        /**
         * The path to the xml by which a list should be sorted
         */
        private String sortPath;
        
        /**
         * An additional attribute that has to be set, indicating the sort order ("ascending" or "descending")
         */
        private String sortOrder;
        
        SORT_CRITERIA(String index, String sortPath)
        {
            this.setIndex(index);
            this.setSortPath(sortPath);
            this.sortOrder="";
        }

        /**
         * Sets the sorting search index
         * @param index
         */
        public void setIndex(String index)
        {
            this.index = index;
        }

        /**
         * Returns the sorting search index
         * @return
         */
        public String getIndex()
        {
            return index;
        }

        /**
         * Sets the path to the xml tag by which the list should be sorted. Used in filter of ItemHandler
         * @param sortPath
         */
        public void setSortPath(String sortPath)
        {
            this.sortPath = sortPath;
        }

        /**
         * Sets the path to the xml tag by awhich the list should be sorted. Used in filter of ItemHandler
         * @return
         */
        public String getSortPath()
        {
            return sortPath;
        }

        /**
         * Sets the sort order. "ascending" or "descending"
         * @param sortOrder
         */
        public void setSortOrder(String sortOrder)
        {
            this.sortOrder = sortOrder;
        }

        /**
         * Returns the sort order. "ascending" or "descending"
         * @param sortOrder
         */
        public String getSortOrder()
        {
            return sortOrder;
        }
        
        
        
    }
    
    /**
     * The HTTP GET parameter name for the sorting criteria.
     */
    private static String parameterSelectedSortBy = "sortBy";
    
    /**
     * The HTTP GET parameter name for the sorting order
     */
    private static String parameterSelectedSortOrder = "sortOrder";

    /**
     * A list containing the menu entries of the sorting criteria menu.
     */
    private List<SelectItem> sortBySelectItems;
    
    /**
     * The currently selected sorting criteria.
     */
    private String selectedSortBy;
    
    /**
     * The currently selected sort order
     */
    private String selectedSortOrder;
    
    /**
     * A string indicating the currently selected submenu of a PubItem list.
     */
    private String subMenu = "VIEW";
    
    /**
     * A string indicating the currently selected list type of a Pub Item list.
     */
    private String listType = "BIB";

    /**
     * A map containing the references of the currently selected pub items of one page. Used to reset selections after a redirect.
     */
    private Map<String, ItemRO> selectedItemRefs;
    
    private LoginHelper loginHelper;
    
    


    public PubItemListSessionBean()
    {
        super();
        loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        selectedItemRefs = new HashMap<String, ItemRO>();
    }
    
  
    /**
     * Called by JSF when the items should be sorted by their state. Redirects to the same page with updated GET parameter for sorting.
     * @return
     */
    public String changeToSortByState()
    {
        try
        {
            setSelectedSortBy("STATE");
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    /**
     * Called by JSF when the items should be sorted by their title. Redirects to the same page with updated GET parameter for sorting.
     * @return
     */
    public String changeToSortByTitle()
    {
        
        try
        {
            setSelectedSortBy("TITLE");
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    /**
     * Called by JSF when the items should be sorted by their genre. Redirects to the same page with updated GET parameter for sorting.
     * @return
     */
    public String changeToSortByGenre()
    {
        
        try
        {
            setSelectedSortBy("GENRE");
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    /**
     * Called by JSF when the items should be sorted by their date. Redirects to the same page with updated GET parameter for sorting.
     * @return
     */
    public String changeToSortByDate()
    {
        
        try
        {
            setSelectedSortBy("DATE");
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    /**
     * Called by JSF when the items should be sorted by their creators. Redirects to the same page with updated GET parameter for sorting.
     * @return
     */
    public String changeToSortByCreator()
    {
        
        try
        {
            setSelectedSortBy("CREATOR");
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
   
    /**
     * Called by JSF when the items should be sorted by their files. Redirects to the same page with updated GET parameter for sorting.
     * @return
     */
    public String changeToSortByFile()
    {
        
        try
        {
            setSelectedSortBy("FILE");
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }



    /**
     * Called by JSF when the sort order should be changed from "ascending" to "descending" or vice versa.
     * @return
     */
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
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    /**
     * Called by JSF when the sorting criteria should be changed.
     * @return
     */
    public String changeSortBy()
    {
        try
        {
            setCurrentPageNumber(1);
            redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
        
    }
    
    /**
     * Called by JSF when submenu should be changed to the VIEW part
     * @return
     */
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
    
    /**
     * Called by JSF when the submenu should be changed to the EXPORT part
     * @return
     */
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
    
    /**
     * Called by JSF when submenu should be changed to the FILTER part
     * @return
     */
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
    
    /**
     * Called by JSF when submenu should be changed to the SORTING part
     * @return
     */
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
    
    /**
     * Called by JSF when the list type should be changed to bibliographic lists
     * @return
     */
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
    
    /**
     * Called by JSF when the list type should be changed to grid lists
     * @return
     */
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
    
    /**
     * Returns true if the current sort order is ascending, false if "descending
     * @return
     */
    public boolean getIsAscending()
    {
        return selectedSortOrder.equals(OrderFilter.ORDER_ASCENDING);
    }


    /**
     * Sets the menu entries for the sorting criteria menu
     * @param sortBySelectItems
     */
    public void setSortBySelectItems(List<SelectItem> sortBySelectItems)
    {
        this.sortBySelectItems = sortBySelectItems;
    }

    /**
     * Returns the menu entries for the sorting criteria menu
     */
    public List<SelectItem> getSortBySelectItems()
    {
        sortBySelectItems = new ArrayList<SelectItem>();
        
        //the last three should not be in if not logged in
        if (!loginHelper.isLoggedIn())
        {
            for (int i = 0; i< SORT_CRITERIA.values().length - 3; i++)
            {
                SORT_CRITERIA sc = SORT_CRITERIA.values()[i];
                sortBySelectItems.add(new SelectItem(sc.name(), getLabel("ENUM_CRITERIA_"+sc.name())));
            }
        }
        else
        {
            for (int i = 0; i< SORT_CRITERIA.values().length; i++)
            {
                SORT_CRITERIA sc = SORT_CRITERIA.values()[i];
                sortBySelectItems.add(new SelectItem(sc.name(), getLabel("ENUM_CRITERIA_"+sc.name())));
            }
            
        }
        
        return sortBySelectItems;
    }


    /**
     * Sets the current sorting criteria
     * @param selectedSortBy
     */
    public void setSelectedSortBy(String selectedSortBy)
    {
        this.selectedSortBy = selectedSortBy;
        getParameterMap().put(parameterSelectedSortBy, selectedSortBy);
        
    }

   /**
    * Returns the currently selected sorting criteria
    * @return
    */
    public String getSelectedSortBy()
    {
        return selectedSortBy;
    }
    
    /**
     * Retu´rns the label in the selected language for the currrently selected sorting criteria
     * @return
     */
    public String getSelectedSortByLabel()
    {
        String returnString = "";
        if (!getSelectedSortBy().equals("all"))
        {
            returnString =  getLabel("ENUM_CRITERIA_"+getSelectedSortBy());
        }
        return returnString;
    }


    /**
     * Returns the current sort order ("ascending" or "descending")
     */
    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }


    /**
     * Sets the current sort order ("ascending" or "descending")
     * @param selectedSortOrder
     */
    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
        getParameterMap().put(parameterSelectedSortOrder, selectedSortOrder);
    }



    /**
     * Reads out additional parmaeters from GET request for sorting criteria and sort order and sets their default values if they are null
     */
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
            setSelectedSortBy("MODIFICATION_DATE");
        }
        
        String sortOrder = getExternalContext().getRequestParameterMap().get(parameterSelectedSortOrder);
        if (sortOrder!=null)
        {
            setSelectedSortOrder(sortOrder);
        }
        else
        {
            setSelectedSortOrder(OrderFilter.ORDER_DESCENDING);
        }
        
       
        
    }



    /**
     * Returns the currently selected sorting criteria which is used as an additional filter
     */
    @Override
    public SORT_CRITERIA getAdditionalFilters()
    {
        SORT_CRITERIA sc = SORT_CRITERIA.valueOf(getSelectedSortBy());
        sc.setSortOrder(getSelectedSortOrder());
        return sc;
    }


    /**
     * Sets the submenu
     * @param subMenu
     */
    public void setSubMenu(String subMenu)
    {
        this.subMenu = subMenu;
    }


    /**
     * Returns a string describing the curently selected submenu
     * @return
     */
    public String getSubMenu()
    {
        return subMenu;
    }


    /**
     * Resets the submenus, clears parameters from the map
     */
    @Override
    protected void pageTypeChanged()
    {
       if (getPageType().equals("MyItems") || getPageType().equals("MyTasks")){
           subMenu = "FILTER";
       }
       else {
           subMenu = "VIEW";
       }
        
       getSelectedItemRefs().clear();
        
    }


    /**
     * Sets the list type ("BIB" or "GRID")
     * @param listType
     */
    public void setListType(String listType)
    {
        this.listType = listType;
    }


    /**
     * Returns the list type ("BIB" or "GRID")
     * @param listType
     */
    public String getListType()
    {
        return listType;
    }
    
    
    /**
     * Returns the currently selected pub items of the displayed list page
     * @return
     */
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
    
    /**
     * Adds the currently selected pub items to the basket and displays corresponding messages.
     * @return
     */
    public String addSelectedToCart()
    {
        PubItemStorageSessionBean pubItemStorage = (PubItemStorageSessionBean) getSessionBean(PubItemStorageSessionBean.class);
        List<PubItemVOPresentation> selectedPubItems = getSelectedItems();
        
        int added = 0;
        int existing = 0;
        for(PubItemVOPresentation pubItem : selectedPubItems)
        {
            
            if (!pubItemStorage.getStoredPubItems().containsKey(pubItem.getVersion().getObjectIdAndVersion()))
            {
                pubItemStorage.getStoredPubItems().put(pubItem.getVersion().getObjectIdAndVersion(), pubItem.getVersion());
                added++;
            }
            else
            {
                existing++;
            }
           
        }

        if(selectedPubItems.size()==0)
        {
            error(getMessage("basket_NoItemsSelected"));
        } 
        if (added>0 || existing>0)
        {
            info(getMessage("basket_MultipleAddedSuccessfully").replace("$1", String.valueOf(added)));   
        }
        if (existing>0)
        {
            info(getMessage("basket_MultipleAlreadyInBasket").replace("$1", String.valueOf(existing)));
        }
        
        redirect();
       
        return "";
    }

    
    /**
     * Before any redirect, the references of the currently selected publication items are stored in this session in order to reselct them after the redirect
     * Thus, the selection is not lost.
     */
    @Override
    protected void beforeRedirect()
    {
        saveSelections();
    }
    
    /**
     * Saves the references of currently selected pub items into a map.
     */
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
    
    /**
     * Checks if items on the current page have to be selected (checked) after an redirect.
     */
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
    
    /*
    @Override
    protected void saveState()
    {
        //saveSelections();
    }
    */
    
    /**
     * Updates the checkboxes of the items on the page after a new list is displayed.
     */
    @Override
    protected void listUpdated()
    {
        updateSelections();
    }
    
    /**
     * Exports the selected items and displays the results.
     * @return
     */
    public String exportSelectedDisplay()
    {
        return showDisplayExportData(getSelectedItems());
    }
    
    /**
     * Exports the selected items and shows the email page.
     * @return
     */
    public String exportSelectedEmail()
    {
        return showExportEmailPage(getSelectedItems());
    }
    
    /**
     * Exports the selected items and allows the user to download them .
     * @return
     */
    public String exportSelectedDownload()
    {
        return downloadExportFile(getSelectedItems());
    }
    
    /**
     * Exports all items (without offset and limit filters) and displays them.
     * @return
     */
    public String exportAllDisplay()
    {
        return showDisplayExportData(retrieveAll());
    }
    
    /**
     * Exports all items (without offset and limit filters) and and shows the email page.
     * @return
     */
    public String exportAllEmail()
    {
        return showExportEmailPage(retrieveAll());
    }
    
    /**
     * Exports all items (without offset and limit filters) and allows the user to download them .
     * @return
     */
    public String exportAllDownload()
    {
        return downloadExportFile(retrieveAll());
    }
    
    /**
     * Retrieves all pub items (without offset and limit filters) and returns them in a list
     * @return
     */
    private List<PubItemVOPresentation> retrieveAll()
    {
        List<PubItemVOPresentation> itemList = getPaginatorListRetriever().retrieveList(0, 0, getAdditionalFilters());
        return itemList;
    }
    
    
    
    /**
     * Exports the given items and displays them
     * @param pubItemList
     * @return
     */
    public String showDisplayExportData(List<PubItemVOPresentation> pubItemList)
    {
        saveSelections();
        
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        String displayExportData = getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        
      
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        if (pubItemList.size() != 0)
        {
            // save selected file format on the web interface
            String selectedFileFormat = sb.getFileFormat();
            // for the display export data the file format should be always HTML
            sb.setFileFormat(FileFormatVO.HTML_NAME);
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            try
            {
                displayExportData = new String(icsb.retrieveExportData(curExportFormat, CommonUtils
                        .convertToPubItemVOList(pubItemList)));
            }
            catch (TechnicalException e)
            {
                ((ErrorPage)this.getSessionBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            if (curExportFormat.getFormatType() == ExportFormatVO.FormatType.STRUCTURED)
            {
                displayExportData =  "<pre>" + displayExportData + "</pre>";
            }
            sb.setExportDisplayData(displayExportData);
            // restore selected file format on the interface
            sb.setFileFormat(selectedFileFormat);
//            return "dialog:showDisplayExportItemsPage";
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
    * Exports the given pub items and shows the email page.
    * @param pubItemList
    * @return
    */
    public String showExportEmailPage(List<PubItemVOPresentation> pubItemList)
    {
        saveSelections();
        
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
       
        if (pubItemList.size() != 0)
        {
            // gets the export format VO that holds the data.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            byte[] exportFileData;
            try
            {
                exportFileData = icsb.retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(pubItemList));
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
     * Exports the given pub items and allows the user to download them
     * @param pubItemList
     * @return
     */
    public String downloadExportFile(List<PubItemVOPresentation> pubItemList)
    {
        saveSelections();
        
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        if (pubItemList.size() != 0)
        {
            // export format and file format.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            byte[] exportFileData = null;
            try
            {
                exportFileData = icsb.retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(pubItemList));
            }
            catch (TechnicalException e)
            {
            	throw new RuntimeException("Cannot retrieve export data", e);
            }
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
            String contentType = curExportFormat.getSelectedFileFormat().getMimeType();
            response.setContentType(contentType);
    	    String fileName = "export_" + curExportFormat.getName().toLowerCase() + "." + sb.getFileFormat();
	    	response.setHeader("Content-disposition", "attachment; filename=" + fileName);
    	    try
    	    {
    	    	OutputStream out = response.getOutputStream();
    	    	out.write(exportFileData);
    	    	out.close();
    	    }
    	    catch (Exception e) 
    	    {
    	    	throw new RuntimeException("Cannot put export result in HttpResponse body:", e);
			}
    	    facesContext.responseComplete();
        }
        else
        {
            error(getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED));
            
        }
        
//        redirect();
        return "";
    }


    /**
     * Returns a map that contains references of the selected pub items of the last page
     * @return
     */
    public Map<String, ItemRO> getSelectedItemRefs()
    {
        return selectedItemRefs;
    }
    
}
