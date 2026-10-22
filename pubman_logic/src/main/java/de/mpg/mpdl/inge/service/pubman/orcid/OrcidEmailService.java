package de.mpg.mpdl.inge.service.pubman.orcid;

import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;

public interface OrcidEmailService {

  String createOrcidEmailText(String nameAuthor, String orcidAuthor, String emailLink) throws TechnicalException;

  void sendEmail(String recipientAddress, String senderAddress, String replyToAddress, String subject, String text)
      throws TechnicalException;
}
