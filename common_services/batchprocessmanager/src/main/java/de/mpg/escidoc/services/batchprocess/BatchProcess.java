package de.mpg.escidoc.services.batchprocess;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;
import de.mpg.escidoc.services.batchprocess.operations.Operation;

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
    private List<String> transformed = new ArrayList<String>();
    protected static Operation operation = null;
    protected static BatchProcessReport report = new BatchProcessReport();
    private static final Logger logger = Logger.getLogger(BatchProcess.class);

    public static void main(String[] args)
    {
        operation = Operation.factory(args);
        logger.info("Batch process starting...");
        try
        {
            operation.execute(args);
            logger.info("Batch Process done!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            logger.info(report.printReport());
        }
    }

    public void run(String[] args)
    {
        operation = Operation.factory(args);
        logger.info("Batch process starting...");
        operation.execute(args);
        logger.info("Batch Process done!");
        logger.info(report.printReport());
    }

	public List<String> getTransformed() 
	{
		return transformed;
	}

	public void setTransformed(List<String> transformed) 
	{
		this.transformed = transformed;
	}
    

}