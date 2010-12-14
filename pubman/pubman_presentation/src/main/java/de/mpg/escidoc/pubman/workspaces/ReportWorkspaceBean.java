/**
 * 
 */
package de.mpg.escidoc.pubman.workspaces;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.AffiliationVOPresentation;
import de.mpg.escidoc.pubman.util.OrganizationVOPresentation;
import de.mpg.escidoc.services.citationmanager.CitationStyleHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.ItemContainerSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;
import de.mpg.escidoc.services.transformation.Configurable;
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
	List<String> childAffilList;

	public ReportWorkspaceBean() {
		InitialContext initialContext;
		try {
			initialContext = new InitialContext();
			this.searchService = (Search) initialContext.lookup(Search.SERVICE_NAME);
			this.transformer = new TransformationBean();
			this.xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
			this.citationStyleHandler = (CitationStyleHandler) initialContext.lookup(CitationStyleHandler.SERVICE_NAME);
			this.configuration = new HashMap<String, String>();
			this.childAffilList = new ArrayList<String>();
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
			error(getMessage("ReportOrgIdNotProvided"));
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
				}
				if (itemListCS != null){
					itemListReportTransformed = doReportTransformation(itemListCS);
					logger.info("Transformed result: \n" + new String(itemListReportTransformed));
				}
				if (itemListReportTransformed != null){
					HttpServletResponse resp = (HttpServletResponse) this.getExternalContext().getResponse();
					resp.setContentType("text/xml; charset=UTF-8");
					
					resp.addHeader("Content-Disposition", "attachment; filename=" +"report.xml");
					
					ServletOutputStream stream = resp.getOutputStream();
					ByteArrayInputStream bais = new ByteArrayInputStream(itemListReportTransformed);
					BufferedInputStream buff = new BufferedInputStream(bais);
					
					int readBytes = 0;
					while((readBytes = buff.read()) != -1){
						stream.write(readBytes);
					}
					stream.close();
					
					FacesContext faces = FacesContext.getCurrentInstance();
					faces.responseComplete();
				} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		}
		
	}
	    
		
	private String doSearchItems() {
		String itemListAsString = null;
		int totalNrOfSerchResultItems = 0;
		// create an initial query with the given reportYear and the org id
		String query = "escidoc.publication.compound.dates" + " = " + this.reportYear + " AND " +
				"(" + "escidoc.publication.creator.person.organization.identifier" + " = " + this.organization.getIdentifier();
		
		try {
			// get a list of children of the given org
			this.childAffilList = getChildOUs(this.organization.getIdentifier());
		} catch (Exception e) {
			logger.error("Error when trying to get the children of the given organization.",e);
			e.printStackTrace();
		}
		// when there are children, concat the org ids to the query 
		if (this.childAffilList.size() > 0) {
			for (String child : this.childAffilList) {
				query = query + " OR " + "escidoc.publication.creator.person.organization.identifier" + " = " + child;
			}
		} 
		// close the brackets of the query
		query = query + ")";
	
		PlainCqlQuery cqlQuery = new PlainCqlQuery(query);
		ItemContainerSearchResult result;
		try {
			result = this.searchService.searchForItemContainer(cqlQuery);
			totalNrOfSerchResultItems = Integer.parseInt(result.getTotalNumberOfResults().toString());
			logger.info("Search result total nr: " + Integer.parseInt(result.getTotalNumberOfResults().toString()));
			if (totalNrOfSerchResultItems > 0){
				itemListAsString = xmlTransforming.transformToItemList(result.extractItemsOfSearchResult());
			} else {
				info(getMessage("ReportNoItemsFound"));
			}
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
		String childConfig = "";
		byte[] result = null;
		Format source = new Format(exportFormat, "application/xml", "UTF-8");
		Format target = new Format(outputFormat, "application/xml", "UTF-8");
		// set the config for the transformation, the institut's name is used for CoNE
		if (this.childAffilList.size() > 0 ){
			for (String childId: this.childAffilList){
				childConfig += childId + " ";
			}
			logger.info("CHILD Config " + childConfig);
			configuration.put("institutsId", childConfig);
		} else {
			configuration.put("institutsId", this.organization.getIdentifier());
		}
		try {
			this.transformer.getConfiguration(source, target);
			result = this.transformer.transform(src, source, target, "escidoc", configuration);
		} catch (TransformationNotSupportedException e) {
			logger.error("This transformation is not supported.", e);
			error("This transformation is not supported.");
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
	
	public List<String> getChildOUs(String orgId) throws Exception {
		List<String> affListAsString = new ArrayList<String>();
		OrganizationalUnitHandler ouHandler = ServiceLocator.getOrganizationalUnitHandler();
		String topLevelOU = ouHandler.retrieve(orgId);
		AffiliationVO affVO = xmlTransforming.transformToAffiliation(topLevelOU);
		
		AffiliationVOPresentation aff = new AffiliationVOPresentation(affVO);
		List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();
		if (aff.getHasChildren()){
			affList.addAll(aff.getChildren());
			for (AffiliationVOPresentation a : affList) {
				String childId = a.getIdPath();
				childId = childId.substring(0, childId.indexOf(" "));
				affListAsString.add(childId);
			}
		}
		return affListAsString;
	}
	
	
	

}
