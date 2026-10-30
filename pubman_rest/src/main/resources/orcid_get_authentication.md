# Ablauf und Diagramme für `getOrcidAuthentication`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `getOrcidAuthentication` (`GET /orcid/getOrcidAuthentication`) in `OrcidRestController` bzw. `OrcidServiceImpl.getOrcidAuthorization`.

---

## 1. REST-Schnittstellen-Spezifikation

- **Endpunkt**: `GET /orcid/getOrcidAuthentication`
- **Controller**: `de.mpg.mpdl.inge.rest.web.controller.OrcidRestController`
- **Request-Parameter**:
  - `secret` (`String`, required): Einmal-Geheimschlüssel aus dem OAuth-Callback.
  - `code` (`String`, optional): Von ORCID übermittelter Authorization Code (im Erfolgsfall).
  - `error_description` (`String`, optional): Fehlerbeschreibung von ORCID (im Fehler-/Abbruchfall).
- **Response**: `ResponseEntity<OrcidAuthorizationDbVO>` (HTTP `200 OK`)

---

## 2. Verwendete Services und Komponenten

| Service / Komponente | Voll qualifizierter Klassenname / Interface | Beschreibung & Rolle im Ablauf | Aufgerufene Methoden |
| :--- | :--- | :--- | :--- |
| **`OrcidRestController`** | `de.mpg.mpdl.inge.rest.web.controller.OrcidRestController` | REST-Controller-Endpunkt für den OAuth-Callback von ORCID; nimmt HTTP-GET-Requests entgegen und gibt die Response zurück. | `getOrcidAuthentication(...)` |
| **`OrcidService` / `OrcidServiceImpl`** | `de.mpg.mpdl.inge.service.pubman.OrcidService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidServiceImpl` | Zentraler Orchestrierungsdienst; koordiniert Fehlerbehandlung, Statusübergänge, Token-Abruf über REST-Client, Bibo-Benachrichtigung und Abschluss des Vorgangs. | `getOrcidAuthorization(...)` |
| **`OrcidAuthorizationRepository`** | `de.mpg.mpdl.inge.db.repository.OrcidAuthorizationRepository` | Spring-Data-JPA-Repository für DB-Zugriffe auf `OrcidAuthorizationDbVO`; sucht Autorisierungen anhand des Secrets und persistiert Statusänderungen. | `findBySecret(secret_)`,<br/>`saveAndFlush(vo)` |
| **`OrcidAuthorizationService` / `OrcidAuthorizationServiceImpl`** | `de.mpg.mpdl.inge.service.pubman.orcid.OrcidAuthorizationService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidAuthorizationServiceImpl` | Domänenservice für Statusfortschritte; dokumentiert den Empfang des Codes (`CODE_RECEIVED`) und den Abschluss des Ablaufs (`EMAIL_SENT_BIBO`). | `updateAuthorizationDbVO(vo, code)`,<br/>`finishAuthorizationDbVO(vo)` |
| **`OrcidRestClient` / `OrcidRestClientImpl`** | `de.mpg.mpdl.inge.service.pubman.orcid.OrcidRestClient`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidRestClientImpl` | REST-Client für die externe ORCID-API; tauscht den Authorization Code via HTTP-POST (`/oauth/token`) gegen Access- und Refresh-Tokens ein und aktualisiert das VO (`TOKEN_RECEIVED`, `verified = true`). | `requestTokenAndOrcid(vo, code, repo)` |
| **`OrcidEmailService` / `OrcidEmailServiceImpl`** | `de.mpg.mpdl.inge.service.pubman.orcid.OrcidEmailService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.OrcidEmailServiceImpl` | ORCID-spezifischer E-Mail-Dienst; steuert den Versand der Bestätigungs-E-Mail an die Bibliothek (`emailBibo`). | `sendEmail(...)` |
| **`EmailService`** | `de.mpg.mpdl.inge.service.pubman.EmailService`<br/>`de.mpg.mpdl.inge.service.pubman.impl.EmailServiceImpl` | Allgemeiner E-Mail-Versandservice der Anwendung; versendet die formatierte HTML-Mail über SMTP. | `sendHtmlMail(...)` |
| **`PropertyReader`** *(Hilfsklasse)* | `de.mpg.mpdl.inge.util.PropertyReader` | Liest Konfigurationsparameter (ORCID-URLs, Client-Credentials, Mailserver-Host, Bibo-Mailtexte, -Absender und -Betreffs). | `getProperty(...)` |

---

## 3. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

```mermaid
sequenceDiagram
    autonumber
    actor Client as ORCID OAuth Callback / REST-Client
    participant Controller as OrcidRestController
    participant Service as OrcidServiceImpl
    participant Repo as OrcidAuthorizationRepository
    participant AuthService as OrcidAuthorizationService
    participant RestClient as OrcidRestClientImpl
    participant OrcidAPI as Externe ORCID Token-API
    participant OrcidEmail as OrcidEmailService
    participant Mailer as EmailService

    Client->>Controller: GET /orcid/getOrcidAuthentication(secret, code, error_description)
    Controller->>Service: getOrcidAuthorization(secret, code, errorDescription)

    rect rgb(240, 240, 240)
        Note over Service,Repo: 1. DB-Lookup & Fehlerprüfung
        Note over Service: Bereinigung: secret auf '?error=' prüfen
        Service->>Repo: findBySecret(secret_)
        alt Datensatz nicht gefunden
            Repo-->>Service: null
            Note over Service: throw IngeApplicationException
        else Datensatz gefunden
            Repo-->>Service: orcidAuthorizationDbVO
        end
    end

    alt Fehlerparameter vorhanden (z. B. access_denied / Error)
        Note over Service,Repo: Fehlerstatus, ErrorCodes & dateError erfassen
        Service->>Repo: saveAndFlush(orcidAuthorizationDbVO)
        Service-->>Controller: orcidAuthorizationDbVO (Status: ACCESS_DENIED / ERROR)
        Controller-->>Client: 200 OK (Fehlerstatus)
    else Regulärer Autorisierungsablauf (Erfolgsfall mit Code)
        rect rgb(235, 245, 255)
            Note over Service,AuthService: 2. Zwischenstatus 'CODE_RECEIVED' erfassen
            Service->>AuthService: updateAuthorizationDbVO(vo, code)
            Note over AuthService: Status: CODE_RECEIVED, dateCodeReceived: now
            AuthService->>Repo: saveAndFlush(vo)
            AuthService-->>Service: vo
        end

        rect rgb(235, 255, 235)
            Note over Service,OrcidAPI: 3. Token & ORCID ID anfordern
            Service->>RestClient: requestTokenAndOrcid(vo, code, repo)
            RestClient->>OrcidAPI: POST /oauth/token (client_id, client_secret, code, redirect_uri)
            OrcidAPI-->>RestClient: JSON (access_token, refresh_token, orcid, name, scope, ...)
            Note over RestClient: Status: TOKEN_RECEIVED, verified: true, dateTokenReceived: now
            RestClient->>Repo: saveAndFlush(vo)
            RestClient-->>Service: vo
        end

        rect rgb(255, 250, 235)
            Note over Service,Mailer: 4. Benachrichtigungs-E-Mail versenden
            Service->>OrcidEmail: sendEmail(emailSender, emailBibo, null, subject, text)
            OrcidEmail->>Mailer: sendHtmlMail(smtpHost, ..., emailBibo, subject, text)
        end

        rect rgb(245, 235, 255)
            Note over Service,AuthService: 5. Autorisierung abschließen
            Service->>AuthService: finishAuthorizationDbVO(vo)
            Note over AuthService: Status: EMAIL_SENT_BIBO, dateEmailSentBibo: now
            AuthService->>Repo: saveAndFlush(vo)
            AuthService-->>Service: vo
        end

        Service-->>Controller: orcidAuthorizationDbVO
        Controller-->>Client: 200 OK (orcidAuthorizationDbVO)
    end
