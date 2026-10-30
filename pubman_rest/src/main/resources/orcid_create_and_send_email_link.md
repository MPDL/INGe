# Ablauf und Diagramme für `createAndSendEmailLink`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `createAndSendEmailLink` (`POST /orcid/createAndSendEmailLink`) in `OrcidRestController` bzw. `OrcidServiceImpl.createAndSendEmailLink`.

---

## 1. REST-Schnittstellen-Spezifikation

- **Endpunkt**: `POST /orcid/createAndSendEmailLink`
- **Controller**: `de.mpg.mpdl.inge.rest.web.controller.OrcidRestController`
- **HTTP-Header**:
  - `Authorization` (`token`): Token zur Authentifizierung und Autorisierung des Aufrufers.
- **Request-Parameter**:
  - `coneIdAuthor` (`String`, required): CoNE-ID des Autors.
  - `orcidAuthor` (`String`, required): ORCID-iD des Autors.
  - `nameAuthor` (`String`, required): Name des Autors.
  - `emailBibo` (`String`, required): E-Mail-Adresse der zuständigen Bibliothek / des Administrators.
  - `emailAuthor` (`String`, required): E-Mail-Adresse des Autors.
- **Response**: `ResponseEntity<OrcidAuthorizationDbVO>` (HTTP `200 OK`)

---

## 2. Verwendete Services und Komponenten

| Service / Komponente | Voll qualifizierter Klassenname / Interface | Beschreibung & Rolle im Ablauf | Aufgerufene Methoden |
| :--- | :--- | :--- | :--- |
| **`OrcidRestController`** | `de.mpg.mpdl.inge.rest.web.controller.OrcidRestController` | REST-Controller-Endpunkt für ORCID-Operationen; nimmt HTTP-POST-Requests entgegen und gibt die Response zurück. | `createAndSendEmailLink(...)` |
| **`OrcidService` / `OrcidServiceImpl`** | `de.mpg.mpdl.inge.service.pubman.OrcidService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidServiceImpl` | Zentraler Orchestrierungsdienst; koordiniert Authentifizierung, Validierung, Dublettenprüfung, Erstellung der Autorisierungsdaten und E-Mail-Versand. | `createAndSendEmailLink(...)` |
| **`AuthorizationService`** | `de.mpg.mpdl.inge.service.aa.AuthorizationService` | Authentifizierung und Autorisierung des Aufrufers; prüft Token und Benutzerrolle (`ORCID_ADMIN`). | `getUserAccountFromToken(token)`,<br/>`checkLoginRequiredWithRole(token, role)` |
| **`OrcidValidatingService`** | `de.mpg.mpdl.inge.inge_validation.OrcidValidatingService` | Validiert Eingabeparameter (`coneIdAuthor`, `orcidAuthor`, `nameAuthor`, `emailBibo`, `emailAuthor`) über `FluentValidator` und spezifische Validatoren. | `validate(...)` |
| **`OrcidAuthorizationRepository`** | `de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository` | Spring-Data-JPA-Repository für DB-Zugriffe auf `OrcidAuthorizationDbVO`; prüft vorhandene Datensätze und persistiert Änderungen. | `findByConeIdAuthor(coneIdAuthor)`,<br/>`saveAndFlush(vo)` |
| **`OrcidAuthorizationService` / `OrcidAuthorizationServiceImpl`** | `de.mpg.mpdl.inge.service.pubman.orcid.OrcidAuthorizationService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidAuthorizationServiceImpl` | Domänenservice zur Generierung des OAuth-Secrets, Zusammenbau des ORCID-OAuth-Autorisierungslinks und Instanziierung des initialen DB-VOs. | `generateSecret()`,<br/>`createEmailLink(scope, redirectUri)`,<br/>`createOrcidAuthorizationDbVO(...)` |
| **`OrcidEmailService` / `OrcidEmailServiceImpl`** | `de.mpg.mpdl.inge.service.pubman.orcid.OrcidEmailService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidEmailServiceImpl` | ORCID-spezifischer E-Mail-Dienst; lädt Template (`orcid_email.txt`), ersetzt Platzhalter und steuert den Mailversand. | `createOrcidEmailText(...)`,<br/>`sendEmail(...)` |
| **`EmailService`** | `de.mpg.mpdl.inge.service.pubman.EmailService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.EmailServiceImpl` | Allgemeiner E-Mail-Versandservice der Anwendung; versendet die formatierte HTML-Mail über SMTP. | `sendHtmlMail(...)` |
| **`PropertyReader`** *(Hilfsklasse)* | `de.mpg.mpdl.inge.util.PropertyReader` | Liest Konfigurationsparameter (ORCID-URLs, Scopes, Betreffzeilen, SMTP-Host). | `getProperty(...)` |
| **`ResourceUtil`** *(Hilfsklasse)* | `de.mpg.mpdl.inge.util.ResourceUtil` | Liest Template-Dateien aus dem Classpath. | `getResourceAsString(...)` |

