package de.mpg.mpdl.inge.model.util;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MapperFactory {

  private final static ObjectMapper jsonObjectMapper = new ObjectMapper();
  
  private final static Mapper dozerMapper = DozerBeanMapperBuilder.buildDefault();

  static {
    jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);

  }

  public static ObjectMapper getObjectMapper() {
    return jsonObjectMapper;
  }
  
  
  public static Mapper getDozerMapper()
  {
    return dozerMapper;
  }



}
