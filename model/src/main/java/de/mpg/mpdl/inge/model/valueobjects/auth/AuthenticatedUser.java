package de.mpg.mpdl.inge.model.valueobjects.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuthenticatedUser implements UserDetails {

  private final String userid;
  private final String token;
  private final Collection<? extends GrantedAuthority> authorities;

  public AuthenticatedUser(String userid, String token,
      Collection<? extends GrantedAuthority> authorities) {
    this.userid = userid;
    this.token = token;
    this.authorities = authorities;
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return userid;
  }

  public String getToken() {
    return token;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }

}
