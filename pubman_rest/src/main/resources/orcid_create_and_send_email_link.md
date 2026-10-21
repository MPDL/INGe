# Ablauf und Diagramme für `createAndSendEmailLink`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `createAndSendEmailLink` (`POST /orcid/createAndSendEmailLink`) in `OrcidRestController` bzw. `OrcidServiceImpl.createAndSendEmailLink`.

---

## 1. REST-Schnittstellen-Spezifikation

- **Endpunkt**: `POST /orcid/createAndSendEmailLink`
- **Controller**: `de.mpg.mpdl.inge.rest.web.controller.OrcidRestController`
- **HTTP-Header**:
  - `Authorization` (`token`): Token zur Authentifizierung des Aufrufers.
- **Request-Parameter**:
  - `coneIdAuthor` (`String`, required): CoNE-ID des Autors.
  - `orcidAuthor` (`String`, required): ORCID-iD des Autors.
  - `nameAuthor` (`String`, required): Name des Autors.
  - `emailBibo` (`String`, required): E-Mail-Adresse der zuständigen Bibliothek / des Administrators.
  - `emailAuthor` (`String`, required): E-Mail-Adresse des Autors.
- **Response**: `ResponseEntity<OrcidAuthorizationDbVO>` (HTTP `200 OK`)

---

## 2. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

```mermaid
sequenceDiagram
    autonumber
    actor Client as REST-Client / Web-UI
    participant Controller as OrcidRestController
    participant Service as OrcidServiceImpl
    participant Auth as AuthorizationService
    participant Validator as OrcidValidatingService
    participant Repo as OrcidAuthorizationRepository
    participant Helper as OrcidHelper
    participant Email as EmailService

    Client->>Controller: POST /orcid/createAndSendEmailLink(token, coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)
    Controller->>Service: createAndSendEmailLink(token, coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)

    rect rgb(240, 240, 240)
        Note over Service,Auth: 1. Authentifizierung & Rollenprüfung
        Service->>Auth: getUserAccountFromToken(token)
        Auth-->>Service: accountUserDbVO
        Service->>Auth: checkLoginRequiredWithRole(token, 'ORCID_ADMIN')
    end

    rect rgb(240, 248, 255)
        Note over Service,Validator: 2. Parameter-Validierung
        Service->>Validator: validate(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)
    end

    rect rgb(255, 245, 238)
        Note over Service,Repo: 3. Dublettenprüfung
        Service->>Repo: findByConeIdAuthor(coneIdAuthor)
        alt Datensatz existiert bereits
            Repo-->>Service: orcidAuthorizationDbVO
            Note over Service: ErrorCode setzen: ERROR_CONE_ID_ALREADY_EXITS
            Service-->>Controller: orcidAuthorizationDbVO (mit ErrorCode)
            Controller-->>Client: 200 OK (orcidAuthorizationDbVO mit ErrorCode)
        else Datensatz ist neu
            Repo-->>Service: null
        end
    end

    rect rgb(235, 255, 235)
        Note over Service,Helper: 4. Autorisierungs-Link & Secret generieren
        Service->>Helper: generateSecret()
        Helper-->>Service: genSecret (UUID)
        Service->>Helper: createEmailLink(scopeRequested, redirectUri)
        Helper-->>Service: emailLink
        Service->>Helper: createOrcidAuthorizationDbVO(repo, userIdBibo, coneId, orcid, name, secret, scope, emailLink, emailBibo, emailAuthor, redirectUri)
        Helper->>Repo: saveAndFlush(vo) (Status: INITIALIZED)
        Helper-->>Service: orcidAuthorizationDbVO
    end

    rect rgb(255, 250, 235)
        Note over Service,Email: 5. Einladungs-E-Mail versenden
        Service->>Helper: createEmailText(nameAuthor, orcidAuthor, emailLink)
        Helper-->>Service: emailText
        Service->>Helper: sendEmail(emailService, emailAuthor, emailBibo, emailBibo, subject, text)
        Helper->>Email: sendEmail(recipientAddress, senderAddress, replyToAddress, subject, text)
    end

    rect rgb(245, 235, 255)
        Note over Service,Repo: 6. Status erfassen & persistieren
        Note over Service: Status: EMAIL_SENT_LINK, dateEmailSentLink: now
        Service->>Repo: saveAndFlush(orcidAuthorizationDbVO)
        Repo-->>Service: orcidAuthorizationDbVO
    end

    Service-->>Controller: orcidAuthorizationDbVO
    Controller-->>Client: 200 OK (orcidAuthorizationDbVO)
```

---

