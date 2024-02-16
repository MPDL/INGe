package de.mpg.mpdl.inge.cone_cache;

import java.util.HashSet;

public enum ConeSet {
  DDC_TITLE(new HashSet<String>()), //
  ISO639_3_IDENTIFIER(new HashSet<String>()), //
  ISO639_3_TITLE(new HashSet<String>()), //
  JEL_TITLE(new HashSet<String>()), //
  JUS_TITLE(new HashSet<String>()), //
  MPICC_PROJECTS_TITLE(new HashSet<String>()), //
  MPING_TITLE(new HashSet<String>()), //
  MPINP_TITLE(new HashSet<String>()), //
  MPIPKS_TITLE(new HashSet<String>()), //
  MPIRG_TITLE(new HashSet<String>()), //
  MPIS_GROUPS_TITLE(new HashSet<String>()), //
  MPIS_PROJECTS_TITLE(new HashSet<String>()), //
  MPIWG_PROJECTS_TITLE(new HashSet<String>());

  private final HashSet<String> set;

  ConeSet(HashSet<String> set) {
    this.set = set;
  }

  public HashSet<String> set() {
    return this.set;
  }
}
