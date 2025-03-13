package de.mpg.mpdl.inge.rest.development.web.spring;

import de.mpg.mpdl.inge.util.PropertyReader;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Custom WebSecurityConfigurerAdapter for development REST interface
 *
 * @author walter
 *
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter {

  //  @Override
  //  public void configure(AuthenticationManagerBuilder auth) throws Exception {
  //    auth.inMemoryAuthentication().withUser(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_USERNAME))
  //        .password("{noop}" + PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_PASSWORD)).roles("ADMIN");
  //  }

  //  @Override
  //  protected void configure(HttpSecurity http) throws Exception {
  //    http.authorizeRequests().antMatchers("/**").hasRole("ADMIN").and().httpBasic().and().csrf().disable();
  //  }

  @Bean
  public UserDetailsService userDetailsService() {
    User.UserBuilder userBuilder = User.withDefaultPasswordEncoder();
    return new InMemoryUserDetailsManager(Arrays.asList(userBuilder.username(PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_USERNAME))
        .password("{noop}" + PropertyReader.getProperty(PropertyReader.INGE_REST_DEVELOPMENT_ADMIN_PASSWORD)).roles("ADMIN").build()));
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.requestMatchers("/**").hasRole("ADMIN")).httpBasic();
    return http.build();
  }
}
