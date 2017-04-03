package de.mpg.mpdl.inge.pubman.web.search.criterions;

public class ElasticSearchIndexField {

  private String fieldname;

  private boolean nested = false;

  private String[] nestedPath;

  public ElasticSearchIndexField(String fieldname, boolean nested, String... nestedPath) {
    this.fieldname = fieldname;
    this.nested = nested;
    this.nestedPath = nestedPath;
  }

  public String[] getNestedPath() {
    return this.nestedPath;
  }

  public void setNestedPath(String[] nestedPath) {
    this.nestedPath = nestedPath;
  }

  public String getFieldname() {
    return this.fieldname;
  }

  public void setFieldname(String fieldname) {
    this.fieldname = fieldname;
  }

  public boolean isNested() {
    return this.nested;
  }

  public void setNested(boolean nested) {
    this.nested = nested;
  }

  public ElasticSearchIndexField(String fieldname, boolean nested) {
    this.fieldname = fieldname;
    this.nested = nested;
  }

  public ElasticSearchIndexField(String fieldname) {
    this.fieldname = fieldname;
  }
}
