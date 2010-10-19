package de.mpg.escidoc.pubman.yearbook;

import gov.loc.www.zing.srw.SearchRetrieveResponseType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ContextHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ItemRelationVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.RelationVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.UserAttributeVO;
import de.mpg.escidoc.services.common.valueobjects.FilterTaskParamVO.Filter;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class YearbookItemSessionBean extends FacesBean
{
    private static Logger logger = Logger.getLogger(YearbookItemSessionBean.class);
    public static String BEAN_NAME = "YearbookItemSessionBean";
    
    private PubItemVO yearbookItem;
    private LoginHelper loginHelper;
    private XmlTransforming xmlTransforming;
    private ItemHandler itemHandler;
    private ContextVO yearbookContext;
    
    public YearbookItemSessionBean()
    {
        
        try
        {
            this.loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
            this.itemHandler = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
            InitialContext initialContext = new InitialContext();
            this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            String yearbookContextId = PropertyReader.getProperty("escidoc.pubman.yearbook.context.id");
            ContextHandler ch = ServiceLocator.getContextHandler(loginHelper.getESciDocUserHandle());
            String contextXml = ch.retrieve(yearbookContextId);
            yearbookContext = xmlTransforming.transformToContext(contextXml);
            
            
            
            HashMap<String, String[]> filterParams = new HashMap<String, String[]>();  
            filterParams.put("operation", new String[] {"searchRetrieve"});
            filterParams.put("version", new String[] {"1.1"});
            
            String orgId="";
            for(UserAttributeVO attr : loginHelper.getAccountUser().getAttributes())
            {
                if(attr.getName().equals("o"))
                {
                    orgId = attr.getValue();
                    break;
                }
            }
            //String orgId = "escidoc:persistent25";
            filterParams.put("query", new String[] {"\"/properties/context/id\"=" + yearbookContextId + " and \"/md-records/md-record/publication/creator/organization/identifier\"=" + orgId});
            filterParams.put("maximumRecords", new String[] {"10"});

            String xmlItemList = itemHandler.retrieveItems(filterParams);

            SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
            

            
            if(result.getNumberOfRecords()!=1)
            {
                error(result.getNumberOfRecords() + " yearbook items found!");
            }
            else
            {
                this.setYearbookItem(new PubItemVO((ItemVO)result.getRecords().get(0).getData()));
            }
        }
        catch (Exception e)
        {
            error("Error retrieving yearbook item!");
            logger.error("Error retrieving yearbook item!", e);
        }

    }

    
    
    
    public void setYearbookItem(PubItemVO yearbookItem)
    {
        this.yearbookItem = yearbookItem;
    }

    public PubItemVO getYearbookItem()
    {
        return yearbookItem;
    }
    
    
    
    public int getNumberOfMembers()
    {
        if(yearbookItem.getRelations()!=null)
        {
            return yearbookItem.getRelations().size();
        }
        else
        {
            return 0;
        }
       
    }
    
    public void addMembers(List<ItemRO> itemIds)
    {
        try
        {
      
            List<ItemRO> newRels = new ArrayList<ItemRO>();
            
            List<String> currentRelations = new ArrayList<String>();
            for(ItemRelationVO rel : yearbookItem.getRelations())
            {
               currentRelations.add(rel.getTargetItemRef().getObjectId());
            }
            
            int successful = 0;
            for(ItemRO id : itemIds)
            {
                if(currentRelations.contains(id.getObjectId()))
                {
                    warn("Item " + id.getObjectId() + " is already in yearbook");
                }
                else
                {

                    newRels.add(id);
                    successful++;
                }
                
            }
            addRelations(newRels);
            info("Added " + successful + " items to yearbook");
        }
        catch (Exception e)
        {
           error("Error adding members to yearbook");
           logger.error("Error adding members to yearbook", e);
        }
    }
    
    public void removeMembers(List<ItemRO> itemIds)
    {
        try
        {
      
           
            removeRelations(itemIds); 
            info("Removed successfully items from yearbook");
        }
        catch (Exception e)
        {
           error("Error removing members from yearbook");
           logger.error("Error removing members from yearbook", e);
        }
    }
    
    
    private void addRelations(List<ItemRO> relList) throws Exception
    {
        
        if(relList.size()>0)
        {
           
            String taskParam = createRelationTaskParam(relList, yearbookItem.getModificationDate());
            itemHandler.addContentRelations(yearbookItem.getVersion().getObjectId(), taskParam); 
            String updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
            this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
        }
    }
    
    private void removeRelations(List<ItemRO> relList) throws Exception
    {
        
        if(relList.size()>0)
        {
           
            String taskParam = createRelationTaskParam(relList, yearbookItem.getModificationDate());
            itemHandler.removeContentRelations(yearbookItem.getVersion().getObjectId(), taskParam); 
            String updatedItemXml = itemHandler.retrieve(yearbookItem.getVersion().getObjectId());
            this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
        }
    }
    
    private static String createRelationTaskParam(List<ItemRO> relList, Date lmd)
    {
        String filter = "<param last-modification-date=\"" + JiBXHelper.serializeDate(lmd) + "\">";
        
        for(ItemRO rel : relList)
        {
            filter+="<relation><targetId>" + rel.getObjectId() + "</targetId><predicate>http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasMember</predicate></relation>";
        }
        
        filter+="</param>";
        return filter;
    }
    
    
    
    private void updateItem(PubItemVO item) throws Exception
    {
        String itemXml = xmlTransforming.transformToItem(item);
        String updatedItemXml = itemHandler.update(item.getVersion().getObjectId(), itemXml);
        this.yearbookItem = xmlTransforming.transformToPubItem(updatedItemXml);
        
    }




    public void setYearbookContext(ContextVO yearbookContext)
    {
        this.yearbookContext = yearbookContext;
    }




    public ContextVO getYearbookContext()
    {
        return yearbookContext;
    }
    
}
