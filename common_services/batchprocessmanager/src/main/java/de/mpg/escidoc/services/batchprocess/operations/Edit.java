package de.mpg.escidoc.services.batchprocess.operations;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.transformers.Transformer;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Edit extends BatchProcess
{
    private Transformer<?> transformer;

    public void run(Elements list)
    {
        try
        {
            list = transform(list);
            save(list);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Batch Edit: ", e);
        }
    }

    public Elements transform(Elements list)
    {
        list.setList(transformer.transform(list.getList()));
        return list;
    }

    public void save(Elements list) throws Exception
    {
        if (ItemVO.class.equals(list.getObjectType()))
        {
            ItemHandler handler = ServiceLocator.getItemHandler();
        }
    }

    public Transformer getTransformer()
    {
        return transformer;
    }

    public void setTransformer(Transformer transformer)
    {
        this.transformer = transformer;
    }
}
