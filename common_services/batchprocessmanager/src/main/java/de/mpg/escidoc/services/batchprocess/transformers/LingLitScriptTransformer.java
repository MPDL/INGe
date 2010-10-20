package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;

public class LingLitScriptTransformer extends Transformer<ItemVO>
{
    @Override
    public List<ItemVO> transform(List<ItemVO> list)
    {
        System.out.println("Transforming...");
        return list;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
