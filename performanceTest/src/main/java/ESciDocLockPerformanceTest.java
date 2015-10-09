import java.util.Date;

import org.apache.log4j.Logger;

import de.escidoc.www.services.adm.AdminHandler;
import de.escidoc.www.services.om.IngestHandler;
import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
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


public class ESciDocLockPerformanceTest {
	
	private static Logger logger = Logger.getLogger(ESciDocLockPerformanceTest.class);
	private long start;
	private long stop;
	private long maxLock = 0;
	private long minLock = Long.MAX_VALUE;
	private long maxUnlock;
	private long minUnlock  = Long.MAX_VALUE;
	private ItemHandler ih;
	private String userHdl;
	private AdminHandler ah;
	
	private String itemId= "escidoc:178489";
	
	private XmlTransformingBean xmlt;
	
	
	
	public ESciDocLockPerformanceTest(int amount) throws Exception
	{
		Login login = new Login();
	    userHdl = login.loginPubManUser();
	    ih = ServiceLocator.getItemHandler(userHdl);
	    ah = ServiceLocator.getAdminHandler(userHdl);
	    
	    
	    String itemXml = ih.retrieve(itemId);
	    xmlt = new XmlTransformingBean();
	    ItemVO item = xmlt.transformToItem(itemXml);
	    
		
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Start performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		start = System.currentTimeMillis();
		
		Date lmd = item.getModificationDate();
		for(int i = 0; i<amount; i++)
		{
			logger.info("-- Start locking/unlocking Item "  + itemId + " try " + i +" --");
			lmd = lockItem(lmd);
			lmd = unlockItem(lmd);
			
			logger.info("");
			
		}
		
		/*
		long indexStart = System.currentTimeMillis();
		ah.reindex("false", null);
		long indexStop = System.currentTimeMillis();
		*/
		stop = System.currentTimeMillis();
		logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		logger.info("Total: " + String.valueOf(stop-start) + " ms for " + amount + " items");
		logger.info("Minimum Lock Time: " + minLock + " ms");
		logger.info("Maximum Lock Time: " + maxLock + " ms");
		logger.info("Minimum Unlock Time: " + minUnlock + " ms");
		logger.info("Maximum Unlock Time: " + maxUnlock + " ms");
		//logger.info("Indexing time: " + String.valueOf(indexStop-indexStart));
		
		
	}
	

	private Date lockItem(Date lmd) throws Exception
	{
		
		TaskParamVO tp = new TaskParamVO(lmd);    
	    String taskParamXml = xmlt.transformToTaskParam(tp);
		
		long startLock = System.currentTimeMillis(); 
		String returnXml = ih.lock(itemId, taskParamXml);
		long stopLock = System.currentTimeMillis();
		
		long lockTime = stopLock - startLock;
		
		//logger.info(returnXml);
		logger.info("Lock item in time: " + lockTime + " ms");
		
		maxLock = Math.max(maxLock, lockTime);
		minLock = Math.min(minLock, lockTime);
		
		return xmlt.transformToResult(returnXml).getLastModificationDate();

	}
	
	private Date unlockItem(Date lmd) throws Exception
	{
		
		TaskParamVO tp = new TaskParamVO(lmd);    
	    String taskParamXml = xmlt.transformToTaskParam(tp);
		
		long startUnLock = System.currentTimeMillis(); 
		String returnXml = ih.unlock(itemId, taskParamXml);
		long stopUnLock = System.currentTimeMillis();
		
		long unlockTime = stopUnLock - startUnLock;
		
		//logger.info(returnXml);
		logger.info("Unlock item in time: " + unlockTime+ " ms");
		
		maxUnlock = Math.max(maxUnlock, unlockTime);
		minUnlock = Math.min(minUnlock, unlockTime);
		
		return xmlt.transformToResult(returnXml).getLastModificationDate();

	}
	
	
	
	public static void main(String[] args) throws Exception
	{
		new ESciDocLockPerformanceTest(10);

	}
	


}
