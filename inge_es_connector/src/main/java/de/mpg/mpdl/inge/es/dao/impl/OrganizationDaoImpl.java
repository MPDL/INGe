package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

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

}