```

---

## 4. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: GET /orcid/getOrcidAuthentication]) --> B[OrcidRestController.getOrcidAuthentication]
    B --> C[OrcidServiceImpl.getOrcidAuthorization]
    C --> D[Prüfung & Bereinigung von secret / error]
    D --> E[OrcidAuthorizationRepository.findBySecret]
    
    E --> F{Datensatz gefunden?}
    F -- Nein --> G[throw IngeApplicationException]
    F -- Ja --> H{Fehler vorhanden?}
    
    H -- Ja: access_denied / Error --> I["Fehlerstatus & ErrorCodes setzen<br/>↳ Status: ACCESS_DENIED oder ERROR<br/>↳ dateError erfassen"]
    I --> J[OrcidAuthorizationRepository.saveAndFlush]
    J --> RetErr([Return: 200 OK mit Fehlerstatus])
    
    H -- Nein: Code vorhanden --> K["1. OrcidAuthorizationService.updateAuthorizationDbVO(vo, code)<br/>↳ OrcidAuthorizationRepository.saveAndFlush (Status: CODE_RECEIVED)"]
    K --> L["2. OrcidRestClient.requestTokenAndOrcid(vo, code, repo)<br/>↳ HTTP POST Token-Request an ORCID API<br/>↳ Abgleich von Name/ORCID (Warn-Codes)<br/>↳ OrcidAuthorizationRepository.saveAndFlush (Status: TOKEN_RECEIVED, verified: true)"]
    L --> M["3. OrcidEmailService.sendEmail(emailSender, emailBibo, null, subject, text)<br/>↳ EmailService.sendHtmlMail(...)"]
    M --> N["4. OrcidAuthorizationService.finishAuthorizationDbVO(vo)<br/>↳ OrcidAuthorizationRepository.saveAndFlush (Status: EMAIL_SENT_BIBO)"]
    N --> RetSuccess([Return: 200 OK mit orcidAuthorizationDbVO])
```

