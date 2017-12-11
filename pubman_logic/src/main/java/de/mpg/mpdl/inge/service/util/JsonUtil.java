package de.mpg.mpdl.inge.service.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtil {

  private final static ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper();
  private final static ObjectMapper PRETTY_OBJECT_MAPPER = new ObjectMapper();

  static {
    PRETTY_OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);

  }

  public static String minifyJsonString(String prettyJsonString) throws JsonMappingException, JsonParseException, IOException {
    return DEFAULT_OBJECT_MAPPER.readValue(prettyJsonString, JsonNode.class).toString();
  }

  public static String prettifyJsonString(String jsonString) throws JsonMappingException, JsonParseException, IOException {
    return PRETTY_OBJECT_MAPPER.writeValueAsString(PRETTY_OBJECT_MAPPER.readValue(jsonString, JsonNode.class));

  }


  public static void main(String[] args) throws Exception {
    String json =
        "{\"match\":{\"metadata.title\":{\"query\":\"test\",\"operator\":\"OR\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":true,\"lenient\":false,\"zero_terms_query\":\"NONE\",\"boost\":1.0}}}";

    System.out.println(prettifyJsonString(json));


  }

}
