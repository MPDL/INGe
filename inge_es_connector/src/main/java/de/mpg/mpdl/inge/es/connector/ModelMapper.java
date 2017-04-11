package de.mpg.mpdl.inge.es.connector;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Repository
public class ModelMapper extends ObjectMapper {

  public ModelMapper() {
    super();
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

}
