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

package de.mpg.mpdl.inge.pubman.web.util.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import jakarta.faces.bean.ManagedBean;

@ManagedBean(name = "IdentifierSuggest")
public class IdentifierSuggest {
  private static final String DIGITALAUTHORID = "DigitalAuthorID";
  private static final String GND = "GND";
  private static final String IDAT = "IDAT";
  private static final String IRIS = "IRIS";
  private static final String ISNI = "ISNI";
  private static final String MPIB = "MPIB";
  private static final String MPIKYB = "MPIKYB";
  private static final String MRAUTHORID = "MRAuthorID";
  private static final String ORCID = "ORCID";
  private static final String RESEARCHERID = "ResearcherID";
  private static final String SCOPUSAUTHORID = "ScopusAuthorID";

  private List<String> results = new ArrayList<>();

  public IdentifierSuggest() {
    // Get query from URL parameters
    final Map<String, String> parameters = FacesTools.getExternalContext().getRequestParameterMap();
    final String query = parameters.get("q");

    // Perform search request
    if (query != null) {
      if (IdentifierSuggest.DIGITALAUTHORID.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.DIGITALAUTHORID);
      }
      if (IdentifierSuggest.GND.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.GND);
      }
      if (IdentifierSuggest.IDAT.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.IDAT);
      }
      if (IdentifierSuggest.IRIS.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.IRIS);
      }
      if (IdentifierSuggest.ISNI.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.ISNI);
      }
      if (IdentifierSuggest.MPIB.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.MPIB);
      }
      if (IdentifierSuggest.MPIKYB.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.MPIKYB);
      }
      if (IdentifierSuggest.MRAUTHORID.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.MRAUTHORID);
      }
      if (IdentifierSuggest.ORCID.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.ORCID);
      }
      if (IdentifierSuggest.RESEARCHERID.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.RESEARCHERID);
      }
      if (IdentifierSuggest.SCOPUSAUTHORID.toUpperCase().startsWith(query.toUpperCase())) {
        this.results.add(IdentifierSuggest.SCOPUSAUTHORID);
      }
    }
  }

  public List<String> getResults() {
    return this.results;
  }

  public void setResults(List<String> results) {
    this.results = results;
  }
}
