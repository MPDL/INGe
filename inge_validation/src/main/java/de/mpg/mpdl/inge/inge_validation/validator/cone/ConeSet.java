package de.mpg.mpdl.inge.inge_validation.validator.cone;

import java.util.HashSet;

public enum ConeSet {
  ISO639_3_IDENTIFIER(new HashSet<String>()), //
  ISO639_3_TITLE(new HashSet<String>()), //
  DDC_TITLE(new HashSet<String>()), //
  MIME_TYPES_TITLE(new HashSet<String>()), //
  MPIPKS_TITLE(new HashSet<String>()), //
  MPIRG_TITLE(new HashSet<String>()), //
  MPIS_GROUPS_TITLE(new HashSet<String>()), //
  MPIS_PROJECTS_TITLE(new HashSet<String>());

  private HashSet<String> set;

  ConeSet(HashSet<String> set) {
    this.set = set;
  }

  public HashSet<String> set() {
    return this.set;
  }
}
