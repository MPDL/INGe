package de.mpg.escidoc.services.cone;

import java.util.Map;

public interface Querier
{
    public Map<String, String> query(String model, String query) throws Exception;
    
    public Map<String, String> details(String model, String query) throws Exception;
    
}
