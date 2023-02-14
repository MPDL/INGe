package de.mpg.mpdl.inge.rest.spring;


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

    return GroupedOpenApi.builder().group("PubmanREST").pathsToMatch("/").build();
    /*
    return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build()
        .apiInfo(apiInfo());
    
     */
  }

  @Bean
  public OpenAPI springShopOpenAPI() {
    return new OpenAPI().info(new Info().title("PubMan REST API").description(apiDescription)
    //.version("v0.0.1")
    //.license(new License().name("Apache 2.0").url("http://springdoc.org"))
    );

  }



}
