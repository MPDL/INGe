package test;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;

import de.mpg.escidoc.services.transformation.TransformationBean;

public class CommonPublicationsFormatsTest {

    public static TransformationBean trans;
    private final Logger logger = Logger.getLogger(CommonPublicationsFormatsTest.class);
    
    /**
     * Initializes the {@link TransformationBean}.
     */
    @BeforeClass
    public static void initTransformation()
    {
        trans = new TransformationBean(true);
    }
	
}
