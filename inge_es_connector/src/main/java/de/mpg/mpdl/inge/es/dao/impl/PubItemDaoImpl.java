package de.mpg.mpdl.inge.es.dao.impl;

import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.util.PropertyReader;
import org.springframework.stereotype.Repository;

import java.util.Base64;

@Repository
public class PubItemDaoImpl extends ElasticSearchGenericDAOImpl<ItemVersionVO> implements PubItemDaoEs {

  private static final String indexName = PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_NAME);
  private static final String indexType = PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_TYPE);
  private static final Class<ItemVersionVO> typeParameterClass = ItemVersionVO.class;

  private static final String JOIN_FIELD_NAME = "joinField";

  private static final String[] SOURCE_EXCLUSIONS = new String[] {"joinField.name", "sort-metadata-creators-first",
      "sort-metadata-creators-compound", "sort-metadata-dates-by-category", "sort-metadata-dates-by-category-year"};

  public PubItemDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }


  @Override
  protected JsonNode applyCustomValues(ItemVersionVO item) {

    //ItemVersionVO itemToIndex = new ItemVersionVO(item);
    //Index files with correct link
    item.setFileLinks();
    ObjectNode node = (ObjectNode) super.applyCustomValues(item);



    node.putObject(JOIN_FIELD_NAME).put("name", "item");
    String[] creatorStrings = createSortCreatorsString(item);
    node.put("sort-metadata-creators-first", creatorStrings[0]);
    node.put("sort-metadata-creators-compound", creatorStrings[1]);
    String firstDate = createSortMetadataDates(item);
    if (firstDate != null) {
      node.put("sort-metadata-dates-by-category", firstDate);
      node.put("sort-metadata-dates-by-category-year", firstDate.substring(0, 4));
    }

    return node;
  }



  private String createSortMetadataDates(ItemVersionVO item) {
    if (item != null && item.getMetadata() != null) {
      if (item.getMetadata().getDatePublishedInPrint() != null) {
        return item.getMetadata().getDatePublishedInPrint();
      }
      if (item.getMetadata().getDatePublishedOnline() != null) {
        return item.getMetadata().getDatePublishedOnline();
      }
      if (item.getMetadata().getDateAccepted() != null) {
        return item.getMetadata().getDateAccepted();
      }
      if (item.getMetadata().getDateSubmitted() != null) {
        return item.getMetadata().getDateSubmitted();
      }
      if (item.getMetadata().getDateModified() != null) {
        return item.getMetadata().getDateModified();
      }
      if (item.getMetadata().getDateCreated() != null) {
        return item.getMetadata().getDateCreated();
      }

    }

    return null;


  }

  private String[] createSortCreatorsString(ItemVersionVO item) {
    StringBuilder sbCompound = new StringBuilder();
    String first = null;
    if (item != null && item.getMetadata() != null && item.getMetadata().getCreators() != null) {
      int i = 0;
      for (CreatorVO creator : item.getMetadata().getCreators()) {
        if (creator.getPerson() != null) {
          if (creator.getPerson().getFamilyName() != null) {
            sbCompound.append(creator.getPerson().getFamilyName());
          }
          if (creator.getPerson().getGivenName() != null) {
            sbCompound.append(" ");
            sbCompound.append(creator.getPerson().getGivenName());
          }

        } else if (creator.getOrganization() != null) {
          if (creator.getOrganization().getName() != null) {
            sbCompound.append(creator.getOrganization().getName());
          }
        }

        if (i == 0) {
          first = sbCompound.toString();
        }
        sbCompound.append(", ");
        i++;
      }
    }

    return new String[] {first, sbCompound.toString()};
  }


  @Override
  protected String[] getSourceExclusions() {
    return SOURCE_EXCLUSIONS;
  }


  public String createFulltext(String itemId, String fileId, byte[] file) throws IngeTechnicalException {
    try {

      ObjectNode rootObject = mapper.createObjectNode();
      rootObject.putObject("fileData").put("itemId", itemId).put("fileId", fileId).put("data", Base64.getEncoder().encodeToString(file));
      rootObject.putObject(JOIN_FIELD_NAME).put("name", "file").put("parent", itemId);


      IndexResponse indexResponse = client.getClient().index(i -> i
              .index(indexName)
              .routing(itemId)
              .pipeline("attachment")
              .id(itemId + "__" + fileId)
              .document(rootObject));


      /*
              IndexResponse indexResponse = client.getClient().prepareIndex().setIndex(indexName).setType(indexType).setRouting(itemId)
          .setPipeline("attachment").setId(itemId + "__" + fileId).setSource(mapper.writeValueAsBytes(rootObject), XContentType.JSON).get();
      */
      return indexResponse.id();


    } catch (Exception e) {
      throw new IngeTechnicalException(e);
    }


  }



}
