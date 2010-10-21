package de.mpg.escidoc.services.batchprocess;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;

public abstract class BatchProcess
{
    public enum CoreServiceObjectType
    {
        ITEM, CONTAINER;
    }

    public enum CoreServiceObjectStatus
    {
        PENDING, SUBMITTED, RELEASED, WITHDRAWN;
    }

    protected static Elements elements = null;
    private static final Logger logger = Logger.getLogger(BatchProcess.class);

    public static void main(String[] args) throws ClassNotFoundException
    {
        BatchProcess batchProcess = BatchProcess.initBatchProcess(args);
        logger.info("Batch process...");
        batchProcess.run(args);
        logger.info("Batch Process done!");
    }

    public abstract void run(String[] args);

    public static BatchProcess initBatchProcess(String[] args)
    {
        try
        {
            BatchProcess bp = (BatchProcess)Class.forName(CommandHelper.getArgument("-o", args, true)).newInstance();
            bp.initElements(args);
            return bp;
        }
        catch (Exception e)
        {
            throw new RuntimeException(CommandHelper.getArgument("-o", args, true) + " is not a valid operation", e);
        }
    }

    public void initElements(String[] args)
    {
        elements = Elements.getBatchProcessList(args);
    }
}