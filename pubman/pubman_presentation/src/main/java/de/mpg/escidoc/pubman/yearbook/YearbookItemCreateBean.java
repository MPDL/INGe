package de.mpg.escidoc.pubman.yearbook;

import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.contextList.ContextListSessionBean;
import de.mpg.escidoc.pubman.util.LoginHelper;
import de.mpg.escidoc.pubman.util.PubContextVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.referenceobjects.AccountUserRO;
import de.mpg.escidoc.services.common.referenceobjects.AffiliationRO;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveRecordVO;
import de.mpg.escidoc.services.common.valueobjects.SearchRetrieveResponseVO;
import de.mpg.escidoc.services.common.valueobjects.UserAttributeVO;
import de.mpg.escidoc.services.common.valueobjects.intelligent.grants.Grant;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selector;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selector.Type;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.Selectors;
import de.mpg.escidoc.services.common.valueobjects.intelligent.usergroup.UserGroup;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;



public class YearbookItemCreateBean extends FacesBean
{
    public static String BEAN_NAME = "YearbookItemCreateBean";
    private static Logger logger = Logger.getLogger(YearbookItemCreateBean.class);
    private String title;
    private List<ContextRO> contextIds;
    private String dateFrom;
    private String dateTo;
    private List<AccountUserRO> collaboratorUserIds;
    private AffiliationVO affiliation;
    private List<SelectItem> contextSelectItems;
    private List<SelectItem> userAccountSelectItems;
    
    private String context;
    
    private int contextPosition;
    private int userPosition;

	private LoginHelper loginHelper;
	private XmlTransforming xmlTransforming;
    


