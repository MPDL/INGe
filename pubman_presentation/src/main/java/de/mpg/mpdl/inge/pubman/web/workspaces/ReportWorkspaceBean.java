package de.mpg.mpdl.inge.pubman.web.workspaces;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.FileFormatVO.FILE_FORMAT;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase;
import de.mpg.mpdl.inge.pubman.web.search.criterions.SearchCriterionBase.SearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.dates.DateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.GenreSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.enums.StateSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.LogicalOperator;
import de.mpg.mpdl.inge.pubman.web.search.criterions.operators.Parenthesis;
import de.mpg.mpdl.inge.pubman.web.search.criterions.stringOrHiddenId.OrganizationSearchCriterion;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.beans.ApplicationBean;
import de.mpg.mpdl.inge.pubman.web.util.vos.OrganizationVOPresentation;
import de.mpg.mpdl.inge.service.pubman.ItemTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.ItemTransformingServiceImpl;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.TransformerFactory.CitationTypes;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.util.ConeUtils;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.model.SelectItem;
import jakarta.servlet.ServletOutputStream;

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


  private Map<String, String> configuration = new HashMap<String, String>();
  private List<String> allOUs = new ArrayList<String>();

  private List<SelectItem> outputFormats = new ArrayList<SelectItem>();
  private TransformerFactory.FORMAT format;

  private ItemTransformingService itemTransformingService = new ItemTransformingServiceImpl();

  public ReportWorkspaceBean() {
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

    byte[] itemListCS = null;
    byte[] itemListReportTransformed = null;

    if ("".equals(this.organization.getIdentifier()) || this.organization.getIdentifier() == null) {
      this.error(this.getMessage("ReportOrgIdNotProvided"));
      return;
    }

    if ("".equals(this.reportYear) || this.reportYear == null) {
      this.error(this.getMessage("ReportYearNotProvided"));
      return;
    }

    try {
      Integer.parseInt(this.reportYear);
    } catch (Exception e) {
      this.error(this.getMessage("ReportYearNotProvided"));
      return;
    }

    try {
      logger.info("Start generation report for YEAR " + this.reportYear + ", ORG " + this.organization.getIdentifier() + ", FORMAT "
          + this.format + " " + this.format.name());

      SearchRetrieveResponseVO<ItemVersionVO> itemListSearchResult = this.doSearchItems();

      if (itemListSearchResult != null) {
        itemListCS = this.doCitationStyle(itemListSearchResult);
      }

      if (itemListCS != null) {
        itemListReportTransformed = this.doReportTransformation(itemListCS);
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
      this.error(this.getMessage("File_errorGenerate"));
    }
  }

  private SearchRetrieveResponseVO<ItemVersionVO> doSearchItems() {
    int totalNrOfSerchResultItems = 0;

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
    StateSearchCriterion ssc = new StateSearchCriterion(); //anonyme Suche -> Nur RELEASED und WITHDRAWN Stati zurückgegeben (s.u.)
    ssc.setSelectedEnum(ItemVO.State.RELEASED); // nur RELEASED ausgeben
    scList.add(ssc);
    scList.add(new LogicalOperator(SearchCriterion.AND_OPERATOR));
    scList.add(new Parenthesis(SearchCriterion.OPENING_PARENTHESIS));

    // when there are children, concat the org ids to the query (inclusive parent)
    try {
      this.allOUs = ApplicationBean.INSTANCE.getOrganizationService().getChildIdPath(this.organization.getIdentifier());
      int i = 0;
      for (String childOU : this.allOUs) {
        if (i > 0) {
          scList.add(new LogicalOperator(SearchCriterion.OR_OPERATOR));
        }
        OrganizationSearchCriterion csc = new OrganizationSearchCriterion();
        csc.setHiddenId(childOU);
        csc.setIncludeSource(true);
        scList.add(csc);
        i++;
      }
    } catch (Exception e) {
      logger.error("Error while getting childOUs.", e);
      this.error(this.getMessage("ChildOuError"));
    }

    scList.add(new Parenthesis(SearchCriterion.CLOSING_PARENTHESIS));

    try {
      Query qb = SearchCriterionBase.scListToElasticSearchQuery(scList);
      SearchRetrieveRequestVO srr = new SearchRetrieveRequestVO(qb, -2, 0); // unbegrenzte Suche
      SearchRetrieveResponseVO<ItemVersionVO> resp = ApplicationBean.INSTANCE.getPubItemService().search(srr, null); //anonyme Suche -> Nur RELEASED und WITHDRAWN Stati zurückgegeben

      totalNrOfSerchResultItems = resp.getNumberOfRecords();
      logger.info("Anzahl gefundener Sätze: " + resp.getNumberOfRecords());

      if (totalNrOfSerchResultItems > 0) {
        return resp;
      } else {

        this.info(this.getMessage("ReportNoItemsFound"));
        return null;
      }
    } catch (final Exception e) {
      logger.error("Error when trying to find search service.", e);
      this.error(this.getMessage("NoSearchService"));
    }


    return null;
  }

  private byte[] doCitationStyle(SearchRetrieveResponseVO<ItemVersionVO> searchResult) {
    byte[] exportData = null;

    try {
      exportData = ApplicationBean.INSTANCE.getItemTransformingService().getOutputForExport(
          new ExportFormatVO(FILE_FORMAT.ESCIDOC_SNIPPET.getName(), CitationTypes.JUS_Report.getCitationName()), searchResult);
    } catch (final Exception e) {
      logger.error("Error when trying to find citation service.", e);
      this.error(this.getMessage("NoCitationService"));
    }

    return exportData;
  }

  private byte[] doReportTransformation(byte[] src) {
    String childConfig = "";
    String result = null;

    // set the config for the transformation
    for (final String childId : this.allOUs) {
      childConfig += childId + " ";
    }
    logger.info("CHILD Config " + childConfig);
    this.configuration.put("institutsId", childConfig);
    this.configuration.put("conePersonsIdIdentifier", ConeUtils.getConePersonsIdIdentifier());

    try {
      // logger.info(new String(src, "UTF-8"));
      result = this.itemTransformingService.transformFromTo(TransformerFactory.FORMAT.JUS_SNIPPET_XML, this.format,
          new String(src, "UTF-8"), this.configuration);
    } catch (final TransformationException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    return result.getBytes();
  }

  public void removeAutoSuggestValues(int position) {
    this.organization = new OrganizationVOPresentation();
  }
}
