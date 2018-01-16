package de.mpg.mpdl.inge.es.dao.impl;

import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.es.dao.ContextDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class ContextDaoImpl extends ElasticSearchGenericDAOImpl<ContextDbVO> implements ContextDaoEs {

  private static final String indexName = PropertyReader.getProperty("inge.index.context.name");
  private static final String indexType = PropertyReader.getProperty("inge.index.context.type");
  private static final Class<ContextDbVO> typeParameterClass = ContextDbVO.class;


  public ContextDaoImpl() {
    super(indexName, indexType, typeParameterClass);
  }

}
