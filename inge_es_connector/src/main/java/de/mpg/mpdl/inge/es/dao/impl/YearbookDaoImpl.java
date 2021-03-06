package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.YearbookDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class YearbookDaoImpl extends ElasticSearchGenericDAOImpl<YearbookDbVO> implements YearbookDaoEs {

  private static final String indexName = PropertyReader.getProperty(PropertyReader.INGE_INDEX_YEARBOOK_NAME);
  private static final String indexType = PropertyReader.getProperty(PropertyReader.INGE_INDEX_YEARBOOK_TYPE);
  private static final Class<YearbookDbVO> typeParameterClass = YearbookDbVO.class;


  public YearbookDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

  @Override
  protected String[] getSourceExclusions() {
    return null;
  }

}
