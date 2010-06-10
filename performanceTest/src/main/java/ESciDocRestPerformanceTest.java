import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;


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
    
    private static String userHdl;
    private static String itemXml = "";
    
    public static void main (String[] args) throws Exception
    {
        start = new Date().getTime();     
        int count = 1000;
        
        Login login = new Login();
        userHdl = login.loginPubManUser();
        
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Start performance tests ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("-- START "+ count +" ITEMS --");
        
        for (int i = 0; i<count; i++)
        {
            itemXml = itemXmlOrg;
            createItem();
            updateItem();
            submitItem();
            retrieveItem();
            assignPID();
            retrieveItem();
            releaseItem();
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

        login.logout(userHdl);
    }
    
    private static void createItem() throws Exception
    {
        //Create item
        String location = "http://dev-coreservice.mpdl.mpg.de/ir/item";       
        String newTitle = "REST Performance Test Item " + new Date();
        itemXml = itemXml.replace("Titledummy", newTitle);
        
        HttpClient client = new HttpClient();
        PutMethod putMethod = new PutMethod(location);        
        putMethod.setRequestBody(itemXml);
        //putMethod.setRequestEntity(new StringRequestEntity(xmlTransforming.transformToItem(item), null, null));
        putMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  

        startCreate = new Date().getTime();
        int returnCode = client.executeMethod(putMethod);
        endCreate = new Date().getTime() - startCreate;
        totalCreate += endCreate;
        if (endCreate > maxCreate) maxCreate = endCreate;
        if (minCreate ==0 || endCreate < minCreate) minCreate = endCreate;
        
        //System.out.println("Create Response: "+returnCode);
        itemXml = putMethod.getResponseBodyAsString();

    }
    
    private static void updateItem() throws Exception
    {       
        itemXml = itemXml.replace("REST Performance Test Item", "REST Performance Test Item 2");
        
        //Update item       
        String location = "http://dev-coreservice.mpdl.mpg.de/ir/item/" + getObjectId(itemXml);
        HttpClient client = new HttpClient();
        PutMethod putMethod = new PutMethod(location);        
        putMethod.setRequestBody(itemXml);
        putMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  

        startUpdate = new Date().getTime();
        int returnCode = client.executeMethod(putMethod);
        endUpdate = new Date().getTime() - startUpdate;
        totalUpdate += endUpdate;
        if (endUpdate > maxUpdate) maxUpdate = endUpdate;
        if (minUpdate ==0 || endUpdate < minUpdate) minUpdate = endUpdate;   
        
        //System.out.println("Update Response: "+returnCode);
        itemXml = putMethod.getResponseBodyAsString();
    }
    
    
    private static void submitItem() throws Exception
    {
        String param = "<param last-modification-date='"+ getLastModificationDate(itemXml) +"'> "+
                            "<comment>test submit for performance test.</comment> "+
                        "</param>";
        //Submit item       
        String location = "http://dev-coreservice.mpdl.mpg.de/ir/item/" + getObjectId(itemXml) + "/submit";
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(location);        
        postMethod.setRequestBody(param);
        postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  
        
        startSubmit = new Date().getTime();
        int returnCode = client.executeMethod(postMethod);
        endSubmit = new Date().getTime() - startSubmit;
        totalSubmit += endSubmit;
        if (endSubmit > maxSubmit) maxSubmit = endSubmit;
        if (minSubmit ==0 || endSubmit < minSubmit) minSubmit = endSubmit;

        //System.out.println("Submit Response: "+returnCode);
    }
    
    private static void retrieveItem() throws Exception
    {
        //Update item       
        String location = "http://dev-coreservice.mpdl.mpg.de/ir/item/" + getObjectId(itemXml);
        HttpClient client = new HttpClient();
        GetMethod getMethod = new GetMethod(location);        
        getMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  
        int returnCode = client.executeMethod(getMethod);

        //System.out.println("Retrieve Response: "+returnCode);
        itemXml = getMethod.getResponseBodyAsString();
    }
    
    private static void assignPID() throws Exception
    {
        //Assign version pid
        String param = "<param last-modification-date='"+ getLastModificationDate(itemXml) +"'> "+
                            "<url>http://dev-pubman.mpdl.mpg.de:8080/pubman/item/" + getObjectId(itemXml) + "</url> "+
                        "</param>";
        
       String location = "http://dev-coreservice.mpdl.mpg.de/ir/item/" + getObjectId(itemXml) + "/assign-version-pid";
       HttpClient client = new HttpClient();
       PostMethod postMethod = new PostMethod(location);        
       postMethod.setRequestBody(param);
       postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  

        startAssignVersionPid = new Date().getTime();
        int returnCode = client.executeMethod(postMethod);
        endAssignVersionPid = new Date().getTime() - startAssignVersionPid;
        totalAssignVersionPid += endAssignVersionPid;
        if (endAssignVersionPid > maxAssignVersionPid) maxAssignVersionPid = endAssignVersionPid;
        if (minAssignVersionPid ==0 || endAssignVersionPid < minAssignVersionPid) minAssignVersionPid = endAssignVersionPid;
        
        //System.out.println("Version PID Response: "+returnCode);
        
        //Assign object pid
        retrieveItem();
        param = "<param last-modification-date='"+ getLastModificationDate(itemXml) +"'> "+
                            "<url>http://dev-pubman.mpdl.mpg.de:8080/pubman/item/" + getObjectId(itemXml) + "</url> "+
                        "</param>";
                    
        location = "http://dev-coreservice.mpdl.mpg.de/ir/item/" + getObjectId(itemXml) + "/assign-object-pid";
        client = new HttpClient();
        postMethod = new PostMethod(location);        
        postMethod.setRequestBody(param);
        postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl); 
        returnCode = client.executeMethod(postMethod);
        
        //System.out.println("Object PID Response: "+returnCode);
    }
    
    private static void releaseItem() throws Exception
    {
        //Release item
        String param = "<param last-modification-date='"+ getLastModificationDate(itemXml) +"'> "+
                            "<comment>test release for performance test.</comment> "+
                        "</param>";
        
        String location = "http://dev-coreservice.mpdl.mpg.de/ir/item/" + getObjectId(itemXml) + "/release";
        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod(location);        
        postMethod.setRequestBody(param);
        postMethod.setRequestHeader("Cookie", "escidocCookie=" + userHdl);  
        
        startRelease = new Date().getTime();
        int returnCode = client.executeMethod(postMethod);
        endRelease = new Date().getTime() - startRelease;
        totalRelease += endRelease;
        if (endRelease > maxRelease) maxRelease = endRelease;
        if (minRelease ==0 || endRelease < minRelease) minRelease = endRelease;
        
        //System.out.println("Release Response: "+returnCode);
    }
    
    private static String getObjectId(String itemXml)
    {
        String id = "";
        String [] itemXmlSplit = itemXml.split("/ir/item/");
        itemXmlSplit = itemXmlSplit[1].split("\"");
        id = itemXmlSplit[0];
        return id;
    }
    
    //last-modification-date="2010-06-09T13:25:09.322Z"
    private static String getLastModificationDate(String item)
    {
        String date = "";
        String [] itemXmlSplit = itemXml.split("last-modification-date=\"");
        itemXmlSplit = itemXmlSplit[1].split("\"");
        date = itemXmlSplit[0];
        return date;
    }
    
    private static String itemXmlOrg = "" +
    		"<?xml version='1.0' encoding='UTF-8'?> " +
            "<escidocItem:item  xmlns:escidocContext='http://www.escidoc.de/schemas/context/0.7' " +
            "                   xmlns:escidocContextList='http://www.escidoc.de/schemas/contextlist/0.7' " +
            "                   xmlns:escidocComponents='http://www.escidoc.de/schemas/components/0.9' " +
            "                   xmlns:escidocItem='http://www.escidoc.de/schemas/item/0.9' " +
            "                   xmlns:escidocItemList='http://www.escidoc.de/schemas/itemlist/0.9' " +
            "                   xmlns:escidocMetadataRecords='http://www.escidoc.de/schemas/metadatarecords/0.5' " +
            "                   xmlns:escidocRelations='http://www.escidoc.de/schemas/relations/0.3' " +
            "                   xmlns:escidocSearchResult='http://www.escidoc.de/schemas/searchresult/0.8' " +
            "                   xmlns:xlink='http://www.w3.org/1999/xlink' " +
            "                   xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
            "                   xmlns:prop='http://escidoc.de/core/01/properties/' " +
            "                   xmlns:srel='http://escidoc.de/core/01/structural-relations/' " +
            "                   xmlns:version='http://escidoc.de/core/01/properties/version/' " +
            "                   xmlns:release='http://escidoc.de/core/01/properties/release/' " +
            "                   xmlns:member-list='http://www.escidoc.de/schemas/memberlist/0.9' " +
            "                   xmlns:container='http://www.escidoc.de/schemas/container/0.8' " +
            "                   xmlns:container-list='http://www.escidoc.de/schemas/containerlist/0.8' " +
            "                   xmlns:struct-map='http://www.escidoc.de/schemas/structmap/0.4' " +
            "                   xmlns:mods-md='http://www.loc.gov/mods/v3' " +
            "                   xmlns:file='http://purl.org/escidoc/metadata/profiles/0.1/file' " +
            "                   xmlns:publication='http://purl.org/escidoc/metadata/profiles/0.1/publication' " +
            "                   xmlns:face='http://purl.org/escidoc/metadata/profiles/0.1/face' " +
            "                   xmlns:jhove='http://hul.harvard.edu/ois/xml/ns/jhove'> " +
            "   <escidocItem:properties> " +
            "       <srel:context xlink:href='/ir/context/escidoc:31126'/> " +
            "       <srel:content-model xlink:href='/cmm/content-model/escidoc:persistent4'/> " +
            "       <prop:version> " +
            "           <version:number>1</version:number> " +
            "           <version:date>2010-06-09T11:26:08.968Z</version:date> " +
            "           <version:status>pending</version:status> " +
            "           <srel:modified-by/> " +
            "           <version:comment/> " +
            "       </prop:version> " +
            "       <prop:latest-version> " +
            "          <version:number>1</version:number> " +
            "           <version:date>2010-06-09T11:26:15.515Z</version:date> " +
            "       </prop:latest-version> " +
            "       <prop:latest-release> " +
            "           <release:number>1</release:number> " +
            "           <release:date>2010-06-09T11:26:15.531Z</release:date> " +
            "       </prop:latest-release> " +
            "       <prop:content-model-specific> " +
            "           <local-tags/> " +
            "       </prop:content-model-specific> " +
            "   </escidocItem:properties> " +
            "   <escidocMetadataRecords:md-records> " +
            "       <escidocMetadataRecords:md-record name='escidoc'> " +
            "           <publication:publication xmlns:dc='http://purl.org/dc/elements/1.1/' " +
            "                                    xmlns:dcterms='http://purl.org/dc/terms/' " +
            "                                    xmlns:eterms='http://purl.org/escidoc/metadata/terms/0.1/' " +
            "                                    xmlns:person='http://purl.org/escidoc/metadata/profiles/0.1/person' " +
            "                                    xmlns:event='http://purl.org/escidoc/metadata/profiles/0.1/event' " +
            "                                    xmlns:source='http://purl.org/escidoc/metadata/profiles/0.1/source' " +
            "                                    xmlns:organization='http://purl.org/escidoc/metadata/profiles/0.1/organization' " +
            "                                    xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
            "                                    type='http://purl.org/escidoc/metadata/ves/publication-types/article'> " +
            "               <eterms:creator role='http://www.loc.gov/loc.terms/relators/AUT'> "+
            "                   <person:person> "+
            "                       <eterms:given-name>muster</eterms:given-name> "+
            "                       <organization:organization> "+
            "                           <dc:title>Test Org</dc:title> "+
            "                       </organization:organization> "+
            "                   </person:person> "+
            "              </eterms:creator> "+
            "               <dc:title>Titledummy</dc:title> "+
            "           </publication:publication> "+
            "       </escidocMetadataRecords:md-record> "+
            "   </escidocMetadataRecords:md-records> "+
            "   <escidocComponents:components/> "+
            "</escidocItem:item> " +
    		"";
}

