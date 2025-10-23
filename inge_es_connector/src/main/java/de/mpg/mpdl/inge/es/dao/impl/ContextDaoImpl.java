package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class ContextDaoImpl extends ElasticSearchGenericDAOImpl<ContextDbVO> implements ContextDaoEs {

  public static final String indexName = PropertyReader.getProperty(PropertyReader.INGE_INDEX_CONTEXT_NAME);
  private static final String indexType = PropertyReader.getProperty(PropertyReader.INGE_INDEX_CONTEXT_TYPE);
  private static final Class<ContextDbVO> typeParameterClass = ContextDbVO.class;


  public ContextDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }


  @Override
  protected String[] getSourceExclusions() {
    return null;
  }

}
