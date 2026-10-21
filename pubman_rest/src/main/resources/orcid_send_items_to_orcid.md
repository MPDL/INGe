# Ablauf und Diagramme für `sendItemsToOrcid`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `sendItemsToOrcid` (`POST /orcid/sendItemsToOrcid`) in `OrcidRestController`, `OrcidServiceImpl.sendItemsToOrcid` bzw. `OrcidSyncServiceImpl.sendItemsToOrcid`.

---

## 1. REST-Schnittstellen-Spezifikation

- **Endpunkt**: `POST /orcid/sendItemsToOrcid`
- **Controller**: `de.mpg.mpdl.inge.rest.web.controller.OrcidRestController`
- **Request-Parameter**: Keine (wird typischerweise durch Scheduler oder administrativen Trigger aufgerufen).
- **Response**: `ResponseEntity<Void>` (HTTP `200 OK`)

---

## 2. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

```mermaid
sequenceDiagram
    autonumber
    actor Scheduler as Scheduler / Admin REST-Client
    participant Controller as OrcidRestController
    participant Service as OrcidServiceImpl
    participant SyncService as OrcidSyncServiceImpl
    participant AuthRepo as OrcidAuthorizationRepository
    participant PubItem as PubItemService / Elasticsearch
    participant OrcidRepo as OrcidRepository
    participant Helper as OrcidHelper
    participant RestClient as OrcidRestClientImpl
    participant OrcidAPI as Externe ORCID Work-API

    Scheduler->>Controller: POST /orcid/sendItemsToOrcid()
    Controller->>Service: sendItemsToOrcid()
    Service->>SyncService: sendItemsToOrcid()

    rect rgb(240, 240, 240)
        Note over SyncService,AuthRepo: 1. Alle verifizierten Autorisierungen laden
        SyncService->>AuthRepo: findByVerifiedTrue()
        AuthRepo-->>SyncService: List<OrcidAuthorizationDbVO>
    end

    loop Für jeden verifizierten Autor (OrcidAuthorizationDbVO)
        rect rgb(240, 248, 255)
            Note over SyncService,PubItem: 2. Publikationen in Elasticsearch abfragen
            SyncService->>Helper: doOrcidQuery(pubItemService, coneIdAuthor, dateLastUpdated)
            Helper->>PubItem: searchDetailed(searchSource, ...)
            PubItem-->>Helper: SearchRetrieveResponseVO<ItemVersionVO>
            Helper-->>SyncService: searchResult
        end

        rect rgb(255, 245, 238)
            Note over SyncService,OrcidRepo: 3. Abgleich mit DB & Kategorisierung
            SyncService->>OrcidRepo: findByConeIdAuthor(coneIdAuthor)
            OrcidRepo-->>SyncService: List<OrcidDbVO> (existingOrcidMap)
            SyncService->>Helper: categorizeRecords(records, existingOrcidMap)
            Helper-->>SyncService: ItemCategorization (newRecords, updateRecords)
        end

        opt Neue Werke vorhanden (newRecords)
            rect rgb(235, 255, 235)
                Note over SyncService,OrcidAPI: 4. Bulk-Erstellung in Batches (max. MAX_ITEMS_PER_REQUEST)
                loop Für jeden Batch (subList bis MAX_ITEMS_PER_REQUEST)
                    SyncService->>Helper: convertToOrcid(batchSearchNew)
                    Helper-->>SyncService: orcidBodyNew (JSON Bulk Payload)
                    SyncService->>RestClient: createWorksInOrcid(orcid, orcidBodyNew, accessToken)
                    RestClient->>OrcidAPI: POST /v3.0/{orcid}/works (Bulk)
                    OrcidAPI-->>RestClient: JSON (Bulk Response mit put-codes / errors)
                    RestClient-->>SyncService: answerNew
                    SyncService->>Helper: parseOrcidResponses(answerNew)
                    Helper-->>SyncService: List<OrcidResponse>
                    alt 401 Unauthorized in Response
                        Note over SyncService: throw OrcidAccessDeniedException
                    else Erfolgs- / Fehlerantworten speichern
                        SyncService->>Helper: saveOrcidDbRecords(orcidRepo, auth, batchSearchNew, orcidResponsesNew, CREATE, existingOrcidMap)
                        Helper->>OrcidRepo: saveAndFlush(orcidDbVO) (Status: CREATE, putCode)
                    end
                end
            end
        end

        opt Zu aktualisierende Werke vorhanden (updateRecords)
            rect rgb(255, 250, 235)
                Note over SyncService,OrcidAPI: 5. Einzelsatz-Aktualisierung (PUT)
                SyncService->>Helper: convertToOrcid(searchResultUpdate)
                Helper-->>SyncService: orcidBodyUpdate
                SyncService->>Helper: getWorksBySourceWorkId(orcidBodyUpdate)
                Helper-->>SyncService: Map<itemId, workNode>
                loop Für jeden zu aktualisierenden Datensatz
                    Note over SyncService,Helper: putCode in workNode setzen
                    SyncService->>Helper: setPutCode(workNode, putCode)
                    SyncService->>RestClient: updateWorkInOrcid(orcid, singleWorkJson, accessToken, putCode)
                    RestClient->>OrcidAPI: PUT /v3.0/{orcid}/work/{putCode}
                    OrcidAPI-->>RestClient: JSON Response
                    RestClient-->>SyncService: answerUpdate
                    SyncService->>Helper: parseOrcidResponses(answerUpdate)
                    Helper-->>SyncService: List<OrcidResponse>
                    alt 401 Unauthorized
                        Note over SyncService: throw OrcidAccessDeniedException
                    else Fehlerantwort (z. B. 404 Not Found)
                        Note over SyncService,Helper: developer-message um putCode ergänzen
                        SyncService->>Helper: saveOrcidDbRecord(orcidRepo, auth, itemId, putCode, errorResponse, UPDATE, existingOrcidMap)
                        Helper->>OrcidRepo: saveAndFlush(orcidDbVO) (Status: UPDATE)
                    else Erfolgsfall (200 / 204)
                        SyncService->>Helper: saveOrcidDbRecord(orcidRepo, auth, itemId, putCode, answerUpdate, UPDATE, existingOrcidMap)
                        Helper->>OrcidRepo: saveAndFlush(orcidDbVO) (Status: UPDATE, putCode)
                    end
                end
            end
        end

        opt Mindestens ein Werk neu angelegt oder aktualisiert
            Note over SyncService,AuthRepo: Datum dateLastUpdated & Status UPDATED persistieren
            SyncService->>AuthRepo: saveAndFlush(auth)
        end

        opt HTTP 401 Unauthorized / Access Denied aufgetreten
            Note over SyncService,AuthRepo: Autorisierung entziehen (verified=false, Status: ACCESS_DENIED, dateError)
            SyncService->>AuthRepo: saveAndFlush(auth)
        end
    end

    SyncService-->>Service: void
    Service-->>Controller: void
    Controller-->>Scheduler: 200 OK
```

