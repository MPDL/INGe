package de.mpg.mpdl.inge.dataacquisition;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.mpdl.inge.dataacquisition.explainSources.FTFetchSettingType;
import de.mpg.mpdl.inge.dataacquisition.explainSources.FTFetchSettingsType;
import de.mpg.mpdl.inge.dataacquisition.explainSources.ImportSourceType;
import de.mpg.mpdl.inge.dataacquisition.explainSources.ImportSourcesDocument;
import de.mpg.mpdl.inge.dataacquisition.explainSources.ImportSourcesType;
import de.mpg.mpdl.inge.dataacquisition.explainSources.MDFetchSettingType;
import de.mpg.mpdl.inge.dataacquisition.explainSources.MDFetchSettingsType;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.FullTextVO;
import de.mpg.mpdl.inge.dataacquisition.valueobjects.MetadataVO;
import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * This class handles the import function from external sources.
 *
 * @author kleinfe1
 * @author $Author$ (last modification)
 */
public class DataSourceHandlerService {
  private static final Logger logger = LogManager.getLogger(DataSourceHandlerService.class);

  public static final String PUBLISHED = "PUBLISHED";

  private InputStream sourceInputStream = null;
  private ImportSourcesDocument importSourcesDocument = null;
  private ImportSourcesType sourceType = null;
  private String sourceXmlPath = null;

  /**
   * Public constructor for DataSourceHandlerBean class.
   */
  public DataSourceHandlerService() {
    this.sourceXmlPath = PropertyReader.getProperty(PropertyReader.INGE_IMPORT_SOURCES_XML);
    DataSourceHandlerService.logger.info("SourcesXml-Property: " + this.sourceXmlPath);
  }

