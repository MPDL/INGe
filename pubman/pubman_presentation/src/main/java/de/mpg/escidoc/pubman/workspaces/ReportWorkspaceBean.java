/**
 * 
 */
package de.mpg.escidoc.pubman.workspaces;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import net.sf.jasperreports.engine.JRException;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.OrganizationVOPresentation;
import de.mpg.escidoc.pubman.util.PubItemResultVO;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.pubman.yearbook.YearbookCandidatesSessionBean;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.citationmanager.CitationStyleManager;
import de.mpg.escidoc.services.citationmanager.CitationStyleManagerException;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.CommonUtils;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.interfaces.SearchResultElement;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ExportSearchResult;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.transformation.Configurable;
import de.mpg.escidoc.services.transformation.Transformation;
import de.mpg.escidoc.services.transformation.TransformationBean;
import de.mpg.escidoc.services.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.escidoc.services.transformation.valueObjects.Format;

/**
 * @author Gergana Stoyanova
 *
 */
public class ReportWorkspaceBean extends FacesBean {

	private static Logger logger = Logger.getLogger(ReportWorkspaceBean.class);

	private OrganizationVOPresentation organization = new OrganizationVOPresentation();
	private String reportYear;
	// Search Service
	private Search searchService = null;
	// Transformation Service
	private Configurable transformer = null;
	// XML TransformingService
	private XmlTransforming xmlTransforming = null;
	// Citation Style Handler
	private CitationStyleHandler citationStyleHandler;

	String cqlQuery = null;
	String exportFormat = "jus_in";
	String outputFormat = "jus_out";

	String csExportFormat = "JUS_Report";
	String csOutputFormat = "escidoc_snippet";
	String index = "escidoc_all";

	private Map<String, String> configuration = null;

	public ReportWorkspaceBean() {
		InitialContext initialContext;
		try {
			initialContext = new InitialContext();
			ApplicationBean appBean = (ApplicationBean) getApplicationBean(ApplicationBean.class);
			this.searchService = (Search) initialContext.lookup(Search.SERVICE_NAME);
			this.transformer = new TransformationBean();
			this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
			this.citationStyleHandler = (CitationStyleHandler) initialContext.lookup(CitationStyleHandler.SERVICE_NAME);
			this.configuration = new HashMap<String, String>();
		} catch (NamingException e) {
			throw new RuntimeException("Search service not initialized", e);
		}

	}

	public OrganizationVOPresentation getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationVOPresentation organization) {
		this.organization = organization;
	}

	public String getReportYear() {
		return reportYear;
	}

	public void setReportYear(String reportYear) {
		this.reportYear = reportYear;
	}
	  
	public String generateReport(){
		String itemLsitSearchResult = null;
		byte[] itemListCS  = null;
		byte[] itemListReportTransformed = null;
		if ("".equals(this.organization.getIdentifier()) || this.organization.getIdentifier() == null){
			error(getMessage("OrgIdNotProvided"));
			return null;
		}else if ("".equals(this.getReportYear()) || this.getReportYear() == null){
			error(getMessage("ReportYearNotProvided"));
			return null;
		} else {
			try {
				logger.info("Start generation report for YEAR " + this.reportYear + ", ORG " + this.organization.getIdentifier());
				itemLsitSearchResult = doSearchItems();
				if (itemLsitSearchResult != null){
					itemListCS = doCitationStyle(itemLsitSearchResult);
					//logger.info("itemListCS " + new String(itemListCS));
				}
				if (itemListCS != null){
				itemListReportTransformed = doReportTransformation(itemListCS);
				//logger.info("Transformed result: " + new String(itemListReportTransformed));
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		}
	}
	    
		
	public String doSearchItems() {
		String itemListAsString = null;
		// create an initial query with the given reportYear and the org id
		String query = "escidoc.publication.compound.dates" + " = " + this.reportYear + " AND " +
				"(" + "escidoc.publication.creator.person.organization.identifier" + " = " + this.organization.getIdentifier();
		
		List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();
		try {
			// get a list of children of the given org
			affList = getChildOUs(this.organization.getIdentifier());
		} catch (Exception e) {
			logger.error("Error when trying to get the children of the given organization.",e);
			e.printStackTrace();
		}
		// when there are children, concat the org ids to the query 
		if (affList.size() > 0) {
			for (AffiliationVOPresentation a : affList) {
				String childId = a.getIdPath();
				childId = childId.substring(0, childId.indexOf(" "));
				query = query + " OR " + "escidoc.publication.creator.person.organization.identifier" + " = " + childId;
			}
		} 
		// close the brackets of the query
		query = query + ")";
	
		PlainCqlQuery cqlQuery = new PlainCqlQuery(query);
		ItemContainerSearchResult result;
		try {
			result = this.searchService.searchForItemContainer(cqlQuery);
			logger.info("Search result total nr: " + Integer.parseInt(result.getTotalNumberOfResults().toString()));
			itemListAsString = xmlTransforming.transformToItemList(result.extractItemsOfSearchResult());
		} 
		catch (Exception e) {
			logger.error("Error when trying to find search service.", e);
			error("Did not find Search service");
		}
		return itemListAsString;
	}

	private byte[] doCitationStyle(String itemListAsString) {
		byte[] exportData = null;
		try {
			exportData = citationStyleHandler.getOutput(csExportFormat, csOutputFormat, itemListAsString);
		} catch (Exception e) {
			logger.error("Error when trying to find citation service.", e);
			error("Did not found Citation service");
		}
		return exportData;
	}
		
	private byte[] doReportTransformation(byte[] src) {
		byte[] result = null;
		Format source = new Format(exportFormat, "application/xml", "UTF-8");
		Format target = new Format(outputFormat, "application/xml", "UTF-8");
		// set the config for the transformation, the institut's name is used for CoNE
		configuration.put("institutsName", this.organization.getName().toString());
		try {
			result = this.transformer.transform(src, source, target, "escidoc", configuration);
		} catch (TransformationNotSupportedException e) {
			logger.error("This transformation is not supported.", e);
			error("This transformation is not supported.");
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return result;

	}
	
	public List<AffiliationVOPresentation> getChildOUs(String orgId) throws Exception {
		OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();
		String topLevelOU = ouHandler.retrieve(orgId);
		AffiliationVO affVO = xmlTransforming.transformToAffiliation(topLevelOU);
		AffiliationVOPresentation aff = new AffiliationVOPresentation(affVO);
		List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();
		affList.addAll(aff.getChildren());
		return affList;
	}


}
