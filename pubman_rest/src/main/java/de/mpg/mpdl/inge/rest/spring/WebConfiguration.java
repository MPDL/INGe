package de.mpg.mpdl.inge.rest.spring;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.providers.ActuatorProvider;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWebMvcConfigurer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.geo.GeoModule;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.mvc.RepresentationModelProcessorInvoker;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import de.mpg.mpdl.inge.model.util.MapperFactory;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.rest"})
@Import({SwaggerConfig.class})
public class WebConfiguration implements WebMvcConfigurer {


  /*
  public WebConfiguration(SwaggerUiConfigParameters swaggerUiConfigParameters, SwaggerIndexTransformer swaggerIndexTransformer,
      Optional<ActuatorProvider> actuatorProvider) {
    super(swaggerUiConfigParameters, swaggerIndexTransformer, actuatorProvider);
  }
   */

  public void configureViewResolvers(ViewResolverRegistry registry) {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setViewClass(JstlView.class);
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");
    registry.viewResolver(viewResolver);

  }

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
    //First place: A Json converter using our default Jackson object mapper
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(MapperFactory.getObjectMapper());
    converters.add(0, converter);

    //Second place: A String converter which allows to return strings as json
    StringHttpMessageConverter smc = new StringHttpMessageConverter();
    smc.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
    converters.add(0, smc);
    //    System.out.println("Converters" + converters);

    //WebMvcConfigurer.super.extendMessageConverters(converters);
  }

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").exposedHeaders("Token").allowedMethods("OPTIONS", "HEAD", "GET", "POST", "PUT", "DELETE");
  }



  // @Override
  // public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
  // configurer.defaultContentType(MediaType.APPLICATION_JSON);
  // }



  /*
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
  
    registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
  */


}
