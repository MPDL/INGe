package de.mpg.mpdl.inge.service.pubman;

import com.fasterxml.jackson.databind.JsonNode;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PubItemService extends GenericService<ItemVersionVO, String> {
  ItemVersionVO addNewDoi(String itemId, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean checkAccess(AuthorizationService.AccessType at, Principal userAccount, ItemVersionVO item)
      throws IngeApplicationException, IngeTechnicalException;

  Map<AuthorizationService.AccessType, Boolean> getAuthorizationInfo(String itemId, String authenticationToken)
      throws IngeApplicationException, IngeTechnicalException;

  JsonNode getAuthorizationInfoForFile(String itemId, String fileId, String authenticationToken)
      throws IngeApplicationException, IngeTechnicalException;

  List<AuditDbVO> getVersionHistory(String pubItemId, String authenticationToken);

  void reindex(String id, boolean includeFulltext, String authenticationToken) throws IngeTechnicalException;

  ItemVersionVO releasePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  ItemVersionVO revisePubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  ItemVersionVO submitPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  ItemVersionVO withdrawPubItem(String pubItemId, Date modificationDate, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
