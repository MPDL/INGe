package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

public class LingLitScriptTransformer extends Transformer<PubItemVO>
{
    @Override
    public List<PubItemVO> transform(List<PubItemVO> list)
    {
        for (PubItemVO item : list)
        {
            report.addEntry("Transform" + item.getVersion().getObjectId(), "Transform "
                    + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
        }
        return list;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
