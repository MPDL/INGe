package de.mpg.mpdl.inge.inge_validation.util;

public enum ValidationPoint {

  DEFAULT("default"), //
  ACCEPT_ITEM("accept_item"), //
  SUBMIT_ITEM("submit_item"), //
  EASY_SUBMISSION_STEP_3("easy_submission_step_3"), //
  EASY_SUBMISSION_STEP_4("easy_submission_step_4"), // 
  EASY_SUBMISSION_STEP_5("easy_submission_step_5");

  private String name;

  ValidationPoint(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

}
