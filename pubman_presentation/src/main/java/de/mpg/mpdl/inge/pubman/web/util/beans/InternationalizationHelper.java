package de.mpg.mpdl.inge.pubman.web.util.beans;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.EventVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.MdsFileVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.pubman.web.util.FacesTools;
import de.mpg.mpdl.inge.pubman.web.util.LanguageChangeObserver;
import de.mpg.mpdl.inge.util.PropertyReader;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.faces.model.SelectItem;

/**
 * Class for Internationalization settings.
 *
 * @author: Tobias Schraut, created 04.07.2007
 * @version: $Revision$ $LastChangedDate$ Revised by ScT: 20.08.2007
 */
@ManagedBean(name = "InternationalizationHelper")
@SessionScoped
@SuppressWarnings("serial")
public class InternationalizationHelper implements Serializable {
  private static final Logger logger = LogManager.getLogger(InternationalizationHelper.class);

  public enum SelectMultipleItems
  {
    SELECT_ITEMS, SELECT_ALL, DESELECT_ALL, SELECT_VISIBLE
  }

  public enum SelectComponentAvailability
  {
    SELECT_HAS_FILES, SELECT_HAS_LOCATORS, SELECT_HAS_COMPONENTS, SELECT_HAS_NO_COMPONENTS, SELECT_HAS_FILES_ONLY, SELECT_HAS_LOCATORS_ONLY
  }

  public enum SelectComponentVisibility
  {
    SELECT_COMPONENT_PRIVATE, SELECT_COMPONENT_PUBLIC, SELECT_COMPONENT_RESTRICTED
  }

  public static final String LABEL_BUNDLE = "Label";
  public static final String MESSAGES_BUNDLE = "Messages";

  private String locale = "en";
  private String homeContent = "n/a";

  private SelectItem NO_ITEM_SET = null;

  private Locale userLocale;

  private List<LanguageChangeObserver> languageChangeObservers = new ArrayList<>();

  public InternationalizationHelper() {
    this.userLocale = FacesTools.getExternalContext().getRequestLocale();

    Iterator<Locale> supportedLocales = FacesTools.getCurrentInstance().getApplication().getSupportedLocales();

    boolean found = false;
    while (supportedLocales.hasNext()) {
      Locale supportedLocale = supportedLocales.next();
      if (null != this.userLocale && supportedLocale.getLanguage().equals(this.userLocale.getLanguage())) {
        found = true;
        break;
      }
    }

    if (!found) {
      this.userLocale = new Locale("en");
    }

    this.locale = this.userLocale.getLanguage();

    this.NO_ITEM_SET = new SelectItem("", this.getLabel("EditItem_NO_ITEM_SET"));
  }

  private String getSelectedLabelBundle() {
    return InternationalizationHelper.LABEL_BUNDLE + "_" + this.userLocale.getLanguage();
  }

  private String getSelectedMessagesBundle() {
    return InternationalizationHelper.MESSAGES_BUNDLE + "_" + this.userLocale.getLanguage();
  }

  public void changeLanguage(ValueChangeEvent event) {
    FacesContext fc = FacesTools.getCurrentInstance();

    if (null != event.getOldValue() && !event.getOldValue().equals(event.getNewValue())) {
      Locale locale = null;
      String language = event.getNewValue().toString();
      String country = language.toUpperCase();
      this.locale = language;

      try {
        locale = new Locale(language, country);
        fc.getViewRoot().setLocale(locale);
        Locale.setDefault(locale);
        this.userLocale = locale;
        this.homeContent = "n/a";
        this.notifyLanguageChanged(event.getOldValue().toString(), event.getNewValue().toString());
        logger.debug("New locale: " + language + "_" + country + " : " + locale);
      } catch (Exception e) {
        logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
      }
    }
  }

  public void notifyLanguageChanged(String oldLang, String newLang) {
    for (LanguageChangeObserver obs : this.languageChangeObservers) {
      if (null != obs) {
        obs.languageChanged(oldLang, newLang);
      }
    }
  }

  public void addLanguageChangeObserver(LanguageChangeObserver obs) {
    if (!this.languageChangeObservers.contains(obs)) {
      this.languageChangeObservers.add(obs);
    }
  }

  public void removeLanguageChangeObserver(LanguageChangeObserver obs) {
    this.languageChangeObservers.remove(obs);
  }

  public Locale getUserLocale() {
    return this.userLocale;
  }

  public void setUserLocale(Locale userLocale) {
    this.userLocale = userLocale;
  }