## 3. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: POST /orcid/createAndSendEmailLink]) --> B[OrcidRestController.createAndSendEmailLink]
    B --> C["1. Authentifizierung & Autorisierung<br/>↳ AuthorizationService.getUserAccountFromToken(token)<br/>↳ AuthorizationService.checkLoginRequiredWithRole('ORCID_ADMIN')"]
    C --> D["2. Validierung<br/>↳ OrcidValidatingService.validate(...)"]
    D --> E["3. Dublettenprüfung<br/>↳ OrcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor)"]
    
    E --> F{Datensatz existiert bereits?}
    F -- Ja --> G[Setze ErrorCode: ERROR_CONE_ID_ALREADY_EXITS]
    G --> RetConflict([Return: 200 OK mit orcidAuthorizationDbVO & ErrorCode])

    F -- Nein --> H["4. Secret & OAuth Redirect-URL erzeugen<br/>↳ OrcidHelper.generateSecret()<br/>↳ OrcidHelper.createEmailLink(...)"]
    H --> I["5. Datensatz anlegen & persistieren<br/>↳ OrcidHelper.createOrcidAuthorizationDbVO(...)<br/>↳ Status: INITIALIZED<br/>↳ Repo.saveAndFlush(...)"]
    I --> J["6. E-Mail generieren & senden<br/>↳ OrcidHelper.createEmailText(...)<br/>↳ OrcidHelper.sendEmail(...)<br/>↳ EmailService.sendEmail(...)"]
    J --> K["7. Status aktualisieren<br/>↳ Status: EMAIL_SENT_LINK<br/>↳ dateEmailSentLink setzen<br/>↳ Repo.saveAndFlush(...)"]
    K --> RetSuccess([Return: 200 OK mit orcidAuthorizationDbVO])
```

---

## 4. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Authentifizierung**:
   - `OrcidRestController.createAndSendEmailLink(...)` nimmt den HTTP-POST-Request (`/orcid/createAndSendEmailLink`) entgegen.
   - `AuthorizationService.getUserAccountFromToken(token)` ermittelt das Benutzerkonto des Aufrufers (`AccountUserDbVO` / `userIdBibo`).
   - `AuthorizationService.checkLoginRequiredWithRole(...)` stellt sicher, dass der Aufrufer über die administrative Rolle `ORCID_ADMIN` verfügt.

2. **Validierung der Eingabeparameter**:
   - `OrcidValidatingService.validate(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)` prüft Format und Vollständigkeit der übergebenen Autoren- und E-Mail-Daten.

3. **Dublettenprüfung**:
   - `OrcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor)` prüft, ob für den angegebenen CoNE-Autor bereits ein Autorisierungsdatensatz vorliegt.
   - Falls ein Datensatz bereits existiert, wird der Fehlercode `ERROR_CONE_ID_ALREADY_EXITS` gesetzt und der Datensatz direkt mit HTTP `200 OK` zurückgegeben.

4. **Secret- und Link-Generierung**:
   - `OrcidHelper.generateSecret()` erzeugt ein kryptografisches Einmal-Token (`secret`, UUID).
   - Die Redirect-URI mit dem Geheimschlüssel wird aufgebaut und via `OrcidHelper.createEmailLink(...)` in die OAuth-Autorisierungs-URL von ORCID eingebettet.
   - `OrcidHelper.createOrcidAuthorizationDbVO(...)` instanziiert das `OrcidAuthorizationDbVO` mit dem Initialstatus `INITIALIZED` und persistiert es via `OrcidAuthorizationRepository.saveAndFlush(...)`.

5. **Versand der Einladungs-E-Mail**:
   - Betreff (`ORCID_EMAIL_SUBJECT_FOR_LINK`) und E-Mail-Vorlage (`orcid_email.txt`) werden geladen.
   - `OrcidHelper.createEmailText(...)` ersetzt die Platzhalter (`$NAME_AUTHOR`, `$ORCID_AUTHOR`, `$EMAIL_LINK`).
   - `OrcidHelper.sendEmail(emailService, emailAuthor, emailBibo, emailBibo, subject, text)` stößt den E-Mail-Versand via `EmailService` an (Absender: `emailAuthor`, Empfänger/Reply-To: `emailBibo`).

6. **Status-Aktualisierung & Rückgabe**:
   - Das Datum `dateEmailSentLink` und der Status `EMAIL_SENT_LINK` werden gesetzt.
   - Der Datensatz wird via `OrcidAuthorizationRepository.saveAndFlush(...)` aktualisiert und mit HTTP `200 OK` zurückgegeben.
