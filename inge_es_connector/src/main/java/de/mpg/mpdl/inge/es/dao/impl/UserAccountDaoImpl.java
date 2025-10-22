package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class UserAccountDaoImpl extends ElasticSearchGenericDAOImpl<AccountUserDbVO> implements UserAccountDaoEs {

  public static final String indexName = PropertyReader.getProperty(PropertyReader.INGE_INDEX_USER_NAME);
  private static final String indexType = PropertyReader.getProperty(PropertyReader.INGE_INDEX_USER_TYPE);
  private static final Class<AccountUserDbVO> typeParameterClass = AccountUserDbVO.class;


  public UserAccountDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return null;
  }

}
