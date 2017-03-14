package de.mpg.mpdl.inge.pubman.web.searchNew.criterions.checkbox;

import java.util.LinkedHashMap;
import java.util.Map;

import de.mpg.mpdl.inge.pubman.web.searchNew.criterions.component.MapListSearchCriterion;

@SuppressWarnings("serial")
public class PublicationStatusListSearchCriterion extends MapListSearchCriterion<String> {



  public PublicationStatusListSearchCriterion() {
    super(getPublicationStatusMap());
    // TODO Auto-generated constructor stub
  }

  private static Map<String, String> getPublicationStatusMap() {


    Map<String, String> publicationStatusMap = new LinkedHashMap<String, String>();

    publicationStatusMap.put("not-specified", "not-specified");
    publicationStatusMap.put("submitted", "submitted");
    publicationStatusMap.put("accepted", "accepted");
    publicationStatusMap.put("published-online", "published-online");
    publicationStatusMap.put("published-in-print", "published-in-print");

    return publicationStatusMap;
  }



  @Override
  public String[] getCqlIndexes(Index indexName, String value) {
    switch (indexName) {

      case ESCIDOC_ALL: {
        return new String[] {"escidoc.publication-status"};
      }
      case ITEM_CONTAINER_ADMIN: {
        return new String[] {"\"/publication-status\""};
      }
    }

    return null;
  }



  @Override
  public String getCqlValue(Index indexName, String value) {

    return value;
  }



}
