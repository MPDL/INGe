import java.util.Date;

import javax.swing.text.html.parser.ContentModel;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.TaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.EventVO;
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

public class ESciDocSoapPerformanceTest
{
    private static long start = 0;
    private static long end = 0;
    private static long total = 0;
    private static long startCreate = 0;
    private static long endCreate = 0;
    private static long totalCreate = 0;
    private static long minCreate = 0;
    private static long maxCreate = 0;
    private static long startUpdate = 0;
    private static long endUpdate = 0;
    private static long totalUpdate = 0;
    private static long minUpdate = 0;
    private static long maxUpdate = 0;
    private static long startSubmit = 0;
    private static long endSubmit = 0;
    private static long totalSubmit = 0;
    private static long minSubmit = 0;
    private static long maxSubmit = 0;
    private static long startAssignVersionPid = 0;
    private static long endAssignVersionPid = 0;
    private static long totalAssignVersionPid = 0;
    private static long minAssignVersionPid = 0;
    private static long maxAssignVersionPid = 0;
    private static long startRelease = 0;
    private static long endRelease = 0;
    private static long totalRelease = 0;
    private static long minRelease = 0;
    private static long maxRelease = 0;
    
    public static void main (String[] args) throws Exception
    {
      Login login = new Login();
      String userHdl = login.loginPubManUser();

      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Start performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
      
      start = new Date().getTime();
      processItem(userHdl, false);
      end = new Date().getTime() - start;
      System.out.println("-- START ONE ITEM --");
      
      System.out.println("-- "+endCreate+"ms    --   Create");
      System.out.println("-- "+endUpdate+"ms    --   Update");
      System.out.println("-- "+endSubmit+"ms    --   Submit");
      System.out.println("-- "+endAssignVersionPid+"ms    --   Assign Version Pid");
      System.out.println("-- "+endRelease+"ms    --   Release");
      System.out.println("-- "+end+"ms    --   Whole process (create, update, submit, pid, release, retrieve)");
      
      System.out.println("-- END ONE ITEM --");
      System.out.println("");
      System.out.println("");
      System.out.println("");
      

      start = new Date().getTime();     
      int count = 1;
      
      System.out.println("-- START "+ count +" ITEMS --");
      for (int i = 0; i<count; i++)
      {
          processItem(userHdl, false);
      }
      end = new Date().getTime() - start;
      total += end;

      System.out.println("-- "+totalCreate+"ms    --   Create                           [min: "+minCreate+"] [max: "+maxCreate+"] [avg: "+totalCreate/count+"]");
      System.out.println("-- "+totalUpdate+"ms    --   Update                           [min: "+minUpdate+"] [max: "+maxUpdate+"] [avg: "+totalUpdate/count+"]");
      System.out.println("-- "+totalSubmit+"ms    --   Submit                           [min: "+minSubmit+"] [max: "+maxSubmit+"] [avg: "+totalSubmit/count+"]");
      System.out.println("-- "+totalAssignVersionPid+"ms    --   Assign Version Pid     [min: "+minAssignVersionPid+"] [max: "+maxAssignVersionPid+"] [avg: "+totalAssignVersionPid/count+"]");
      System.out.println("-- "+totalRelease+"ms    --   Release                         [min: "+minRelease+"] [max: "+maxRelease+"] [avg: "+totalRelease/count+"]");
      System.out.println("-- "+total+"ms    --   Whole process (create, update, submit, pid, release, retrieve)");
      
      System.out.println("-- END "+count+" ITEMS --");
      
      System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    
    public static void processItem(String user, boolean multiple) throws Exception
    {      
        XmlTransforming xmlTransforming = new XmlTransformingBean();
        ItemHandler itemHandler = ServiceLocator.getItemHandler(user);
        
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
        
        //Create item
        startCreate = new Date().getTime();
        String itemXml = itemHandler.create(xmlTransforming.transformToItem(item));
        endCreate = new Date().getTime() - startCreate;
        totalCreate += endCreate;
        if (endCreate > maxCreate) maxCreate = endCreate;
        if (minCreate ==0 || endCreate < minCreate) minCreate = endCreate;
        item = xmlTransforming.transformToPubItem(itemXml);
        
        //Update metadata
        item.getMetadata().setFreeKeywords(new TextVO("performance test"));
        EventVO event = new EventVO();
        event.setTitle(new TextVO("test event"));
        item.getMetadata().setEvent(event);
        item.getMetadata().setTotalNumberOfPages("xx");
        
        //Update item
        startUpdate = new Date().getTime();
        String updatedItem = itemHandler.update(item.getLatestVersion().getObjectIdAndVersion(),xmlTransforming.transformToItem(item));  
        endUpdate = new Date().getTime() - startUpdate;
        totalUpdate += endUpdate;
        if (endUpdate > maxUpdate) maxUpdate = endUpdate;
        if (minUpdate ==0 || endUpdate < minUpdate) minUpdate = endUpdate;
        item = xmlTransforming.transformToPubItem(updatedItem);
        
        //Submit item       
        TaskParamVO taskParam = new TaskParamVO(item.getModificationDate(), "performance test");
        startSubmit = new Date().getTime();
        itemHandler.submit(item.getLatestVersion().getObjectIdAndVersion(), xmlTransforming.transformToTaskParam(taskParam));
        endSubmit = new Date().getTime() - startSubmit;
        totalSubmit += endSubmit;
        if (endSubmit > maxSubmit) maxSubmit = endSubmit;
        if (minSubmit ==0 || endSubmit < minSubmit) minSubmit = endSubmit;

        itemXml = itemHandler.retrieve(item.getLatestVersion().getObjectIdAndVersion());
        item = xmlTransforming.transformToPubItem(itemXml);
    
        //Assign version pid
        String url = "http://dev-pubman.mpdl.mpg.de:8080/pubman/item/" + item.getLatestVersion().getObjectId();
        PidTaskParamVO pidParam = new PidTaskParamVO(item.getModificationDate(), url);
        String paramXml = xmlTransforming.transformToPidTaskParam(pidParam);       
        startAssignVersionPid = new Date().getTime();
        itemHandler.assignVersionPid(item.getLatestVersion().getObjectId(), paramXml);
        endAssignVersionPid = new Date().getTime() - startAssignVersionPid;
        totalAssignVersionPid += endAssignVersionPid;
        if (endAssignVersionPid > maxAssignVersionPid) maxAssignVersionPid = endAssignVersionPid;
        if (minAssignVersionPid ==0 || endAssignVersionPid < minAssignVersionPid) minAssignVersionPid = endAssignVersionPid;
        
        itemXml = itemHandler.retrieve(item.getLatestVersion().getObjectId());
        item = xmlTransforming.transformToPubItem(itemXml);
        
        //Assign object pid
        pidParam = new PidTaskParamVO(item.getModificationDate(), url);
        paramXml = xmlTransforming.transformToPidTaskParam(pidParam);  
        itemHandler.assignObjectPid(item.getLatestVersion().getObjectId(), paramXml);
        
        itemXml = itemHandler.retrieve(item.getLatestVersion().getObjectId());
        item = xmlTransforming.transformToPubItem(itemXml);
        
        //Release item
        taskParam = new TaskParamVO(item.getModificationDate(), "performance test");
        paramXml = xmlTransforming.transformToTaskParam(taskParam);
        startRelease = new Date().getTime();
        ServiceLocator.getItemHandler(user).release(item.getLatestVersion().getObjectIdAndVersion(), paramXml);
        endRelease = new Date().getTime() - startRelease;
        totalRelease += endRelease;
        if (endRelease > maxRelease) maxRelease = endRelease;
        if (minRelease ==0 || endRelease < minRelease) minRelease = endRelease;
    }
}
