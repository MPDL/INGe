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
public class DeleteUriTransformer extends Transformer<ItemVO> {
	
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
                	item = removeUriIdentifiers(item);
                	resultList.add(item);
                	this.getTransformed().add(item.getVersion().getObjectId());
                    logger.info("TRANSFORM" + item.getVersion().getObjectId() +  " - Identifiers with type URI removed");
                }
                
                report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                        + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            }
            catch (Exception e) {
                throw new RuntimeException("Error transforming uri identifiers from element: ", e);
            }
        }
        return resultList;
    }

    /**
     * removes all identifiers with type URI from a publication item
     * @param item
     * @return
     * @throws Exception
     */
    private ItemVO removeUriIdentifiers(ItemVO item) throws Exception
    {
    	PubItemVO pubItemVO = new PubItemVO(item);
    	List<IdentifierVO> identifierList = new ArrayList<IdentifierVO> (pubItemVO.getMetadata().getIdentifiers());
        for (IdentifierVO identifierVO : identifierList)
        {
        	if ("URI".equals(identifierVO.getType().name()))
        	{
        		pubItemVO.getMetadata().getIdentifiers().remove(identifierVO);
        	}
        }
        return (ItemVO) pubItemVO;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
