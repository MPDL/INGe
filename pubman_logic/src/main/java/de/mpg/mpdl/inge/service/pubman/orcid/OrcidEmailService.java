package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;

public interface OrcidEmailService {

  String createOrcidEmailAuthorText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException;

  String createOrcidEmailBiboText() throws TechnicalException;

  String createOrcidEmailConsentAuthorText(String nameAuthor) throws TechnicalException;

  String createOrcidEmailConsentBiboText(String nameAuthor) throws TechnicalException;

  String createOrcidEmailNoConsentAuthorText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException;

  String createOrcidEmailNoConsentBiboText(String nameAuthor) throws TechnicalException;

  void sendEmail(String recipientAddress, String senderAddress, String replyToAddress, String subject, String text)
      throws TechnicalException;
}
