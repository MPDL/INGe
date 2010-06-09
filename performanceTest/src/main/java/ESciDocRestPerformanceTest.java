import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.referenceobjects.ContextRO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.LockStatus;
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


public class ESciDocRestPerformanceTest
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
        start = new Date().getTime();     
        int count = 1;
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Start performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("-- START "+ count +" ITEMS --");
        
        createItem();
        
        System.out.println("-- "+totalCreate+"ms    --   Create                           [min: "+minCreate+"] [max: "+maxCreate+"] [avg: "+totalCreate/count+"]");
        //System.out.println("-- "+totalUpdate+"ms    --   Update                           [min: "+minUpdate+"] [max: "+maxUpdate+"] [avg: "+totalUpdate/count+"]");
        //System.out.println("-- "+totalSubmit+"ms    --   Submit                           [min: "+minSubmit+"] [max: "+maxSubmit+"] [avg: "+totalSubmit/count+"]");
        //System.out.println("-- "+totalAssignVersionPid+"ms    --   Assign Version Pid     [min: "+minAssignVersionPid+"] [max: "+maxAssignVersionPid+"] [avg: "+totalAssignVersionPid/count+"]");
        //System.out.println("-- "+totalRelease+"ms    --   Release                         [min: "+minRelease+"] [max: "+maxRelease+"] [avg: "+totalRelease/count+"]");
        //System.out.println("-- "+total+"ms    --   Whole process (create, update, submit, pid, release, retrieve)");
        
        System.out.println("-- END "+count+" ITEMS --");
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ End performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    
    private static void createItem() throws Exception
    {
        XmlTransforming xmlTransforming = new XmlTransformingBean();
        Login login = new Login();
        String userHdl = login.loginPubManUser();

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
        md.setTitle(new TextVO("REST Performance Test Item " + new Date()));
        md.setGenre(Genre.ARTICLE);
        item.setMetadata(md);
        ContextRO ctx = new ContextRO();
        ctx.setObjectId("/ir/context/escidoc:31126");
        item.setContext(ctx);
        item.setContentModel("/cmm/content-model/escidoc:persistent4");
        //item.setPublicStatus(State.valueOf(""));
        
        //Create item
        startCreate = new Date().getTime();
        String location = "http://dev-coreservice.mpdl.mpg.de:8080/ir/item";
        
        HttpClient client = new HttpClient();
        PutMethod putMethod = new PutMethod(location);        
        putMethod.setRequestBody(xmlTransforming.transformToItem(item));
        //putMethod.setRequestEntity(new StringRequestEntity(xmlTransforming.transformToItem(item), null, null));
        putMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  

        startCreate = new Date().getTime();
        int returnCode = client.executeMethod(putMethod);
        endCreate = new Date().getTime() - startCreate;
        totalCreate += endCreate;
        if (endCreate > maxCreate) maxCreate = endCreate;
        if (minCreate ==0 || endCreate < minCreate) minCreate = endCreate;
        
        System.out.println("Create Response: "+returnCode);
        String itemXml = putMethod.getResponseBodyAsString();

        login.logout(userHdl);
    }
    
}