  /**
   * Turn the values of an enum to an array of SelectItem.
   *
   * @param includeNoItemSelectedEntry Decide if a SelectItem with null value should be inserted.
   * @param values The values of an enum.
   * @return An array of SelectItem.
   */
  public SelectItem[] getSelectItemsForEnum(boolean includeNoItemSelectedEntry, Object[] values) {
    Object[] valuesWithoutNull = removeNullValues(values);
    SelectItem[] selectItems = new SelectItem[valuesWithoutNull.length];

    for (int i = 0; i < valuesWithoutNull.length; i++) {
      if (null != valuesWithoutNull[i]) {
        SelectItem selectItem =
            new SelectItem(valuesWithoutNull[i].toString(), this.getLabel(this.convertEnumToString(valuesWithoutNull[i])));
        selectItems[i] = selectItem;
      }
    }

    if (includeNoItemSelectedEntry) {
      selectItems = this.addNoItemSelectedEntry(selectItems);
    }

    return selectItems;
  }

  private Object[] removeNullValues(Object[] values) {
    List<Object> listWithoutNulls = new ArrayList<>();
    for (Object o : values) {
      if (null != o) {
        listWithoutNulls.add(o);
      }
    }

    return listWithoutNulls.toArray();
  }

  /**
   * Adds an entry for NoItemSelected in front of the given array.
   *
   * @param selectItems the array where the entry should be added
   * @return a new array with an entry for NoItemSelected
   */
  private SelectItem[] addNoItemSelectedEntry(SelectItem[] selectItems) {
    SelectItem[] newSelectItems = new SelectItem[selectItems.length + 1];

    // add the entry for NoItemSelected in front of the array
    newSelectItems[0] = this.NO_ITEM_SET;
    System.arraycopy(selectItems, 0, newSelectItems, 1, selectItems.length);

    return newSelectItems;
  }

  /**
   * TODO FrM: Check this Converts an enum to a String for output.
   *
   * @param enumObject the enum to convert
   * @return the converted String for output
   */
  public String convertEnumToString(Object enumObject) {
    if (null != enumObject) {
      return "ENUM_" + enumObject.getClass().getSimpleName().toUpperCase() + "_" + enumObject;
    }

    return "ENUM_EMPTY";
  }

  public String getLabel(String placeholder) {
    try {
      return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
    } catch (MissingResourceException e) {
      return "???" + placeholder + "???";
    }
  }

  public String getMessage(String placeholder) {
    try {
      return ResourceBundle.getBundle(this.getSelectedMessagesBundle()).getString(placeholder);
    } catch (MissingResourceException e) {
      return "???" + placeholder + "???";
    }
  }

