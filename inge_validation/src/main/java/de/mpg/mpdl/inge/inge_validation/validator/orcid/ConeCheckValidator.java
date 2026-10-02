package de.mpg.mpdl.inge.inge_validation.validator.orcid;

import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.baidu.unbiz.fluentvalidator.ValidationError;
import com.baidu.unbiz.fluentvalidator.ValidatorContext;
import com.baidu.unbiz.fluentvalidator.ValidatorHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.inge_validation.util.ValidationTools;
import de.mpg.mpdl.inge.util.PropertyReader;

public class ConeCheckValidator extends ValidatorHandler<String> {
  private static final Logger logger = LogManager.getLogger(ConeCheckValidator.class);

  private final String nameAuthor;
  private final String orcidAuthor;

  public ConeCheckValidator(String nameAuthor, String orcidAuthor) {
    this.nameAuthor = nameAuthor;
    this.orcidAuthor = orcidAuthor;
  }

  @Override
  public boolean validate(ValidatorContext context, String coneIdAuthor) {
    if (ValidationTools.isEmpty(coneIdAuthor) || ValidationTools.isEmpty(this.nameAuthor) || ValidationTools.isEmpty(this.orcidAuthor)) {
      return false;
    }

    String urlString = buildConeUrl(coneIdAuthor);
    String nameAuthorCone = null;
    String orcidAuthorCone = null;

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rootNode = mapper.readTree(new URL(urlString));

      if (rootNode != null) {
        if (rootNode.hasNonNull("http_purl_org_dc_elements_1_1_title")) {
          nameAuthorCone = rootNode.get("http_purl_org_dc_elements_1_1_title").asText();
        }

        JsonNode identifierNode = rootNode.get("http_purl_org_dc_elements_1_1_identifier");
        if (identifierNode != null) {
          if (identifierNode.isArray()) {
            for (JsonNode item : identifierNode) {
              if (item.hasNonNull("http_www_w3_org_1999_02_22_rdf_syntax_ns_value")) {
                String type = item.hasNonNull("http_www_w3_org_2001_XMLSchema_instance_type")
                    ? item.get("http_www_w3_org_2001_XMLSchema_instance_type").asText()
                    : "";
                if ("ORCID".equalsIgnoreCase(type) || orcidAuthorCone == null) {
                  orcidAuthorCone =
                      item.get("http_www_w3_org_1999_02_22_rdf_syntax_ns_value").asText().substring(ValidationTools.ORCID_HTTPS.length());
                }
              }
            }
          } else if (identifierNode.isObject() && identifierNode.hasNonNull("http_www_w3_org_1999_02_22_rdf_syntax_ns_value")) {
            orcidAuthorCone = identifierNode.get("http_www_w3_org_1999_02_22_rdf_syntax_ns_value").asText();
          }
        }

        if (orcidAuthorCone == null && rootNode.hasNonNull("http_www_w3_org_1999_02_22_rdf_syntax_ns_value")) {
          orcidAuthorCone = rootNode.get("http_www_w3_org_1999_02_22_rdf_syntax_ns_value").asText();
        }
      }
    } catch (Exception e) {
      logger.error("Error fetching or parsing CoNE person JSON from: " + urlString, e);
      context.addError(ValidationError.create(ErrorMessages.ORCID_CONE_CHECK_NOT_POSSIBLE).setErrorCode(ErrorMessages.ERROR));
      return false;
    }

    boolean ok = true;

    // Validate nameAuthor against nameAuthorCone
    if (nameAuthorCone == null || !this.nameAuthor.trim().equalsIgnoreCase(nameAuthorCone.trim())) {
      context
          .addError(ValidationError.create(ErrorMessages.ORCID_NAME_AUTHOR_NOT_EQUAL_NAME_AUTHOR_CONE).setErrorCode(ErrorMessages.ERROR));
      ok = false;
    }

    // Validate orcidAuthor against orcidAuthorCone
    String expectedOrcid = this.orcidAuthor.replace(ValidationTools.ORCID_HTTPS, "").trim();
    if (!expectedOrcid.equalsIgnoreCase(this.orcidAuthor)) {
      context.addError(ValidationError.create(ErrorMessages.ORCID_AUTHOR_NOT_EQUAL_ORCID_AUTHOR_CONE).setErrorCode(ErrorMessages.ERROR));
      ok = false;
    }

    return ok;
  }

  private String buildConeUrl(String coneIdAuthor) {
    String coneServiceUrl = PropertyReader.getProperty(PropertyReader.INGE_CONE_SERVICE_URL);
    if (!coneServiceUrl.endsWith("/")) {
      coneServiceUrl += "/";
    }

    String path = coneIdAuthor;
    String url = coneServiceUrl + path + "?format=json";

    return url;
  }
}
