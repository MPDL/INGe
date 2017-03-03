/**
 * 
 */
package de.mpg.mpdl.inge.pubman.web.workspaces;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.escidoc.www.services.oum.OrganizationalUnitHandler;
import de.mpg.mpdl.inge.citationmanager.CitationStyleHandler;
import de.mpg.mpdl.inge.framework.ServiceLocator;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO.FormatType;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransforming;
import de.mpg.mpdl.inge.pubman.web.appbase.FacesBean;
import de.mpg.mpdl.inge.pubman.web.util.AffiliationVOPresentation;
import de.mpg.mpdl.inge.pubman.web.util.OrganizationVOPresentation;
import de.mpg.mpdl.inge.search.SearchService;
import de.mpg.mpdl.inge.search.query.ItemContainerSearchResult;
import de.mpg.mpdl.inge.search.query.PlainCqlQuery;
import de.mpg.mpdl.inge.transformation.Configurable;
import de.mpg.mpdl.inge.transformation.Transformation;
import de.mpg.mpdl.inge.transformation.TransformationBean;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationNotSupportedException;
import de.mpg.mpdl.inge.transformation.valueObjects.Format;

/**
 * @author Gergana Stoyanova
 * 
 */
@SuppressWarnings("serial")
public class ReportWorkspaceBean extends FacesBean {
  private static final Logger logger = Logger.getLogger(ReportWorkspaceBean.class);

  private static final Format JUS_REPORT_SNIPPET_FORMAT = new Format("jus_report_snippet",
      "application/xml", "UTF-8");

  private OrganizationVOPresentation organization = new OrganizationVOPresentation();
  private String reportYear;
  // Transformation Service
  private Configurable transformer = null;
  // XML TransformingService
  private XmlTransforming xmlTransforming = null;
  // Citation Style Handler
  private CitationStyleHandler citationStyleHandler;

  // String cqlQuery = null;
  private String csExportFormat = "JUS_Report";
  private String csOutputFormat = "escidoc_snippet";
  // String index = "escidoc_all";

  private Map<String, String> configuration = null;
  List<String> childAffilList;

  private List<SelectItem> outputFormats = new ArrayList<SelectItem>();
  private Format format;

  private Converter formatConverter = new Converter() {
    public Object getAsObject(FacesContext arg0, javax.faces.component.UIComponent arg1,
        String value) {
      if (value != null && !"".equals(value)) {
        String[] parts = value.split("[\\[\\,\\]]");
        if (parts.length > 3) {
          return new Format(parts[1], parts[2], parts[3]);
        }
      }

      return null;
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object format) {
      if (format instanceof Format) {
        return ((Format) format).toString();
      }

      return null;
    }
  };