---

## 5. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Lookup**:
   - `OrcidRestController.getOrcidAuthentication(...)` empfängt den Callback-Request von ORCID mit den Parametern `secret`, optional `code` und optional `error_description`.
   - `OrcidServiceImpl.getOrcidAuthorization(...)` bereinigt eventuelle Fehler-Query-Parameter im Secret (`?error=...`) und sucht den Autorisierungsdatensatz via `OrcidAuthorizationRepository.findBySecret(secret_)`.
   - Wenn kein Datensatz existiert, wird eine `IngeApplicationException` ausgelöst.

2. **Fehlerbehandlung (z. B. OAuth abgebrochen)**:
   - Falls ein Fehlerparameter übergeben wurde (z. B. `access_denied`), wird das Datum `dateError` gesetzt.
   - Der Status wird entsprechend auf `ACCESS_DENIED` oder `ERROR` gesetzt, `errorCodes` werden hinterlegt und der Datensatz wird in der Datenbank via `saveAndFlush(...)` persistiert und als HTTP `200 OK` zurückgegeben.

3. **Empfang des Authorization Codes**:
   - `OrcidAuthorizationService.updateAuthorizationDbVO(vo, code)` setzt `codeReceived`, das Datum `dateCodeReceived` und den Status `CODE_RECEIVED` und speichert diesen Zwischenstand via `OrcidAuthorizationRepository.saveAndFlush(...)` in der Datenbank.

4. **Token- und Profilabruf bei ORCID via `OrcidRestClient`**:
   - `OrcidRestClientImpl.requestTokenAndOrcid(vo, code, repo)` sendet einen HTTP-POST-Request an den Token-Endpunkt von ORCID (`/oauth/token`) mit `client_id`, `client_secret`, `grant_type=authorization_code`, `code` und `redirect_uri`.
   - Die JSON-Antwort mit `access_token`, `refresh_token`, `orcid`, `name`, `scope`, `token_type` und `expires_in` wird geparst.
   - Der Datensatz wird auf `verified = true`, Status `TOKEN_RECEIVED` und `dateTokenReceived` aktualisiert und persistiert.
   - Eventuelle Abweichungen zwischen dem registrierten Namen bzw. ORCID und den Rückgabewerten werden als Warn-Codes (`WARN_DIFFERENT_NAME`, `WARN_DIFFERENT_ORCID`) erfasst.

5. **Benachrichtigung der Bibliothek**:
   - E-Mail-Absender (`ORCID_EMAIL_SENDER_FOR_BIBO`), Betreff (`ORCID_EMAIL_SUBJECT_FOR_BIBO`) und Bestätigungstext (`ORCID_EMAIL_TEXT_FOR_BIBO`) werden aus den Properties geladen.
   - `OrcidEmailService.sendEmail(...)` versendet die Erfolgsbenachrichtigung an die hinterlegte Bibliotheksadresse (`emailBibo`) und delegiert an `EmailService.sendHtmlMail(...)`.

6. **Abschluss & Rückgabe**:
   - `OrcidAuthorizationService.finishAuthorizationDbVO(vo)` setzt den Status auf `EMAIL_SENT_BIBO` sowie den Zeitstempel `dateEmailSentBibo` und persistiert den Datensatz via `OrcidAuthorizationRepository.saveAndFlush(...)`.
   - Der finale Datensatz wird über den Controller als HTTP `200 OK` zurückgegeben.
