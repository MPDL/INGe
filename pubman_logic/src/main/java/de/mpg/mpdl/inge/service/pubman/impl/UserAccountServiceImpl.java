package de.mpg.mpdl.inge.service.pubman.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.model.referenceobjects.AccountUserRO;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.UserAttributeVO;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.exceptions.ValidationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.services.IngeServiceException;
import de.mpg.mpdl.inge.util.PropertyReader;

@Service
public class UserAccountServiceImpl implements UserAccountService {

  @Override
  public AccountUserVO create(AccountUserVO object, String userToken) throws IngeServiceException,
      AaException, ValidationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AccountUserVO update(AccountUserVO object, String userToken) throws IngeServiceException,
      AaException, ValidationException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(String id, String userToken) throws IngeServiceException, AaException {
    // TODO Auto-generated method stub

  }

  @Override
  public AccountUserVO get(String id, String userToken) throws IngeServiceException, AaException {
    try {
      final URL url = new URL(PropertyReader.getProperty("auth.users.url") + "/" + id);
      final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Authorization", userToken);

      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode rawUser = mapper.readTree(conn.getInputStream());
      conn.disconnect();

      return jsonToAccountUser(rawUser);
    } catch (final MalformedURLException e) {
      throw new IngeServiceException("Could not get user", e);
    } catch (final JsonParseException e) {
      throw new IngeServiceException("Could not get user", e);
    } catch (final IOException e) {
      throw new IngeServiceException("Could not get user", e);
    }

  }

  @Override
  public SearchRetrieveResponseVO<AccountUserVO> search(SearchRetrieveRequestVO<QueryBuilder> srr,
      String userToken) throws IngeServiceException, AaException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AccountUserVO get(String userToken) throws IngeServiceException, AaException {

    try {
      DecodedJWT jwt = JWT.decode(userToken);
      String userId = jwt.getSubject();
      return get(userId, userToken);

    } catch (JWTDecodeException e) {
      throw new AaException("Could not decode token", e);
    }



  }

  @Override
  public String login(String username, String password) throws IngeServiceException, AaException {
    HttpURLConnection conn = null;
    try {
      final URL url = new URL(PropertyReader.getProperty("auth.token.url"));
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");

      final String input = "{\"userid\":\"" + username + "\",\"password\":\"" + password + "\"}";

      final OutputStream os = conn.getOutputStream();
      os.write(input.getBytes());
      os.flush();

      if (conn.getResponseCode() != 200) {
        throw new AaException("Could not login, Please provide correct username and password");
      }
      final String token = conn.getHeaderField("Token");

      return token;
    } catch (AaException e) {
      throw e;
    } catch (Exception e) {
      throw new IngeServiceException(e);
    } finally {
      conn.disconnect();
    }


  }



  private static AccountUserVO jsonToAccountUser(JsonNode rawUser) {
    final AccountUserVO accountUser = new AccountUserVO();

    final AccountUserRO userRO = new AccountUserRO();
    userRO.setObjectId(rawUser.path("exid").asText());
    userRO.setTitle(rawUser.path("lastName").asText() + ", " + rawUser.path("firstName").asText());

    accountUser.setReference(userRO);
    accountUser.setUserid(rawUser.path("sub").asText());
    final List<UserAttributeVO> attributes = new ArrayList<UserAttributeVO>();
    final UserAttributeVO email = new UserAttributeVO();
    email.setName("email");
    email.setValue(rawUser.path("email").asText());
    final UserAttributeVO ou = new UserAttributeVO();
    ou.setName("o");
    ou.setValue(rawUser.path("ouid").asText());
    attributes.add(email);
    attributes.add(ou);
    accountUser.setAttributes(attributes);
    accountUser.setActive(rawUser.path("active").asBoolean());
    accountUser.setName(rawUser.path("lastName").asText() + ", "
        + rawUser.path("firstName").asText());



    // get all user-grants

    final JsonNode grants = rawUser.path("grants");

    if (grants.isArray()) {
      for (final JsonNode grant : grants) {

        final GrantVO grantVo = new GrantVO();
        grantVo.setGrantedTo(rawUser.path("exid").asText());
        grantVo.setGrantType("");
        if (grant.path("targetId").asText().contains("all")) {

        } else {
          grantVo.setObjectRef(grant.path("targetId").asText());
          final String roleName = grant.path("role").path("name").asText();
          grantVo.setRole(roleName);
          accountUser.getGrants().add(grantVo);
          accountUser.getGrantsWithoutAudienceGrants().add(grantVo);

        }

      }
    }
    return accountUser;

  }



}
