/*
 *
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * http://www.escidoc.org/license. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

package de.mpg.mpdl.inge.cone;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.mpg.mpdl.inge.util.PropertyReader;

/**
 * Helper class for result pattern.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class ModelHelper {

  private static final Logger logger = LogManager.getLogger(ModelHelper.class);

  private static final String REGEX_BRACKETS = "<[^>]+>";

  private static final ReplacePattern[] replacePattern = {new ReplacePattern("AND\\{[^,\\}]+(,[^,\\}]+)*\\}", "a"),
      new ReplacePattern("AND\\{,[^\\{\\}]*\\}|AND\\{[^\\{\\}]*,\\}|AND\\{[^\\{\\}]*,,[^\\{\\}]*\\}", ""),
      new ReplacePattern("OR\\{[^\\{\\}]*[^,\\{\\}]+[^\\{\\}]*\\}", "o"), new ReplacePattern("OR\\{,*\\}", ""),
      new ReplacePattern("NOT\\{\\}", "n"), new ReplacePattern("NOT\\{[^\\{\\}]+\\}", "")};

  /**
   * Hide constructor.
   */
  private ModelHelper() {

  }



  public static List<Pair<ResultEntry>> buildObjectFromPatternNew(String modelName, String currentSubject, TreeFragment poMap,
      boolean loggedIn) throws ConeException {
    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);
    List<Pair<ResultEntry>> results = new ArrayList<>();
    Set<String> languages = getLanguagesForResults(model, poMap, loggedIn);

    for (ModelList.ModelResult modelResult : model.getResults()) {
      if (languages.isEmpty()) {
        List<Map<String, List<LocalizedTripleObject>>> permutationMaps = getPermutations(model, poMap, modelResult, loggedIn, "");
        results.addAll(getReplaceResult(modelResult, permutationMaps, null));
      } else {
        for (String lang : languages) {
          List<Map<String, List<LocalizedTripleObject>>> permutationMaps = getPermutations(model, poMap, modelResult, loggedIn, lang);
          if (!"".equals(lang)) {
            results.addAll(getReplaceResult(modelResult, permutationMaps, lang));
          } else {
            results.addAll(getReplaceResult(modelResult, permutationMaps, null));
          }
        }
      }
    }

    return results;
  }



  private static List<Pair<ResultEntry>> getReplaceResult(ModelList.ModelResult modelResult,
      List<Map<String, List<LocalizedTripleObject>>> permutationMaps, String lang) {
    List<ResultEntry> resList = new ArrayList<>();
    for (Map<String, List<LocalizedTripleObject>> map : permutationMaps) {
      String result = getResultStringFromPattern(modelResult.getResultPattern(), map);
      ResultEntry replaceResult = new ResultEntry(result);
      replaceResult.setType(modelResult.getType());
      replaceResult.setLanguage(lang);

      if (null != modelResult.getSortPattern() && !modelResult.getSortPattern().trim().isEmpty()) {
        String sortKey = getResultStringFromPattern(modelResult.getSortPattern(), map);
        replaceResult.setSortResult(sortKey);
      }
      resList.add(replaceResult);

    }

    // remove duplicates and empty results
    List<ResultEntry> resListWithoutDuplicates = new ArrayList<>();
    for (ResultEntry res : resList) {
      if (!resListWithoutDuplicates.contains(res) && null != res.getValue() && !res.getValue().isEmpty()) {
        resListWithoutDuplicates.add(res);
      }
    }

    List<Pair<ResultEntry>> pairResultList = new ArrayList<>();
    for (ResultEntry res : resListWithoutDuplicates) {
      pairResultList.add(new Pair<>(null, res));
    }


    return pairResultList;
  }


  private static List<Map<String, List<LocalizedTripleObject>>> getPermutations(ModelList.Model model, TreeFragment poMap,
      ModelList.ModelResult modelResult, boolean loggedIn, String lang) throws ConeException {
    // logger.info("----------------------------Get Permutations-----------------------------------\n"
    // + modelResult.getResultPattern() + "\n" +
    // "------------------------------------------------------------------------------");
    List<Map<String, List<LocalizedTripleObject>>> permutationList = new ArrayList<>();
    permutationList.add(new HashMap<>());
    return getPermutations(model, null, poMap, modelResult, loggedIn, lang, permutationList, "");
  }


  private static List<Map<String, List<LocalizedTripleObject>>> getPermutations(ModelList.Model model, ModelList.Predicate superPredicate, TreeFragment poMap,
      ModelList.ModelResult modelResult, boolean loggedIn, String lang, List<Map<String, List<LocalizedTripleObject>>> permutationList, String prefix)
      throws ConeException {

    for (String predicateName : poMap.keySet()) {
      String regex = "(<|\\|)" + Pattern.quote(predicateName) + "(>|\\|)";
      Pattern p = Pattern.compile(regex);
      Matcher resultPatternMatcher = p.matcher(modelResult.getResultPattern());
      Matcher sortPatternMatcher = null;
      if (null != modelResult.getSortPattern()) {
        sortPatternMatcher = p.matcher(modelResult.getSortPattern());
      }

      if (resultPatternMatcher.find() || (null != sortPatternMatcher && sortPatternMatcher.find())) {
        // logger.info("Starting with predicate: " + predicateName);
        ModelList.Predicate predicate = null;
        if (null != superPredicate) {
          predicate = superPredicate.getPredicate(predicateName);
        } else {
          predicate = model.getPredicate(predicateName);
        }


        List<Map<String, List<LocalizedTripleObject>>> newPermutationList = new ArrayList<>();

        for (LocalizedTripleObject value : poMap.get(predicate.getId())) {

          if (!predicate.isResource() && (value instanceof TreeFragment treeValue && (lang.equals(value.getLanguage()) || null == value.getLanguage()
              || "".equals(value.getLanguage())
              || (lang.isEmpty() && value.getLanguage().equals(PropertyReader.getProperty(PropertyReader.INGE_CONE_LANGUAGE_DEFAULT)))))) {

            newPermutationList.addAll(getPermutations(model, predicate, treeValue, modelResult, loggedIn, lang, permutationList,
                prefix + predicate.getId() + "|"));

          } else if (predicate.isResource() && value instanceof TreeFragment && predicate.isIncludeResource()) {
            Querier querier = QuerierFactory.newQuerier(loggedIn);
            TreeFragment treeFragment = querier.details(predicate.getResourceModel(), ((TreeFragment) value).getSubject(), lang);
            querier.release();
            ModelList.Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());

            newPermutationList.addAll(getPermutations(newModel, null, treeFragment, modelResult, loggedIn, lang, permutationList,
                prefix + predicate.getId() + "|"));

          } else if (predicate.isResource() && value instanceof LocalizedString && predicate.isIncludeResource()) {
            Querier querier = QuerierFactory.newQuerier(loggedIn);
            TreeFragment treeFragment = querier.details(predicate.getResourceModel(), ((LocalizedString) value).getValue(), lang);
            querier.release();
            ModelList.Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());

            newPermutationList.addAll(getPermutations(newModel, null, treeFragment, modelResult, loggedIn, lang, permutationList,
                prefix + predicate.getId() + "|"));

          } else {

            if (lang.equals(value.getLanguage()) || "".equals(value.getLanguage())
                || (!predicate.isLocalized() && null == value.getLanguage()) || (lang.isEmpty() && (null == value.getLanguage()
                    || value.getLanguage().equals(PropertyReader.getProperty(PropertyReader.INGE_CONE_LANGUAGE_DEFAULT))))) {

              for (Map<String, List<LocalizedTripleObject>> currentMap : permutationList) {
                Map<String, List<LocalizedTripleObject>> newMap = new HashMap<>(currentMap);
                List<LocalizedTripleObject> list = new ArrayList<>();
                list.add(value);
                newMap.put(prefix + predicate.getId(), list);
                newPermutationList.add(newMap);
              }
            }
          }

        }
        if (!newPermutationList.isEmpty()) {
          permutationList = newPermutationList;
        }
      }
    }
    return permutationList;
  }



  private static String getResultStringFromPattern(String pattern, Map<String, List<LocalizedTripleObject>> permutationMap) {
    StringBuilder result = new StringBuilder();
    String[] patternPieces = pattern.split("\\n");
    for (String line : patternPieces) {
      // logger.info("------------------------------------- patternLine: " + line
      // +"---------------------------------------------------");
      List<ResultEntry> strings = new ArrayList<>();
      strings.add(new ResultEntry(line));


      for (Map.Entry<String, List<LocalizedTripleObject>> entry : permutationMap.entrySet()) {
        String predicateName = entry.getKey();
        // List<String> strings = new ArrayList<String>();
        if (line.contains("<" + predicateName + ">")) {
          for (LocalizedTripleObject value : entry.getValue()) {
            line = line.replace("<" + predicateName + ">", value.toString().replace(":", "&#x3A;").replace(",", "&#x2C;"));
          }
        }

      }


      String singleString = line;
      singleString = singleString.replaceAll(REGEX_BRACKETS, "");
      String newString = null;
      while (!singleString.equals(newString)) {
        newString = singleString;
        singleString = replaceTokens(singleString);
      }
      if (singleString.startsWith(":")) {
        singleString = "";
      } else if (singleString.contains(":")) {
        singleString = singleString.substring(singleString.indexOf(":") + 1);
      }

      line = singleString.replace("&#x3A;", ":").replace("&#x2C;", ",");

      result.append(line);

    }

    return result.toString();
  }

  private static Set<String> getLanguagesForResults(ModelList.Model model, TreeFragment poMap, boolean loggedIn) throws ConeException {
    Set<String> languages = new HashSet<>();

    if (model.isLocalizedResultPattern()) {
      for (Map.Entry<String, List<LocalizedTripleObject>> entry : poMap.entrySet()) {
        String key = entry.getKey();
        List<LocalizedTripleObject> objects = entry.getValue();
        for (LocalizedTripleObject object : objects) {
          if (null == object.getLanguage()) {
            languages.add("");
          } else {
            languages.add(object.getLanguage());
          }
          if (object instanceof TreeFragment && model.getPredicate(key).isResource()) {
            Querier querier = QuerierFactory.newQuerier(loggedIn);
            ModelList.Model subModel = ModelList.getInstance().getModelByAlias(model.getPredicate(key).getResourceModel());
            TreeFragment subResource = querier.details(subModel.getName(), ((TreeFragment) object).getSubject(), "*");
            languages.addAll(getLanguagesForResults(subModel, subResource, loggedIn));
            querier.release();
          }
        }
      }
    }
    if (model.isGlobalResultPattern()) {
      languages.add("");
    }
    return languages;
  }

  private static Set<String> getLanguagesForMatches(ModelList.Model model, TreeFragment poMap, boolean loggedIn) throws ConeException {
    Set<String> languages = new HashSet<>();

    if (model.isLocalizedMatches()) {
      for (Map.Entry<String, List<LocalizedTripleObject>> entry : poMap.entrySet()) {
        String key = entry.getKey();
        List<LocalizedTripleObject> objects = entry.getValue();
        for (LocalizedTripleObject object : objects) {
          if (null == object.getLanguage()) {
            languages.add("");
          } else {
            languages.add(object.getLanguage());
          }
          if (object instanceof TreeFragment && model.getPredicate(key).isResource()) {
            Querier querier = QuerierFactory.newQuerier(loggedIn);
            ModelList.Model subModel = ModelList.getInstance().getModelByAlias(model.getPredicate(key).getResourceModel());
            TreeFragment subResource = querier.details(subModel.getName(), ((TreeFragment) object).getSubject(), "*");
            languages.addAll(getLanguagesForMatches(subModel, subResource, loggedIn));
            querier.release();
          }
        }
      }
    }

    if (model.isGlobalMatches()) {
      languages.add("");
    }

    return languages;
  }


  /**
   * @param string
   * @return
   */
  private static String replaceTokens(String string) {

    if (null == string) {
      return null;
    }
    String newString;
    do {
      newString = string;
      for (ReplacePattern pattern : replacePattern) {
        Matcher matcher = pattern.getPattern().matcher(string);
        if (matcher.find()) {
          string = matcher.replaceFirst(pattern.getReplace());
          break;
        }
      }
    } while (!string.equals(newString));

    return string;
  }

  public static List<Pair<LocalizedString>> buildMatchStringFromModel(String modelName, String id, TreeFragment values, boolean loggedIn)
      throws ConeException {
    ModelList.Model model = ModelList.getInstance().getModelByAlias(modelName);
    List<Pair<LocalizedString>> results = new ArrayList<>();
    Set<String> languages = getLanguagesForMatches(model, values, loggedIn);

    for (String lang : languages) {
      String matchString = id + getMatchString(model.getPredicates(), values, lang, loggedIn);
      Pair<LocalizedString> pair = new Pair<>(lang, new LocalizedString(matchString));
      results.add(pair);
    }

    return results;
  }

  private static String getMatchString(List<ModelList.Predicate> predicates, TreeFragment values, String lang, boolean loggedIn)
      throws ConeException {
    StringWriter result = new StringWriter();

    for (ModelList.Predicate predicate : predicates) {
      if (predicate.isSearchable() && null != values.get(predicate.getId()) && !values.get(predicate.getId()).isEmpty()) {
        for (LocalizedTripleObject value : values.get(predicate.getId())) {
          if (null == value.getLanguage() || "".equals(value.getLanguage()) || lang.equals(value.getLanguage())) {
            if (predicate.isResource() && value instanceof TreeFragment) {
              Querier querier = QuerierFactory.newQuerier(loggedIn);
              String id = ((TreeFragment) value).getSubject();
              TreeFragment treeFragment = querier.details(predicate.getResourceModel(), id, "*");
              ModelList.Model newModel = ModelList.getInstance().getModelByAlias(predicate.getResourceModel());
              result.append(getMatchString(newModel.getPredicates(), treeFragment, lang, loggedIn));
              querier.release();
            } else if (value instanceof LocalizedString) {
              result.append("|");
              result.append(((LocalizedString) value).getValue());
            } else if (value instanceof TreeFragment) {
              result.append(getMatchString(predicate.getPredicates(), (TreeFragment) value, lang, loggedIn));
            }
          }
        }
      }
    }

    return result.toString();
  }


  public static void printOutMap(Map<String, TreeFragment> map) {
    if (null != map) {
      for (Map.Entry<String, TreeFragment> entry : map.entrySet()) {
        logger.info("Map Key: " + entry.getKey());
        if (null != entry.getValue()) {
          logger.info("Map value: " + entry.getValue().toString2());
        } else {
          logger.info("Map value: " + entry.getValue());
        }

      }
    }

  }

  static class ReplacePattern {
    Pattern pattern;
    String replace;

    /**
     * Convenience constructor.
     *
     * @param patternString Will be converted to a @see java.util.regex.Pattern
     * @param replace The string the matching pattern will be substituted by.
     */
    private ReplacePattern(String patternString, String replace) {
      this.pattern = Pattern.compile(patternString);
      this.replace = replace;
    }

    public Pattern getPattern() {
      return this.pattern;
    }

    public void setPattern(Pattern pattern) {
      this.pattern = pattern;
    }

    public String getReplace() {
      return this.replace;
    }

    public void setReplace(String replace) {
      this.replace = replace;
    }
  }
}
