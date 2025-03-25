package de.mpg.mpdl.inge.rest.spring;

import de.mpg.mpdl.inge.model.util.MapperFactory;
import java.util.Arrays;
import java.util.List;

import de.mpg.mpdl.inge.util.PropertyReader;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.rest"})
public class WebConfiguration implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    // First place: A Json converter using our default Jackson object mapper
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(MapperFactory.getObjectMapper());
    converters.add(0, converter);

    // Second Place: For SpringDoc (otherwise Malformed (base64 encoded) api-docs)
    converters.add(0, new ByteArrayHttpMessageConverter());

    // Third place: A String converter which allows to return strings as json
    StringHttpMessageConverter smc = new StringHttpMessageConverter();
    smc.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
    converters.add(0, smc);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    CorsRegistration corsRegistration = registry.addMapping("/**").exposedHeaders("Token")
        .allowedMethods("OPTIONS", "HEAD", "GET", "POST", "PUT", "DELETE").allowCredentials(true);
    String allowed = PropertyReader.getProperty(PropertyReader.INGE_REST_ACCESS_CONTROL_ALLOWED_ORIGINS);
    if (allowed != null && !allowed.isEmpty()) {
      String[] allowedOrigins = allowed.split(",");
      corsRegistration.allowedOrigins(allowedOrigins);
    } else {
      corsRegistration.allowedOriginPatterns("*");
    }

  }
}
