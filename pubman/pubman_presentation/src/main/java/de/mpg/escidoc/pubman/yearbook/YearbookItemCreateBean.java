package de.mpg.escidoc.pubman.yearbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;

import de.escidoc.www.services.om.ItemHandler;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;



public class YearbookItemCreateBean extends FacesBean
{
    public static String BEAN_NAME = "YearbookItemCreateBean";
    
    private String title;
    private String orgId;
    private String contextIds;
    private String dateFrom;
    private String dateTo;
    private String collaboratorUserIds;
    
    public YearbookItemCreateBean()
    {
        
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getOrgId()
    {
        return orgId;
    }

    public void setOrgId(String orgId)
    {
        this.orgId = orgId;
    }

    public String getContextIds()
    {
        return contextIds;
    }

    public void setContextIds(String contextIds)
    {
        this.contextIds = contextIds;
    }

    public String getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public String getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(String dateTo)
    {
        this.dateTo = dateTo;
    }

   
    public String save() throws Exception
    {
        
        LoginHelper loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
        InitialContext initialContext = new InitialContext();
        XmlTransforming xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
        ItemHandler ih = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle());
        
        PubItemVO pubItem = new PubItemVO();
        pubItem.setContentModel(PropertyReader.getProperty("escidoc.pubman.yearbook.content-model.id"));
        pubItem.setContext(new ContextRO(PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")));
        MdsPublicationVO mds = new MdsPublicationVO();
        pubItem.getMetadataSets().add(mds);
        mds.setTitle(new TextVO(getTitle()));
        
        OrganizationalUnitHandler ouh = ServiceLocator.getOrganizationalUnitHandler(loginHelper.getESciDocUserHandle());
        String orgUnitXml = ouh.retrieve(getOrgId());
        AffiliationVO aff = xmlTransforming.transformToAffiliation(orgUnitXml);
        

        CreatorVO creatorVO = new CreatorVO();
        OrganizationVO orgUnit = new OrganizationVO();
        orgUnit.setName(new TextVO(aff.getDefaultMetadata().getName())); 
        orgUnit.setIdentifier(aff.getReference().getObjectId());
        creatorVO.setOrganization(orgUnit);
        mds.getCreators().add(creatorVO);
        
       

        String datequery = "(( escidoc.publication.date>=\"" + getDateFrom() + "\"";
        datequery+=(" AND escidoc.publication.date<=\"" + getDateTo() + "\" )");
        datequery += (" OR ( escidoc.publication.published-online>=\"" + getDateFrom() + "\"");
        datequery += (" AND escidoc.publication.published-online<=\"" + getDateTo() + "\" ) )");
        
        String orgQuery = "( escidoc.any-organization-pids=\"" + getOrgId() + "\" )";
        
        String contextQuery="";
        if(contextIds!=null && !contextIds.trim().equals(""))
        {
            contextQuery+="(";
            String[] conIds = contextIds.split(",");
            int i=0;
            for(String contextId : conIds)
            {
                if(!contextId.trim().equals(""))
                {
                    if(i!=0)
                    {
                        contextQuery += " OR";
                    } 
                    contextQuery+=" escidoc.context.objid=\"" + contextId.trim() + "\""; 
                    i++;
                }
            }
            contextQuery+=" )";
        }

        
        String query = datequery + "AND" + orgQuery + "AND" + contextQuery;
        String inverseQuery= contextQuery + "NOT ( " + datequery + " AND " + orgQuery + " ) ";
        
        pubItem.getLocalTags().add(query);
        pubItem.getLocalTags().add(inverseQuery);
        
//        YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
//        
//        if(yisb.getYearbookItem()!=null)
//        {
//            error("There already exists an yearbook item with id: " + yisb.getYearbookItem().getVersion().getObjectId());
//            
//        }
//        else
//            
//        {
        
        
        HashMap<String, String[]> filterParams = new HashMap<String, String[]>(); 
        
        
        String yearbookContextId = PropertyReader.getProperty("escidoc.pubman.yearbook.context.id");
        filterParams.put("operation", new String[] {"searchRetrieve"});
        filterParams.put("version", new String[] {"1.1"});
        filterParams.put("query", new String[] {"\"/properties/context/id\"=" + yearbookContextId + " and \"/md-records/md-record/publication/creator/organization/identifier\"=" + getOrgId()});
        filterParams.put("maximumRecords", new String[] {"10"});
        String xmlItemList = ih.retrieveItems(filterParams); 
        SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
        
        if(result.getNumberOfRecords()>0)
        {
            error("A yearbook related to this object id already exists");
        }
            
        else
        {
            String itemXml = xmlTransforming.transformToItem(pubItem);
            
            String updatedXml = ih.create(itemXml);
            pubItem = xmlTransforming.transformToPubItem(updatedXml);
            
            info("Item created successfully with id: " + pubItem.getVersion().getObjectId());
            
            
            //Create collaborator grant
            Grant grant = new Grant();
            grant.setAssignedOn(pubItem.getVersion().getObjectId());
            grant.setGrantedTo(getCollaboratorUserIds());
            grant.setGrantType("user-account");
            grant.setRole(Grant.CoreserviceRole.COLLABORATOR_MODIFIER.getRoleId());
            grant.createInCoreservice(loginHelper.getESciDocUserHandle(), "Grant for Yearbook created");
            
            info("Collaborator Grant added successfully: " + grant.getObjid());
            
        }
            
           
            
            
        //}

        return "";
    }

    public void setCollaboratorUserIds(String collaboratorUserId)
    {
        this.collaboratorUserIds = collaboratorUserId;
    }

    public String getCollaboratorUserIds()
    {
        return collaboratorUserIds;
    }
    
    
    public String check()
    {
        YearbookItemSessionBean yisb = (YearbookItemSessionBean) getSessionBean(YearbookItemSessionBean.class);
        return "";
//      
//      if(yisb.getYearbookItem()!=null)
//      {
//          error("There already exists an yearbook item with id: " + yisb.getYearbookItem().getVersion().getObjectId());
//          
//      }
//      else
//          
//      {
    }
    
   
    
}
