package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.framework.PropertyReader;

public class BPCImportTransformer extends Transformer<PubItemVO>
{
    private static Logger logger = Logger.getLogger(BPCImportTransformer.class);

    @Override
    public List<PubItemVO> transform(List<PubItemVO> list)
    {
        System.out.println("Number of items: " + list.size());
        for (PubItemVO item : list)
        {
            try
            {
                if (!this.getTransformed().contains(item.getVersion().getObjectId()))
                {
                	System.out.println("Transforming item " + item.getVersion().getObjectIdAndVersion() + "!");
                	item = fixBrokenRootId(item);
    	            item = removeEmptyOus(item);
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

    private PubItemVO fixBrokenRootId(PubItemVO item) throws Exception
    {
        for (CreatorVO creatorVO : item.getMetadata().getCreators())
        {
            if (creatorVO.getPerson() != null && creatorVO.getPerson().getOrganizations() != null)
            {
                for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations())
                {
                    if ("Max Planck Society".equals(organizationVO.getIdentifier()))
                    {
                        logger.info("Item " + item.getVersion().getObjectIdAndVersion() + ": " + "fixed ou identifier");
                        organizationVO.setIdentifier(PropertyReader.getProperty("escidoc.pubman.root.organisation.id"));
                    }
                }
            }
        }
        return item;
    }

    private PubItemVO removeEmptyOus(PubItemVO item) throws Exception
    {
        for (CreatorVO creatorVO : item.getMetadata().getCreators())
        {
            if (creatorVO.getPerson() != null && creatorVO.getPerson().getOrganizations() != null)
            {
                for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations())
                {
                    if ("".equals(organizationVO.getName()))
                    {
                        logger.info("Item " + item.getVersion().getObjectIdAndVersion() + ": " + "removed empty ou");
                        creatorVO.getPerson().getOrganizations().remove(organizationVO);
                        break;
                    }
                }
            }
        }
        return item;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }

}