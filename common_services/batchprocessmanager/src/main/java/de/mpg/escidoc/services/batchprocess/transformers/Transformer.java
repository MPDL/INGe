package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;

public abstract class Transformer<ObjectType>
{
    public static Transformer<?> getTransformer(String name)
    {
        if ("LingLitScript".equals(name))
        {
            return new LingLitScriptTransformer();
        }
        else
        {
            throw new RuntimeException(name + " is not a valid transformer");
        }
    }

    public abstract List<ObjectType> transform(List<ObjectType> list);

    public abstract CoreServiceObjectType getObjectType();
}
