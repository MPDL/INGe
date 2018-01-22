package de.mpg.mpdl.inge.rest.development.web.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Custom WebSecurityConfigurerAdapter for development REST interface 
 * 
 * @author walter
 *
 */
@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication().withUser(PropertyReader.getProperty("inge.rest.development.admin.username"))
        .password(PropertyReader.getProperty("inge.rest.development.admin.password")).roles("ADMIN");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/**").hasRole("ADMIN").and().httpBasic().and().csrf().disable();
  }
}