---

## 3. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

```mermaid
sequenceDiagram
    autonumber
    actor Client as REST-Client / Web-UI
    participant Controller as OrcidRestController
    participant Service as OrcidServiceImpl
    participant Auth as AuthorizationService
    participant Validator as OrcidValidatingService
    participant Repo as OrcidAuthorizationRepository
    participant AuthService as OrcidAuthorizationService
    participant OrcidEmail as OrcidEmailService
    participant Mailer as EmailService

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
        Note over Service,AuthService: 4. Autorisierungs-Link & Secret generieren, initial speichern
        Service->>AuthService: generateSecret()
        AuthService-->>Service: secret (UUID)
        Service->>AuthService: createEmailLink(scopeRequested, redirectUri)
        AuthService-->>Service: emailLink
        Service->>AuthService: createOrcidAuthorizationDbVO(userIdBibo, coneIdAuthor, orcidAuthor, nameAuthor, secret, scopeRequested, emailLink, emailBibo, emailAuthor, redirectUri)
        AuthService->>Repo: saveAndFlush(vo)
        AuthService-->>Service: orcidAuthorizationDbVO
    end

    rect rgb(255, 250, 235)
        Note over Service,Mailer: 5. Einladungs-E-Mail generieren & versenden
        Service->>OrcidEmail: createOrcidEmailText(nameAuthor, orcidAuthor, emailLink)
        OrcidEmail-->>Service: text
        Service->>OrcidEmail: sendEmail(emailAuthor, emailBibo, emailBibo, subject, text)
        OrcidEmail->>Mailer: sendHtmlMail(smtpHost, ..., emailAuthor, subject, text)
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

## 4. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: POST /orcid/createAndSendEmailLink]) --> B[OrcidRestController.createAndSendEmailLink]
    B --> C["1. Authentifizierung & Autorisierung<br/>↳ AuthorizationService.getUserAccountFromToken(token)<br/>↳ AuthorizationService.checkLoginRequiredWithRole('ORCID_ADMIN')"]
    C --> D["2. Validierung<br/>↳ OrcidValidatingService.validate(...)"]
    D --> E["3. Dublettenprüfung<br/>↳ OrcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor)"]
    
    E --> F{Datensatz existiert bereits?}
    F -- Ja --> G[Setze ErrorCode: ERROR_CONE_ID_ALREADY_EXITS]
    G --> RetConflict([Return: 200 OK mit orcidAuthorizationDbVO & ErrorCode])

    F -- Nein --> H["4. Secret & OAuth Redirect-URL erzeugen<br/>↳ OrcidAuthorizationService.generateSecret()<br/>↳ OrcidAuthorizationService.createEmailLink(...)"]
    H --> I["5. Datensatz anlegen & persistieren<br/>↳ OrcidAuthorizationService.createOrcidAuthorizationDbVO(...)<br/>↳ OrcidAuthorizationRepository.saveAndFlush(...)"]
    I --> J["6. E-Mail generieren & senden<br/>↳ OrcidEmailService.createOrcidEmailText(...)<br/>↳ OrcidEmailService.sendEmail(...)<br/>↳ EmailService.sendHtmlMail(...)"]
    J --> K["7. Status aktualisieren<br/>↳ Status: EMAIL_SENT_LINK<br/>↳ dateEmailSentLink setzen<br/>↳ OrcidAuthorizationRepository.saveAndFlush(...)"]
    K --> RetSuccess([Return: 200 OK mit orcidAuthorizationDbVO])
```

