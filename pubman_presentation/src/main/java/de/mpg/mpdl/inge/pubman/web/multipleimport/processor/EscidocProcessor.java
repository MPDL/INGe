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
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class EscidocProcessor extends FormatProcessor {
  private boolean init = false;
  private List<String> items = null;
  private int counter = -1;
  private int length = -1;
  private byte[] originalData = null;

  public EscidocProcessor() {}

  @Override
  public String getDataAsBase64() {
    if (null == this.originalData) {
      return null;
    }

    return Base64.getEncoder().encodeToString(this.originalData);
  }

  @Override
  public int getLength() {
    if (!this.init) {
      this.initialize();
    }
    return this.length;
  }

  @Override
  public boolean hasNext() {
    if (!this.init) {
      this.initialize();
    }
    return (this.counter < this.length);
  }

  @Override
  public String next() {
    if (!this.init) {
      this.initialize();
    }
    return this.items.get(this.counter++);
  }

  private void initialize() {
    if (null == this.getSourceFile()) {
      throw new RuntimeException("No input source");
    } else {
      this.init = true;
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      int read;
      byte[] buffer = new byte[2048];
      try {
        InputStream is = new FileInputStream(this.getSourceFile());
        while (-1 != (read = is.read(buffer))) {
          byteArrayOutputStream.write(buffer, 0, read);
        }
        is.close();

        this.originalData = byteArrayOutputStream.toByteArray();

        List<PubItemVO> itemList;
        String source = new String(this.originalData, StandardCharsets.UTF_8);
        if (source.contains("item-list")) {
          itemList = XmlTransformingService.transformToPubItemList(source);
        } else {
          itemList = new ArrayList<>();
          PubItemVO itemVO = XmlTransformingService.transformToPubItem(source);
          itemList.add(itemVO);
        }
        this.items = new ArrayList<>();
        for (ItemVO itemVO : itemList) {
          this.items.add(XmlTransformingService.transformToItem(itemVO));
        }
        this.counter = 0;
        this.length = this.items.size();
      } catch (Exception e) {
        throw new RuntimeException("Error reading input stream", e);
      }
    }
  }

  /**
   * Not implemented.
   */
  @Override
  @Deprecated
  public void remove() {
    throw new RuntimeException("Not implemented");
  }
}