  /**
   * Returns all available Sources.
   *
   * @return List of DataSourceVO
   * @throws RuntimeException
   */
  public List<DataSourceVO> getSources(String transformationFormat, String sourceStatus) throws RuntimeException {
    List<DataSourceVO> sources = new ArrayList<>();

    try {
      ImportSourceType[] sourceTypes = getImportSourceTypes();

      for (ImportSourceType sourceType : sourceTypes) {
        List<FullTextVO> fulltextVec = new ArrayList<>();
        List<MetadataVO> mdVec = new ArrayList<>();

        String status = simpleLiteralTostring(sourceType.getStatus());
        if (status.equalsIgnoreCase(sourceStatus)) {
          DataSourceVO sourceVO = new DataSourceVO();
          sourceVO.setName(sourceType.getName());
          sourceVO.setDescription(simpleLiteralTostring(sourceType.getDescription()));
          sourceVO.setUrl(new URL(simpleLiteralTostring(sourceType.getIdentifier())));
          sourceVO.setType(simpleLiteralTostring(sourceType.getFormatArray(0)));
          sourceVO.setEncoding(simpleLiteralTostring(sourceType.getFormatArray(1)));
          sourceVO.setHarvestProtocol(simpleLiteralTostring(sourceType.getHarvestProtocol()));
          sourceVO.setTimeout(Integer.parseInt(sourceType.getTimeout().toString()));
          sourceVO.setStatus(simpleLiteralTostring(sourceType.getStatus()));

          // Accepted identifier Prefixes
          SimpleLiteral[] idPrefArr = sourceType.getSourceIdentifierArray();
          List<String> idPrefVec = new ArrayList<>();
          for (SimpleLiteral literal : idPrefArr) {
            String idPref = simpleLiteralTostring(literal);
            idPrefVec.add(idPref);
          }
          sourceVO.setIdentifier(idPrefVec);

          // Identifier Examples
          SimpleLiteral[] idExArr = sourceType.getSourceIdentifierExampleArray();
          List<String> idExVec = new ArrayList<>();
          for (SimpleLiteral simpleLiteral : idExArr) {
            String idEx = simpleLiteralTostring(simpleLiteral);
            idExVec.add(idEx);
          }
          sourceVO.setIdentifierExample(idExVec);
          sourceVO.setLicense(sourceType.getLicense());
          sourceVO.setCopyright(sourceType.getCopyright());
          if (sourceType.getItemUrl() != null) {
            sourceVO.setItemUrl(new URL(simpleLiteralTostring(sourceType.getItemUrl())));
          }

          // Metadata parameters
          MDFetchSettingsType mdfs = sourceType.getMDFetchSettings();
          MDFetchSettingType[] mdfArray = mdfs.getMDFetchSettingArray();
          for (MDFetchSettingType mdf : mdfArray) {
            MetadataVO mdVO = new MetadataVO();
            if (mdf.getDescription() != null) {
              mdVO.setMdDesc(simpleLiteralTostring(mdf.getDescription()));
            }
            mdVO.setMdUrl(new URL(simpleLiteralTostring(mdf.getIdentifier())));
            mdVO.setMdFormat(simpleLiteralTostring(mdf.getFormat()));
            mdVO.setMdDefault(mdf.getDefault());
            mdVO.setMdLabel(simpleLiteralTostring(mdf.getLabel()));
            mdVO.setName(simpleLiteralTostring(mdf.getName()));
            mdVO.setEncoding(simpleLiteralTostring(mdf.getEncoding()));
            mdVec.add(mdVO);
          }
          sourceVO.setMdFormats(mdVec);

          // Fulltext parameters
          FTFetchSettingsType ftfs = sourceType.getFTFetchSettings();
          FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();
          for (FTFetchSettingType ftf : ftfArray) {
            FullTextVO fulltextVO = new FullTextVO();
            if (ftf.getDescription() != null) {
              fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
            }
            fulltextVO.setFtUrl(new URL(simpleLiteralTostring(ftf.getIdentifier())));
            fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
            fulltextVO.setFtDefault(ftf.getDefault());
            fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
            fulltextVO.setContentCategory(simpleLiteralTostring(ftf.getContentCategorie()));
            fulltextVO.setVisibility(simpleLiteralTostring(ftf.getVisibility()));
            fulltextVO.setName(simpleLiteralTostring(ftf.getName()));
            fulltextVO.setEncoding(simpleLiteralTostring(ftf.getEncoding()));
            fulltextVec.add(fulltextVO);
          }
          sourceVO.setFtFormats(fulltextVec);

          // Check if a transformation for the default MD format is possible
          if (transformationFormat != null) {
            for (int x = 0; x < sourceVO.getMdFormats().size(); x++) {
              MetadataVO md = sourceVO.getMdFormats().get(x);
              if (md.isMdDefault()) {
                if (md.isMdDefault()) {
                  if (Util.checkXsltTransformation(md.getName(), transformationFormat)
                      || (transformationFormat.equalsIgnoreCase(md.getName()))) {
                    sources.add(sourceVO);
                  }
                }
              }
            }
          } else {
            sources.add(sourceVO);
          }
        }
      }
    } catch (MalformedURLException e) {
      DataSourceHandlerService.logger.error("Processing the source URL caused an error", e);
      throw new RuntimeException(e);
    } catch (Exception e) {
      DataSourceHandlerService.logger.error("Parsing sources.xml caused an error", e);
      throw new RuntimeException(e);
    }

    return sources;
  }

  private InputStream getSourceInputStream() {
    if (this.sourceInputStream == null) {
      ClassLoader cl = this.getClass().getClassLoader();
      this.sourceInputStream = cl.getResourceAsStream(this.sourceXmlPath);
    }

    return this.sourceInputStream;
  }

  private ImportSourcesDocument getImportSourcesDocument() {
    if (this.importSourcesDocument == null) {
      try {
        this.importSourcesDocument = ImportSourcesDocument.Factory.parse(getSourceInputStream());
      } catch (Exception e) {
        DataSourceHandlerService.logger.error("Parsing sources.xml caused an error", e);
        throw new RuntimeException(e);
      }
    }

    return this.importSourcesDocument;
  }

  private ImportSourceType[] getImportSourceTypes() {
    if (this.sourceType == null) {
      this.sourceType = getImportSourcesDocument().getImportSources();
    }

    return this.sourceType.getImportSourceArray();
  }


