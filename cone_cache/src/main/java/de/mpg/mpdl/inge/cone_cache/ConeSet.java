package de.mpg.mpdl.inge.cone_cache;

import java.util.HashSet;

public enum ConeSet {
  DDC_TITLE(new HashSet<>()), //
  ISO639_3_IDENTIFIER(new HashSet<>()), //
  ISO639_3_TITLE(new HashSet<>()), //
  JEL_TITLE(new HashSet<>()), //
  JUS_TITLE(new HashSet<>()), //
  MPICC_PROJECTS_TITLE(new HashSet<>()), //
  MPING_TITLE(new HashSet<>()), //
  MPINP_TITLE(new HashSet<>()), //
  MPIPKS_TITLE(new HashSet<>()), //
  MPIRG_TITLE(new HashSet<>()), //
  MPIS_GROUPS_TITLE(new HashSet<>()), //
  MPIS_PROJECTS_TITLE(new HashSet<>()), //
  MPIWG_PROJECTS_TITLE(new HashSet<>());

  private final HashSet<String> set;

  ConeSet(HashSet<String> set) {
    this.set = set;
  }

  public HashSet<String> set() {
    return this.set;
  }
}
