package de.mpg.escidoc.pubman.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import de.mpg.escidoc.pubman.ErrorPage;
import de.mpg.escidoc.pubman.ItemControllerSessionBean;
import de.mpg.escidoc.pubman.export.ExportItems;
import de.mpg.escidoc.pubman.export.ExportItemsSessionBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.ExportFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FileFormatVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.OrderFilter;

public class CartItemsRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, OrderFilter>
{
    public static String BEAN_NAME = "CartItemsRetrieverRequestBean";
    private int numberOfRecords;

    public CartItemsRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class));
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }

    @Override
    public String getType()
    {
        return "CartItems";
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void readOutParameters()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, OrderFilter additionalFilters)
    {
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
        List<PubItemVOPresentation> selectedItems = pssb.getStoredPubItems();
        this.numberOfRecords = selectedItems.size();
        for (int i = offset; i < offset+limit; i++)
        {
            if (i < selectedItems.size())
            {
                returnList.add(selectedItems.get(i));
            }
        }
        return returnList;
    }

    /**
     * Returns the navigation string for loading the DisplayExportItemsPage.jsp .
     * 
     * @author: StG
     */
    public String showDisplayExportData()
    {
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        String displayExportData = getMessage(ExportItems.MESSAGE_NO_ITEM_FOREXPORT_SELECTED);
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        if (pssb.getSelectedPubItems().size() != 0)
        {
            // save selected file format on the web interface
            String selectedFileFormat = sb.getFileFormat();
            // for the display export data the file format should be always HTML
            sb.setFileFormat(FileFormatVO.HTML_NAME);
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            try
            {
                displayExportData = new String(icsb.retrieveExportData(curExportFormat, CommonUtils
                        .convertToPubItemVOList(pssb.getSelectedPubItems())));
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
            return "";
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
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        if (pssb.getSelectedPubItems().size() != 0)
        {
            // gets the export format VO that holds the data.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            byte[] exportFileData;
            try
            {
                exportFileData = icsb.retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(pssb
                        .getSelectedPubItems()));
            }
            catch (TechnicalException e)
            {
                ((ErrorPage)getSessionBean(ErrorPage.class)).setException(e);
                return ErrorPage.LOAD_ERRORPAGE;
            }
            if ((exportFileData == null) || (new String(exportFileData)).trim().equals(""))
            {
                error(getMessage(ExportItems.MESSAGE_NO_EXPORTDATA_DELIVERED));
                return "";
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
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        ItemControllerSessionBean icsb = (ItemControllerSessionBean)getSessionBean(ItemControllerSessionBean.class);
        // set the currently selected items in the FacesBean
        // this.setSelectedItemsAndCurrentItem();
        ExportItemsSessionBean sb = (ExportItemsSessionBean)getSessionBean(ExportItemsSessionBean.class);
        if (pssb.getSelectedPubItems().size() != 0)
        {
            // export format and file format.
            ExportFormatVO curExportFormat = sb.getCurExportFormatVO();
            byte[] exportFileData = null;
            try
            {
                exportFileData = icsb.retrieveExportData(curExportFormat, CommonUtils.convertToPubItemVOList(pssb
                        .getSelectedPubItems()));
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
        
        getBasePaginatorListSessionBean().redirect();
        return "";
    }

    public String deleteSelected()
    {
        PubItemStorageSessionBean pssb = (PubItemStorageSessionBean)getSessionBean(PubItemStorageSessionBean.class);
        pssb.getStoredPubItems().removeAll(pssb.getSelectedPubItems());
        getBasePaginatorListSessionBean().redirect();
       
        return "";
    }

    @Override
    public String getListPageName()
    {
        return "CartItemsPage.jsp";
    }
    
    
}