	public YearbookItemCreateBean() throws Exception
    {
		xmlTransforming = new XmlTransformingBean();
		loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
		this.affiliation = loginHelper.getAccountUsersAffiliations().get(0);
		
		initContextMenu();
		initUserAccountMenu();
		
		contextIds = new ArrayList<ContextRO>();
		contextIds.add(new ContextRO((String)contextSelectItems.get(0).getValue()));
		collaboratorUserIds = new ArrayList<AccountUserRO>();
		collaboratorUserIds.add(new AccountUserRO((String)getUserAccountSelectItems().get(0).getValue()));
		

    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

   

   public String addContext()
   {
	   contextIds.add(getContextPosition()+1, new ContextRO((String)getContextSelectItems().get(0).getValue())); 
	   return "";
   }
   
   public String removeContext()
   {
	   contextIds.remove(getContextPosition());
	   return "";
   }
   
   public String addUser()
   {
	   collaboratorUserIds.add(getUserPosition()+1, new AccountUserRO((String)getUserAccountSelectItems().get(0).getValue()));
	   return "";
   }
   
   public String removeUser()
   {
	   collaboratorUserIds.remove(getUserPosition());
	   return "";
   }
    
   
	/*
    public String writeContext()
    {
        if(getContextIds().length()>0)
        {
        	setContextIds(getContextIds()+","+getContext());
        }
        else
        	setContextIds(getContext());
        return null;
    }
*/
    
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

   
    public String save()
    {
        
        try {
			loginHelper = (LoginHelper) getSessionBean(LoginHelper.class);
			
			ItemHandler ih = ServiceLocator.getItemHandler(loginHelper.getESciDocUserHandle()); 
			
			PubItemVO pubItem = new PubItemVO();
			pubItem.setContentModel(PropertyReader.getProperty("escidoc.pubman.yearbook.content-model.id"));
			pubItem.setContext(new ContextRO(PropertyReader.getProperty("escidoc.pubman.yearbook.context.id")));
			MdsPublicationVO mds = new MdsPublicationVO();
			pubItem.getMetadataSets().add(mds);
			mds.setTitle(new TextVO(getTitle()));
			
			
			
			

			CreatorVO creatorVO = new CreatorVO();
			OrganizationVO orgUnit = new OrganizationVO();
			orgUnit.setName(new TextVO(getAffiliation().getDefaultMetadata().getName())); 
			orgUnit.setIdentifier(getAffiliation().getReference().getObjectId());
			creatorVO.setOrganization(orgUnit);
			mds.getCreators().add(creatorVO);
			
      
			//date query
			String datequery = "(( escidoc.publication.issued>=\"" + getDateFrom() + "\"";
			datequery+=(" AND escidoc.publication.issued<=\"" + getDateTo() + "\" )");
			datequery += (" OR ( escidoc.publication.published-online>=\"" + getDateFrom() + "\"");
			datequery += (" AND escidoc.publication.published-online<=\"" + getDateTo() + "\" ) )");
			
			mds.getSubjects().add(new TextVO("escidoc.publication.issued>=\"" + getDateFrom() + "\""));
			mds.getSubjects().add(new TextVO("escidoc.publication.issued<=\"" + getDateTo() + "\""));
			mds.getSubjects().add(new TextVO("escidoc.publication.published-online>=\"" + getDateFrom() + "\""));
			mds.getSubjects().add(new TextVO("escidoc.publication.published-online<=\"" + getDateTo() + "\""));
			
			//org query
			String orgIndex = "escidoc.any-organization-pids=\"" + getAffiliation().getReference().getObjectId() + "\"";
			String orgQuery = "( " + orgIndex + " )";
			mds.getSubjects().add(new TextVO(orgIndex));
			
			//context query
			String contextQuery="";
			if(contextIds!=null && contextIds.size()>0)
			{
			    contextQuery+="(";
			    
			    int i=0;
			    for(ContextRO contextId : contextIds)
			    {
			        if(!contextId.getObjectId().trim().equals(""))
			        {
			            if(i!=0)
			            {
			                contextQuery += " OR";
			            }
			            String context = " escidoc.context.objid=\"" + contextId.getObjectId().trim() + "\""; 
			            contextQuery += context;
			            mds.getSubjects().add(new TextVO(context.trim()));
			            i++;
			        }
			    }
			    contextQuery+=" )";
			}

			
			String query = datequery + " AND " + orgQuery + " AND " + contextQuery;
			
			//String inverseQuery= contextQuery + " AND " + orgQuery + " AND " +inverseDatequery; 
			
			pubItem.getLocalTags().add(query);
			//pubItem.getLocalTags().add(inverseQuery);
			
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
			filterParams.put("query", new String[] {"\"/properties/context/id\"=" + yearbookContextId + " and \"/md-records/md-record/publication/creator/organization/identifier\"=" + getAffiliation().getReference().getObjectId()});
			filterParams.put("maximumRecords", new String[] {"10"});
			String xmlItemList = ih.retrieveItems(filterParams); 
			SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponse(xmlItemList);
			
			if(result.getNumberOfRecords()>0)
			{
			    error("A yearbook related to this organization object id already exists");
			    return "";
			}
			    
			else
			{
			    String itemXml = xmlTransforming.transformToItem(pubItem); 
			    
			    String updatedXml = ih.create(itemXml);
			    pubItem = xmlTransforming.transformToPubItem(updatedXml);
			    
			    info(getMessage("Yearbook_createdSuccessfully"));
			    
			    UserGroup ug = new UserGroup();
			    ug.setName("Yearbook User Group for: " + getAffiliation().getDefaultMetadata().getName() + " (" +getAffiliation().getReference().getObjectId() +")" );
			    ug.setLabel("yearbook " + getAffiliation().getReference().getObjectId());
			   
			    ug.createInCoreservice(loginHelper.getESciDocUserHandle());
			    
			    for(AccountUserRO userId : collaboratorUserIds)
			    {
			    	if(!("").equals(userId.getObjectId()))
			    	{
			    		Selector selector = new Selector();
					    selector.setType(Type.INTERNAL);
					    selector.setObjid(userId.getObjectId());
					    selector.setName("user-account");
					    selector.setString(userId.getObjectId());
					    Selectors selectors = new Selectors();
					    selectors.getSelectors().add(selector);
					    ug.addNewSelectorsInCoreservice(selectors, loginHelper.getESciDocUserHandle());
			    	}
			    	
			    }
			  
			    
			    
			   
			    
			    //Create collaborator grant
			    Grant grant = new Grant();
			    grant.setAssignedOn(pubItem.getVersion().getObjectId());
			    grant.setGrantedTo(ug.getObjid());
			    grant.setGrantType("user-group");
			    grant.setRole(Grant.CoreserviceRole.COLLABORATOR_MODIFIER.getRoleId());
			    grant.createInCoreservice(loginHelper.getESciDocUserHandle(), "Grant for Yearbook created");
			    
			    info(getMessage("Yearbook_grantsAdded"));
			    
			}

			YearbookItemSessionBean yisb = (YearbookItemSessionBean)getSessionBean(YearbookItemSessionBean.class);
			yisb.initYearbook();
			return "loadYearbookPage";
		
		} catch (Exception e) {
			error(getMessage("Yearbook_creationError"));
			logger.error("Error while creating yearbook", e);
			return "";
		}
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


	public void setAffiliation(AffiliationVO affiliation) {
		this.affiliation = affiliation;
	}

	public AffiliationVO getAffiliation() {
		return affiliation;
	}

	public void setContextSelectItems(List<SelectItem> contextSelectItems) {
		this.contextSelectItems = contextSelectItems;
	}

	public List<SelectItem> getContextSelectItems() {
		
		return contextSelectItems;
	}

	public void setUserAccountSelectItems(List<SelectItem> userAccountSelectItems) {
		this.userAccountSelectItems = userAccountSelectItems;
	}

	public List<SelectItem> getUserAccountSelectItems() {
		return userAccountSelectItems;
	}

	

	public void setContextPosition(int contextPosition) {
		this.contextPosition = contextPosition;
	}

	public int getContextPosition() {
		return contextPosition;
	}

	
	
	public void initUserAccountMenu() throws Exception
	{
		UserAccountHandler uah = ServiceLocator.getUserAccountHandler(loginHelper.getESciDocUserHandle());
		userAccountSelectItems = new ArrayList<SelectItem>();
		userAccountSelectItems.add(new SelectItem("", ""));
		
		 HashMap<String, String[]> filterParams = new HashMap<String, String[]>();  
         filterParams.put("operation", new String[] {"searchRetrieve"});
         filterParams.put("version", new String[] {"1.1"});
         
         
         //String orgId = "escidoc:persistent25";
         filterParams.put("query", new String[] {"\"http://escidoc.de/core/01/structural-relations/organizational-unit\"=" + getAffiliation().getReference().getObjectId()});
         filterParams.put("maximumRecords", new String[] {"100"});
         
         String uaList = uah.retrieveUserAccounts(filterParams);
         SearchRetrieveResponseVO result = xmlTransforming.transformToSearchRetrieveResponseAccountUser(uaList);
         
         
         List<SearchRetrieveRecordVO> results = result.getRecords();
         for(SearchRetrieveRecordVO rec : results)
         { 
        	 AccountUserVO userVO = (AccountUserVO)rec.getData();
        	 if(!userVO.getReference().getObjectId().equals(loginHelper.getAccountUser().getReference().getObjectId()))
        	 {
        		 userAccountSelectItems.add(new SelectItem(userVO.getReference().getObjectId(), userVO.getName())); 
        	 }
        	 
         }
         
         
        
         
         
		/*
		for(PubContextVOPresentation context : clsb.getModeratorContextList())
		{
			userAccountSelectItems.add(new SelectItem(context.getReference().getObjectId(), context.getName() + " (" + context.getReference().getObjectId() + ")"));
		}
		*/
	}
	
	public void initContextMenu()
	{
		contextSelectItems = new ArrayList<SelectItem>();
		ContextListSessionBean clsb = (ContextListSessionBean) getSessionBean(ContextListSessionBean.class);
		for(PubContextVOPresentation context : clsb.getModeratorContextList())
		{
			contextSelectItems.add(new SelectItem(context.getReference().getObjectId(), context.getName() + " (" + context.getReference().getObjectId() + ")"));
		}
	}

	

	public void setUserPosition(int userPosition) {
		this.userPosition = userPosition;
	}

	public int getUserPosition() {
		return userPosition;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getContext() {
		return context;
	}

	public void setCollaboratorUserIds(List<AccountUserRO> collaboratorUserIds) {
		this.collaboratorUserIds = collaboratorUserIds;
	}

	public List<AccountUserRO> getCollaboratorUserIds() {
		return collaboratorUserIds;
	}

	public void setContextIds(List<ContextRO> contextIds) {
		this.contextIds = contextIds;
	}

	public List<ContextRO> getContextIds() {
		return contextIds;
	}
    
   
    
}
