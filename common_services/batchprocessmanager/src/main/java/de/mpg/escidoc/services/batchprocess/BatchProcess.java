package de.mpg.escidoc.services.batchprocess;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.operations.Delete;
import de.mpg.escidoc.services.batchprocess.operations.Edit;
import de.mpg.escidoc.services.batchprocess.transformers.Transformer;

public abstract class BatchProcess
{
    public enum CoreServiceObjectType
    {
        ITEM, CONTAINER;
    }

    private static Elements<?> list = null;
    private static final Logger logger = Logger.getLogger(BatchProcess.class);

    public static void main(String[] args) throws ClassNotFoundException
    {
        if (args.length < 2)
        {
            throw new RuntimeException("Agruments : Operation Name, list Name");
        }
        else
        {
            String operationName = args[0];
            String elementsName = args[1];
            BatchProcess batchProcess;
            list = Elements.getBatchProcessList(elementsName);
            batchProcess = BatchProcess.getBatchProcess(operationName);
            if (batchProcess instanceof Edit)
            {
                if (args.length < 3)
                {
                    throw new RuntimeException("Edit needs a transformer as argument");
                }
                else
                {
                    ((Edit)batchProcess).setTransformer(Transformer.getTransformer(args[2]));
                }
            }
            batchProcess.run(list);
            logger.info(operationName + " " + elementsName + " done!");
        }
    }

    public static BatchProcess getBatchProcess(String name)
    {
        if ("edit".equals(name))
        {
            return new Edit();
        }
        else if ("delete".equals(name))
        {
            return new Delete();
        }
        else
        {
            throw new RuntimeException(name + " is not a valid operation");
        }
    }

    public abstract void run(Elements<?> list);
}