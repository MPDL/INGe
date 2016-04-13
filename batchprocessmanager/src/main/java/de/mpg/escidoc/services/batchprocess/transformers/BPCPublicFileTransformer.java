package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.FileVO;
import de.mpg.escidoc.services.common.valueobjects.FileVO.Visibility;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

public class BPCPublicFileTransformer extends Transformer<ItemVO>
{
    private static Logger logger = Logger.getLogger(BPCImportTransformer.class);

    @Override
    public List<ItemVO> transform(List<ItemVO> list)
    {
        System.out.println("Number of items: " + list.size());
        for (ItemVO item : list)
        {
            try
            {
                if (!this.getTransformed().contains(item.getVersion().getObjectId()))
                {
                	System.out.println("Transforming item " + item.getVersion().getObjectIdAndVersion() + "!");
                	item = makeFulltextPublic(item);
                	getTransformed().add(item.getVersion().getObjectId());
                }
                
                report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                        + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            }
            catch (Exception e) {
                throw new RuntimeException("Error transforming BPCImportElements: ", e);
            }
        }
        return list;
    }

    private ItemVO makeFulltextPublic(ItemVO item) throws Exception
    {
        if (item.getFiles().size() == 1)
        {
            for (FileVO fileVO : item.getFiles())
            {
                if (fileVO.getVisibility() == Visibility.AUDIENCE)
                {
                    fileVO.setVisibility(Visibility.PUBLIC);
                }
            }
        }
        else
        {
            System.out.println("WARNING: " + item.getVersion().getObjectId() + " has more than one file!");
        }
        
        return item;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }

}