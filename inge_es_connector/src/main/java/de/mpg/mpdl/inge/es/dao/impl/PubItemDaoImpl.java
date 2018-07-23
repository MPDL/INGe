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
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class PubItemDaoImpl extends ElasticSearchGenericDAOImpl<ItemVersionVO> implements PubItemDaoEs {

  private static final String indexName = PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_NAME);
  private static final String indexType = PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_TYPE);
  private static final Class<ItemVersionVO> typeParameterClass = ItemVersionVO.class;

  private static final String JOIN_FIELD_NAME = "joinField";

  private static final String[] SOURCE_EXCLUSIONS = new String[] {"joinField.name", "sort-metadata-creators-compound"};

  public PubItemDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }


  @Override
  protected JsonNode applyCustomValues(ItemVersionVO item) {
    ObjectNode node = (ObjectNode) super.applyCustomValues(item);
    node.putObject(JOIN_FIELD_NAME).put("name", "item");
    node.put("sort-metadata-creators-compound", createSortCreatorsString(item));

    return node;
  }


  private String createSortCreatorsString(ItemVersionVO item) {
    StringBuilder sb = new StringBuilder();
    if (item != null && item.getMetadata() != null && item.getMetadata().getCreators() != null) {
      for (CreatorVO creator : item.getMetadata().getCreators()) {
        if (creator.getPerson() != null) {
          if (creator.getPerson().getFamilyName() != null) {
            sb.append(creator.getPerson().getFamilyName());
          }
          if (creator.getPerson().getGivenName() != null) {
            sb.append(" ");
            sb.append(creator.getPerson().getGivenName());
          }

        } else if (creator.getOrganization() != null) {
          if (creator.getOrganization().getName() != null) {
            sb.append(creator.getOrganization().getName());
          }
        }

        sb.append(", ");
      }
    }

    return sb.toString();
  }


  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
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
