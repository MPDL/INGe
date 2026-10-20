# Ablauf und Diagramme für `sendEmailLink`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `sendEmailLink` (`POST /orcid/sendEmailLink`) in `OrcidRestController` bzw. `OrcidServiceImpl`.

---

## 1. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

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

    Client->>Controller: POST /orcid/sendEmailLink(token, coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)
    Controller->>Service: sendEmailLink(token, coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)

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
            Service-->>Controller: orcidAuthorizationDbVO (ErrorCode: ERROR_CONE_ID_ALREADY_EXITS)
            Controller-->>Client: 200 OK (mit Fehlercode)
        else Datensatz ist neu
            Repo-->>Service: null
        end
    end

    rect rgb(235, 255, 235)
        Note over Service,Helper: 4. Autorisierungs-Link & Secret generieren
        Service->>Helper: generateSecret()
        Helper-->>Service: genSecret
        Service->>Helper: createEmailLink(scopeRequested, redirectUri)
        Helper-->>Service: emailLink
        Service->>Helper: createOrcidAuthorizationDbVO(repo, userIdBibo, coneId, orcid, name, secret, scope, emailLink, emailBibo, emailAuthor, redirectUri)
        Helper->>Repo: saveAndFlush(vo)
        Helper-->>Service: orcidAuthorizationDbVO
    end

    rect rgb(255, 250, 235)
        Note over Service,Email: 5. Einladungs-E-Mail versenden
        Service->>Helper: createEmailText(nameAuthor, orcidAuthor, emailLink)
        Helper-->>Service: emailText
        Service->>Helper: sendEmail(emailService, emailBibo, emailBibo, subject, text)
        Helper->>Email: sendEmail(sender, recipient, subject, text)
    end

    rect rgb(245, 235, 255)
        Note over Service,Repo: 6. Status erfassen & persistieren
        Service->>Repo: saveAndFlush(orcidAuthorizationDbVO) (Status: EMAIL_SENT_LINK, dateEmailSentLink)
        Repo-->>Service: orcidAuthorizationDbVO
    end

    Service-->>Controller: orcidAuthorizationDbVO
    Controller-->>Client: 200 OK (orcidAuthorizationDbVO)
```

---

## 2. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: POST /orcid/sendEmailLink]) --> B[OrcidRestController.sendEmailLink]
    B --> C["1. AuthorizationService.getUserAccountFromToken(token)<br/>+ checkLoginRequiredWithRole('ORCID_ADMIN')"]
    C --> D[2. OrcidValidatingService.validate]
    D --> E[3. OrcidAuthorizationRepository.findByConeIdAuthor]
    
    E --> F{Datensatz existiert bereits?}
    F -- Ja --> G[Setze ErrorCode: ERROR_CONE_ID_ALREADY_EXITS]
    G --> RetConflict([Return: orcidAuthorizationDbVO mit ErrorCode])

    F -- Nein --> H["4. Secret & Redirect-URL erzeugen<br/>↳ OrcidHelper.generateSecret()<br/>↳ OrcidHelper.createEmailLink(...)"]
    H --> I["5. Datensatz anlegen<br/>↳ OrcidHelper.createOrcidAuthorizationDbVO(...)<br/>↳ Repo.saveAndFlush(...)"]
    I --> J["6. E-Mail generieren & senden<br/>↳ OrcidHelper.createEmailText(...)<br/>↳ OrcidHelper.sendEmail(...)<br/>↳ EmailService.sendEmail(...)"]
    J --> K["7. Status aktualisieren<br/>↳ Status: EMAIL_SENT_LINK<br/>↳ dateEmailSentLink setzen<br/>↳ Repo.saveAndFlush(...)"]
    K --> RetSuccess([Return: 200 OK mit orcidAuthorizationDbVO])
```

---

## 3. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Authentifizierung**:
   - `OrcidRestController.sendEmailLink(...)` nimmt den HTTP-POST-Request entgegen.
   - `AuthorizationService.getUserAccountFromToken(token)` ermittelt den eingeloggten Bibo-Benutzer (`userIdBibo`).
   - `AuthorizationService.checkLoginRequiredWithRole(...)` verifiziert, dass der Aufrufer über die Rolle `ORCID_ADMIN` verfügt.

2. **Validierung der Eingabedaten**:
   - `OrcidValidatingService.validate(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)` prüft Format und Konsistenz der übergebenen Autoren- und E-Mail-Daten.

3. **Dublettenprüfung**:
   - `OrcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor)` prüft, ob bereits eine Autorisierung für die CoNE-ID vorliegt.
   - Falls vorhanden, wird `ERROR_CONE_ID_ALREADY_EXITS` gesetzt und der Datensatz direkt zurückgegeben.

4. **Secret- und Link-Generierung**:
   - `OrcidHelper.generateSecret()` erzeugt einen kryptografischen Einmalschlüssel (`secret`).
   - Die Redirect-URI mit dem Geheimschlüssel wird aufgebaut und via `OrcidHelper.createEmailLink(...)` in die OAuth-Autorisierungs-URL von ORCID eingebettet.
   - `OrcidHelper.createOrcidAuthorizationDbVO(...)` instanziiert den Datensatz und speichert ihn in der Datenbank.

5. **Versand der Autorisierungs-E-Mail**:
   - Aus den Konfigurations-Properties werden Betreff und Vorlage geladen.
   - `OrcidHelper.createEmailText(...)` bereitet den E-Mail-Inhalt mit dem generierten OAuth-Link auf.
   - `OrcidHelper.sendEmail(emailService, ...)` stößt den Versand an die Bibliotheks-/Autoren-Adresse an.

6. **Status-Aktualisierung & Rückgabe**:
   - Das Datum `dateEmailSentLink` und der Status `EMAIL_SENT_LINK` werden gesetzt.
   - Der Datensatz wird via `OrcidAuthorizationRepository.saveAndFlush(...)` final persistiert und als HTTP 200 OK zurückgegeben.
