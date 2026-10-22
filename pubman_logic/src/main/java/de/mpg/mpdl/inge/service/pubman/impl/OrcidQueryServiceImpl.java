package de.mpg.mpdl.inge.service.pubman.impl;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidQueryService;
import de.mpg.mpdl.inge.util.PropertyReader;
import java.io.StringReader;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrcidQueryServiceImpl implements OrcidQueryService {

  private static final Logger logger = LogManager.getLogger(OrcidQueryServiceImpl.class);

  private static final int MAX_ITEMS_PER_SEARCH = Integer.parseInt(PropertyReader.getProperty(PropertyReader.ORCID_MAX_ITEMS_PER_SEARCH));

  private PubItemService pubItemService;

  @Autowired
  public OrcidQueryServiceImpl(PubItemService pubItemService) {
    this.pubItemService = pubItemService;
  }

  @Override
  public JSONObject createOrcidQueryJsonNode(String coneIdAuthor, Date dateModified) {
    String adaptedPersonId = "/" + coneIdAuthor; // ConeId aus CONE does not start with '/', in Pubman handled with starting '/' !!!!

    JSONObject rootNode = new JSONObject();
    JSONObject queryObj = new JSONObject();
    JSONObject boolObj = new JSONObject();
    JSONArray mustArray = new JSONArray();

    boolObj.put("must", mustArray);
    queryObj.put("bool", boolObj);
    rootNode.put("query", queryObj);

    // 1. term: metadata.creators.person.identifier.id
    JSONObject termObj = new JSONObject();
    JSONObject termInner = new JSONObject();
    termInner.put("metadata.creators.person.identifier.id", adaptedPersonId);
    termObj.put("term", termInner);
    mustArray.put(termObj);

    // 2. range: modificationDate (optional)
    if (dateModified != null) {
      JSONObject rangeObj = new JSONObject();
      JSONObject rangeInner = new JSONObject();
      JSONObject modDateObj = new JSONObject();
      modDateObj.put("gte", dateModified.getTime());
      rangeInner.put("modificationDate", modDateObj);
      rangeObj.put("range", rangeInner);
      mustArray.put(rangeObj);
    }

    // 3. bool: should: bool: must: versionState RELEASED, must_not: publicState WITHDRAWN
    JSONObject stateBoolObj = new JSONObject();
    JSONObject stateInnerBool = new JSONObject();
    JSONArray shouldArray = new JSONArray();
    stateInnerBool.put("should", shouldArray);
    stateBoolObj.put("bool", stateInnerBool);

    JSONObject shouldInnerBoolObj = new JSONObject();
    JSONObject shouldInnerBool = new JSONObject();
    JSONArray releaseMust = new JSONArray();
    JSONObject versionStateTerm = new JSONObject();
    JSONObject versionStateInner = new JSONObject();
    versionStateInner.put("versionState", "RELEASED");
    versionStateTerm.put("term", versionStateInner);
    releaseMust.put(versionStateTerm);

    JSONArray withdrawnMustNot = new JSONArray();
    JSONObject publicStateTerm = new JSONObject();
    JSONObject publicStateInner = new JSONObject();
    publicStateInner.put("publicState", "WITHDRAWN");
    publicStateTerm.put("term", publicStateInner);
    withdrawnMustNot.put(publicStateTerm);

    shouldInnerBool.put("must", releaseMust);
    shouldInnerBool.put("must_not", withdrawnMustNot);
    shouldInnerBoolObj.put("bool", shouldInnerBool);
    shouldArray.put(shouldInnerBoolObj);
    mustArray.put(stateBoolObj);

    // size, from
    rootNode.put("size", MAX_ITEMS_PER_SEARCH); // TODO: eventuell über eine Scroll Anfrage
    rootNode.put("from", 0);

    // sort
    JSONArray sortArray = new JSONArray();
    JSONObject sortObj = new JSONObject();
    JSONObject modDateObj = new JSONObject();
    modDateObj.put("order", "asc");
    sortObj.put("modificationDate", modDateObj);
    sortArray.put(sortObj);
    rootNode.put("sort", sortArray);

    return rootNode;
  }

  @Override
  public SearchRetrieveResponseVO<ItemVersionVO> doOrcidQuery(String coneIdAuthor, Date dateModified)
      throws IngeTechnicalException, IngeApplicationException {
    if (this.pubItemService == null) {
      logger.warn("ORCID: pubItemService is null, returning empty SearchRetrieveResponseVO");
      return new SearchRetrieveResponseVO<>();
    }
    JSONObject searchSource = createOrcidQueryJsonNode(coneIdAuthor, dateModified);
    logger.info("ORCID: query: {}", searchSource);

    try {
      String searchSourceText = searchSource.toString();
      SearchRequest srequ = SearchRequest.of(sr -> sr.withJson(new StringReader(searchSourceText)));

      ResponseBody<?> responseBody = this.pubItemService.searchDetailed(srequ, -1, null);
      if (responseBody != null) {
        return ElasticSearchGenericDAOImpl.getSearchRetrieveResponseFromElasticSearchResponse((ResponseBody) responseBody,
            ItemVersionVO.class);
      } else {
        return new SearchRetrieveResponseVO<>();
      }
    } catch (Exception e) {
      throw new IngeTechnicalException("Error creating search request or converting Elasticsearch response", e);
    }
  }
}
