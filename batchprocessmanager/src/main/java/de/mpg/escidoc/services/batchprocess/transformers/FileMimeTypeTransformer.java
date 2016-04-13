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

public class FileMimeTypeTransformer extends Transformer<ItemVO>
{
    private static Logger logger = Logger.getLogger(FileMimeTypeTransformer.class);

    @Override
    public List<ItemVO> transform(List<ItemVO> list)
    {
        List<ItemVO> resultList = new ArrayList<ItemVO>();
        System.out.println("Number of items: " + list.size());
        for (ItemVO item : list)
        {

            try
            {
                if (!this.getTransformed().contains(item.getVersion().getObjectId()) && item.getVersion().getVersionNumber() == 1)
                {
                	System.out.println("Transforming item " + item.getVersion().getObjectIdAndVersion() + "!");
                	item = reloadFile(item);
                	resultList.add((ItemVO) item);
                	this.getTransformed().add(item.getVersion().getObjectId());
                }
                
                report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                        + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            }
            catch (Exception e) {
                throw new RuntimeException("Error transforming elements: ", e);
            }
        }
        
        return resultList;
    }

    

    private ItemVO reloadFile(ItemVO item) throws Exception
    {
        
        for (FileVO file : item.getFiles())
        {
            file.getReference().setObjectId(null);
            file.setMimeType("application/zip");
        }
        return item;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }

}