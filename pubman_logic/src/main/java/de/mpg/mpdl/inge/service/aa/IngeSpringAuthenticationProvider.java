package de.mpg.mpdl.inge.service.aa;

import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.util.PropertyReader;

public class IngeSpringAuthenticationProvider implements AuthenticationProvider {

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    /*
     * try { final URI url = new URL(PropertyReader.getProperty("auth.token.url")).toURI(); final
     * String input = "{\"userid\":\"" + authentication.getPrincipal() + "\",\"password\":\"" +
     * authentication.getCredentials() + "\"}";
     * 
     * HttpResponse resp = Request.Post(url).bodyString(input,
     * ContentType.APPLICATION_JSON).execute() .returnResponse();
     * 
     * if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) { throw new
     * BadCredentialsException("Please provide correct username and password"); }
     * 
     * //return resp.getHeaders("Token")[0].getValue();
     * 
     * } catch (AaException e) { throw e; } catch (Exception e) { throw new IngeServiceException(e);
     * }
     */
    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

}
