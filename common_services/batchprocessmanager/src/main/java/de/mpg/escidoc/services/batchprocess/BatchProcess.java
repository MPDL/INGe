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
    	
    	if (args == null || args.length == 0)
    	{
    		System.out.println("Usage: BatchProcess -o [OperationClass] -e [ElementClass] -t [TransformationClass] -s [Status] -n [MaximumNumberOfElements]");
    		System.out.println("-o : OperationClass: May be one of Edit, Info, Delete, Submit, Release or write your own Operation.");
    		System.out.println("-e : ElementClass: Defines the class name used to select the items you want to operate on, e.g. ElementsWithLocalTag. Usually, the according class has to be implemented or an existing class has to be modified.");
    		System.out.println("-t : TransformationClass: This parameter is only needed when the operation is \"Edit\". It defines how the elements should be transformed.");
    		System.out.println("-s : Status: This parameter is only needed when the operation is \"Edit\". Defines the eSciDoc lifecycle state the elements should be in after the operation. It is one of \"pending\", \"submitted\" or \"released\".");
    		System.out.println("-n : MaximumNumberOfElements: Optional. Defines the maximum number of elements that should be processed. default is 5000.");
    		System.out.println();
    		System.out.println("Example: java de.mpg.escidoc.services.batchprocess.BatchProcess -o de.mpg.escidoc.services.batchprocess.operations.Info -e de.mpg.escidoc.services.batchprocess.elements.ElementsWithRestrictedFiles -n 1");
    	}
    	else
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