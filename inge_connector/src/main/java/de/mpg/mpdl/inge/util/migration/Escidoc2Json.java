package de.mpg.mpdl.inge.util.migration;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.escidoc.core.client.Authentication;
import de.escidoc.core.client.UserAccountHandlerClient;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.client.exceptions.application.security.AuthenticationException;
import de.escidoc.core.resources.aa.useraccount.Grant;
import de.escidoc.core.resources.aa.useraccount.Grants;
import de.escidoc.core.resources.aa.useraccount.UserAccount;
import de.escidoc.core.resources.aa.useraccount.UserAccountElements;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationResultVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.UserAttributeVO;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.common.xmltransforming.exceptions.UnmarshallingException;
import de.mpg.escidoc.services.common.xmltransforming.wrappers.ItemVOListWrapper;
import de.mpg.escidoc.services.util.AdminHelper;
import de.mpg.mpdl.inge.util.es.ElasticTransportClient;

public class Escidoc2Json {

  private static final String BASE = "https://coreservice.mpdl.mpg.de/ir/item/escidoc:";
  private static final String SEARCH =
      "https://coreservice.mpdl.mpg.de/srw/search/escidoc_all?query=escidoc.objecttype=item%20and%20escidoc.context.objid=escidoc:23042&maximumRecords=200&startRecord=2401";
  private static final String MPDL =
      "https://coreservice.mpdl.mpg.de/srw/search/escidoc_all?query=escidoc.objecttype%3D%22item%22+AND+escidoc.content-model.objid%3D%22escidoc%3Apersistent4%22+AND+%28%28escidoc.publication.creator.compound.organization-path-identifiers%3D%22escidoc%3Apersistent25%22%29+%29&q=ORGUNIT%3D%22Max+Planck+Digital+Library%2C+Max+Planck+Society||escidoc%3Apersistent25%22";
  private static final String PURE = "https://coreservice.mpdl.mpg.de/ir/items?maximumRecords=10";
  private static final String OUS = "https://coreservice.mpdl.mpg.de/oum/organizational-unit/";
  private static XmlTransforming xmlTransforming = new XmlTransformingBean();
  private static ObjectMapper mapper = new ObjectMapper();
  private static final String path2UserIds = "/home/frank/data/wilhelm/user_ids";
  private static Login login = new Login();



