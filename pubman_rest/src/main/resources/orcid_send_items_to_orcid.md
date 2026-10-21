# Ablauf und Diagramme für `sendItemsToOrcid`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `sendItemsToOrcid` (`GET /orcid/sendItemsToOrcid`) in `OrcidRestController` bzw. `OrcidServiceImpl.sendItemsToOrcid`.

---

## 1. Sequenzdiagramm (Methoden-Aufrufreihenfolge)

```mermaid
sequenceDiagram
    autonumber
    actor Scheduler as Scheduler / REST-Client
    participant Controller as OrcidRestController
    participant Service as OrcidServiceImpl
    participant AuthRepo as OrcidAuthorizationRepository
    participant PubItem as PubItemService / Elasticsearch
    participant OrcidRepo as OrcidRepository
    participant Helper as OrcidHelper
    participant OrcidAPI as Externe ORCID Work-API

    Scheduler->>Controller: GET /orcid/sendItemsToOrcid()
    Controller->>Service: sendItemsToOrcid()

    rect rgb(240, 240, 240)
        Note over Service,AuthRepo: 1. Alle verifizierten Autorisierungen laden
        Service->>AuthRepo: findByVerifiedTrue()
        AuthRepo-->>Service: List<OrcidAuthorizationDbVO>
    end

    loop Für jeden verifizierten Autor (OrcidAuthorizationDbVO)
        rect rgb(240, 248, 255)
            Note over Service,PubItem: 2. Publikationen in Elasticsearch abfragen
            Service->>Helper: doOrcidQuery(pubItemService, coneIdAuthor, dateLastUpdated)
            Helper->>PubItem: searchDetailed(queryNode, ...)
            PubItem-->>Helper: ResponseBody (Elasticsearch)
            Helper-->>Service: SearchRetrieveResponseVO<ItemVersionVO>
        end

        rect rgb(255, 245, 238)
            Note over Service,OrcidRepo: 3. Abgleich mit DB (Kategorisierung: Neu vs. Update)
            Service->>OrcidRepo: findByConeIdAuthor(coneIdAuthor)
            OrcidRepo-->>Service: List<OrcidDbVO> (existingOrcidMap)
            Note over Service: Kein Datensatz oder putCode == null -> newRecords<br/>Datensatz mit putCode vorhanden -> updateRecords
        end

        opt Neue Werke vorhanden (newRecords)
            rect rgb(235, 255, 235)
                Note over Service,OrcidAPI: 4. Bulk-Erstellung in Batches (max. 100 pro Request)
                loop Für jeden Batch (subList bis MAX_ITEMS_PER_REQUEST)
                    Service->>Helper: convertToOrcid(batchSearchNew)
                    Helper-->>Service: orcidBodyNew (JSON)
                    Service->>Helper: createWorksInOrcid(orcid, orcidBodyNew, accessToken)
                    Helper->>OrcidAPI: POST /v3.0/{orcid}/works (Bulk)
                    OrcidAPI-->>Helper: JSON (bulk response mit put-codes)
                    Helper-->>Service: answerNew
                    Service->>Helper: parseOrcidResponses(answerNew)
                    Helper-->>Service: List<OrcidResponse>
                    Service->>Helper: saveOrcidDbRecords(orcidRepo, authVO, batchSearchNew, orcidResponsesNew, CREATE)
                    Helper->>OrcidRepo: saveAndFlush(orcidDbVO)
                end
            end
        end

        opt Zu aktualisierende Werke vorhanden (updateRecords)
            rect rgb(255, 250, 235)
                Note over Service,OrcidAPI: 5. Einzelsatz-Aktualisierung (PUT)
                Service->>Helper: convertToOrcid(searchResultUpdate)
                Helper-->>Service: orcidBodyUpdate
                Service->>Helper: getWorksBySourceWorkId(orcidBodyUpdate)
                Helper-->>Service: Map<itemId, workNode>
                loop Für jeden zu aktualisierenden Datensatz
                    Service->>Helper: setPutCode(workNode, putCode)
                    Service->>Helper: updateWorkInOrcid(orcid, singleWorkJson, accessToken, putCode)
                    Helper->>OrcidAPI: PUT /v3.0/{orcid}/work/{putCode}
                    OrcidAPI-->>Helper: JSON
                    Helper-->>Service: answerUpdate
                    Service->>Helper: parseOrcidResponses(answerUpdate)
                    Helper-->>Service: List<OrcidResponse>
                    alt Fehlerantwort (z. B. HTTP 404 - Not Found)
                        Note over Service,Helper: Developer-Message um putCode ergänzen & putCode in DB auf null setzen
                        Service->>Helper: saveOrcidDbRecord(orcidRepo, authVO, itemId, putCode, errorResponse, UPDATE)
                        Helper->>OrcidRepo: saveAndFlush(orcidDbVO)
                    else Erfolgsfall (HTTP 200/204)
                        Service->>Helper: saveOrcidDbRecord(orcidRepo, authVO, itemId, putCode, answerUpdate, UPDATE)
                        Helper->>OrcidRepo: saveAndFlush(orcidDbVO)
                    end
                end
            end
        end

        opt Mindestens ein Werk angelegt oder aktualisiert
            Note over Service,AuthRepo: dateLastUpdated im Autorisierungsdatensatz aktualisieren
            Service->>AuthRepo: saveAndFlush(authVO)
        end

        opt HTTP 401 Unauthorized / Token abgelaufen
            Note over Service,AuthRepo: Zugriff verweigert (verified=false, Status: ACCESS_DENIED)
            Service->>AuthRepo: saveAndFlush(authVO)
        end
    end

    Service-->>Controller: void
    Controller-->>Scheduler: 200 OK
```

