package de.mpg.mpdl.inge.es.dao.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.OrganizationDaoEs;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class OrganizationDaoImpl extends ElasticSearchGenericDAOImpl<AffiliationVO> implements
    OrganizationDaoEs {

  private static final String indexName = PropertyReader.getProperty("organization_index_name");
  private static final String indexType = PropertyReader.getProperty("organization_index_type");
  private static final Class<AffiliationVO> typeParameterClass = AffiliationVO.class;


  public OrganizationDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
