package de.mpg.mpdl.inge.inge_validation.util;

public enum ValidationTypeBatchProcess {

  CONTEXT("context"), //
  EXTERNAL_REFERENCE_CONTENT_CATEGORY("external referenece content category"),
  FILE_AUDIENCE("file audience"),
  FILE_CONTENT_CATEGORY("file content category"),
  FILE_VISIBILITY("file visibility"),
  GENRE("genre"),
  KEYWORDS("keywords"), //
  LOCAL_TAGS("local tags"), //
  REVIEW_METHODE("review methode"),
  RELEASE("release"),
  REVISE("revise"),
  SOURCE_GENRE("source genre"),
  SOURCE_ID("source id"), //
  SOURCE_ISSUE("source issue"),
  SUBMIT("submit"),
  WITHDRAW("withdraw");
  

  private String name;

  ValidationTypeBatchProcess(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
