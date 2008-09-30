package de.mpg.escidoc.services.cone;

import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpg.escidoc.services.cone.util.Pair;
import de.mpg.escidoc.services.cone.util.Triple;

public interface Querier
{
    public List<Pair> query(String model, String query) throws Exception;
    
    public List<Pair> query(String model, String query, String lang) throws Exception;
    
    public Map<String, List<String>> details(String model, String id) throws Exception;
    
    public Map<String, List<String>> details(String model, String id, String lang) throws Exception;
    
}
