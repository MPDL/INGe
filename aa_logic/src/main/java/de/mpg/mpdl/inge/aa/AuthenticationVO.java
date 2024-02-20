/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.aa;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.mpg.mpdl.inge.aa.util.ShortContentHandler;


/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class AuthenticationVO implements Serializable {
  public enum Type
  {
    USER,
    GROUP,
    ATTRIBUTE
  }

  public static class Grant {
    private String key;
    private String value;

    public String toString() {
      if (null == this.value) {
        return this.key;
      } else {
        return this.key + "=" + this.value;
      }
    }

    public String getKey() {
      return this.key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getValue() {
      return this.value;
    }

    public void setValue(String value) {
      this.value = value;
    }

  }

  public static class Role {
    private String key;

    public String getKey() {
      return this.key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String toString() {
      return this.key;
    }
  }

  private class AuthenticationHandler extends ShortContentHandler {
    private Grant currentGrant = null;
    private Role currentRole = null;

    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      super.startElement(uri, localName, name, attributes);

      if ("authentication-object".equals(getLocalStack().toString())) {
        AuthenticationVO.this.tan = attributes.getValue("tan");
        AuthenticationVO.this.type = Type.valueOf(attributes.getValue("type"));
      } else if ("grant".equals(name)) {
        this.currentGrant = new Grant();
        this.currentGrant.setKey(attributes.getValue("key"));
      } else if ("role".equals(name)) {
        this.currentRole = new Role();
      }
    }

    @Override
    public void content(String uri, String localName, String name, String content) {
      if ("authentication-object/userid".equals(getLocalStack().toString())) {
        AuthenticationVO.this.userId = content;
      } else if ("authentication-object/username".equals(getLocalStack().toString())) {
        AuthenticationVO.this.username = content;
      } else if ("authentication-object/fullname".equals(getLocalStack().toString())) {
        AuthenticationVO.this.fullName = content;
      } else if ("grant".equals(name) && null != this.currentGrant) {
        this.currentGrant.setValue(content);
      } else if ("role".equals(name) && null != this.currentRole) {
        this.currentRole.setKey(content);
      }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      super.endElement(uri, localName, name);

      if ("grant".equals(name) && null != this.currentGrant) {
        AuthenticationVO.this.grants.add(this.currentGrant);
        this.currentGrant = null;
      } else if ("role".equals(name) && null != this.currentRole) {
        AuthenticationVO.this.roles.add(this.currentRole);
        this.currentRole = null;
      }

    }

  }

  private String tan;

  private Type type;
  private String userId;
  private String username;
  private String fullName;
  private final Set<Grant> grants = new HashSet<>();
  private final Set<Role> roles = new HashSet<>();
  private String token;

  public AuthenticationVO() {}

  public AuthenticationVO(String xml) {
    try {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(new InputSource(new StringReader(xml)), new AuthenticationHandler());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public String toXml() {
    StringWriter writer = new StringWriter();
    writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    writer.append("<authentication-object type=\"");
    writer.append(this.type.toString());
    writer.append("\" tan=\"");
    writer.append(this.tan);
    writer.append("\">\n");

    writer.append("\t<userid>");
    writer.append(this.userId);
    writer.append("</userid>\n");

    writer.append("\t<username>");
    writer.append(this.username);
    writer.append("</username>\n");

    writer.append("\t<fullname>");
    writer.append(this.fullName);
    writer.append("</fullname>\n");

    if (!this.roles.isEmpty()) {
      writer.append("\t<roles>\n");
      for (Role role : this.roles) {
        writer.append("\t\t<role>");
        writer.append(role.getKey());
        writer.append("</role>\n");
      }
      writer.append("\t</roles>\n");
    }

    if (!this.grants.isEmpty()) {
      writer.append("\t<grants>\n");
      for (Grant grant : this.grants) {
        if (null == grant.getValue()) {
          writer.append("\t\t<grant key=\"");
          writer.append(grant.getKey());
          writer.append("\"/>\n");
        } else {
          writer.append("\t\t<grant key=\"");
          writer.append(grant.getKey());
          writer.append("\">");
          writer.append(grant.getValue());
          writer.append("</grant>\n");
        }
      }
      writer.append("\t</grants>\n");
    }
    writer.append("</authentication-object>\n");

    return writer.toString();

  }

  public String toString() {
    return toXml();
  }

  public String getTan() {
    return this.tan;
  }

  public void setTan(String tan) {
    this.tan = tan;
  }

  public Type getType() {
    return this.type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Set<Grant> getGrants() {
    return this.grants;
  }

  public Set<Role> getRoles() {
    return this.roles;
  }

  public String getUserId() {
    return this.userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getToken() {
    return this.token;
  }

  public void setToken(String token) {
    this.token = token;
  }

}
