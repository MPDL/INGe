package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

public class CBSPriorTransformer extends Transformer<PubItemVO>
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
                	item = addPriorInstituteId(item);
    	            item = addInstituteId(item);
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

    private PubItemVO addPriorInstituteId(PubItemVO item) throws Exception
    {
        for (CreatorVO creatorVO : item.getMetadata().getCreators())
        {
            if (creatorVO.getPerson() != null && creatorVO.getPerson().getOrganizations() != null)
            {
                for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations())
                {
                    if ("MPI of Cognitive Neuroscience (Leipzig, -2003), The Prior Institutes, MPI for Human Cognitive and Brain Sciences, Max Planck Society".equals(organizationVO.getName().getValue()))
                    {
                        logger.info("Item " + item.getVersion().getObjectIdAndVersion() + ": " + "added ou identifier");
                        organizationVO.setIdentifier("escidoc:634574");
                    }
                    else if ("MPI for Psychological Research (Munich, -2003), The Prior Institutes, MPI for Human Cognitive and Brain Sciences, Max Planck Society".equals(organizationVO.getName().getValue()))
                    {
                        logger.info("Item " + item.getVersion().getObjectIdAndVersion() + ": " + "added MPI ou identifier");
                        organizationVO.setIdentifier("escidoc:634573");
                    }
                        
                }
            }
        }
        return item;
    }

    private PubItemVO addInstituteId(PubItemVO item) throws Exception
    {
        for (CreatorVO creatorVO : item.getMetadata().getCreators())
        {
            if (creatorVO.getPerson() != null && creatorVO.getPerson().getOrganizations() != null)
            {
                for (OrganizationVO organizationVO : creatorVO.getPerson().getOrganizations())
                {
                    if ("MPI for Human Cognitive and Brain Sciences, Max Planck Society".equals(organizationVO.getName().getValue()))
                    {
                        logger.info("Item " + item.getVersion().getObjectIdAndVersion() + ": " + "added ou identifier");
                        organizationVO.setIdentifier("escidoc:634548");
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