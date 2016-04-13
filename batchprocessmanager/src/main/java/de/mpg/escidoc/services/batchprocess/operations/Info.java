package de.mpg.escidoc.services.batchprocess.operations;

public class Info extends Operation
{
    
    @Override
    public void execute(String[] args)
    {
        System.out.println(elements.getElements());
    }

}
