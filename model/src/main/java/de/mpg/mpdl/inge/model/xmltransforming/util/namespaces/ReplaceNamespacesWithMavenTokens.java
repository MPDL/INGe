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

package de.mpg.mpdl.inge.model.xmltransforming.util.namespaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * TODO Description
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ReplaceNamespacesWithMavenTokens {

  private static String POM_PATH = "../pom.xml";

  private ReplaceNamespacesWithMavenTokens() {}

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    if (0 == args.length) {
      System.out.println("Usage: java ReplaceNamespacesWithMavenTokens xml1 [[,xml2] ...]");
      System.out.println("Optional: -Dpom=path_to_parent_pom");
      System.out
          .println("This will replace all namespaces that can be found in the input xml file with available tokens from the pom.xml.");
    } else {
      if (null != System.getProperty(PropertyReader.POM)) {
        POM_PATH = System.getProperty(PropertyReader.POM);
      }

      File pom = new File(POM_PATH);
      Map<String, String> namespaces = new HashMap<>();

      BufferedReader bufferedReader = new BufferedReader(new FileReader(pom));
      String line;
      Pattern pattern = Pattern.compile("\\s*<(xsd.[^>]+)>([^<]+)</xsd.[^>]+>");
      while (null != (line = bufferedReader.readLine())) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
          namespaces.put(matcher.group(2), matcher.group(1));
        }
      }

      for (String arg : args) {
        File file = new File(arg);
        String fileName = file.getName();
        File folder = file.getParentFile();
        File tempFile = File.createTempFile(fileName, ".tmp", folder);
        FileWriter fileWriter = new FileWriter(tempFile);
        bufferedReader = new BufferedReader(new FileReader(file));
        while (null != (line = bufferedReader.readLine())) {
          for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            line = line.replace("=\"" + entry.getKey() + "\"", "=\"${" + entry.getValue() + "}\"");
          }
          fileWriter.write(line);
          fileWriter.write("\n");
        }
        fileWriter.close();
        bufferedReader.close();
        File backup = new File(folder, "_" + fileName);
        file.renameTo(backup);
        tempFile.renameTo(file);
        backup.delete();
      }
    }
  }
}
