package de.mpg.mpdl.inge.model.json.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonObjectMapperFactory {

  private final static ObjectMapper jsonObjectMapper = new ObjectMapper();

  static {
    jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    jsonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jsonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);

  }

  public static ObjectMapper getObjectMapper() {
    return jsonObjectMapper;
  }



}
