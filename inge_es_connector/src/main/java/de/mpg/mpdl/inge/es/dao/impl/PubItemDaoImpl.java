package de.mpg.mpdl.inge.es.dao.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.dao.PubItemDao;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class PubItemDaoImpl extends ElasticSearchGenericDAOImpl<PubItemVO> implements
    PubItemDao<QueryBuilder> {

  private static final String indexName = PropertyReader.getProperty("item_index_name");
  private static final String indexType = PropertyReader.getProperty("item_index_type");
  private static final Class<PubItemVO> typeParameterClass = PubItemVO.class;


  public PubItemDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