---

## 2. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: GET /orcid/sendItemsToOrcid]) --> B[OrcidRestController.sendItemsToOrcid]
    B --> C[OrcidAuthorizationRepository.findByVerifiedTrue]
    
    C --> D{Verifizierte Autoren vorhanden?}
    D -- Nein --> RetEnd([Return: 200 OK])
    D -- Ja --> E[Schleife: Nächster verifizierter Autor]
    
    E --> F["Publikationen abfragen<br/>↳ OrcidHelper.doOrcidQuery(...)"]
    F --> G["Vorhandene DB-Einträge laden<br/>↳ OrcidRepository.findByConeIdAuthor(...)"]
    G --> H[Kategorisierung: Item in DB mit putCode vorhanden?]
    
    H -- Nein oder putCode ist null --> I[Zu newRecords hinzufügen]
    H -- Ja mit existierendem putCode --> J[Zu updateRecords hinzufügen]
    
    I & J --> K{Neue Records vorhanden?}
    
    K -- Ja --> L["Aufteilung in Batches (max. MAX_ITEMS_PER_REQUEST)<br/>↳ OrcidHelper.convertToOrcid(...)<br/>↳ POST an ORCID: OrcidHelper.createWorksInOrcid(...)<br/>↳ OrcidHelper.parseOrcidResponses(...)<br/>↳ DB speichern: OrcidHelper.saveOrcidDbRecords(..., CREATE)"]
    K -- Nein --> M{Update Records vorhanden?}
    L --> M
    
    M -- Ja --> N["Für jedes Record einzeln:<br/>↳ PutCode in Work-JSON setzen<br/>↳ PUT an ORCID: OrcidHelper.updateWorkInOrcid(...)<br/>↳ OrcidHelper.parseOrcidResponses(...)<br/>↳ Bei Erfolg: DB speichern (Status UPDATE, putCode)<br/>↳ Bei 404/Fehler: Developer-Message + putCode erfassen, putCode=null"]
    M -- Nein --> P{Änderungen durchgeführt?}
    N --> P
    
    P -- Ja --> Q["dateLastUpdated aktualisieren<br/>↳ AuthRepo.saveAndFlush(...)"]
    P -- Nein --> R{HTTP 401 aufgetreten?}
    Q --> R
    
    R -- Ja --> S["Zugriffsberechtigung entziehen<br/>↳ Status: ACCESS_DENIED, verified=false<br/>↳ AuthRepo.saveAndFlush(...)"]
    R -- Nein --> O{Weitere Autoren?}
    S --> O
    
    O -- Ja --> E
    O -- Nein --> RetEnd