  public static void main(String... strings) {
    /*
     * PubItemVO pi = itemUrl2PubItemVO("1795753"); String id =
     * pi.getVersion().getObjectId().substring(pi.getVersion().getObjectId().indexOf(":"));
     * System.out.println(id);
     * 
     * index("pure", "pubitem", "pure" + id, pi);
     */
    // ElasticTransportClient.nestedQuery();
    // itemUrl2PubItemVO("1094626");
    long start = System.currentTimeMillis();
    try {
      String handle = login.loginSysAdmin();
      // users("/aa/user-account/escidoc:2235635", handle);
      // Files.lines(Paths.get(path2UserIds), StandardCharsets.UTF_8).forEach(line -> users(line,
      // handle));
      // contexts(handle);
      ous(handle);
      ou2aff("escidoc:persistent25");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /*
     * for (int i = 89862; i < 89871; i += 10) { System.out.println("ingesting records starting at "
     * + i); listUrl2PubItemList(i); }
     */
    System.out.println("ingest took: " + (System.currentTimeMillis() - start));
  }

  private static AffiliationVO ou2aff(String id) {
    try {
      URL url = new URL(OUS + id);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String ouAsString = reader.lines().collect(Collectors.joining("\n"));
      AffiliationVO ou = xmlTransforming.transformToAffiliation(ouAsString);
      ou.g
      mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, ou);
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private static PubItemVO itemUrl2PubItemVO(String id) {

    try {
      URL url = new URL(BASE + id);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String itemAsString = reader.lines().collect(Collectors.joining("\n"));
      PubItemVO item = xmlTransforming.transformToPubItem(itemAsString);

      System.out.println(item.getVersion().getObjectId());
      index(
          "pure",
          "pubitem",
          "pure"
              + item.getVersion().getObjectId()
                  .substring(item.getVersion().getObjectId().indexOf(":")), item);
      // return item;
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  private static void listUrl2PubItemList(int start) {
    try {
      URL url = new URL(PURE + "&startRecord=" + start);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String itemAsString = reader.lines().collect(Collectors.joining("\n"));
      ItemVOListWrapper wrapper =
          xmlTransforming.transformSearchRetrieveResponseToItemList(itemAsString);
      ArrayList<PubItemVO> items = (ArrayList<PubItemVO>) wrapper.getItemVOList();
      System.out.println("list length: " + items.size());
      for (PubItemVO item : items) {
        String id =
            item.getVersion().getObjectId()
                .substring(item.getVersion().getObjectId().indexOf(":") + 1);
        itemUrl2PubItemVO(id);
      }
      // bulk("pure", "pubitem", items);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (TechnicalException e) {
      e.printStackTrace();
    }
  }

  public static String index(String index, String type, String objId, Object o) {

    Client client = ElasticTransportClient.start();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    try {
      String thing = mapper.writeValueAsString(o);
      IndexResponse indexResponse =
          client.prepareIndex(index, type).setId(objId).setSource(thing).get();
      String id = indexResponse.getId();
      return id;
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      client.close();
    }
    return null;
  }

  public static void bulk(String index, String type, ArrayList<PubItemVO> things) {
    Client client = ElasticTransportClient.start();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
    // things.forEach((String thing) -> bulkRequestBuilder.add(client.prepareIndex(index,
    // type).setSource(thing)));
    for (PubItemVO pi : things) {
      try {
        String id =
            "pure"
                + pi.getVersion().getObjectId()
                    .substring(pi.getVersion().getObjectId().indexOf(":"));
        String json = mapper.writeValueAsString(pi);
        bulkRequestBuilder.add(client.prepareIndex(index, type).setId(id).setSource(json));
      } catch (JsonProcessingException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
    if (bulkResponse.hasFailures()) {
      System.out.println(bulkResponse.buildFailureMessage());
    }
  }
  
  public static void ous(String handle) {
	  
	    String ous_response = login.login2target(handle, "/oum/organizational-unit/escidoc:1942295");
	    try {
			 AffiliationVO ou = xmlTransforming.transformToAffiliation(ous_response);
			System.out.println(ou.getDefaultMetadata().getName());
		} catch (TechnicalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

  }

  public static void contexts(String handle) {

    String ctx_response = login.login2target(handle, "/ir/contexts");
    try {
  	  //URL url = new URL("https://coreservice.mpdl.mpg.de/ir/contexts");
      //BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      //String itemAsString = reader.lines().collect(Collectors.joining("\n"));

      List<ContextVO> ctxs =
          xmlTransforming.transformSearchRetrieveResponseToContextList(ctx_response);
      // ContextVO ctx = xmlTransforming.transformToContext(ctx_response);
      ContextVO ctx = ctxs.get(0);
      // mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, ctx);
      ctxs.forEach(c -> {String s = "pure_" + c.getReference().getObjectId().split(":")[1];
      System.out.println(index("pure_contexts", "context", s, c));});
    } catch (TechnicalException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void users(String target, String handle) {

    try {
      String usr_response = login.login2target(handle, target);
      AccountUserVO user = xmlTransforming.transformToAccountUser(usr_response);
      String grants_response = login.login2target(handle, target + "/resources/current-grants");
      List<GrantVO> grants = xmlTransforming.transformToGrantVOList(grants_response);
      String attrs_response = login.login2target(handle, target + "/resources/attributes");
      List<UserAttributeVO> attrs = xmlTransforming.transformToUserAttributesList(attrs_response);
      grants.forEach(g -> user.getGrants().add(g));
      attrs.forEach(a -> user.getAttributes().add(a));
      String objId = "pure_" + user.getReference().getObjectId().split(":")[1];
      //mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, user);
      System.out.println(index("user_accounts", "account", objId, user));

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
