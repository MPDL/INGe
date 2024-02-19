package de.mpg.mpdl.inge.rest.spring;

import java.util.Collections;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfiguration {

  @Value("${inge.rest.api.description}")
  private String apiDescription;

  @Value("${inge.rest.service.url}")
  private String restServiceUrl;

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder().group("public").pathsToMatch("/**").packagesToScan("de.mpg.mpdl.inge.rest.web.controller").build();
  }

  @Bean
  public OpenAPI customOpenAPI() {

    OpenAPI openAPI = new OpenAPI();
    openAPI.info(new Info().title("PubMan REST API").description(apiDescription).version("1.0"));
    openAPI.servers(Collections.singletonList(new Server().url(restServiceUrl)));

    return openAPI;
  }
}
