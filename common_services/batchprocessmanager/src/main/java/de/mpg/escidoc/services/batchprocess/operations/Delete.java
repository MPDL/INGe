package de.mpg.escidoc.services.batchprocess.operations;

import de.mpg.escidoc.services.batchprocess.elements.Elements;

public class Delete extends Operation
{
    @Override
    public void execute(String[] args)
    {
        delete(elements);
    }

    public void delete(Elements<?> list)
    {
        System.out.println("deleting...");
    }
}
