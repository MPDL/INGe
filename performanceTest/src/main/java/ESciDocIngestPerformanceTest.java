import java.util.Date;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.IngestHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorType;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;


public class ESciDocIngestPerformanceTest {
	
	private static Logger logger = Logger.getLogger(ESciDocIngestPerformanceTest.class);
	private long start;
	private long stop;
	private long maxIngest = 0;
	private long minIngest = Long.MAX_VALUE;
	private String itemXml;
	private IngestHandler ih;
	private String userHdl;
	
	
	public ESciDocIngestPerformanceTest(int amount, ItemVO item) throws Exception
	{
		Login login = new Login();
	    userHdl = login.login("roland", "dnalor");
	    ih = ServiceLocator.getIngestHandler(userHdl);
	    
	    XmlTransforming xmlt = new XmlTransformingBean();
	    itemXml = xmlt.transformToItem(item);
		
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Start performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		start = System.currentTimeMillis();
		
		for(int i = 0; i<amount; i++)
		{
			logger.info("-- Start Ingest Item no. " + i + " --");
			ingestItem();
			
		}
		
		
		stop = System.currentTimeMillis();
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		logger.info("Total: " + String.valueOf(stop-start) + " for " + amount + "items");
		logger.info("Minimum Ingestion Time: " + minIngest);
		logger.info("Maximum Ingestion Time: " + maxIngest);
		
		
	}
	

	private void ingestItem() throws Exception
	{

		long startIngest = System.currentTimeMillis(); 
		ih.ingest(itemXml);
		long stopIngest = System.currentTimeMillis();
		
		long ingestTime = stopIngest - startIngest;
		
		logger.info("Ingested item in time: " + ingestTime);
		
		maxIngest = Math.max(maxIngest, ingestTime);
		minIngest = Math.min(minIngest, ingestTime);

	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		new ESciDocIngestPerformanceTest(20, getItemVO());

	}
	

	private static PubItemVO getItemVO()
	{
		PubItemVO item = new PubItemVO();
        MdsPublicationVO md = new MdsPublicationVO();
        CreatorVO creator = new CreatorVO();
        PersonVO person = new PersonVO();
        OrganizationVO org = new OrganizationVO();
        org.setName(new TextVO("Test Org"));
        person.setGivenName("maria");
        person.setGivenName("muster");
        person.getOrganizations().add(org);
        creator.setPerson(person);
        creator.setRole(CreatorRole.AUTHOR);
        creator.setType(CreatorType.PERSON);
        md.getCreators().add(creator);
        md.setTitle(new TextVO("SOAP Performance Test Item " + new Date()));
        md.setGenre(Genre.ARTICLE);
        item.setMetadata(md);
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("escidoc:31126");
        item.setContext(ctx);
        item.setContentModel("escidoc:persistent4");
        item.setPublicStatus(State.PENDING);
        return item;
	}

}
