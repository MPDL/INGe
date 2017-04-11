package de.mpg.mpdl.inge.es.dao.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.dao.OrganizationDao;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.OrganizationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class OrganizationDaoImpl extends ElasticSearchGenericDAOImpl<AffiliationVO> implements
    OrganizationDao<QueryBuilder> {

  private static final String indexName = PropertyReader.getProperty("organization_index_name");
  private static final String indexType = PropertyReader.getProperty("organization_index_type");
  private static final Class<AffiliationVO> typeParameterClass = AffiliationVO.class;


  public OrganizationDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
