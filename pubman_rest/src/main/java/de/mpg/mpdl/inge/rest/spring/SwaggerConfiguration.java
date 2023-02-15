package de.mpg.mpdl.inge.rest.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration

public class SwaggerConfiguration {


  @Value("${inge.rest.api.description}")
  private String apiDescription;

  @Bean
  public GroupedOpenApi api() {

    return GroupedOpenApi.builder().group("public").pathsToMatch("/**").packagesToScan("de.mpg.mpdl.inge.rest.web.controller").build();
  }

  @Bean
  public OpenAPI springShopOpenAPI() {
    return new OpenAPI().info(new Info().title("PubMan REST API").description(apiDescription));
  }

}
