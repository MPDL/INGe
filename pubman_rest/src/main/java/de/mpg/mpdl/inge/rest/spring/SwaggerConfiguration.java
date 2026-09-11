package de.mpg.mpdl.inge.rest.spring;

import java.util.Collections;

import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SpringDocConfiguration.class, SpringDocConfigProperties.class, SwaggerUiConfigProperties.class, SwaggerUiOAuthProperties.class,
    MultipleOpenApiSupportConfiguration.class, SpringDocWebMvcConfiguration.class, SwaggerConfig.class, JacksonAutoConfiguration.class})
public class SwaggerConfiguration {

  @Bean
  public GroupedOpenApi api() {
    return GroupedOpenApi.builder().group("public").pathsToMatch("/**").packagesToScan("de.mpg.mpdl.inge.rest.web.controller").build();
  }

  @Bean
  public OpenAPI customOpenAPI() {

    OpenAPI openAPI = new OpenAPI();
    openAPI.info(new Info().title("PubMan REST API").description(PropertyReader.getProperty(PropertyReader.INGE_REST_API_DESCRIPTION))
        .version("1.0"));
    openAPI.servers(Collections.singletonList(new Server().url(PropertyReader.getProperty(PropertyReader.INGE_REST_SERVICE_URL))));

    return openAPI;
  }
}
