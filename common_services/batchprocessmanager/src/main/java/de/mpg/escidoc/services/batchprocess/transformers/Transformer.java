package de.mpg.escidoc.services.batchprocess.transformers;

import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;

public abstract class Transformer<ObjectType> extends BatchProcess
{
    public static Transformer<?> getTransformer(String name)
    {
        try
        {
            return (Transformer) Class.forName(name).newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(name + " is not a valid transformer", e);
        }
        
    }

    public abstract List<ObjectType> transform(List<ObjectType> list);

    public abstract CoreServiceObjectType getObjectType();
}
