package de.mpg.escidoc.pubman.yearbook;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.common_presentation.BaseListRetrieverRequestBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean;
import de.mpg.escidoc.pubman.itemList.PubItemListSessionBean.SORT_CRITERIA;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.MetadataSearchQuery;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;

/**
 * This bean is an implementation of the BaseListRetrieverRequestBean class for the Yearbook workspace.
 * It uses the PubItemListSessionBean as corresponding BasePaginatorListSessionBean and adds additional functionality for filtering the items by their state.
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author: mfranke $ (last modification)
 * @version $Revision: 3780 $ $LastChangedDate: 2010-07-23 10:01:12 +0200 (Fri, 23 Jul 2010) $
 *
 */
public class YearbookArchiveRetrieverRequestBean extends BaseListRetrieverRequestBean<PubItemVOPresentation, PubItemListSessionBean.SORT_CRITERIA>
{
    private static final Logger logger = Logger.getLogger(YearbookArchiveRetrieverRequestBean.class);
    public static final String BEAN_NAME = "YearbookArchiveRetrieverRequestBean";
    private String selectedSortOrder;
    private static String parameterSelectedOrgUnit = "orgUnit"; 
    private int numberOfRecords;
    private Search searchService;
    private PubItemListSessionBean pilsb;
    
    public YearbookArchiveRetrieverRequestBean()
    {
        super((PubItemListSessionBean)getSessionBean(PubItemListSessionBean.class), false);   
        
    }

    @Override
    public void init()
    {
        pilsb = (PubItemListSessionBean)getBasePaginatorListSessionBean();
        
        try
        {
            InitialContext initialContext = new InitialContext();
            this.searchService = (Search) initialContext.lookup(Search.SERVICE_NAME);
            
        }
        catch (NamingException e)
        {
            logger.error("Error when trying to find search service.", e);
            error("Did not find Search service");
        }
      
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return numberOfRecords;
    }
    
    /**
     * Reads out the item state parameter from the HTTP GET request and sets an default value if it is null.
     */
    @Override
    public void readOutParameters()
    {
        String orgUnit = getExternalContext().getRequestParameterMap().get(parameterSelectedOrgUnit);
        if (orgUnit==null) 
        {
           
                setSelectedOrgUnit(getSessionBean().getSelectedOrgUnit());
           
            
        }
        else
        {
            setSelectedOrgUnit(orgUnit);
        }
    }

    @Override
    public String getType()
    {
        return "SearchResult";
    }
    
    @Override
    public String getListPageName()
    {
        return "YearbookArchiveItemViewPage.jsp";
    }
    
    public List<SelectItem> getOrgUnitSelectItems()
    {
        return this.getSessionBean().getOrgUnitSelectItems(); 
    }

    public void setSelectedOrgUnit(String selectedOrgUnit)
    {        
        this.getSessionBean().setSelectedOrgUnit(selectedOrgUnit); 
        getBasePaginatorListSessionBean().getParameterMap().put(parameterSelectedOrgUnit, selectedOrgUnit);
    }

    public String getSelectedOrgUnit()
    {
        return this.getSessionBean().getSelectedOrgUnit(); 
    } 
    
    public YearbookCandidatesSessionBean getSessionBean()
    {
        return (YearbookCandidatesSessionBean) getSessionBean(YearbookCandidatesSessionBean.class);
    }
    
    /**
     * Called by JSF whenever the organizational unit filter menu is changed. Causes a redirect to the page with updated context GET parameter.
     * @return
     */
    public String changeOrgUnit()
    {
        try
        { 
            getBasePaginatorListSessionBean().setCurrentPageNumber(1);
            getBasePaginatorListSessionBean().redirect();
        }
        catch (Exception e)
        {
           error("Could not redirect");
        }
        return "";
    }

    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }
    
   
    
   
    
    @Override
    public List<PubItemVOPresentation> retrieveList(int offset, int limit, SORT_CRITERIA sc)
    {
    	  List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
          try
          {
              LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class); 
              YearbookArchiveBean yearbookArchiveBean = (YearbookArchiveBean) getSessionBean(YearbookArchiveBean.class);
              InitialContext initialContext = new InitialContext();
              XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        
              // define the filter criteria
              FilterTaskParamVO filter = new FilterTaskParamVO();
              
              // add all contexts for which the user has moderator rights (except the "all" item of the menu)
              List<ItemRO> itemRelations = new ArrayList<ItemRO> ();
              for(ItemRelationVO itemRelation : yearbookArchiveBean.getSelectedYearbook().getRelations())
              {
                 itemRelations.add(itemRelation.getTargetItemRef());
              }
              if (!itemRelations.isEmpty())
              {
                  filter.getFilterList().add(filter.new ItemRefFilter(itemRelations));
              }
              // add views per page limit
              Filter f8 = filter.new LimitFilter(String.valueOf(limit));
              filter.getFilterList().add(f8);
              Filter f9 = filter.new OffsetFilter(String.valueOf(offset));
              filter.getFilterList().add(f9);
              if(sc != null)
              {
                  Filter sortFilter = filter.new OrderFilter(sc.getSortPath(), sc.getSortOrder());
                  filter.getFilterList().add(sortFilter);
              }
              if (this.getSessionBean().getSelectedOrgUnit() != null)
              {
                  Filter organisationFilter = filter.new PersonsOrganizationsFilter(this.getSessionBean().getSelectedOrgUnit());
                  filter.getFilterList().add(organisationFilter);
              }
              
              String xmlItemList = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()).retrieveItems(filter.toMap());

              SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);

              List<PubItemVO> pubItemList = new ArrayList<PubItemVO>();
              for(SearchRetrieveRecordVO record : result.getRecords())
              {
                  pubItemList.add((PubItemVO)record.getData());
              }
              
              numberOfRecords =result.getNumberOfRecords();
              returnList = CommonUtils.convertToPubItemVOPresentationList(pubItemList);
          }
          catch (Exception e)
          {
              logger.error("Error in retrieving items", e);
              error("Error in retrieving items");
              numberOfRecords = 0;
          }
          return returnList;
    }
    
    public String exportSelectedDownload()
    {
    	try {
			pilsb.downloadExportFile(pilsb.getSelectedItems());

		} catch (Exception e) 
		{
			error("Error while exporting");
			logger.error("Error exporting yearbook", e);
		}
        
    	return "";
    }
}
