# Ablauf und Diagramme für `getOrcidAuthentication`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `getOrcidAuthentication` (`GET /orcid/createOrcidAuthentication`) in `OrcidRestController` bzw. `OrcidServiceImpl.createOrcidAuthorization`.

---

## 1. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

```mermaid
sequenceDiagram
    autonumber
    actor Client as ORCID OAuth Callback / REST-Client
    participant Controller as OrcidRestController
    participant Service as OrcidServiceImpl
    participant Repo as OrcidAuthorizationRepository
    participant Helper as OrcidHelper
    participant OrcidAPI as Externe ORCID Token-API
    participant Email as EmailService

    Client->>Controller: GET /orcid/createOrcidAuthentication(secret, code, error_description)
    Controller->>Service: createOrcidAuthorization(secret, code, errorDescription)

    rect rgb(240, 240, 240)
        Note over Service,Repo: 1. DB-Lookup & Fehlerprüfung
        Service->>Repo: findBySecret(secret_)
        Repo-->>Service: orcidAuthorizationDbVO
    end

    alt Fehlerparameter vorhanden (z. B. access_denied / Error)
        Service->>Repo: saveAndFlush(orcidAuthorizationDbVO)
        Service-->>Controller: orcidAuthorizationDbVO (Status: ACCESS_DENIED / ERROR)
        Controller-->>Client: 200 OK (Fehlerstatus)
    else Regulärer Autorisierungsablauf (Erfolgsfall mit Code)
        rect rgb(235, 245, 255)
            Note over Service,Helper: 2. Zwischenstatus 'CODE_RECEIVED' erfassen
            Service->>Helper: updateAuthorizationDbVO(vo, code, repo)
            Helper->>Repo: saveAndFlush(vo)
            Helper-->>Service: vo
        end

        rect rgb(235, 255, 235)
            Note over Service,OrcidAPI: 3. Token & ORCID ID anfordern
            Service->>Helper: requestTokenAndOrcid(vo, code, repo)
            Helper->>OrcidAPI: POST /oauth/token (Form-Data: code, client_id, client_secret)
            OrcidAPI-->>Helper: JSON (access_token, refresh_token, orcid, name, ...)
            Helper->>Helper: updateAuthorizationDbVO(vo, tokenData..., repo)
            Helper->>Repo: saveAndFlush(vo) (Status: TOKEN_RECEIVED, verified=true)
            Helper-->>Service: vo
        end

        rect rgb(255, 250, 235)
            Note over Service,Email: 4. Benachrichtigungs-E-Mail versenden
            Service->>Helper: sendEmail(emailService, sender, emailBibo, subject, text)
            Helper->>Email: sendEmail(sender, recipient, subject, text)
        end

        rect rgb(245, 235, 255)
            Note over Service,Repo: 5. Autorisierung abschließen
            Service->>Helper: finishAuthorizationDbVO(vo, repo)
            Helper->>Repo: saveAndFlush(vo) (Status: EMAIL_SENT_BIBO)
            Helper-->>Service: vo
        end

        Service-->>Controller: orcidAuthorizationDbVO
        Controller-->>Client: 200 OK (orcidAuthorizationDbVO)
    end
```

---

## 2. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: GET /orcid/createOrcidAuthentication]) --> B[OrcidRestController.createOrcidAuthentication]
    B --> C[Prüfung & Bereinigung von secret / error]
    C --> D[OrcidAuthorizationRepository.findBySecret]
    
    D --> E{Datensatz gefunden?}
    E -- Nein --> F[throw IngeApplicationException]
    E -- Ja --> G{Fehler vorhanden?}
    
    G -- Ja: access_denied / Error --> H[Status & ErrorCodes setzen]
    H --> I[OrcidAuthorizationRepository.saveAndFlush]
    I --> RetErr([Return: 200 OK mit Fehlerstatus])
    
    G -- Nein: Code vorhanden --> J["1. OrcidHelper.updateAuthorizationDbVO(vo, code, repo)<br/>↳ Repo.saveAndFlush (Status: CODE_RECEIVED)"]
    J --> K["2. OrcidHelper.requestTokenAndOrcid(vo, code, repo)<br/>↳ HTTP POST Token-Request an ORCID<br/>↳ OrcidHelper.updateAuthorizationDbVO(vo, tokenData, repo)<br/>↳ Repo.saveAndFlush (Status: TOKEN_RECEIVED, verified: true)"]
    K --> L["3. OrcidHelper.sendEmail(emailService, sender, emailBibo, subject, text)<br/>↳ EmailService.sendEmail(...)"]
    L --> M["4. OrcidHelper.finishAuthorizationDbVO(vo, repo)<br/>↳ Repo.saveAndFlush (Status: EMAIL_SENT_BIBO)"]
    M --> RetSuccess([Return: 200 OK mit orcidAuthorizationDbVO])
```

---

## 3. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Lookup**:
   - `OrcidRestController.createOrcidAuthentication(...)` empfängt den Callback-Request von ORCID mit den Parametern `secret`, `code` und optional `error_description`.
   - `OrcidServiceImpl.createOrcidAuthorization(...)` bereinigt eventuelle Fehler-Query-Parameter im Secret und sucht den Autorisierungsdatensatz via `OrcidAuthorizationRepository.findBySecret(secret_)`.
   - Wenn kein Datensatz existiert, wird eine `IngeApplicationException` ausgelöst.

2. **Fehlerbehandlung (z. B. OAuth abgebrochen)**:
   - Falls ein Fehlerparameter übergeben wurde (z. B. `access_denied`), wird der Status entsprechend auf `ACCESS_DENIED` oder `ERROR` gesetzt, mit Zeitstempel (`dateError`) versehen und in der Datenbank persistiert.

3. **Empfang des Authorization Codes**:
   - `OrcidHelper.updateAuthorizationDbVO(vo, code, repo)` setzt `codeReceived`, das Datum `dateCodeReceived` und den Status `CODE_RECEIVED` und speichert diesen Zwischenstand.

4. **Token- und Profilabruf bei ORCID**:
   - `OrcidHelper.requestTokenAndOrcid(vo, code, repo)` sendet einen HTTP-POST-Request an den Token-Endpunkt von ORCID (`/oauth/token`).
   - Die JSON-Antwort mit `access_token`, `refresh_token`, `orcid` und Profilname wird geparst.
   - Der Datensatz wird auf `verified = true`, Status `TOKEN_RECEIVED` aktualisiert und persistiert.
   - Eventuelle Abweichungen zwischen dem registrierten Namen/ORCID und den Rückgabewerten werden als Warn-Codes erfasst.

5. **Benachrichtigung der Bibliothek**:
   - E-Mail-Absender, Betreff und Bestätigungstext werden aus den Properties geladen.
   - `OrcidHelper.sendEmail(...)` versendet die Erfolgsbenachrichtigung an die hinterlegte Bibliotheksadresse (`emailBibo`).

6. **Abschluss & Rückgabe**:
   - `OrcidHelper.finishAuthorizationDbVO(vo, repo)` setzt den Status auf `EMAIL_SENT_BIBO` sowie den Zeitstempel `dateEmailSentBibo`.
   - Der finale Datensatz wird persistiert und über den Controller als HTTP 200 OK zurückgegeben.
