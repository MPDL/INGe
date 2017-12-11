package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class UserAccountDaoImpl extends ElasticSearchGenericDAOImpl<AccountUserVO> implements UserAccountDaoEs {

  private static final String indexName = PropertyReader.getProperty("inge.index.user.name");
  private static final String indexType = PropertyReader.getProperty("inge.index.user.type");
  private static final Class<AccountUserVO> typeParameterClass = AccountUserVO.class;


  public UserAccountDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
