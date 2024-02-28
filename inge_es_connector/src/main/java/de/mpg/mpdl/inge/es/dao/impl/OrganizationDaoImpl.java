package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class OrganizationDaoImpl extends ElasticSearchGenericDAOImpl<AffiliationDbVO> implements OrganizationDaoEs {

  private static final String indexName = PropertyReader.getProperty(PropertyReader.INGE_INDEX_ORGANIZATION_NAME);
  private static final String indexType = PropertyReader.getProperty(PropertyReader.INGE_INDEX_ORGANIZATION_TYPE);
  private static final Class<AffiliationDbVO> typeParameterClass = AffiliationDbVO.class;


  public OrganizationDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return null;
  }

  @Override
  protected JsonNode applyCustomValues(AffiliationDbVO aff) {

    //ItemVersionVO itemToIndex = new ItemVersionVO(item);
    //Index files with correct link
    ObjectNode node = (ObjectNode) super.applyCustomValues(aff);

    ArrayNode namePath = node.putArray("namePath");
    ArrayNode idPath = node.putArray("idPath");

    AffiliationDbVO parentAff = aff;
    while (parentAff != null) {
      namePath.add(parentAff.getName());
      idPath.add(parentAff.getObjectId());
      parentAff = (AffiliationDbVO) parentAff.getParentAffiliation();

    }

    return node;
  }

}