---

## 5. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Authentifizierung**:
   - `OrcidRestController.createAndSendEmailLink(...)` nimmt den HTTP-POST-Request (`/orcid/createAndSendEmailLink`) entgegen.
   - `AuthorizationService.getUserAccountFromToken(token)` ermittelt das Benutzerkonto des Aufrufers (`AccountUserDbVO` / `userIdBibo`).
   - `AuthorizationService.checkLoginRequiredWithRole(...)` stellt sicher, dass der Aufrufer über die administrative Rolle `ORCID_ADMIN` verfügt.

2. **Validierung der Eingabeparameter**:
   - `OrcidValidatingService.validate(coneIdAuthor, orcidAuthor, nameAuthor, emailBibo, emailAuthor)` prüft Format und Vollständigkeit der übergebenen Autoren- und E-Mail-Daten mittels FluentValidator und spezifischer Validatoren (`ConeIdAuthorValidator`, `OrcidValidator`, `NameAuthorValidator`, `EmailBiboValidator`, `EmailAuthorValidator`, `ConeCheckValidator`).

3. **Dublettenprüfung**:
   - `OrcidAuthorizationRepository.findByConeIdAuthor(coneIdAuthor)` prüft, ob für den angegebenen CoNE-Autor bereits ein Autorisierungsdatensatz vorliegt.
   - Falls ein Datensatz bereits existiert, wird der Fehlercode `ERROR_CONE_ID_ALREADY_EXITS` gesetzt und der Datensatz direkt mit HTTP `200 OK` zurückgegeben.

4. **Secret- und Link-Generierung sowie Persistierung**:
   - `OrcidAuthorizationService.generateSecret()` erzeugt ein kryptografisches Einmal-Token (`secret`, UUID).
   - Die Redirect-URI mit dem Geheimschlüssel wird aufgebaut und via `OrcidAuthorizationService.createEmailLink(...)` in die OAuth-Autorisierungs-URL von ORCID eingebettet.
   - `OrcidAuthorizationService.createOrcidAuthorizationDbVO(...)` instanziiert das `OrcidAuthorizationDbVO` und persistiert es via `OrcidAuthorizationRepository.saveAndFlush(...)`.

5. **Versand der Einladungs-E-Mail**:
   - Betreff (`ORCID_EMAIL_SUBJECT_FOR_LINK`) und E-Mail-Vorlage (`orcid_email.txt`) werden geladen.
   - `OrcidEmailService.createOrcidEmailText(...)` ersetzt die Platzhalter (`$NAME_AUTHOR`, `$ORCID_AUTHOR`, `$EMAIL_LINK`).
   - `OrcidEmailService.sendEmail(emailAuthor, emailBibo, emailBibo, subject, text)` stößt den E-Mail-Versand an (Empfänger: `emailAuthor`, Absender/Reply-To: `emailBibo`) und delegiert an `EmailService.sendHtmlMail(...)`.

6. **Status-Aktualisierung & Rückgabe**:
   - Das Datum `dateEmailSentLink` und der Status `EMAIL_SENT_LINK` werden gesetzt.
   - Der Datensatz wird via `OrcidAuthorizationRepository.saveAndFlush(...)` aktualisiert und mit HTTP `200 OK` zurückgegeben.
