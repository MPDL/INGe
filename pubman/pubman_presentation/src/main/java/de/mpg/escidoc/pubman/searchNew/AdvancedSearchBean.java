package de.mpg.escidoc.pubman.searchNew;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;
import javax.naming.InitialContext;

import org.ajax4jsf.config.FacesConfig;
import org.apache.axis.utils.URLHashSet;
import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.search.SearchRetrieverRequestBean;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.DisplayType;
import de.mpg.escidoc.pubman.searchNew.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.checkbox.EmbargoDateAvailableSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.component.FileAvailableSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.component.LocatorAvailableSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.dates.DateSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.genre.GenreListSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.LogicalOperator;
import de.mpg.escidoc.pubman.searchNew.criterions.operators.Parenthesis;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.AnyFieldSearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ComponentContentCategory;
import de.mpg.escidoc.pubman.searchNew.criterions.standard.ComponentVisibilitySearchCriterion;
import de.mpg.escidoc.pubman.searchNew.criterions.stringOrHiddenId.PersonSearchCriterion;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.SelectItemComparator;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.pubman.PubItemDepositing;

public class AdvancedSearchBean extends FacesBean implements Serializable{
	
	private static Logger logger = Logger.getLogger(AdvancedSearchBean.class);
	
	private List<SearchCriterionBase> criterionList;
	
	private List<SelectItem> criterionTypeListMenu = initCriterionTypeListMenu();
	
	private List<SelectItem> operatorTypeListMenu = initOperatorListMenu();
	
	private List<SelectItem> contextListMenu;
	
	private List<SelectItem> genreListMenu = initGenreListMenu();
	
	private List<SelectItem> contentCategoryListMenu = initContentCategoryListMenu();
	
	private List<SelectItem> componentVisibilityListMenu = initComponentVisibilityListMenu();
	
	private List<SelectItem> subjectTypesListMenu = initSubjectTypesListMenu();

	private SearchCriterionBase fileAvailableSearchCriterion;
	
	private SearchCriterionBase locatorAvailableSearchCriterion;
	
	private SearchCriterionBase embargoDateAvailableSearchCriterion;
	
	private SearchCriterionBase componentContentCategory;
	
	private SearchCriterionBase componentVisibilitySearchCriterion;
	
	private SearchCriterionBase genreListSearchCriterion;

	private Parenthesis currentlyOpenedParenthesis;
	private Map<SearchCriterionBase, Boolean> possibleCriterionsForClosingParenthesisMap = new HashMap<SearchCriterionBase, Boolean>();
	
	private Map<SearchCriterionBase, Integer> balanceMap = new HashMap<SearchCriterionBase, Integer>();
	
	
	
	public AdvancedSearchBean()
	{
		
	}
	
	
	private void clearAndInit()
	{
		this.fileAvailableSearchCriterion = new FileAvailableSearchCriterion();
		this.locatorAvailableSearchCriterion = new LocatorAvailableSearchCriterion();
		this.embargoDateAvailableSearchCriterion = new EmbargoDateAvailableSearchCriterion();
		this.componentContentCategory = new ComponentContentCategory();
		this.componentVisibilitySearchCriterion = new ComponentVisibilitySearchCriterion();
		this.genreListSearchCriterion = new GenreListSearchCriterion();
		
		initCriterionListWithEmptyValues();
		
	}
	
