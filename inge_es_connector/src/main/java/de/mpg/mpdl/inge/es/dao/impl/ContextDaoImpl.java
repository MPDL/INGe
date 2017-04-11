package de.mpg.mpdl.inge.es.dao.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.dao.ContextDao;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class ContextDaoImpl extends ElasticSearchGenericDAOImpl<ContextVO> implements
    ContextDao<QueryBuilder> {

  private static final String indexName = PropertyReader.getProperty("context_index_name");
  private static final String indexType = PropertyReader.getProperty("context_index_type");
  private static final Class<ContextVO> typeParameterClass = ContextVO.class;


  public ContextDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
