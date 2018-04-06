package de.mpg.mpdl.inge.pubman.web.workspaces;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilder;

import de.mpg.mpdl.inge.citationmanager.CitationStyleExecuterService;
import de.mpg.mpdl.inge.citationmanager.utils.XmlHelper;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.GenreSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.vos.OrganizationVOPresentation;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;

/**
 * @author Gergana Stoyanova
 * 
 */
@ManagedBean(name = "ReportWorkspaceBean")
@SuppressWarnings("serial")
public class ReportWorkspaceBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ReportWorkspaceBean.class);

  private OrganizationVOPresentation organization = new OrganizationVOPresentation();
  private String reportYear;

  private final String csExportFormatName = XmlHelper.JUS_REPORT;
  private final String csOutputFormatName = XmlHelper.ESCIDOC_SNIPPET;

  private Map<String, String> configuration = null;
  List<String> childAffilList;

  private List<SelectItem> outputFormats = new ArrayList<SelectItem>();
  private TransformerFactory.FORMAT format;

  private ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

  public ReportWorkspaceBean() {
    this.configuration = new HashMap<String, String>();
    this.childAffilList = new ArrayList<String>();

    final TransformerFactory.FORMAT[] targetFormats =
        itemTransformingService.getAllTargetFormatsFor(TransformerFactory.FORMAT.JUS_SNIPPET_XML);

    for (TransformerFactory.FORMAT f : targetFormats) {
      if (!TransformerFactory.FORMAT.JUS_SNIPPET_XML.equals(f)) {
        this.outputFormats.add(new SelectItem(f, getLabel(f.getName())));
      }
    }
  }

  public OrganizationVOPresentation getOrganization() {
    return this.organization;
  }

  public void setOrganization(OrganizationVOPresentation organization) {
    this.organization = organization;
  }

  public String getReportYear() {
    return this.reportYear;
  }

  public void setReportYear(String reportYear) {
    this.reportYear = reportYear;
  }

  public TransformerFactory.FORMAT getFormat() {
    return this.format;
  }

  public void setFormat(TransformerFactory.FORMAT format) {
    this.format = format;
  }

  public List<SelectItem> getOutputFormats() {
    return this.outputFormats;
  }

  public void setOutputFormats(List<SelectItem> outputFormats) {
    this.outputFormats = outputFormats;
  }

  public void generateReport() {
    String itemListSearchResult = null;
    byte[] itemListCS = null;
    byte[] itemListReportTransformed = null;

    if ("".equals(this.organization.getIdentifier()) || this.organization.getIdentifier() == null) {
      this.error(this.getMessage("ReportOrgIdNotProvided"));
      return;
    }
    if ("".equals(this.getReportYear()) || this.getReportYear() == null) {
      this.error(this.getMessage("ReportYearNotProvided"));
      return;
    }

    try {
      logger.info("Start generation report for YEAR " + this.reportYear + ", ORG " + this.organization.getIdentifier() + ", FORMAT "
          + this.format + " " + this.format.name());

      itemListSearchResult = this.doSearchItems();

      if (itemListSearchResult != null) {
        itemListCS = this.doCitationStyle(itemListSearchResult);
      }

      if (itemListCS != null) {
        itemListReportTransformed = this.doReportTransformation(itemListCS);
        logger.info("Transformed result: \n" + new String(itemListReportTransformed));
      }

      if (itemListReportTransformed != null) {
        FacesTools.getResponse().setContentType("text/html; charset=UTF-8");

        final String fileName = format.name().contains("HTML") ? "Jus_Report.html" : "Jus_Report_InDesign.xml";
        FacesTools.getResponse().addHeader("Content-Disposition", "attachment; filename=" + fileName);

        final ServletOutputStream stream = FacesTools.getResponse().getOutputStream();
        final ByteArrayInputStream bais = new ByteArrayInputStream(itemListReportTransformed);
        final BufferedInputStream buff = new BufferedInputStream(bais);

        int readBytes = 0;
        while ((readBytes = buff.read()) != -1) {
          stream.write(readBytes);
        }
        stream.close();

        FacesTools.getCurrentInstance().responseComplete();
      }
    } catch (final Exception e) {
      logger.error("Error while generating report output file.", e);
      this.error("Error while generating output file.");
    }
  }

  private String doSearchItems() {
    String itemListAsString = null;
    int totalNrOfSerchResultItems = 0;
    // create an initial query with the given reportYear and the org id

    List<SearchCriterionBase> scList = new ArrayList<>();
    scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    DateSearchCriterion dsc1 = new DateSearchCriterion(SearchCriterion.ANYDATE);
    dsc1.setFrom(this.reportYear);
    dsc1.setTo(this.reportYear);
    scList.add(dsc1);
    scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
    GenreSearchCriterion gsc = new GenreSearchCriterion();
    gsc.setSelectedEnum(Genre.JOURNAL);
    scList.add(gsc);
    scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
    GenreSearchCriterion gsc2 = new GenreSearchCriterion();
    gsc2.setSelectedEnum(Genre.SERIES);
    scList.add(gsc2);
    scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));
    scList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));
    OrganizationSearchCriterion osc = new OrganizationSearchCriterion();
    osc.setSearchString(this.organization.getName());
    osc.setHiddenId(this.organization.getIdentifier());
    osc.setIncludeSource(true);
    scList.add(osc);
    scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

    /*
     * 
     * String query = "(escidoc.publication.compound.dates=\"" + this.reportYear + "*\" OR " +
     * "escidoc.publication.type=\"http://purl.org/escidoc/metadata/ves/publication-types/journal\" OR "
     * +
     * "escidoc.publication.type=\"http://purl.org/escidoc/metadata/ves/publication-types/series\") AND "
     * +
     * 
     * "(escidoc.publication.creator.person.organization.identifier=\"" +
     * this.organization.getIdentifier() +
     * "\" OR escidoc.publication.source.creator.person.organization.identifier=\"" +
     * this.organization.getIdentifier() + "\" ";
     * 
     * try { // get a list of children of the given org this.childAffilList =
     * this.getChildOUs(this.organization.getIdentifier()); } catch (final Exception e) {
     * logger.error("Error when trying to get the children of the given organization.", e);
     * e.printStackTrace(); }
     * 
     * // when there are children, concat the org ids to the query if (this.childAffilList.size() >
     * 0) { for (final String child : this.childAffilList) { query = query +
     * "OR escidoc.publication.creator.person.organization.identifier=\"" + child +
     * "\" OR escidoc.publication.source.creator.person.organization.identifier=\"" + child + "\"";
     * } }
     * 
     * // close the brackets of the query query = query + ")";
     */
    try {
      QueryBuilder qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb);
      SearchRetrieveResponseVO<ItemVersionVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, null);

      totalNrOfSerchResultItems = resp.getNumberOfRecords();
      logger.info("Search result total nr: " + resp.getNumberOfRecords());

      if (totalNrOfSerchResultItems > 0) {
        itemListAsString = XmlTransformingService.transformToItemList(
            EntityTransformer.transformToOld(resp.getRecords().stream().map(i -> i.getData()).collect(Collectors.toList())));
      } else {
        this.info(this.getMessage("ReportNoItemsFound"));
      }
    } catch (final Exception e) {
      logger.error("Error when trying to find search service.", e);
      this.error("Did not find Search service");
    }

    return itemListAsString;
  }

  private byte[] doCitationStyle(String itemListAsString) {
    byte[] exportData = null;

    try {
      exportData = CitationStyleExecuterService.getOutput(itemListAsString,
          new ExportFormatVO(ExportFormatVO.FormatType.LAYOUT, this.csExportFormatName, this.csOutputFormatName));
    } catch (final Exception e) {
      logger.error("Error when trying to find citation service.", e);
      this.error("Did not find Citation service");
    }

    return exportData;
  }

  private byte[] doReportTransformation(byte[] src) {
    String childConfig = "";
    String result = null;

    // set the config for the transformation, the institut's name is used for CoNE
    if (this.childAffilList.size() > 0) {
      for (final String childId : this.childAffilList) {
        childConfig += childId + " ";
      }
      logger.info("CHILD Config " + childConfig);
      this.configuration.put("institutsId", childConfig);
    } else {
      this.configuration.put("institutsId", this.organization.getIdentifier());
    }

    try {
      result = this.itemTransformingService.transformFromTo(TransformerFactory.FORMAT.JUS_SNIPPET_XML, this.format,
          new String(src, "UTF-8"), this.configuration);
    } catch (final TransformationException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    return result.getBytes();
  }

  public List<String> getChildOUs(String orgId) throws Exception {
    final List<String> affListAsString = new ArrayList<String>();
    final AffiliationDbVO affVO = ApplicationBean.INSTANCE.getOrganizationService().get(orgId, null);
    final AffiliationVOPresentation aff = new AffiliationVOPresentation(affVO);
    final List<AffiliationVOPresentation> affList = new ArrayList<AffiliationVOPresentation>();

    if (aff.getHasChildren()) {
      affList.addAll(aff.getChildren());
      for (final AffiliationVOPresentation a : affList) {
        String childId = a.getIdPath();
        childId = childId.substring(0, childId.indexOf(" "));
        affListAsString.add(childId);
      }
    }

    return affListAsString;
  }
}