	private void initCriterionListWithEmptyValues()
	{
		criterionList = new ArrayList<SearchCriterionBase>();
		criterionList.add(new AnyFieldSearchCriterion());
		criterionList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		criterionList.add(new PersonSearchCriterion(SearchCriterion.ANYPERSON));
		criterionList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		criterionList.add(new DateSearchCriterion(SearchCriterion.ANYDATE));
	}
	
	
	
	
	private void initWithQueryParam(String queryParam)
	{
		List<SearchCriterionBase> scList = SearchCriterionBase.queryStringToScList(queryParam);

		List<SearchCriterionBase> toBeRemovedList = new ArrayList<SearchCriterionBase>();
		for(int i=scList.size()-1; i>=0; i--)
		{
			
			SearchCriterionBase sc = scList.get(i);
			if(SearchCriterion.FILE_AVAILABLE.equals(sc.getSearchCriterion()))
			{
				this.fileAvailableSearchCriterion = sc;
				toBeRemovedList.add(sc);
			}
			else if(SearchCriterion.LOCATOR_AVAILABLE.equals(sc.getSearchCriterion()))
			{
				this.locatorAvailableSearchCriterion = sc;
				toBeRemovedList.add(sc);
			}
			else if(SearchCriterion.EMBARGO_DATE_AVAILABLE.equals(sc.getSearchCriterion()))
			{
				this.embargoDateAvailableSearchCriterion = sc;
				toBeRemovedList.add(sc);
			}
			else if(SearchCriterion.COMPONENT_CONTENT_CATEGORY.equals(sc.getSearchCriterion()))
			{
				this.componentContentCategory = sc;
				toBeRemovedList.add(sc);
			}
			else if(SearchCriterion.COMPONENT_VISIBILITY.equals(sc.getSearchCriterion()))
			{
				this.componentVisibilitySearchCriterion = sc;
				toBeRemovedList.add(sc);
			}
			else if(SearchCriterion.GENRE_DEGREE_LIST.equals(sc.getSearchCriterion()))
			{
				this.genreListSearchCriterion = sc;
				toBeRemovedList.add(sc);
			}
		}
		
		for(SearchCriterionBase sc : toBeRemovedList)
		{
			SearchCriterionBase.removeSearchCriterionWithOperator(scList, sc);
		}
		
		this.criterionList = scList;
		
		if(criterionList.isEmpty())
		{
			initCriterionListWithEmptyValues();	
		}
	}
	
	
	/**
	 * Dummy getter method which reads out query parameter form url;
	 * @return
	 */
	public String getReadOutParams()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		String query = fc.getExternalContext().getRequestParameterMap().get("q");
		query = CommonUtils.fixURLEncoding(query);
		boolean isPostback = fc.getRenderKit().getResponseStateManager().isPostback(fc);
		
