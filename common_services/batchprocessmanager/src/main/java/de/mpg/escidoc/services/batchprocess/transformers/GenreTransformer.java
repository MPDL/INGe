package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;

/**
 * Changes the Genre of an ItemVO
 * 
 * @author walter
 *
 */
public class GenreTransformer extends Transformer<ItemVO>
{
    private static Logger logger = Logger.getLogger(BPCImportTransformer.class);
    

    @Override
    public List<ItemVO> transform(List<ItemVO> list)
    {
    	List<ItemVO> resultList = new ArrayList<ItemVO>();
        System.out.println("Number of items: " + list.size());
        for (ItemVO item : list)
        {
        	
            item = replaceGenre(item);
            resultList.add((ItemVO) item);
        	this.getTransformed().add(item.getVersion().getObjectId());
            this.report.addEntry("TRANSFORM" + item.getVersion().getObjectId(), "Item Genre changed",
                    ReportEntryStatusType.FINE);
            
        }
        
        return resultList;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
    
    /**
     * Changes the Genre of a given ItemVO
     * 
     * @param item
     * @return ItemVO with changed genre
     */
    private ItemVO replaceGenre(ItemVO item) {
    	PubItemVO pubItem = new PubItemVO(item);
    	MdsPublicationVO metaData = pubItem.getMetadata();
    	if (metaData.getGenre().equals(Genre.BOOK))
    	{
    		MdsPublicationVO.Genre genre = null;
    		for (CreatorVO creator : metaData.getCreators())
    		{
    			if (creator.getRole().equals(CreatorVO.CreatorRole.EDITOR))
    			{
    				genre = Genre.COLLECTED_EDITION;
    			}
    		}
    		
    		if (genre == null)
    		{
    			genre = Genre.MONOGRAPH;
    		}
    		
			metaData.setGenre(genre);
    		// Remove superfluous MetaDate if existing
    		metaData = removeSuperfluousMD(metaData); 
    	}
    	pubItem.setMetadata(metaData);
    	return (ItemVO) pubItem;
    }

    /**
     * Remove superfluous MetaDate from MdsPublicationVO, if existing
     * 
     * @param metaData
     * @return cleaned MdsPublicationVO
     */
    private MdsPublicationVO removeSuperfluousMD (MdsPublicationVO metaData)
    {
    	
		if (metaData.getReviewMethod() != null)
		{
			metaData.setReviewMethod(null);
		}
		if (metaData.getSources() != null)
		{
			for(int i = 0; i < metaData.getSources().size(); i++)
			{
				if (metaData.getSources().get(i) != null)
				{
					if(metaData.getSources().get(i).getTotalNumberOfPages() != null)
					{
						metaData.getSources().get(i).setTotalNumberOfPages(null);
					}
					if(metaData.getSources().get(i).getPublishingInfo() != null)
					{
						if( metaData.getSources().get(i).getPublishingInfo().getPublisher() != null )
						{
							metaData.getSources().get(i).getPublishingInfo().setPublisher(null);
						}
						if( metaData.getSources().get(i).getPublishingInfo().getPlace() != null )
						{
							metaData.getSources().get(i).getPublishingInfo().setPlace(null);
						}
					}
					if(metaData.getSources().get(i).getIdentifiers() != null)
					{
						metaData.getSources().get(i).getIdentifiers().clear();
					}
				}
			}
		}
		return metaData;
    }
}