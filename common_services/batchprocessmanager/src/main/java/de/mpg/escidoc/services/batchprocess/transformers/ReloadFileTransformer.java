package de.mpg.escidoc.services.batchprocess.transformers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.EdocHandler;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemResultVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

public class ReloadFileTransformer extends Transformer<ItemVO>
{
    private static Logger logger = Logger.getLogger(ReloadFileTransformer.class);
    private static Map<String, Map<String, String>> fileMap = null;
    private static final String filename = "target/classes/ExportCBSMain.xml";

    @Override
    public List<ItemVO> transform(List<ItemVO> list)
    {
        
        System.out.println("Number of items: " + list.size());
        for (ItemVO item : list)
        {

            try
            {
                if (!this.getTransformed().contains(item.getVersion().getObjectId()) && item.getVersion().getVersionNumber() == 1)
                {
                	System.out.println("Transforming item " + item.getVersion().getObjectIdAndVersion() + "!");
                	item = reloadFile(item);
                }
                
                report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                        + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            }
            catch (Exception e) {
                throw new RuntimeException("Error transforming elements: ", e);
            }
        }
        List<ItemVO> resultList = new ArrayList<ItemVO>();
        for (ItemVO item : list)
        {
            if (getTransformed().contains(item.getVersion().getObjectId()))
            {
                resultList.add((ItemVO) item);
            }
        }
        return resultList;
    }

    

    private ItemVO reloadFile(ItemVO item) throws Exception
    {
        String id = null;
        for (IdentifierVO identifier : ((MdsPublicationVO) item.getMetadataSets().get(0)).getIdentifiers())
        {
            if (identifier.getType() == IdType.EDOC)
            {
                id = identifier.getId();
                break;
            }
        }
        
        for (FileVO file : item.getFiles())
        {
            if (file.getVisibility() == Visibility.AUDIENCE || file.getVisibility() == Visibility.PRIVATE)
            {
                file.getReference().setObjectId(null);
                String filename = file.getName();
                if (getFileMap().containsKey(id) && getFileMap().get(id).containsKey(filename))
                {
                    String url = getFileMap().get(id).get(filename);
                    file.setContent(url);
                    getTransformed().add(item.getVersion().getObjectId());
                }
                
            }
        }
        return item;
    }

    private static Map<String, Map<String, String>> getFileMap() throws Exception
    {
        if (fileMap == null)
        {
            initFileMap();
        }
        return fileMap;
    }

    private static void initFileMap() throws Exception
    {
        System.out.print("Initializing file map...");
        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        EdocHandler edocHandler = new EdocHandler();
        saxParser.parse(new File(filename), edocHandler);
        fileMap = edocHandler.getFileMap();
        System.out.println("done!");
    }



    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }

}