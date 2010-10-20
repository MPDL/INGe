package de.mpg.escidoc.services.batchprocess.operations;

import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;

public class Delete extends BatchProcess
{
    @Override
    public void run(String[] args)
    {
        delete(elements);
    }

    public void delete(Elements<?> list)
    {
        System.out.println("deleting...");
    }
}