---

## 3. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: POST /orcid/sendItemsToOrcid]) --> B[OrcidRestController.sendItemsToOrcid]
    B --> C[OrcidServiceImpl.sendItemsToOrcid]
    C --> D[OrcidSyncServiceImpl.sendItemsToOrcid]
    D --> E[OrcidAuthorizationRepository.findByVerifiedTrue]
    
    E --> F{Verifizierte Autoren vorhanden?}
    F -- Nein --> RetEnd([Return: 200 OK])
    F -- Ja --> G[Schleife: Nächster verifizierter Autor]
    
    G --> H["Publikationen in Elasticsearch suchen<br/>↳ OrcidHelper.doOrcidQuery(...)"]
    H --> I{Publikationen gefunden?}
    I -- Nein --> NextAuthor{Weitere Autoren?}
    
    I -- Ja --> J["Bestehende DB-Einträge laden & kategorisieren<br/>↳ OrcidRepository.findByConeIdAuthor(...)<br/>↳ OrcidHelper.categorizeRecords(...)"]
    
    J --> K{Neue Records vorhanden?}
    K -- Ja --> L["Batches bilden (max. MAX_ITEMS_PER_REQUEST)<br/>↳ OrcidHelper.convertToOrcid(...)<br/>↳ POST an ORCID: OrcidRestClient.createWorksInOrcid(...)<br/>↳ OrcidHelper.parseOrcidResponses(...)<br/>↳ DB speichern: OrcidHelper.saveOrcidDbRecords(..., CREATE)"]
    K -- Nein --> M{Update Records vorhanden?}
    L --> M
    
    M -- Ja --> N["Für jedes Record einzeln:<br/>↳ PutCode im Work-JSON setzen<br/>↳ PUT an ORCID: OrcidRestClient.updateWorkInOrcid(...)<br/>↳ OrcidHelper.parseOrcidResponses(...)<br/>↳ Bei Erfolg: DB speichern (Status UPDATE, putCode)<br/>↳ Bei 404/Fehler: Developer-Message + putCode erfassen"]
    M -- Nein --> P{Änderungen durchgeführt?}
    N --> P
    
    P -- Ja --> Q["dateLastUpdated & Status UPDATED setzen<br/>↳ AuthRepo.saveAndFlush(auth)"]
    P -- Nein --> R{HTTP 401 aufgetreten?}
    Q --> R
    
    R -- Ja --> S["Zugriffsberechtigung entziehen<br/>↳ Status: ACCESS_DENIED, verified=false<br/>↳ dateError erfassen<br/>↳ AuthRepo.saveAndFlush(auth)"]
    R -- Nein --> NextAuthor
    S --> NextAuthor
    
    NextAuthor -- Ja --> G
    NextAuthor -- Nein --> RetEnd
