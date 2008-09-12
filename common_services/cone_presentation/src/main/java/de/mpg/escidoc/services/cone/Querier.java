package de.mpg.escidoc.services.cone;

import java.util.Set;

import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.Triple;

public interface Querier
{
    public Set<Pair> query(String model, String query) throws Exception;
    
    public Set<Pair> query(String model, String query, String lang) throws Exception;
    
    public Set<Triple> details(String model, String query) throws Exception;
    
}