  /**
   * Returns an array of SelectItems for the enum genre.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for genre
   */
  private SelectItem[] getSelectItemsGenre(boolean includeNoItemSelectedEntry) {
    MdsPublicationVO.Genre[] values = MdsPublicationVO.Genre.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Returns an array of SelectItems for the enum CreatorType.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for CreatorType
   */
  public SelectItem[] getSelectItemsCreatorType(boolean includeNoItemSelectedEntry) {
    CreatorVO.CreatorType[] values = CreatorVO.CreatorType.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Returns an array of SelectItems for the enum CreatorRole.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for CreatorRole
   */
  public SelectItem[] getSelectItemsCreatorRole(boolean includeNoItemSelectedEntry) {
    Map<String, String> negativeRoles = ApplicationBean.INSTANCE.getCreatorRoleMap();

    List<CreatorVO.CreatorRole> values = new ArrayList<>();
    Collections.addAll(values, CreatorVO.CreatorRole.values());

    int i = 0;
    while (i < values.size()) {
      if (negativeRoles.containsValue(values.get(i).getUri())) {
        values.remove(i);
      } else {
        i++;
      }
    }

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values.toArray());
  }

  /**
   * Returns an array of SelectItems for the enum genre.
   *
   * @return array of SelectItems for genre
   */
  public SelectItem[] getSelectItemsGenre() {
    return this.getSelectItemsGenre(false);
  }

  /**
   * Returns an array of SelectItems for the enum DegreeType.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for DegreeType
   */
  public SelectItem[] getSelectItemsDegreeType(boolean includeNoItemSelectedEntry) {
    MdsPublicationVO.DegreeType[] values = MdsPublicationVO.DegreeType.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Returns an array of SelectItems for the enum genre.
   *
   * @return array of SelectItems for genre
   */
  public SelectItem[] getSelectItemsReviewMethod() {
    return this.getSelectItemsReviewMethod(false);
  }

  /**
   * Returns an array of SelectItems for the enum ReviewMethod.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for ReviewMethod
   */
  public SelectItem[] getSelectItemsReviewMethod(boolean includeNoItemSelectedEntry) {
    MdsPublicationVO.ReviewMethod[] values = MdsPublicationVO.ReviewMethod.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Returns an array of SelectItems for the enum visibility.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for visibility
   */
  public SelectItem[] getSelectItemsVisibility(boolean includeNoItemSelectedEntry) {
    FileDbVO.Visibility[] values = FileDbVO.Visibility.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Returns an array of SelectItems for the content-categories.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for ReviewMethod
   */
  public SelectItem[] getSelectItemsContentCategory(boolean includeNoItemSelectedEntry) {
    Map<String, String> values = ApplicationBean.INSTANCE.getContentCategoryMap();
    SelectItem[] selectItems = new SelectItem[values.size()];
    int i = 0;

    for (Map.Entry<String, String> entry : values.entrySet()) {
      // Prefix for the label is set to ENUM_CONTENTCATEGORY_
      SelectItem selectItem = new SelectItem(entry.getKey().toLowerCase().replace("_", "-"),
          this.getLabel("ENUM_CONTENTCATEGORY_" + entry.getKey().toLowerCase().replace("_", "-")));
      selectItems[i] = selectItem;
      i++;
    }

    if (includeNoItemSelectedEntry) {
      selectItems = this.addNoItemSelectedEntry(selectItems);
    }

    return selectItems;
  }

  /**
   * Returns an array of SelectItems for the enum OA_STATUS.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for visibility
   */
  public SelectItem[] getSelectItemsOaStatus(boolean includeNoItemSelectedEntry) {
    MdsFileVO.OA_STATUS[] values = MdsFileVO.OA_STATUS.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Returns an array of SelectItems for the enum InvitationStatus.
   *
   * @param includeNoItemSelectedEntry if true an entry for NoItemSelected is added
   * @return array of SelectItems for InvitationStatus
   */
  public SelectItem[] getSelectItemsInvitationStatus(boolean includeNoItemSelectedEntry) {
    EventVO.InvitationStatus[] values = EventVO.InvitationStatus.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  public String getLocale() {
    return this.locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  /**
   * Returns an array of SelectItems for the enum {@link SelectComponentAvailability}
   *
   * @return array of SelectItems for SelectComponentAvailability
   */
  public SelectItem[] getSelectedItemsComponentAvailability(boolean includeNoItemSelectedEntry) {
    InternationalizationHelper.SelectComponentAvailability[] values = InternationalizationHelper.SelectComponentAvailability.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  public SelectItem[] getSelectedItemsComponentVisibility(boolean includeNoItemSelectedEntry) {
    InternationalizationHelper.SelectComponentVisibility[] values = InternationalizationHelper.SelectComponentVisibility.values();

    return this.getSelectItemsForEnum(includeNoItemSelectedEntry, values);
  }

  /**
   * Part of Pubman's homepage van be drawn from an external html source. The property
   * inge.pubman.home.content.url has to be set with the url. If different languages should be
   * supported, The url source has to be provided with the different lcoale endings, e.g. ".de",
   * ".en", ".ja".
   *
   * @return
   */
  public String getHomeContent() {
    if ("n/a".equals(this.homeContent)) {
      try {
        String contentUrl = PropertyReader.getProperty(PropertyReader.INGE_PUBMAN_HOME_CONTENT_URL);

        if (null != contentUrl && !contentUrl.isEmpty()) {
          // Try if there's a specific local version
          this.homeContent = this.getContent(new URL(contentUrl + "." + this.locale));

          // If not try the url without locale
          if (null == this.homeContent) {
            this.homeContent = this.getContent(new URL(contentUrl));
          }
        } else {
          this.homeContent = null;
        }

      } catch (Exception e) {
        logger.error("Could not retrieve content for home page", e);
        this.homeContent = null;
      }
    }

    return this.homeContent;
  }

  private String getContent(URL url) throws Exception {
    HttpClient httpClient = new HttpClient();
    GetMethod getMethod = new GetMethod(url.toExternalForm());

    httpClient.executeMethod(getMethod);

    if (200 == getMethod.getStatusCode()) {
      BufferedReader in = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));

      String inputLine = "";
      String content = "";

      while (null != inputLine) {
        inputLine = in.readLine();
        if (null != inputLine) {
          content += inputLine + "  ";
        }
      }

      in.close();

      return content;
    }

    return null;
  }

  public List<LanguageChangeObserver> getLanguageChangeObservers() {
    return this.languageChangeObservers;
  }

  public void setLanguageChangeObservers(List<LanguageChangeObserver> languageChangeObservers) {
    this.languageChangeObservers = languageChangeObservers;
  }
}
