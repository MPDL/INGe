package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.IdentifierVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

/**
 * 
 * Transformer which removes UriIdentifiers from a list of publications
 * 
 * @author walter
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class LocalTagTransformer extends Transformer<ItemVO> {
	
	private final static String LOCAL_TAG_TO_ADD = "Web";
	
    private static Logger logger = Logger.getLogger(BPCImportTransformer.class);

    @Override
    public List<ItemVO> transform(List<ItemVO> list)
    {
        System.out.println("Number of items: " + list.size());
        List<ItemVO> resultList = new ArrayList<ItemVO>();
        for (ItemVO item : list)
        {
            try
            {
                if (!this.getTransformed().contains(item.getVersion().getObjectId()))
                {
                	System.out.println("Transforming item " + item.getVersion().getObjectIdAndVersion() + "!");
                	item = addLocalTag(item, LOCAL_TAG_TO_ADD);
                	resultList.add(item);
                	this.getTransformed().add(item.getVersion().getObjectId());
                    logger.info("TRANSFORM" + item.getVersion().getObjectId() +  " - Local tags edited");
                }
                
                report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                        + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            }
            catch (Exception e) {
                throw new RuntimeException("Error transforming local tags from element: ", e);
            }
        }
        return resultList;
    }

    /**
     * removes all local tags from a publication item
     * @param item
     * @return
     * @throws Exception
     */
    private ItemVO removeAllLocalTags(ItemVO item) throws Exception
    {
    	item.getLocalTags().clear();
        return item;
    }
    
    /**
     * add a local tag to a publication item
     * @param item
     * @return
     * @throws Exception
     */
    private ItemVO addLocalTag(ItemVO item, String localTag) throws Exception
    {
    	item.getLocalTags().add(localTag);
    	return item;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
