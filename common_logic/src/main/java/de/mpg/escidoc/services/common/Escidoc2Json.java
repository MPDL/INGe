package de.mpg.escidoc.services.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;

public class Escidoc2Json {

  private static final String BASE = "https://coreservice.mpdl.mpg.de/ir/item/escidoc:";
  private static XmlTransforming xmlTransforming = new XmlTransformingBean();
  private static ObjectMapper mapper = new ObjectMapper();

  public static void main(String... strings) {
	  /*
    PubItemVO pi = itemUrl2PubItemVO("1795753");
    String id = pi.getVersion().getObjectId().substring(pi.getVersion().getObjectId().indexOf(":"));
    System.out.println(id);

    index("pure", "pubitem", "pure" + id, pi);
    */
    ElasticTransportClient.nestedQuery();
  }

  private static PubItemVO itemUrl2PubItemVO(String id) {

    try {
      URL url = new URL(BASE + id);
      BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
      String itemAsString = reader.lines().collect(Collectors.joining("\n"));
      PubItemVO item = xmlTransforming.transformToPubItem(itemAsString);

      System.out.println(item.getVersion().getObjectId());
      return item;
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

}
