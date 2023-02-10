package de.mpg.mpdl.inge.model.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

public class MapperFactory {

  private static final ObjectMapper jsonObjectMapper = new ObjectMapper();

  private static final Mapper dozerMapper = DozerBeanMapperBuilder.buildDefault();

  static {

    jsonObjectMapper.registerModule(new JavaTimeModule());
    jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    jsonObjectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

  }

  public static ObjectMapper getObjectMapper() {
    return jsonObjectMapper;
  }


  public static Mapper getDozerMapper() {
    return dozerMapper;
  }



}
