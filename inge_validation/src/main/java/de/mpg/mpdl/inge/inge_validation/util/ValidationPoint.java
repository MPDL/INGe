package de.mpg.mpdl.inge.inge_validation.util;

public enum ValidationPoint {

  SIMPLE("import"), //
  SAVE("save"), //
  STANDARD("accept, submit, release"), //
  EASY_SUBMISSION_STEP_3("easy_submission_step_3"), //
  EASY_SUBMISSION_STEP_4("easy_submission_step_4");

  private final String name;

  ValidationPoint(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
