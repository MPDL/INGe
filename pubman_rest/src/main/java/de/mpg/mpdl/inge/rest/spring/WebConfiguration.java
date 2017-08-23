package de.mpg.mpdl.inge.rest.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
// class WebConfiguration extends WebMvcConfigurerAdapter {
public class WebConfiguration extends RepositoryRestMvcConfiguration {

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").exposedHeaders("Token")
        .allowedMethods("OPTIONS", "HEAD", "GET", "POST", "PUT", "DELETE");
    // super.addCorsMappings(registry);
  }

  // @Override
  // public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
  // ObjectMapper objectMapper = new ObjectMapper();
  //
  // objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
  // objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  // objectMapper.registerModule(new JavaTimeModule());
  //
  // MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  // converter.setObjectMapper(objectMapper);
  // converters.add(converter);
  // super.configureMessageConverters(converters);
  // }

  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setViewClass(JstlView.class);
    viewResolver.setPrefix("/WEB-INF/jsp/");
    viewResolver.setSuffix(".jsp");
    registry.viewResolver(viewResolver);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("swagger-ui.html").addResourceLocations(
        "classpath:/META-INF/resources/");

    registry.addResourceHandler("/webjars/**").addResourceLocations(
        "classpath:/META-INF/resources/webjars/");
    // super.addResourceHandlers(registry);
  }

}
