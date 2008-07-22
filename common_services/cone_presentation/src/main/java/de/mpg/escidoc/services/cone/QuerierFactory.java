package de.mpg.escidoc.services.cone;

import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.PropertyReader;

public class QuerierFactory
{
    private static final Logger logger = Logger.getLogger(QuerierFactory.class);
    
    private static final String DEFAULT_QUERIER = MulgaraQuerier.class.getName();
    
    /**
     * Hide constructor for factory class.
     */
    private QuerierFactory()
    {
        
    }
    
    /**
     * Retrieve correct {@link Querier} instance defined by property.
     */
    public static Querier newQuerier()
    {
        String querier;
        try
        {
            querier = PropertyReader.getProperty("escidoc.cone.querier.class");
            
        }
        catch (Exception e) {
            logger.warn("Property \"escidoc.cone.querier.class\" not found, taking default querier class: " + DEFAULT_QUERIER);
            querier = DEFAULT_QUERIER;
        }
        try
        {
            Object querierImpl = Class.forName(querier).newInstance();
            if (querierImpl instanceof Querier)
            {
                return (Querier)querierImpl;
            }
            else
            {
                throw new RuntimeException("Instantiated querier class (" + querierImpl.getClass().getName() + ") does not implement the Querier interface.");
            }
        }
        catch (Exception e) {
            logger.error("Unable to instantiate querier.", e);
            return null;
        }
    }
}