		if(!isPostback)
		{
			if(query!=null && !query.trim().isEmpty())
			{
				logger.info("Found query, initialize");
				clearAndInit();
				initWithQueryParam(query);
			}
			else
			{
				logger.info("No internal query found, initialize empty");
				clearAndInit();
			}
		}
		else
		{
			//logger.info("Postback, do nothing");
			
		}
		return "";
		
		
	}


	private List<SelectItem> initComponentVisibilityListMenu() {
		return Arrays.asList(this.i18nHelper.getSelectedItemsComponentVisibility(true));
	}


	private List<SelectItem> initContentCategoryListMenu() {
		return Arrays.asList(this.i18nHelper.getSelectItemsContentCategory(true));
	}
	
	private List<SelectItem> initGenreListMenu() {
		return Arrays.asList(this.i18nHelper.getSelectItemsGenre());
	}
	
	private List<SelectItem> initSubjectTypesListMenu()
    {
        List <SelectItem> vocabs = new ArrayList<SelectItem>();
        try
        {
            String vocabsStr = PropertyReader.getProperty("escidoc.cone.subjectVocab");
            String [] vocabsArr = vocabsStr.split(";");
            for (int i = 0; i< vocabsArr.length; i++)
            {
            	String type= vocabsArr[i].trim().toUpperCase().replace("-", "_");
            	String label= vocabsArr[i].trim().toUpperCase();
            	SelectItem si = new SelectItem(type, label);
                vocabs.add(si);
            }
        }
        catch(Exception e)
        {
            logger.error("Could not read Property: 'escidoc.cone.subjectVocab'", e);
        }
        return vocabs;
    }
	
	
	


	private List<SelectItem> initCriterionTypeListMenu()
	{
		List<SelectItem> criterionTypeList = new ArrayList<SelectItem>();
		
		
		//General
		criterionTypeList.add(new SelectItem(SearchCriterion.TITLE, getLabel("adv_search_lblRgbTitle")));
		criterionTypeList.add(new SelectItem(SearchCriterion.KEYWORD, getLabel("adv_search_lblRgbTopic")));
		criterionTypeList.add(new SelectItem(SearchCriterion.CLASSIFICATION, getLabel("adv_search_lblClassification")));
		criterionTypeList.add(new SelectItem(SearchCriterion.ANY, getLabel("adv_search_lblRgbAny")));
		criterionTypeList.add(new SelectItem(SearchCriterion.ANYFULLTEXT, getLabel("adv_search_lblRgbAnyFulltext")));
		
		//Persons
		List<SelectItem> personGroupList = new ArrayList<SelectItem>();
		personGroupList.add(new SelectItem(SearchCriterion.ANYPERSON, getLabel("adv_search_lblSearchPerson")));
		personGroupList.add(new SelectItem(SearchCriterion.AUTHOR, getLabel("ENUM_CREATORROLE_AUTHOR")));
		personGroupList.add(new SelectItem(SearchCriterion.EDITOR,getLabel("ENUM_CREATORROLE_EDITOR")));
		personGroupList.add(new SelectItem(SearchCriterion.ADVISOR,getLabel("ENUM_CREATORROLE_ADVISOR")));
		personGroupList.add(new SelectItem(SearchCriterion.ARTIST,getLabel("ENUM_CREATORROLE_ARTIST")));
		personGroupList.add(new SelectItem(SearchCriterion.COMMENTATOR,getLabel("ENUM_CREATORROLE_COMMENTATOR")));
		personGroupList.add(new SelectItem(SearchCriterion.CONTRIBUTOR,getLabel("ENUM_CREATORROLE_CONTRIBUTOR")));
		personGroupList.add(new SelectItem(SearchCriterion.ILLUSTRATOR,  getLabel("ENUM_CREATORROLE_ILLUSTRATOR")));
		personGroupList.add(new SelectItem(SearchCriterion.PAINTER,  getLabel("ENUM_CREATORROLE_PAINTER")));
		personGroupList.add(new SelectItem(SearchCriterion.PHOTOGRAPHER, getLabel("ENUM_CREATORROLE_PHOTOGRAPHER")));
		personGroupList.add(new SelectItem(SearchCriterion.TRANSCRIBER, getLabel("ENUM_CREATORROLE_TRANSCRIBER")));
		personGroupList.add(new SelectItem(SearchCriterion.TRANSLATOR, getLabel("ENUM_CREATORROLE_TRANSLATOR")));
		personGroupList.add(new SelectItem(SearchCriterion.HONOREE, getLabel("ENUM_CREATORROLE_HONOREE")));
		personGroupList.add(new SelectItem(SearchCriterion.INVENTOR, getLabel("ENUM_CREATORROLE_INVENTOR")));
		personGroupList.add(new SelectItem(SearchCriterion.APPLICANT, getLabel("ENUM_CREATORROLE_APPLICANT")));
		
		SelectItemGroup personGroup = new SelectItemGroup(getLabel("adv_search_lblSearchPerson"));
		personGroup.setSelectItems(personGroupList.toArray(new SelectItem[0]));
		criterionTypeList.add(personGroup);
		
		
		//Organisation
		criterionTypeList.add(new SelectItem(SearchCriterion.ORGUNIT, getLabel("adv_search_lbHeaderOrgan")));
		
		//Dates
		List<SelectItem> dateGroupList = new ArrayList<SelectItem>();
		dateGroupList.add(new SelectItem(SearchCriterion.ANYDATE, getLabel("adv_search_lbHeaderDate")));
		dateGroupList.add(new SelectItem(SearchCriterion.PUBLISHEDPRINT,  getLabel("adv_search_lblChkType_abb_publishedpr")));
		dateGroupList.add(new SelectItem(SearchCriterion.PUBLISHED,  getLabel("adv_search_lblChkType_publishedon")));
		dateGroupList.add(new SelectItem(SearchCriterion.ACCEPTED,  getLabel("adv_search_lblChkType_accepted")));
		dateGroupList.add(new SelectItem(SearchCriterion.SUBMITTED, getLabel("adv_search_lblChkType_submitted")));
		dateGroupList.add(new SelectItem(SearchCriterion.MODIFIED,  getLabel("adv_search_lblChkType_modified")));
		dateGroupList.add(new SelectItem(SearchCriterion.CREATED, getLabel("adv_search_lblChkType_created")));
		
		SelectItemGroup dateGroup = new SelectItemGroup(getLabel("adv_search_lbHeaderDate"));
		dateGroup.setSelectItems(dateGroupList.toArray(new SelectItem[0]));
		criterionTypeList.add(dateGroup);

		
		//Event
		List<SelectItem> eventGroupList = new ArrayList<SelectItem>();
		eventGroupList.add(new SelectItem(SearchCriterion.EVENT,  getLabel("adv_search_lbHeaderEvent")));
		eventGroupList.add(new SelectItem(SearchCriterion.EVENT_STARTDATE,  getLabel("adv_search_lblChkType_abb_event_start_date")));
		eventGroupList.add(new SelectItem(SearchCriterion.EVENT_ENDDATE, getLabel("adv_search_lblChkType_abb_event_end_date")));
		eventGroupList.add(new SelectItem(SearchCriterion.EVENT_INVITATION,  getLabel("ENUM_INVITATIONSTATUS_INVITED")));
		
		SelectItemGroup eventGroup = new SelectItemGroup(getLabel("adv_search_lbHeaderEvent"));
		eventGroup.setSelectItems(eventGroupList.toArray(new SelectItem[0]));
		criterionTypeList.add(eventGroup);
		
		

		//Language
		criterionTypeList.add(new SelectItem(SearchCriterion.GENRE,  getLabel("adv_search_lbHeaderGenre")));
		
		//Language
		criterionTypeList.add(new SelectItem(SearchCriterion.LANG,  getLabel("adv_search_lblLanguageTerm")));
		
		//Source
		criterionTypeList.add(new SelectItem(SearchCriterion.SOURCE, getLabel("adv_search_lbHeaderSource")));
		criterionTypeList.add(new SelectItem(SearchCriterion.JOURNAL, " - " + getLabel("adv_search_lblSourceJournal")));
		
		//LocalTag
		criterionTypeList.add(new SelectItem(SearchCriterion.LOCAL, getLabel("adv_search_lbHeaderLocalTag")));
		
		//Identifier
		criterionTypeList.add(new SelectItem(SearchCriterion.IDENTIFIER, getLabel("adv_search_lbHeaderIdent")));
		
		
		//Collection
		criterionTypeList.add(new SelectItem(SearchCriterion.COLLECTION, getLabel("adv_search_lbHeaderCollection")));
		
		return criterionTypeList;
		
	}
	
	private List<SelectItem> initOperatorListMenu()
	{
		List<SelectItem> operatorTypeList = new ArrayList<SelectItem>();
		
		//General
		operatorTypeList.add(new SelectItem(SearchCriterion.AND_OPERATOR, getLabel("adv_search_logicop_and")));
		operatorTypeList.add(new SelectItem(SearchCriterion.OR_OPERATOR, getLabel("adv_search_logicop_or")));
		operatorTypeList.add(new SelectItem(SearchCriterion.NOT_OPERATOR, getLabel("adv_search_logicop_not")));
		
		
		return operatorTypeList;
		
	}
	
	
	
	

	
	public void changeCriterion(ValueChangeEvent evt)
	{
		
		Integer position = (Integer) evt.getComponent().getAttributes().get("indexOfCriterion");
		SearchCriterion newValue = (SearchCriterion)evt.getNewValue();
		if(newValue != null && position!=null)
		{
			logger.info("Changing sortCriteria at position " + position + " to " + newValue);
			
			criterionList.remove(position.intValue());
			criterionList.add(position, SearchCriterionBase.initSearchCriterion(newValue));
		}
		
		
		/*
		Map<String,String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String position = params.get("indexOfChangedCriterion");
		*/
		
	}
	
	
	
	

	public List<SearchCriterionBase> getCriterionList() {
		return criterionList;
	}


	public void setCriterionList(List<SearchCriterionBase> criterionList) {
		this.criterionList = criterionList;
	}


	public List<SelectItem> getCriterionTypeListMenu() {
		return criterionTypeListMenu;
	}


	public void setCriterionTypeListMenu(List<SelectItem> criterionTypeListMenu) {
		this.criterionTypeListMenu = criterionTypeListMenu;
	}
	
	public void addSearchCriterion(ActionEvent ae)
	{
		Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
		SearchCriterion scType = criterionList.get(position).getSearchCriterion();
		criterionList.add(position.intValue() + 1, SearchCriterionBase.initSearchCriterion(scType));
		criterionList.add(position.intValue() + 1, new LogicalOperator(SearchCriterion.AND_OPERATOR));
		updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
	}
	
	public void removeSearchCriterion(ActionEvent ae)
	{
		Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
		SearchCriterionBase sc = criterionList.get(position);
		SearchCriterionBase.removeSearchCriterionWithOperator(criterionList, sc);
		updateListForClosingParenthesis(this.currentlyOpenedParenthesis);
		
	}
	
	public void addOpeningParenthesis(ActionEvent ae)
	{
		Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
		this.currentlyOpenedParenthesis = new Parenthesis(SearchCriterion.OPENING_PARENTHESIS);
		//add before criterion
		criterionList.add(position.intValue(), currentlyOpenedParenthesis);
		updateListForClosingParenthesis(currentlyOpenedParenthesis);
	}
	
	public void addClosingParenthesis(ActionEvent ae)
	{
		Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
		Parenthesis closingParenthesis = new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS);
		this.currentlyOpenedParenthesis.setPartnerParenthesis(closingParenthesis);
		closingParenthesis.setPartnerParenthesis(this.currentlyOpenedParenthesis);
		this.currentlyOpenedParenthesis = null;
		criterionList.add(position.intValue() + 1, closingParenthesis);
		updateListForClosingParenthesis(closingParenthesis);
	}
	
	public void removeParenthesis(ActionEvent ae)
	{
		Integer position = (Integer) ae.getComponent().getAttributes().get("indexOfCriterion");
		Parenthesis parenthesis = (Parenthesis)criterionList.get(position);
		Parenthesis partnerParenthesis = parenthesis.getPartnerParenthesis();

		criterionList.remove(parenthesis);
		criterionList.remove(partnerParenthesis);
		
		this.currentlyOpenedParenthesis = null;
		possibleCriterionsForClosingParenthesisMap.clear();
	}
	
	
	private void updateListForClosingParenthesis(SearchCriterionBase startParenthesis)
	{
		this.possibleCriterionsForClosingParenthesisMap.clear();
		if(startParenthesis != null)
		{
			int pos = criterionList.indexOf(startParenthesis);
			int balanceCounter = 0;
			
			for(int i=pos; i<criterionList.size(); i++)
			{
				SearchCriterionBase sc = criterionList.get(i);
				
				sc.setLevel(sc.getLevel() + balanceCounter);
				
				if(SearchCriterion.OPENING_PARENTHESIS.equals(sc.getSearchCriterion()))
				{
					balanceCounter++;
				}
				else if (SearchCriterion.CLOSING_PARENTHESIS.equals(sc.getSearchCriterion()))
				{
					balanceCounter--;
				}
				
				
				
			
				
				if(!DisplayType.OPERATOR.equals(sc.getSearchCriterion().getDisplayType()) && balanceCounter == 1)
				{
					possibleCriterionsForClosingParenthesisMap.put(sc, true);
				}
			}
			
		}
	}
	
	
	public List<SelectItem> getContextListMenu() throws Exception
	{
		
		if(contextListMenu == null)
		{
			
			try{
	            InitialContext initialContext = new InitialContext(); 
	            // initialize used Beans
	            PubItemDepositing pubItemDepositing = (PubItemDepositing) initialContext.lookup(PubItemDepositing.SERVICE_NAME);
	            List<ContextVO> contexts = pubItemDepositing.getPubCollectionListForDepositing();
	            
	            contextListMenu = new ArrayList<SelectItem>();
	    		
	            for (ContextVO c : contexts)
	            {
	            	contextListMenu.add(new SelectItem(c.getReference().getObjectId(), c.getName()));
	            }
	            
	            Collections.sort(contextListMenu, new SelectItemComparator());
	            contextListMenu.add(0, new SelectItem("", "--"));
	    		
	            
	            
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
		}
		
		
		return contextListMenu;
		
		
	}


	public List<SelectItem> getOperatorTypeListMenu() {
		return operatorTypeListMenu;
	}


	public void setOperatorTypeListMenu(List<SelectItem> operatorTypeListMenu) {
		this.operatorTypeListMenu = operatorTypeListMenu;
	}

	public String startSearch()
	{
		List<SearchCriterionBase> allCriterions = new ArrayList<SearchCriterionBase>();
		allCriterions.addAll(getCriterionList());
	
		//GenreCriterions
		allCriterions.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		allCriterions.add(this.genreListSearchCriterion);
		
		allCriterions.addAll(getComponentSearchCriterions());
		
		
		
		String cql = SearchCriterionBase.scListToCql(allCriterions, true);
		logger.info(cql);
		
		String query = SearchCriterionBase.scListToQueryString(allCriterions);
		logger.info(query);
		
		
		List<SearchCriterionBase> scList = SearchCriterionBase.queryStringToScList(query);
		logger.info(scList);
		
		
		 try {
			getExternalContext().redirect("SearchResultListPage.jsp?cql="+URLEncoder.encode(cql, "UTF-8")+"&q="+URLEncoder.encode(query, "UTF-8")+"&"+SearchRetrieverRequestBean.parameterSearchType+"=advanced");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error while redirecting to search result page", e);
		}
		
	
		return "";
	}
	
	
	public List<SearchCriterionBase> getComponentSearchCriterions()
	{
		List<SearchCriterionBase> returnList = new ArrayList<SearchCriterionBase>();
		returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		returnList.add(fileAvailableSearchCriterion);
		returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		returnList.add(locatorAvailableSearchCriterion);
		returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		returnList.add(embargoDateAvailableSearchCriterion);
		returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		returnList.add(componentContentCategory);
		returnList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
		returnList.add(componentVisibilitySearchCriterion);
		
		return returnList;

	}
	
	
	
	


	public static Logger getLogger() {
		return logger;
	}


	public static void setLogger(Logger logger) {
		AdvancedSearchBean.logger = logger;
	}





	public void setContextListMenu(List<SelectItem> contextListMenu) {
		this.contextListMenu = contextListMenu;
	}


	


	public SearchCriterionBase getFileAvailableSearchCriterion() {
		return fileAvailableSearchCriterion;
	}


	public void setFileAvailableSearchCriterion(
			SearchCriterionBase fileAvailableSearchCriterion) {
		this.fileAvailableSearchCriterion = fileAvailableSearchCriterion;
	}


	public SearchCriterionBase getLocatorAvailableSearchCriterion() {
		return locatorAvailableSearchCriterion;
	}


	public void setLocatorAvailableSearchCriterion(
			SearchCriterionBase locatorAvailableSearchCriterion) {
		this.locatorAvailableSearchCriterion = locatorAvailableSearchCriterion;
	}


	public SearchCriterionBase getEmbargoDateAvailableSearchCriterion() {
		return embargoDateAvailableSearchCriterion;
	}


	public void setEmbargoDateAvailableSearchCriterion(
			SearchCriterionBase embargoDateAvailableSearchCriterion) {
		this.embargoDateAvailableSearchCriterion = embargoDateAvailableSearchCriterion;
	}


	public SearchCriterionBase getComponentContentCategory() {
		return componentContentCategory;
	}


	public void setComponentContentCategory(SearchCriterionBase componentContentCategory) {
		this.componentContentCategory = componentContentCategory;
	}


	public List<SelectItem> getContentCategoryListMenu() {
		return contentCategoryListMenu;
	}


	public void setContentCategoryListMenu(List<SelectItem> contentCategoryListMenu) {
		this.contentCategoryListMenu = contentCategoryListMenu;
	}


	public SearchCriterionBase getComponentVisibilitySearchCriterion() {
		return componentVisibilitySearchCriterion;
	}


	public void setComponentVisibilitySearchCriterion(
			SearchCriterionBase componentVisibilitySearchCriterion) {
		this.componentVisibilitySearchCriterion = componentVisibilitySearchCriterion;
	}


	public List<SelectItem> getComponentVisibilityListMenu() {
		return componentVisibilityListMenu;
	}


	public void setComponentVisibilityListMenu(
			List<SelectItem> componentVisibilityListMenu) {
		this.componentVisibilityListMenu = componentVisibilityListMenu;
	}


	public List<SelectItem> getSubjectTypesListMenu() {
		return subjectTypesListMenu;
	}


	public void setSubjectTypesListMenu(List<SelectItem> subjectTypesListMenu) {
		this.subjectTypesListMenu = subjectTypesListMenu;
	}









	public Parenthesis getCurrentlyOpenedParenthesis() {
		return currentlyOpenedParenthesis;
	}





	public void setCurrentlyOpenedParenthesis(Parenthesis currentlyOpenedParenthesis) {
		this.currentlyOpenedParenthesis = currentlyOpenedParenthesis;
	}





	public Map<SearchCriterionBase, Boolean> getPossibleCriterionsForClosingParenthesisMap() {
		return possibleCriterionsForClosingParenthesisMap;
	}





	public void setPossibleCriterionsForClosingParenthesisMap(
			Map<SearchCriterionBase, Boolean> possibleCriterionsForClosingParenthesisMap) {
		this.possibleCriterionsForClosingParenthesisMap = possibleCriterionsForClosingParenthesisMap;
	}





	public Map<SearchCriterionBase, Integer> getBalanceMap() {
		return balanceMap;
	}





	public void setBalanceMap(Map<SearchCriterionBase, Integer> balanceMap) {
		this.balanceMap = balanceMap;
	}





	public SearchCriterionBase getGenreListSearchCriterion() {
		return genreListSearchCriterion;
	}





	public void setGenreListSearchCriterion(SearchCriterionBase genreListSearchCriterion) {
		this.genreListSearchCriterion = genreListSearchCriterion;
	}


	public List<SelectItem> getGenreListMenu() {
		return genreListMenu;
	}


	public void setGenreListMenu(List<SelectItem> genreListMenu) {
		this.genreListMenu = genreListMenu;
	}











}
