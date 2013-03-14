package de.mpg.escidoc.services.batchprocess.helper;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectStatus;

public class CommandHelper
{
    public static String getArgument(String argumentSymbole, String[] args, boolean required)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (argumentSymbole.equals(args[i]))
            {
                return args[i + 1];
            }
        }
        if (required)
        {
            throw new RuntimeException(
                    "Error reading argument "
                            + argumentSymbole
                            + "\n Usage: BatchProcess -o [OperationClass] -e [ElementClass] -t [TransformationClass] -s [Status] -n [MaximumNumberOfElements]");
        }
        return null;
    }

    public static CoreServiceObjectStatus getStatusEnumValue(String str)
    {
        if (str != null)
        {
            try
            {
                return CoreServiceObjectStatus.valueOf(str.toUpperCase());
            }
            catch (Exception e)
            {
                String message = "";
                for (CoreServiceObjectStatus value : CoreServiceObjectStatus.values())
                {
                    message += value.name().toLowerCase() + ", ";
                }
                throw new RuntimeException(str + " is not a valid status value! Allowed are: " + message);
            }
        }
        return null;
    }
}
