package de.mpg.mpdl.inge.es.dao.impl;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class UserAccountDaoImpl extends ElasticSearchGenericDAOImpl<AccountUserVO> implements
    UserAccountDaoEs<QueryBuilder> {

  private static final String indexName = PropertyReader.getProperty("user_index_name");
  private static final String indexType = PropertyReader.getProperty("user_index_type");
  private static final Class<AccountUserVO> typeParameterClass = AccountUserVO.class;


  public UserAccountDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}