```

---

## 3. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Ladevorgang**:
   - `OrcidRestController.sendItemsToOrcid()` stößt die Synchronisierung der Publikationen aller verifizierten Autoren an (`GET /orcid/sendItemsToOrcid`).
   - `OrcidAuthorizationRepository.findByVerifiedTrue()` lädt alle Benutzer mit aktiver, bestätigter ORCID-Verbindung (`verified = true`).

2. **Abfrage relevanter Publikationen**:
   - Für jeden Autor wird `OrcidHelper.doOrcidQuery(pubItemService, coneIdAuthor, dateLastUpdated)` ausgeführt.
   - `PubItemService.searchDetailed(...)` führt eine Elasticsearch-Suche nach allen Publikationen der zugehörigen CoNE-Autoren-ID (`coneIdAuthor`) durch, die seit `dateLastUpdated` geändert wurden und den Status `versionState = RELEASED` sowie `publicState != WITHDRAWN` besitzen.

3. **Klassifizierung in neue vs. geänderte Datensätze**:
   - `OrcidRepository.findByConeIdAuthor(coneIdAuthor)` lädt alle bereits zu diesem Autor gespeicherten ORCID-Einträge in eine Map.
   - Die Suchergebnisse werden iteriert und aufgeteilt:
     - **Neu (`newRecords`)**: Noch kein Eintrag in `OrcidRepository` vorhanden oder noch kein `putCode` vergeben.
     - **Update (`updateRecords`)**: Eintrag mit existierendem `putCode` in der Datenbank vorhanden.

4. **Batch-Verarbeitung neuer Publikationen (`CREATE`)**:
   - `newRecords` wird in Blöcke von maximal `MAX_ITEMS_PER_REQUEST` (z. B. 100 Datensätze) aufgeteilt.
   - Jeder Block wird via `OrcidHelper.convertToOrcid(...)` in die ORCID-Bulk-Payload konvertiert.
   - `OrcidHelper.createWorksInOrcid(...)` sendet den Batch per HTTP-POST an `/v3.0/{orcid}/works`.
   - Die Antwort wird geparst (`OrcidHelper.parseOrcidResponses`) und alle neu vergebenen `putCode`s werden via `OrcidHelper.saveOrcidDbRecords(...)` im `OrcidRepository` mit Status `CREATE` persistiert.

5. **Einzelverarbeitung geänderter Publikationen (`UPDATE`)**:
   - `updateRecords` wird konvertiert und in einzelne Werke aufgeteilt (`OrcidHelper.getWorksBySourceWorkId`).
   - Da die ORCID-API Updates nicht als Bulk-Payload erlaubt, wird jeder Datensatz einzeln abgearbeitet:
     - Der zugehörige `putCode` wird in den JSON-Knoten eingefügt (`OrcidHelper.setPutCode`).
     - `OrcidHelper.updateWorkInOrcid(...)` führt einen HTTP-PUT gegen `/v3.0/{orcid}/work/{putCode}` aus.
     - Die Antwort wird mit `OrcidHelper.parseOrcidResponses` ausgewertet:
       - **Erfolgsfall**: `OrcidHelper.saveOrcidDbRecord(...)` aktualisiert den Status `UPDATE` mit dem `putCode`.
       - **Fehlerfall (z. B. HTTP 404 - Not Found)**: Der Prozess bricht nicht ab. Die `developer-message` wird um den `putCode` ergänzt und im `OrcidDbVO` gespeichert, während der vorhandene `putCode` im Datensatz überschrieben (auf `null` gesetzt) wird, sodass das Werk beim nächsten Lauf als Neuanlage behandelt werden kann.

6. **Abschluss & Fehlerbehandlung**:
   - Wenn Werke angelegt oder aktualisiert wurden, wird `dateLastUpdated` im `OrcidAuthorizationDbVO` aktualisiert und persistiert.
   - Tritt bei der Kommunikation mit ORCID ein HTTP 401 Unauthorized auf (z. B. wenn der Benutzer den Zugriff in seinem ORCID-Profil widerrufen hat), wird `auth.setVerified(false)` und der Status `ACCESS_DENIED` gesetzt.
   - Nach Abarbeitung aller Autoren liefert der Controller HTTP 200 OK zurück.
