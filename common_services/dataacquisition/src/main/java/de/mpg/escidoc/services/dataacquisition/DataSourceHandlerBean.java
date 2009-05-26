package de.mpg.escidoc.services.dataacquisition;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.purl.dc.elements.x11.SimpleLiteral;

import de.mpg.escidoc.metadataprofile.schema.x01.importSource.FTFetchSettingType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.FTFetchSettingsType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourceType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourcesDocument;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.ImportSourcesType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.MDFetchSettingType;
import de.mpg.escidoc.metadataprofile.schema.x01.importSource.MDFetchSettingsType;
import de.mpg.escidoc.services.dataacquisition.valueobjects.DataSourceVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.FullTextVO;
import de.mpg.escidoc.services.dataacquisition.valueobjects.MetadataVO;
import de.mpg.escidoc.services.transformation.transformations.thirdPartyFormats.ThirdPartyTransformation;

/**
 * This class handles the import function from external sources.
 * 
 * @author kleinfe1
 */
public class DataSourceHandlerBean
{
    private ImportSourcesDocument sourceDoc = null;
    private ImportSourcesType sourceType = null;
    private ThirdPartyTransformation thirdPartyTransformer = null;
    private static final Logger LOGGER = Logger.getLogger(DataHandlerBean.class);
    private String transformationFormat = null;
    private final String sourceXmlPath ="resources/sources.xml";

    /**
     * Public constructor for DataSourceHandlerBean class.
     */
    public DataSourceHandlerBean()
    {
    }

