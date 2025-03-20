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

package de.mpg.mpdl.inge.service.pubman.importprocess.processor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.NoSuchElementException;

/**
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class BibtexProcessor extends FormatProcessor {

  private boolean init = false;
  private String[] items = null;
  private int counter = -1;
  private int length = -1;
  private byte[] originalData = null;

  /*
   * (non-Javadoc)
   *
   * @see java.util.Iterator#hasNext()
   */
  @Override
  public boolean hasNext() {
    if (!this.init) {
      this.initialize();
    }
    return (null != this.items && this.counter < this.items.length);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.Iterator#next()
   */
  @Override
  public String next() throws NoSuchElementException {
    if (!this.init) {
      this.initialize();
    }
    if (null != this.items && this.counter < this.items.length) {
      this.counter++;
      return this.items[this.counter - 1];
    } else {
      throw new NoSuchElementException("No more entries left");
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see java.util.Iterator#remove()
   */
  @Override
  public void remove() {
    throw new RuntimeException("Method not implemented");
  }

  private void initialize() {
    this.init = true;
    try {
      BufferedReader bufferedReader =
          new BufferedReader(new InputStreamReader(new FileInputStream(this.getSourceFile()), this.getEncoding()));
      String line = null;
      ArrayList<String> itemList = new ArrayList<>();
      StringWriter stringWriter = new StringWriter();
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      boolean first = true;

      while (null != (line = bufferedReader.readLine())) {

        byteArrayOutputStream.write(line.getBytes(this.getEncoding()));
        byteArrayOutputStream.write("\n".getBytes(this.getEncoding()));

        if (line.matches("^@[a-zA-Z]+\\{.*")) {
          if (first) {
            first = false;
          } else {
            itemList.add(stringWriter.toString());
          }
          stringWriter = new StringWriter();
        }

        stringWriter.write(line);
        stringWriter.write("\n");
      }

      itemList.add(stringWriter.toString());


      this.originalData = byteArrayOutputStream.toByteArray();

      this.items = itemList.toArray(new String[] {});

      this.length = this.items.length;

      this.counter = 0;

      bufferedReader.close();

    } catch (Exception e) {
      throw new RuntimeException("Error reading input stream", e);
    }

  }

  @Override
  public int getLength() {
    return this.length;
  }

  @Override
  public String getDataAsBase64() {
    if (null == this.originalData) {
      return null;
    }

    return Base64.getEncoder().encodeToString(this.originalData);
  }
}
