package de.mpg.mpdl.inge.es.dao.impl;

import java.util.Base64;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class PubItemDaoImpl extends ElasticSearchGenericDAOImpl<ItemVersionVO> implements PubItemDaoEs {

  private static final String indexName = PropertyReader.getProperty("inge.index.item.name");
  private static final String indexType = PropertyReader.getProperty("inge.index.item.type");
  private static final Class<ItemVersionVO> typeParameterClass = ItemVersionVO.class;

  private static final String JOIN_FIELD_NAME = "joinField";

  public PubItemDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }


  @Override
  protected JsonNode applyCustomValues(ItemVersionVO item) {
    JsonNode node = super.applyCustomValues(item);
    ((ObjectNode) node).putObject(JOIN_FIELD_NAME).put("name", "item");
    return node;
  }


  /**
   * 
   * @param indexName
   * @param indexType
   * @param id
   * @param vo
   * @return {@link String}
   */
  public String createFulltext(String itemId, String fileId, byte[] file) throws IngeTechnicalException {
    try {

      ObjectNode rootObject = mapper.createObjectNode();
      rootObject.putObject("fileData").put("itemId", itemId).put("fileId", fileId).put("data", Base64.getEncoder().encodeToString(file));
      rootObject.putObject(JOIN_FIELD_NAME).put("name", "file").put("parent", itemId);

      IndexResponse indexResponse = client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setRouting(itemId)
          .setPipeline("attachment").setId(itemId + "__" + fileId).setSource(mapper.writeValueAsBytes(rootObject), XContentType.JSON).get();
      return indexResponse.getId();

    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }


  }

}
