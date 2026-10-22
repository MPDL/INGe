package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidResponse;
import de.mpg.mpdl.inge.service.pubman.orcid.OrcidTransformationService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.exceptions.TransformationException;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import de.mpg.mpdl.inge.transformation.sources.TransformerVoSource;
import de.mpg.mpdl.inge.transformation.transformers.SearchRetrieveResponseToItemOrcid;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OrcidTransformationServiceImpl implements OrcidTransformationService {

  @Override
  public String convertToOrcid(SearchRetrieveResponseVO<ItemVersionVO> searchResult) throws IngeTechnicalException {
    try {
      SearchRetrieveResponseToItemOrcid transformer = new SearchRetrieveResponseToItemOrcid();
      transformer.setSourceFormat(TransformerFactory.FORMAT.SEARCH_RESULT_VO);
      transformer.setTargetFormat(TransformerFactory.FORMAT.ORCID);
      TransformerVoSource source = new TransformerVoSource(searchResult != null ? searchResult : new SearchRetrieveResponseVO<>());
      StringWriter writer = new StringWriter();
      TransformerStreamResult result = new TransformerStreamResult(writer);
      transformer.transform(source, result);
      return writer.toString();
    } catch (TransformationException e) {
      throw new IngeTechnicalException("Error transforming SearchRetrieveResponse to ORCID format", e);
    }
  }

  @Override
  public List<OrcidResponse> parseOrcidResponses(JSONObject orcidAnswer) {
    if (orcidAnswer == null) {
      return Collections.emptyList();
    }

    if (orcidAnswer.has("bulk")) {
      JSONArray bulkArray = orcidAnswer.optJSONArray("bulk");
      if (bulkArray == null) {
        return Collections.emptyList();
      }

      List<OrcidResponse> results = new ArrayList<>(bulkArray.length());

      for (int i = 0; i < bulkArray.length(); i++) {
        JSONObject node = bulkArray.optJSONObject(i);
        if (node == null) {
          continue;
        }

        if (node.has("work")) {
          JSONObject workObj = node.optJSONObject("work");
          if (workObj != null) {
            OrcidResponse item = new OrcidResponse(OrcidResponse.Type.WORK, i, workObj);
            if (workObj.has("put-code") && !workObj.isNull("put-code")) {
              item.setPutCode(workObj.optLong("put-code"));
            }
            results.add(item);
          }
        } else if (node.has("error")) {
          JSONObject errorObj = node.optJSONObject("error");
          if (errorObj != null) {
            results.add(parseOrcidErrorAnswer(errorObj, i));
          }
        }
      }

      return results;
    }

    if (orcidAnswer.has("error")) {
      JSONObject errorObj = orcidAnswer.optJSONObject("error");
      if (errorObj != null) {
        return Collections.singletonList(parseOrcidErrorAnswer(errorObj, 0));
      }
    }

    if (orcidAnswer.has("response-code") || orcidAnswer.has("error-code") || orcidAnswer.has("developer-message")
        || orcidAnswer.has("user-message")) {
      return Collections.singletonList(parseOrcidErrorAnswer(orcidAnswer, 0));
    }

    if (orcidAnswer.has("work")) {
      JSONObject workObj = orcidAnswer.optJSONObject("work");
      if (workObj != null) {
        OrcidResponse item = new OrcidResponse(OrcidResponse.Type.WORK, 0, workObj);
        if (workObj.has("put-code") && !workObj.isNull("put-code")) {
          item.setPutCode(workObj.optLong("put-code"));
        }
        return Collections.singletonList(item);
      }
    }

    if (orcidAnswer.has("put-code") && !orcidAnswer.isNull("put-code")) {
      OrcidResponse item = new OrcidResponse(OrcidResponse.Type.WORK, 0, orcidAnswer);
      item.setPutCode(orcidAnswer.optLong("put-code"));
      return Collections.singletonList(item);
    }

    return Collections.emptyList();
  }

  private OrcidResponse parseOrcidErrorAnswer(JSONObject errorObj, int index) {
    OrcidResponse orcidResponse = new OrcidResponse(OrcidResponse.Type.ERROR, index, errorObj);
    if (errorObj.has("response-code") && !errorObj.isNull("response-code")) {
      orcidResponse.setResponseCode(errorObj.optInt("response-code"));
    }
    if (errorObj.has("error-code") && !errorObj.isNull("error-code")) {
      orcidResponse.setErrorCode(errorObj.optInt("error-code"));
    }
    orcidResponse.setUserMessage(errorObj.optString("user-message", null));
    orcidResponse.setDeveloperMessage(errorObj.optString("developer-message", null));
    orcidResponse.setMoreInfo(errorObj.optString("more-info", null));

    return orcidResponse;
  }

  @Override
  public Map<String, JSONObject> extractWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    if (orcidJson == null || orcidJson.trim().isEmpty()) {
      return new LinkedHashMap<>();
    }
    try {
      JSONObject rootNode = new JSONObject(orcidJson);
      return extractWorksBySourceWorkId(rootNode);
    } catch (Exception e) {
      throw new IngeTechnicalException("Error parsing ORCID JSON string", e);
    }
  }

  @Override
  public Map<String, JSONObject> extractWorksBySourceWorkId(JSONObject rootNode) {
    Map<String, JSONObject> worksBySourceWorkId = new LinkedHashMap<>();
    if (rootNode == null) {
      return worksBySourceWorkId;
    }

    if (rootNode.has("bulk")) {
      JSONArray bulkArray = rootNode.optJSONArray("bulk");
      if (bulkArray != null) {
        for (int i = 0; i < bulkArray.length(); i++) {
          JSONObject itemWrapper = bulkArray.optJSONObject(i);
          if (itemWrapper != null) {
            JSONObject workNode = itemWrapper.has("work") ? itemWrapper.optJSONObject("work") : itemWrapper;
            if (workNode != null) {
              String sourceWorkId = getSourceWorkId(workNode);
              if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
                worksBySourceWorkId.put(sourceWorkId, workNode);
              }
            }
          }
        }
      }
    } else if (rootNode.has("work")) {
      JSONObject workNode = rootNode.optJSONObject("work");
      if (workNode != null) {
        String sourceWorkId = getSourceWorkId(workNode);
        if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
          worksBySourceWorkId.put(sourceWorkId, workNode);
        }
      }
    } else if (rootNode.has("external-ids")) {
      String sourceWorkId = getSourceWorkId(rootNode);
      if (sourceWorkId != null && !sourceWorkId.isEmpty()) {
        worksBySourceWorkId.put(sourceWorkId, rootNode);
      }
    }

    return worksBySourceWorkId;
  }

  @Override
  public Map<String, JSONObject> getWorksBySourceWorkId(String orcidJson) throws IngeTechnicalException {
    return extractWorksBySourceWorkId(orcidJson);
  }

  @Override
  public String getSourceWorkId(JSONObject workNode) {
    if (workNode == null) {
      return null;
    }
    JSONObject extIds = workNode.optJSONObject("external-ids");
    if (extIds == null) {
      return null;
    }
    Object extIdObj = extIds.opt("external-id");
    if (extIdObj instanceof JSONArray extIdList) {
      for (int i = 0; i < extIdList.length(); i++) {
        JSONObject extId = extIdList.optJSONObject(i);
        if (extId != null) {
          String type = extId.optString("external-id-type", null);
          if ("source-work-id".equalsIgnoreCase(type)) {
            return extId.optString("external-id-value", null);
          }
        }
      }
    } else if (extIdObj instanceof JSONObject extId) {
      String type = extId.optString("external-id-type", null);
      if ("source-work-id".equalsIgnoreCase(type)) {
        return extId.optString("external-id-value", null);
      }
    }
    return null;
  }

  @Override
  public void setPutCode(JSONObject workNode, Object putCode) {
    if (workNode == null || putCode == null) {
      return;
    }
    if (putCode instanceof Number) {
      workNode.put("put-code", ((Number) putCode).longValue());
    } else if (putCode instanceof String) {
      String str = ((String) putCode).trim();
      try {
        workNode.put("put-code", Long.parseLong(str));
      } catch (NumberFormatException e) {
        workNode.put("put-code", str);
      }
    } else {
      workNode.put("put-code", putCode.toString());
    }
  }

}
