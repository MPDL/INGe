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

package de.mpg.mpdl.inge.aa.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

import de.mpg.mpdl.inge.util.PropertyReader;
import de.mpg.mpdl.inge.util.ResourceUtil;


/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class RSAEncoder {

  private RSAEncoder() {}

  public static String rsaEncrypt(String string) throws Exception {
    StringWriter resultWriter = new StringWriter();
    byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
    PublicKey pubKey = (PublicKey) readKeyFromFile(PropertyReader.getProperty(PropertyReader.INGE_AA_PUBLIC_KEY_FILE), true);
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    int blockSize = 245;
    for (int i = 0; i < bytes.length; i += blockSize) {
      byte[] result = cipher.doFinal(bytes, i, (i + blockSize < bytes.length ? blockSize : bytes.length - i));
      if (i > 0) {
        resultWriter.write("&");
      }
      resultWriter.write("auth=");
      resultWriter.write(URLEncoder.encode(new String(Base64.encodeBase64(result)), StandardCharsets.ISO_8859_1));
    }
    return resultWriter.toString();

  }

  public static String rsaDecrypt(String[] string) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrivateKey privateKey = (PrivateKey) readKeyFromFile(PropertyReader.getProperty(PropertyReader.INGE_AA_PRIVATE_KEY_FILE), false);
    Cipher cipher = Cipher.getInstance("RSA");
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    for (String part : string) {
      byte[] inArr = Base64.decodeBase64(part.getBytes(StandardCharsets.UTF_8));
      baos.write(cipher.doFinal(inArr));
      baos.flush();
    }

    return new String(baos.toByteArray(), StandardCharsets.UTF_8);

  }

  public static Key readKeyFromFile(String keyFileName, boolean publ) throws Exception {
    InputStream in = ResourceUtil.getResourceAsStream(keyFileName, RSAEncoder.class.getClassLoader());
    try (ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in))) {
      BigInteger m = (BigInteger) oin.readObject();
      BigInteger e = (BigInteger) oin.readObject();
      KeySpec keySpec;
      if (publ) {
        keySpec = new RSAPublicKeySpec(m, e);
      } else {
        keySpec = new RSAPrivateKeySpec(m, e);
      }
      KeyFactory fact = KeyFactory.getInstance("RSA");
      if (publ) {
        PublicKey pubKey = fact.generatePublic(keySpec);
        return pubKey;
      } else {
        PrivateKey privKey = fact.generatePrivate(keySpec);
        return privKey;
      }
    } catch (Exception e) {
      throw new RuntimeException("Error reading key from file", e);
    }
  }

}
