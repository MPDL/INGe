package de.mpg.escidoc.services.batchprocess;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.elements.Elements;

public abstract class BatchProcess
{
    public enum CoreServiceObjectType
    {
        ITEM, CONTAINER;
    }

    public enum CoreServiceObjectStatus
    {
        PENDING, SUBMITtED, RELEASED, WITHDRAWN
    }

    protected static Elements elements = null;
    private static final Logger logger = Logger.getLogger(BatchProcess.class);

    public static void main(String[] args) throws ClassNotFoundException
    {
        BatchProcess batchProcess = BatchProcess.getBatchProcess(BatchProcess.getArgument("-o", args));
        logger.info("Batch process...");
        batchProcess.run(args);
        logger.info("Batch Process done!");
    }

    public static BatchProcess getBatchProcess(String name)
    {
        try
        {
            return (BatchProcess)Class.forName(name).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(name + " is not a valid operation", e);
        }
    }

    public static String getArgument(String argumentSymbole, String[] args)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (argumentSymbole.equals(args[i]))
            {
                return args[i + 1];
            }
        }
        throw new RuntimeException("Error reading argument" + argumentSymbole
                + "\n Usage: BatchProcess -o [OperationClass] -e [ElementClass] -t [TransformationClass] -s [Status]");
    }

    public abstract void run(String[] args);

    public Elements<?> getElements(String[] args)
    {
        return Elements.getBatchProcessList(BatchProcess.getArgument("-e", args));
    }
}