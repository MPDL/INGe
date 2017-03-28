package de.mpg.mpdl.inge.pubman.web.searchNew.criterions;

public class ElasticSearchIndexField {

  private String fieldname;

  private boolean nested = false;

  private String[] nestedPath;

  public ElasticSearchIndexField(String fieldname, boolean nested, String... nestedPath) {
    super();
    this.fieldname = fieldname;
    this.nested = nested;
    this.nestedPath = nestedPath;
  }

  public String[] getNestedPath() {
    return nestedPath;
  }

  public void setNestedPath(String[] nestedPath) {
    this.nestedPath = nestedPath;
  }

  public String getFieldname() {
    return fieldname;
  }

  public void setFieldname(String fieldname) {
    this.fieldname = fieldname;
  }

  public boolean isNested() {
    return nested;
  }

  public void setNested(boolean nested) {
    this.nested = nested;
  }

  public ElasticSearchIndexField(String fieldname, boolean nested) {
    super();
    this.fieldname = fieldname;
    this.nested = nested;
  }

  public ElasticSearchIndexField(String fieldname) {
    super();
    this.fieldname = fieldname;
  }



}
