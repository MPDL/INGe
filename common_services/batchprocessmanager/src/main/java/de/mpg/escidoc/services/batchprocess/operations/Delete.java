package de.mpg.escidoc.services.batchprocess.operations;

import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;

public class Delete extends BatchProcess
{
    @Override
    public void run(Elements<?> list)
    {
        delete(list);
    }

    public void delete(Elements<?> list)
    {
        System.out.println("deleting...");
    }
}