  /**
   * Returns a specific source.
   *
   * @param name
   * @return corresponding source
   * @throws RuntimeException
   */
  public DataSourceVO getSourceByName(String name) throws RuntimeException {
    DataSourceVO sourceVO = new DataSourceVO();
    List<FullTextVO> fulltextVec = new ArrayList<>();
    List<MetadataVO> mdVec = new ArrayList<>();
    boolean found = false;

    try {
      ImportSourceType[] sourceTypes = getImportSourceTypes();

      for (ImportSourceType sourceType : sourceTypes) {
        if (!sourceType.getName().equalsIgnoreCase(name)) {
          continue;
        } else {
          found = true;
        }
        sourceVO.setName(sourceType.getName());
        sourceVO.setDescription(simpleLiteralTostring(sourceType.getDescription()));
        sourceVO.setUrl(new URL(simpleLiteralTostring(sourceType.getIdentifier())));
        sourceVO.setType(simpleLiteralTostring(sourceType.getFormatArray(0)));
        sourceVO.setEncoding(simpleLiteralTostring(sourceType.getFormatArray(1)));
        sourceVO.setHarvestProtocol(simpleLiteralTostring(sourceType.getHarvestProtocol()));
        sourceVO.setTimeout(Integer.parseInt(sourceType.getTimeout().toString()));
        sourceVO.setNumberOfTries(Integer.parseInt(sourceType.getNumberOfTries().toString()));
        sourceVO.setStatus(simpleLiteralTostring(sourceType.getStatus()));

        // Accepted identifier Prefixes
        SimpleLiteral[] idPrefArr = sourceType.getSourceIdentifierArray();
        List<String> idPrefVec = new ArrayList<>();
        for (SimpleLiteral literal : idPrefArr) {
          String idPref = simpleLiteralTostring(literal);
          idPrefVec.add(idPref);
        }
        sourceVO.setIdentifier(idPrefVec);

        // Identifier Examples
        SimpleLiteral[] idExArr = sourceType.getSourceIdentifierExampleArray();
        List<String> idExVec = new ArrayList<>();
        for (SimpleLiteral simpleLiteral : idExArr) {
          String idEx = simpleLiteralTostring(simpleLiteral);
          idExVec.add(idEx);
        }
        sourceVO.setIdentifierExample(idExVec);
        sourceVO.setLicense(sourceType.getLicense());
        sourceVO.setCopyright(sourceType.getCopyright());

        if (sourceType.getItemUrl() != null) {
          sourceVO.setItemUrl(new URL(simpleLiteralTostring(sourceType.getItemUrl())));
        }

        // Metadata parameters
        MDFetchSettingsType mdfs = sourceType.getMDFetchSettings();
        MDFetchSettingType[] mdfArray = mdfs.getMDFetchSettingArray();
        for (MDFetchSettingType mdf : mdfArray) {
          MetadataVO mdVO = new MetadataVO();
          if (mdf.getDescription() != null) {
            mdVO.setMdDesc(simpleLiteralTostring(mdf.getDescription()));
          }
          mdVO.setMdUrl(new URL(simpleLiteralTostring(mdf.getIdentifier())));
          mdVO.setMdFormat(simpleLiteralTostring(mdf.getFormat()));
          mdVO.setMdDefault(mdf.getDefault());
          mdVO.setMdLabel(simpleLiteralTostring(mdf.getLabel()));
          mdVO.setName(simpleLiteralTostring(mdf.getName()));
          mdVO.setEncoding(simpleLiteralTostring(mdf.getEncoding()));
          mdVec.add(mdVO);
        }
        sourceVO.setMdFormats(mdVec);

        // Fulltext parameters
        FTFetchSettingsType ftfs = sourceType.getFTFetchSettings();
        FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();
        for (FTFetchSettingType ftf : ftfArray) {
          FullTextVO fulltextVO = new FullTextVO();
          if (ftf.getDescription() != null) {
            fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
          }
          fulltextVO.setFtUrl(new URL(simpleLiteralTostring(ftf.getIdentifier())));
          fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
          fulltextVO.setFtDefault(ftf.getDefault());
          fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
          fulltextVO.setContentCategory(simpleLiteralTostring(ftf.getContentCategorie()));
          fulltextVO.setVisibility(simpleLiteralTostring(ftf.getVisibility()));
          fulltextVO.setName(simpleLiteralTostring(ftf.getName()));
          fulltextVO.setEncoding(simpleLiteralTostring(ftf.getEncoding()));
          fulltextVec.add(fulltextVO);
        }
        sourceVO.setFtFormats(fulltextVec);
      }
    } catch (MalformedURLException e) {
      DataSourceHandlerService.logger.error("Processing the source URL caused an error", e);
      throw new RuntimeException(e);
    } catch (Exception e) {
      DataSourceHandlerService.logger.error("Parsing sources.xml caused an error", e);
      throw new RuntimeException(e);
    }

    if (found) {
      return sourceVO;
    } else {
      return null;
    }
  }

