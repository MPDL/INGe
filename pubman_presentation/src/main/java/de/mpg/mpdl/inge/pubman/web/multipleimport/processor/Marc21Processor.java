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

package de.mpg.mpdl.inge.pubman.web.multipleimport.processor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;

/*
 * takes Marc21 binary data and returns chunks of single MARRCXML records This is kind of cheating,
 * but FormatProcessor has to return String
 *
 * @author Stefan Krause, Editura GmbH & Co. KG (initial creation)
 *
 * @author $Author: skrause $ (last modification)
 *
 * @version $Revision: 261 $ $LastChangedDate: 2013-04-30 20:57:29 +0200 (Di, 30 Apr 2013) $
 */

public class Marc21Processor extends FormatProcessor {
  private boolean isInitialized = false;
  private MarcXmlProcessor marcxmlprocessor;

  private void initialize() {
    MarcStreamReader reader;
    ByteArrayOutputStream result;
    MarcXmlWriter writer;
    try {
      final InputStream is = new FileInputStream(this.getSourceFile());
      if (this.encoding == null || this.encoding.trim().isEmpty() || this.encoding.trim().equals("*")) {
        reader = new MarcStreamReader(is);
      } else {
        reader = new MarcStreamReader(is, this.encoding);
      }

      result = new ByteArrayOutputStream();

      writer = new MarcXmlWriter(result, "UTF-8", true);

      while (reader.hasNext()) {
        final Record record = reader.next();
        writer.write(record);
      }

      is.close();
      writer.close();
    } catch (final Exception e1) {
      throw new RuntimeException("Error while reading marc21 file");
    }

    try {
      // nasty workaround to get rid of the namespace issues, has to be fixed, Stf, 2013-03-22
      final String xml = new String(result.toString(StandardCharsets.UTF_8).replaceAll("xmlns=\"http://www.loc.gov/MARC21/slim\"", "")
          .replaceAll("<collection", "<collection xmlns=\"http://www.loc.gov/MARC21/slim\""));
      this.marcxmlprocessor = new MarcXmlProcessor();

      final File f = File.createTempFile("marcXml", "xml");
      final FileOutputStream fos = new FileOutputStream(f);
      IOUtils.write(xml, fos, "UTF-8");
      this.marcxmlprocessor.setSourceFile(f);
    } catch (final Exception e) {
      throw new RuntimeException("Can't encode the result to UTF-8", e);
    }

    result = null;
    writer = null;
    reader = null;

    this.isInitialized = true;

  }

  @Override
  public boolean hasNext() {
    if (!this.isInitialized) {
      this.initialize();
    }
    return this.marcxmlprocessor.hasNext();
  }

  @Override
  public String next() {
    if (!this.isInitialized) {
      this.initialize();
    }
    return this.marcxmlprocessor.next();
  }

  @Override
  @Deprecated
  public void remove() {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public int getLength() {
    if (!this.isInitialized) {
      this.initialize();
    }
    return this.marcxmlprocessor.getLength();
  }

  @Override
  public String getDataAsBase64() {
    if (this.getSourceFile() == null) {
      return null;
    }

    try {
      final InputStream is = new FileInputStream(this.getSourceFile());
      final String base64 = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
      is.close();
      return base64;
    } catch (final Exception e) {
      throw new RuntimeException("Can't read input stream", e);
    }
  }
}