```

---

## 4. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Delegation**:
   - `OrcidRestController.sendItemsToOrcid()` nimmt den POST-Request (`POST /orcid/sendItemsToOrcid`) entgegen.
   - Der Request wird über `OrcidServiceImpl.sendItemsToOrcid()` an die dedizierte Synchronisierungskomponente `OrcidSyncServiceImpl.sendItemsToOrcid()` delegiert.

2. **Laden der verifizierten Autorisierungen**:
   - `OrcidAuthorizationRepository.findByVerifiedTrue()` lädt alle Autorenkonten mit bestätigter und aktiver ORCID-Verknüpfung (`verified = true`).

3. **Publikationssuche in Elasticsearch**:
   - Für jeden Autor wird `OrcidHelper.doOrcidQuery(pubItemService, coneIdAuthor, dateLastUpdated)` ausgeführt.
   - `PubItemService.searchDetailed(...)` führt eine Elasticsearch-Abfrage nach allen Publikationen der CoNE-Autoren-ID (`coneIdAuthor`) durch, die seit `dateLastUpdated` geändert wurden (`versionState = RELEASED`, `publicState != WITHDRAWN`).

4. **Kategorisierung in Neuanlagen und Aktualisierungen**:
   - `OrcidRepository.findByConeIdAuthor(coneIdAuthor)` lädt bereits synchronisierte Einträge in eine Map (`existingOrcidMap`).
   - `OrcidHelper.categorizeRecords(records, existingOrcidMap)` unterteilt die Treffer:
     - **`newRecords`**: Noch kein Eintrag in `OrcidRepository` vorhanden oder `putCode == null`.
     - **`updateRecords`**: Eintrag mit gültigem `putCode` in der Datenbank vorhanden.

5. **Batch-Verarbeitung neuer Publikationen (`CREATE`)**:
   - `newRecords` wird in Teilmengen von maximal `MAX_ITEMS_PER_REQUEST` (Property `ORCID_MAX_ITEMS_PER_REQUEST`) aufgeteilt.
   - Jeder Batch wird via `OrcidHelper.convertToOrcid(...)` in die ORCID-Bulk-JSON-Payload transformiert.
   - `OrcidRestClientImpl.createWorksInOrcid(orcid, body, accessToken)` sendet den HTTP-POST-Request an `/v3.0/{orcid}/works`.
   - Die Antwort wird geparst (`OrcidHelper.parseOrcidResponses`) und die `putCode`s werden via `OrcidHelper.saveOrcidDbRecords(...)` im `OrcidRepository` mit Status `CREATE` persistiert.

6. **Einzelverarbeitung geänderter Publikationen (`UPDATE`)**:
   - `updateRecords` wird konvertiert und via `OrcidHelper.getWorksBySourceWorkId(...)` in einzelne Werke aufgeteilt.
   - Für jedes Werk:
     - Der zugehörige `putCode` wird via `OrcidHelper.setPutCode(...)` im Work-JSON hinterlegt.
     - `OrcidRestClientImpl.updateWorkInOrcid(orcid, singleWorkJson, accessToken, putCode)` sendet einen HTTP-PUT-Request an `/v3.0/{orcid}/work/{putCode}`.
     - Bei Erfolg (200/204) wird der Status `UPDATE` und der `putCode` in der Datenbank persistiert.
     - Bei Fehlern (z. B. 404 Not Found) wird die `developer-message` um den `putCode` erweitert und im `OrcidDbVO` erfasst.

7. **Aktualisierung und Fehlerbehandlung**:
   - Wenn Werke angelegt oder modifiziert wurden, wird `dateLastUpdated` und der Status `UPDATED` im `OrcidAuthorizationDbVO` aktualisiert und persistiert.
   - Bei einem HTTP 401 Unauthorized (z. B. Token abgelaufen oder Berechtigung entzogen) wird `auth.setVerified(false)`, `auth.setStatus("ACCESS_DENIED")` und `auth.setDateError(now)` gesetzt und persistiert.
   - Nach Abarbeitung aller Autoren gibt der Controller HTTP `200 OK` zurück.