    /**
     * Returns all available Sources.
     * 
     * @return Vector of DataSourceVO
     * @throws RuntimeException
     */
    public Vector<DataSourceVO> getSources()throws RuntimeException
    {
        Vector<DataSourceVO> sourceVec = new Vector<DataSourceVO>();
        
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            java.io.InputStream in = cl.getResourceAsStream("resources/sources.xml");
            this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
            //System.out.println(this.sourceDoc);
            this.thirdPartyTransformer = new ThirdPartyTransformation();
            this.sourceType = this.sourceDoc.getImportSources();
            ImportSourceType[] sources = this.sourceType.getImportSourceArray();
            for (int i =0; i< sources.length; i++)
            {
                ImportSourceType source = sources[i];
                Vector<FullTextVO> fulltextVec = new Vector<FullTextVO>();
                Vector<MetadataVO> mdVec = new Vector<MetadataVO>();
                Vector<String> examplesVec = new Vector<String>();

                String status = simpleLiteralTostring(source.getStatus());
                if (status.toLowerCase().trim().equals("published"))
                {   DataSourceVO sourceVO = new DataSourceVO();
                    sourceVO.setName(source.getName());
                    sourceVO.setDescription(simpleLiteralTostring(source.getDescription()));
                    sourceVO.setUrl(new URL(simpleLiteralTostring(source.getIdentifier())));
                    sourceVO.setType(simpleLiteralTostring(source.getFormatArray(0)));
                    sourceVO.setEncoding(simpleLiteralTostring(source.getFormatArray(1)));
                    sourceVO.setHarvestProtocol(simpleLiteralTostring(source.getHarvestProtocol()));
                    sourceVO.setTimeout(Integer.parseInt(source.getTimeout().toString()));
                    sourceVO.setStatus(simpleLiteralTostring(source.getStatus()));
                    sourceVO.setIdentifier(simpleLiteralTostring(source.getSourceIdentifier()));
                    sourceVO.setLicense(source.getLicense());   
                    sourceVO.setCopyright(source.getCopyright());
                    if (source.getItemUrl() != null)
                    {
                        sourceVO.setItemUrl(new URL(simpleLiteralTostring(source.getItemUrl())));
                    }                    
//                    SimpleLiteral[] examples = source.getSourceIdentifierExampleArray();
//                    for (SimpleLiteral example : examples)
//                    {
//                        examplesVec.add(simpleLiteralTostring(example));
//                    }
//                    sourceVO.setIdentifierExample(examplesVec); 
                    //TODO oai identifier
                    // Metadata parameters
                    MDFetchSettingsType mdfs = source.getMDFetchSettings();
                    MDFetchSettingType[] mdfArray = mdfs.getMDFetchSettingArray();
                    for (MDFetchSettingType mdf : mdfArray)
                    {
                        MetadataVO mdVO = new MetadataVO();
                        if (mdf.getDescription() != null)
                        { mdVO.setMdDesc(simpleLiteralTostring(mdf.getDescription())); }
                        mdVO.setMdUrl(new URL(simpleLiteralTostring(mdf.getIdentifier())));
                        mdVO.setMdFormat(simpleLiteralTostring(mdf.getFormat()));
                        mdVO.setMdDefault(mdf.getDefault());
                        mdVO.setMdLabel(simpleLiteralTostring(mdf.getLabel()));
                        mdVO.setFileType(simpleLiteralTostring(mdf.getFileType()));
                        mdVO.setName(simpleLiteralTostring(mdf.getName()));
                        mdVO.setEncoding(simpleLiteralTostring(mdf.getEncoding()));
                        mdVec.add(mdVO);
                    }
                    sourceVO.setMdFormats(mdVec);
                    // Fulltext parameters
                    FTFetchSettingsType ftfs = source.getFTFetchSettings();
                    FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();
                    for (FTFetchSettingType ftf : ftfArray)
                    {
                        FullTextVO fulltextVO = new FullTextVO();
                        if (ftf.getDescription() != null)
                        { fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription())); }
                        fulltextVO.setFtUrl(new URL(simpleLiteralTostring(ftf.getIdentifier())));
                        fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
                        fulltextVO.setFtDefault(ftf.getDefault());
                        fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
                        fulltextVO.setFileType(simpleLiteralTostring(ftf.getFileType()));
                        fulltextVO.setContentCategory(simpleLiteralTostring(ftf.getContentCategorie()));
                        fulltextVO.setVisibility(simpleLiteralTostring(ftf.getVisibility()));
                        fulltextVO.setName(simpleLiteralTostring(ftf.getName()));
                        fulltextVO.setEncoding(simpleLiteralTostring(ftf.getEncoding()));
                        fulltextVec.add(fulltextVO);
                    }
                    sourceVO.setFtFormats(fulltextVec);
                    // Check if a transformation for the default MD format is possible
                    if (this.transformationFormat != null)
                    {
                        for (int x = 0; x < sourceVO.getMdFormats().size(); x++)
                        {
                            MetadataVO md = sourceVO.getMdFormats().get(x);
                            if (md.isMdDefault())
                            {
                                if (this.thirdPartyTransformer.checkXsltTransformation(md.getName(), 
                                        this.transformationFormat) 
                                        || (this.transformationFormat.toLowerCase().equals(md.getName().toLowerCase())))
                                { sourceVec.add(sourceVO); }
                            }
                        }
                    }
                    else
                    { 
                        sourceVec.add(sourceVO); 
                    }
                }
            }
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("Processing the source URL caused an error", e); throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("Parsing sources.xml caused an error", e);
            throw new RuntimeException(e);
        }
        return sourceVec;
    }

    /**
     * Gets all Sources for a specific format.
     * @param format
     * @return DataSourceVO
     * @throws RuntimeException
     */
    public Vector<DataSourceVO> getSources(String format) throws RuntimeException
    {
        this.transformationFormat = format;
        return this.getSources();
    }

    /**
     * Returns a specific source.
     * 
     * @param name
     * @return corresponding source
     * @throws RuntimeException
     */
    public DataSourceVO getSourceByName(String name) throws RuntimeException
    {
        DataSourceVO sourceVO = new DataSourceVO();
        Vector<FullTextVO> fulltextVec = new Vector<FullTextVO>();
        Vector<MetadataVO> mdVec = new Vector<MetadataVO>();
        Vector<String> examplesVec = new Vector<String>();
        boolean found = false;
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            java.io.InputStream in = cl.getResourceAsStream(this.sourceXmlPath);
            this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
            this.sourceType = this.sourceDoc.getImportSources();
            ImportSourceType[] sources = this.sourceType.getImportSourceArray();
            for (ImportSourceType source : sources)
            {
                if (!source.getName().trim().toLowerCase().equals(name.trim().toLowerCase()))
                {
                    continue;
                }
                else
                {
                    found = true;
                }
                sourceVO.setName(source.getName());
                sourceVO.setDescription(simpleLiteralTostring(source.getDescription()));
                sourceVO.setUrl(new URL(simpleLiteralTostring(source.getIdentifier())));
                sourceVO.setType(simpleLiteralTostring(source.getFormatArray(0)));
                sourceVO.setEncoding(simpleLiteralTostring(source.getFormatArray(1)));
                sourceVO.setHarvestProtocol(simpleLiteralTostring(source.getHarvestProtocol()));
                sourceVO.setTimeout(Integer.parseInt(source.getTimeout().toString()));
                sourceVO.setNumberOfTries(Integer.parseInt(source.getNumberOfTries().toString()));
                sourceVO.setStatus(simpleLiteralTostring(source.getStatus()));
                sourceVO.setIdentifier(simpleLiteralTostring(source.getSourceIdentifier()));
                sourceVO.setLicense(source.getLicense());   
                sourceVO.setCopyright(source.getCopyright());
                
                if (source.getItemUrl() != null)
                {
                    sourceVO.setItemUrl(new URL(simpleLiteralTostring(source.getItemUrl())));
                }   
//                SimpleLiteral[] examples = source.getSourceIdentifierExampleArray();
//                for (SimpleLiteral example : examples)
//                {
//                    examplesVec.add(simpleLiteralTostring(example));
//                }
//                sourceVO.setIdentifierExample(examplesVec);    
                // Metadata parameters
                MDFetchSettingsType mdfs = source.getMDFetchSettings();
                MDFetchSettingType[] mdfArray = mdfs.getMDFetchSettingArray();
                for (MDFetchSettingType mdf : mdfArray)
                {
                    MetadataVO mdVO = new MetadataVO();
                    if (mdf.getDescription() != null)
                    {
                        mdVO.setMdDesc(simpleLiteralTostring(mdf.getDescription()));
                    }
                    mdVO.setMdUrl(new URL(simpleLiteralTostring(mdf.getIdentifier())));
                    mdVO.setMdFormat(simpleLiteralTostring(mdf.getFormat()));
                    mdVO.setMdDefault(mdf.getDefault());
                    mdVO.setMdLabel(simpleLiteralTostring(mdf.getLabel()));
                    mdVO.setFileType(simpleLiteralTostring(mdf.getFileType()));
                    mdVO.setName(simpleLiteralTostring(mdf.getName()));
                    mdVO.setEncoding(simpleLiteralTostring(mdf.getEncoding()));
                    mdVec.add(mdVO);
                }
                sourceVO.setMdFormats(mdVec);
                // Fulltext parameters
                FTFetchSettingsType ftfs = source.getFTFetchSettings();
                FTFetchSettingType[] ftfArray = ftfs.getFTFetchSettingArray();
                for (FTFetchSettingType ftf : ftfArray)
                {
                    FullTextVO fulltextVO = new FullTextVO();
                    if (ftf.getDescription() != null)
                    {
                        fulltextVO.setFtDesc(simpleLiteralTostring(ftf.getDescription()));
                    }
                    fulltextVO.setFtUrl(new URL(simpleLiteralTostring(ftf.getIdentifier())));
                    fulltextVO.setFtFormat(simpleLiteralTostring(ftf.getFormat()));
                    fulltextVO.setFtDefault(ftf.getDefault());
                    fulltextVO.setFtLabel(simpleLiteralTostring(ftf.getLabel()));
                    fulltextVO.setFileType(simpleLiteralTostring(ftf.getFileType()));
                    fulltextVO.setContentCategory(simpleLiteralTostring(ftf.getContentCategorie()));
                    fulltextVO.setVisibility(simpleLiteralTostring(ftf.getVisibility()));
                    fulltextVO.setName(simpleLiteralTostring(ftf.getName()));
                    fulltextVO.setEncoding(simpleLiteralTostring(ftf.getEncoding()));
                    fulltextVec.add(fulltextVO);
                }
                sourceVO.setFtFormats(fulltextVec);
            }
        }
        catch (MalformedURLException e)
        {
            LOGGER.error("Processing the source URL caused an error", e);
            throw new RuntimeException(e);
        }
        catch (Exception e)
        {
            LOGGER.error("Parsing sources.xml caused an error", e);
            throw new RuntimeException(e);
        }
        // this.printSourceXML(sourceVO);
        if (found)
        {
            return sourceVO;
        }
        else
        {
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
    public DataSourceVO getSourceByIdentifier(String id) throws RuntimeException
    {
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            java.io.InputStream in = cl.getResourceAsStream(this.sourceXmlPath);
            this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            LOGGER.error("Parsing sources.xml caused an error", e);
            throw new RuntimeException(e);
        }
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {
            if (!simpleLiteralTostring(source.getSourceIdentifier()).trim().toLowerCase().equals(
                    id.trim().toLowerCase()))
            {
                continue;
            }
            else
            {
                return this.getSourceByName(source.getName());
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
    public String getSourceNameByIdentifier(String id) throws RuntimeException
    {
        try
        {
            ClassLoader cl = this.getClass().getClassLoader();
            java.io.InputStream in = cl.getResourceAsStream(this.sourceXmlPath);
            this.sourceDoc = ImportSourcesDocument.Factory.parse(in);
        }
        catch (Exception e)
        {
            LOGGER.error("Parsing sources.xml caused an error", e);
            throw new RuntimeException(e);
        }
        this.sourceType = this.sourceDoc.getImportSources();
        ImportSourceType[] sources = this.sourceType.getImportSourceArray();
        for (ImportSourceType source : sources)
        {
            if (!simpleLiteralTostring(source.getSourceIdentifier()).trim().toLowerCase().equals(
                    id.trim().toLowerCase()))
            {
                continue;
            }
            else
            {
                return source.getName();
            }
        }
        return null;
    }

    /**
     * This operation returns the metadata informations to fetch. If no format from was specified the default metadata
     * informations are fetched
     * 
     * @param source
     * @param format the format of which the metadata will be retrieved
     * @return metadata informations
     */
    public MetadataVO getMdObjectfromSource(DataSourceVO source, String format)
    {
        MetadataVO md = null;
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            md = source.getMdFormats().get(i);
            if (md.getName().trim().toLowerCase().equals(format.trim().toLowerCase()))
            {
                return md;
            }
        }
        return null;
    }

    /**
     * Returns the default MetadataVO from a source.
     * 
     * @param source
     * @return MetadataVO
     */
    public MetadataVO getDefaultMdFormatFromSource(DataSourceVO source)
    {
        Vector<MetadataVO> mdv = source.getMdFormats();
        for (int i = 0; i < mdv.size(); i++)
        {
            MetadataVO mdVO = source.getMdFormats().get(i);
            if (mdVO.isMdDefault())
            {
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
    public DataSourceVO updateMdEntry(DataSourceVO source, MetadataVO md)
    {
        Vector<MetadataVO> mdv = source.getMdFormats();
        if (md != null)
        {
            for (int i = 0; i < mdv.size(); i++)
            {
                MetadataVO mdVO = source.getMdFormats().get(i);
                if (mdVO.getName().trim().toLowerCase().equals(md.getName().trim().toLowerCase()))
                {
                    mdv.setElementAt(md, i);
                }
            }
        }
        source.setMdFormats(mdv);
        return source;
    }

    private String simpleLiteralTostring(SimpleLiteral sl)
    {
        return sl.toString().substring(sl.toString().indexOf(">") + 1, sl.toString().lastIndexOf("<"));
    }

    /**
     * Print out source values for debug purpose.
     * 
     * @param source
     */
    public void printSourceXML(DataSourceVO source)
    {
        String seperator = "____________________________________________________________________";
        LOGGER.info(seperator);
        LOGGER.info("Source Name        : " + source.getName());
        LOGGER.info("Description        : " + source.getDescription());
        LOGGER.info("Main URL           : " + java.net.URLDecoder.decode(source.getUrl().toString()));
        LOGGER.info("Doc type           : " + source.getType());
        LOGGER.info("Doc encoding       : " + source.getEncoding());
        LOGGER.info("Identfier          : " + source.getIdentifier());
        LOGGER.info("Harvest protocol   : " + source.getHarvestProtocol());
        LOGGER.info("Timeout            : " + source.getTimeout());
        LOGGER.info("Retry after        : " + source.getRetryAfter());
        LOGGER.info("Number of tries    : " + source.getNumberOfTries());
        LOGGER.info("Status             : " + source.getStatus());
        LOGGER.info("License            : " + source.getLicense());
        LOGGER.info("Copyright          : " + source.getCopyright());
        LOGGER.info(seperator);
        for (int i = 0; i < source.getMdFormats().size(); i++)
        {
            MetadataVO md = source.getMdFormats().get(i);
            LOGGER.info(seperator);
            LOGGER.info("MD description     : " + md.getMdDesc());
            LOGGER.info("MD format          : " + md.getMdFormat());
            LOGGER.info("MD label           : " + md.getMdLabel());
            LOGGER.info("MD URL             : " + java.net.URLDecoder.decode(md.getMdUrl().toString()));
            LOGGER.info("MD default         : " + md.isMdDefault());
            LOGGER.info(seperator);
        }
        for (int i = 0; i < source.getFtFormats().size(); i++)
        {
            FullTextVO ft = source.getFtFormats().get(i);
            LOGGER.info(seperator);
            LOGGER.info("FT description     : " + ft.getFtDesc());
            LOGGER.info("FT format          : " + ft.getFtFormat());
            LOGGER.info("FT label           : " + ft.getFtLabel());
            LOGGER.info("FT URL             : " + java.net.URLDecoder.decode(ft.getFtUrl().toString()));
            LOGGER.info("FT default         : " + ft.isFtDefault());
            LOGGER.info(seperator);
        }
    }
}