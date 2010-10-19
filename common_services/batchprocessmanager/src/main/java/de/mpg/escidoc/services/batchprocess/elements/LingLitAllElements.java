package de.mpg.escidoc.services.batchprocess.elements;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;

public class LingLitAllElements extends Elements<ItemVO>
{
    @Override
    public List<ItemVO> getList()
    {
        List<ItemVO> list = new ArrayList<ItemVO>();
        return list;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
