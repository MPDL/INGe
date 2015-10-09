package de.mpg.escidoc.services.batchprocess.operations;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;

public abstract class Operation extends BatchProcess
{
    protected static Elements elements = null;
    private static final Logger logger = Logger.getLogger(BatchProcess.class);

    public abstract void execute(String[] args);

    public static Operation factory(String[] args)
    {
        try
        {
            Operation op = (Operation)Class.forName(CommandHelper.getArgument("-o", args, true)).newInstance();
            op.initElements(args);
            return op;
        }
        catch (Exception e)
        {
            throw new RuntimeException(CommandHelper.getArgument("-o", args, true) + " is not a valid operation", e);
        }
    }

    public void initElements(String[] args)
    {
        elements = Elements.factory(args);
    }
}