  public ReportWorkspaceBean() {
    try {
      InitialContext initialContext = new InitialContext();
      this.transformer = new TransformationBean();
      this.xmlTransforming =
          (XmlTransforming) initialContext
              .lookup("java:global/pubman_ear/common_logic/XmlTransformingBean");
      this.citationStyleHandler =
          (CitationStyleHandler) initialContext
              .lookup("java:global/pubman_ear/citationmanager/CitationStyleHandlerBean");
      this.configuration = new HashMap<String, String>();
      this.childAffilList = new ArrayList<String>();
      Format[] targetFormats =
          ((Transformation) this.transformer).getTargetFormats(JUS_REPORT_SNIPPET_FORMAT);
      for (Format f : targetFormats) {
        if (!JUS_REPORT_SNIPPET_FORMAT.matches(f)) {
          String formatName =
              f.getName() + "_" + ("text/html".equals(f.getType()) ? "html" : "indesign");
          outputFormats.add(new SelectItem(f, getLabel(formatName)));
        }
      }
    } catch (NamingException e) {
      throw new RuntimeException("Search service not initialized", e);
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

  public Format getFormat() {
    return this.format;
  }

  public void setFormat(Format format) {
    this.format = format;
  }

  public List<SelectItem> getOutputFormats() {
    return this.outputFormats;
  }

  public void setOutputFormats(List<SelectItem> outputFormats) {
    this.outputFormats = outputFormats;
  }

  /**
   * @return the formatConverter
   */
  public Converter getFormatConverter() {
    return this.formatConverter;
  }

  /**
   * @param formatConverter the formatConverter to set
   */
  public void setFormatConverter(Converter formatConverter) {
    this.formatConverter = formatConverter;
  }

  public String generateReport() {
    String itemLsitSearchResult = null;
    byte[] itemListCS = null;
    byte[] itemListReportTransformed = null;

    if ("".equals(this.organization.getIdentifier()) || this.organization.getIdentifier() == null) {
      error(getMessage("ReportOrgIdNotProvided"));
      // return null;
    } else if ("".equals(this.getReportYear()) || this.getReportYear() == null) {
      error(getMessage("ReportYearNotProvided"));
      // return null;
    } else {
      try {
        logger.info("Start generation report for YEAR " + this.reportYear + ", ORG "
            + this.organization.getIdentifier() + ", FORMAT " + this.format + " "
            + this.format.getName());

        itemLsitSearchResult = doSearchItems();
        if (itemLsitSearchResult != null) {
          itemListCS = doCitationStyle(itemLsitSearchResult);
        }
        if (itemListCS != null) {
          itemListReportTransformed = doReportTransformation(itemListCS);
          logger.info("Transformed result: \n" + new String(itemListReportTransformed));
        }
        if (itemListReportTransformed != null) {
          HttpServletResponse resp =
              (HttpServletResponse) FacesBean.getExternalContext().getResponse();
          resp.setContentType("text/html; charset=UTF-8");

          String fileName =
              "text/html".equals(this.format.getType()) ? "Jus_Report.html"
                  : "Jus_Report_InDesign.xml";
          resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);

          ServletOutputStream stream = resp.getOutputStream();
          ByteArrayInputStream bais = new ByteArrayInputStream(itemListReportTransformed);
          BufferedInputStream buff = new BufferedInputStream(bais);

          int readBytes = 0;
          while ((readBytes = buff.read()) != -1) {
            stream.write(readBytes);
          }
          stream.close();

          FacesContext faces = FacesContext.getCurrentInstance();
          faces.responseComplete();
        }
      } catch (Exception e) {
        logger.error("Error while generatiring report output file.", e);
        error("Error while generatiring output file.");
      }
      // return null;
    }

    return null;
  }


  private String doSearchItems() {
    String itemListAsString = null;
    int totalNrOfSerchResultItems = 0;
    // create an initial query with the given reportYear and the org id
    String query =
        "(escidoc.publication.compound.dates=\""
            + this.reportYear
            + "*\" OR "
            + "escidoc.publication.type=\"http://purl.org/escidoc/metadata/ves/publication-types/journal\" OR "
            + "escidoc.publication.type=\"http://purl.org/escidoc/metadata/ves/publication-types/series\") AND "
            +

            "(escidoc.publication.creator.person.organization.identifier=\""
            + this.organization.getIdentifier()
            + "\" OR escidoc.publication.source.creator.person.organization.identifier=\""
            + this.organization.getIdentifier() + "\" ";

    try {
      // get a list of children of the given org
      this.childAffilList = getChildOUs(this.organization.getIdentifier());
    } catch (Exception e) {
      logger.error("Error when trying to get the children of the given organization.", e);
      e.printStackTrace();
    }

    // when there are children, concat the org ids to the query
    if (this.childAffilList.size() > 0) {
      for (String child : this.childAffilList) {
        query =
            query + "OR escidoc.publication.creator.person.organization.identifier=\"" + child
                + "\" OR escidoc.publication.source.creator.person.organization.identifier=\""
                + child + "\"";
      }
    }

    // close the brackets of the query
    query = query + ")";

    PlainCqlQuery cqlQuery = new PlainCqlQuery(query);
    ItemContainerSearchResult result;
    try {
      result = SearchService.searchForItemContainer(cqlQuery);
      totalNrOfSerchResultItems = Integer.parseInt(result.getTotalNumberOfResults().toString());
      logger.info("Search result total nr: "
          + Integer.parseInt(result.getTotalNumberOfResults().toString()));
      if (totalNrOfSerchResultItems > 0) {
        itemListAsString = xmlTransforming.transformToItemList(result.extractItemsOfSearchResult());
      } else {
        info(getMessage("ReportNoItemsFound"));
      }
    } catch (Exception e) {
      logger.error("Error when trying to find search service.", e);
      error("Did not find Search service");
    }

    return itemListAsString;
  }

  private byte[] doCitationStyle(String itemListAsString) {
    byte[] exportData = null;
    try {
      exportData =
          citationStyleHandler.getOutput(itemListAsString, new ExportFormatVO(FormatType.LAYOUT,
              csExportFormat, csOutputFormat));
    } catch (Exception e) {
      logger.error("Error when trying to find citation service.", e);
      error("Did not find Citation service");
    }

    return exportData;
  }

  private byte[] doReportTransformation(byte[] src) {
    String childConfig = "";
    byte[] result = null;

    // set the config for the transformation, the institut's name is used for CoNE
    if (this.childAffilList.size() > 0) {
      for (String childId : this.childAffilList) {
        childConfig += childId + " ";
      }
      logger.info("CHILD Config " + childConfig);
      configuration.put("institutsId", childConfig);
    } else {
      configuration.put("institutsId", this.organization.getIdentifier());
    }

    try {
      result =
          this.transformer.transform(src, JUS_REPORT_SNIPPET_FORMAT, this.format, "escidoc",
              configuration);
    } catch (TransformationNotSupportedException e) {
      throw new RuntimeException(e);
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
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

    // if (aff.getChildren()!= null && aff.getChildren().size() > 0){
    if (aff.getHasChildren()) {
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