  /**
   * Returns a specific source.
   *
   * @param id
   * @return corresponding source
   * @throws RuntimeException
   */
  public DataSourceVO getSourceByIdentifier(String id) throws RuntimeException {
    ImportSourceType[] sourceTypes = getImportSourceTypes();

    for (ImportSourceType sourceType : sourceTypes) {
      SimpleLiteral[] idPrefVec = sourceType.getSourceIdentifierArray();
      for (SimpleLiteral idPref : idPrefVec) {
        if (!simpleLiteralTostring(idPref).equalsIgnoreCase(id)) {
        } else {
          return this.getSourceByName(sourceType.getName());
        }
      }
    }

    return null;
  }

  /**
   * Returns a source name.
   *
   * @param id
   * @return corresponding source name
   * @throws RuntimeException
   */
  public String getSourceNameByIdentifier(String id) throws RuntimeException {
    ImportSourceType[] sourceTypes = getImportSourceTypes();

    for (ImportSourceType sourceType : sourceTypes) {
      SimpleLiteral[] idPrefVec = sourceType.getSourceIdentifierArray();
      for (SimpleLiteral idPref : idPrefVec) {
        if (!simpleLiteralTostring(idPref).equalsIgnoreCase(id)) {
        } else {
          return sourceType.getName();
        }
      }
    }

    return null;
  }

  //  /**
  //   * This operation returns the metadata informations to fetch. If no format from was specified the
  //   * default metadata informations are fetched
  //   *
  //   * @param dataSourceVO
  //   * @param format the format of which the metadata will be retrieved
  //   * @return metadata informations
  //   */
  //  public MetadataVO getMdObjectfromSource(DataSourceVO dataSourceVO, String format) {
  //    MetadataVO md = null;
  //
  //    for (int i = 0; i < dataSourceVO.getMdFormats().size(); i++) {
  //      md = dataSourceVO.getMdFormats().get(i);
  //      if (md.getName().equalsIgnoreCase(format)) {
  //        return md;
  //      }
  //    }
  //
  //    return null;
  //  }

  /**
   * Returns the default MetadataVO from a source.
   *
   * @param source
   * @return MetadataVO
   */
  public MetadataVO getDefaultMdFormatFromSource(DataSourceVO source) {
    List<MetadataVO> mdv = source.getMdFormats();

    for (int i = 0; i < mdv.size(); i++) {
      MetadataVO mdVO = source.getMdFormats().get(i);
      if (mdVO.isMdDefault()) {
        return mdVO;
      }
    }

    return null;
  }

  /**
   * This operation updates a metadata information set of the importSource.
   *
   * @param source
   * @param md the metadata object which will be updated
   * @return ImportSourceVO with updated metadata informations
   */
  public DataSourceVO updateMdEntry(DataSourceVO source, MetadataVO md) {
    List<MetadataVO> mdv = source.getMdFormats();

    if (md != null) {
      for (int i = 0; i < mdv.size(); i++) {
        MetadataVO mdVO = source.getMdFormats().get(i);
        if (mdVO.getName().equalsIgnoreCase(md.getName())) {
          mdv.set(i, md);
        }
      }
    }

    source.setMdFormats(mdv);

    return source;
  }

  private String simpleLiteralTostring(SimpleLiteral sl) {
    return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
  }
}
