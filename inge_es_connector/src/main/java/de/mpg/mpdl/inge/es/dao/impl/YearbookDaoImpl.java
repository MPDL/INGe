package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.UserAccountDaoEs;
import de.mpg.mpdl.inge.es.dao.YearbookDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class YearbookDaoImpl extends ElasticSearchGenericDAOImpl<YearbookDbVO> implements
    YearbookDaoEs {

  private static final String indexName = PropertyReader.getProperty("yearbook_index_name");
  private static final String indexType = PropertyReader.getProperty("yearbook_index_type");
  private static final Class<YearbookDbVO> typeParameterClass = YearbookDbVO.class;


  public YearbookDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
