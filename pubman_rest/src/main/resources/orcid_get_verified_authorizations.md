# Ablauf und Diagramme für `getVerifiedOrcidAuthorizations`

Dieses Dokument visualisiert den Ablauf, die Schnittstellen und die Methodenaufrufe der REST-Methode `getVerifiedOrcidAuthorizations` (`GET /orcid/getVerifiedOrcidAuthorizations`) in `OrcidRestController` bzw. `OrcidServiceImpl.getVerifiedOrcidAuthorizations`.

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

    Scheduler->>Controller: GET /orcid/getVerifiedOrcidAuthorizations()
    Controller->>Service: getVerifiedOrcidAuthorizations()

    rect rgb(240, 240, 240)
        Note over Service,AuthRepo: 1. Alle verifizierten Autorisierungen laden
        Service->>AuthRepo: findByVerifiedTrue()
        AuthRepo-->>Service: List<OrcidAuthorizationDbVO>
    end

    loop Für jeden verifizierten Autor (OrcidAuthorizationDbVO)
        rect rgb(240, 248, 255)
            Note over Service,PubItem: 2. Publikationen in CoNE/Elasticsearch abfragen
            Service->>Helper: doOrcidQuery(pubItemService, coneIdAuthor, dateLastUpdated, null)
            Helper->>PubItem: searchDetailed(queryNode, ...)
            PubItem-->>Helper: SearchResponse (Items)
            Helper-->>Service: SearchRetrieveResponseVO<ItemVersionVO>
        end

        rect rgb(255, 245, 238)
            Note over Service,OrcidRepo: 3. Abgleich mit DB (Neu vs. Update)
            loop Für jeden Datensatz / Item
                Service->>OrcidRepo: findByConeIdAuthorAndItemId(coneId, itemId)
                OrcidRepo-->>Service: orcidDbVO
                Note over Service: Kein Datensatz oder putCode == null -> searchResultNew<br/>Datensatz mit putCode vorhanden -> searchResultUpdate
            end
        end

        opt Neue Werke vorhanden (searchResultNew)
            rect rgb(235, 255, 235)
                Note over Service,OrcidAPI: 4. Bulk-Erstellung in Batches (max. 100 pro Request)
                loop Für jeden Batch (subList bis 100 Records)
                    Service->>Helper: convertToOrcid(batchSearchResultNew)
                    Helper-->>Service: orcidBodyNew (JSON)
                    Service->>Helper: createWork(orcid, orcidBodyNew, accessToken)
                    Helper->>OrcidAPI: POST /v3.0/{orcid}/works (Bulk)
                    OrcidAPI-->>Helper: JSON (bulk response mit put-codes)
                    Helper-->>Service: answerNew
                    Service->>Helper: parseBulkResponse(answerNew)
                    Helper-->>Service: List<OrcidBulkItem>
                    Service->>Helper: saveOrcidDbRecords(orcidRepo, authVO, batch, bulkItems, 'CREATE')
                    Helper->>OrcidRepo: saveAndFlush(orcidDbVO)
                end
            end
        end

        opt Zu aktualisierende Werke vorhanden (searchResultUpdate)
            rect rgb(255, 250, 235)
                Note over Service,OrcidAPI: 5. Einzelsatz-Aktualisierung (PUT)
                Service->>Helper: convertToOrcid(searchResultUpdate)
                Helper-->>Service: orcidBodyUpdate
                Service->>Helper: getWorksBySourceWorkId(orcidBodyUpdate)
                Helper-->>Service: Map<itemId, workNode>
                loop Für jeden zu aktualisierenden Datensatz
                    Service->>Helper: setPutCode(workNode, putCode)
                    Service->>Helper: updateWork(orcid, singleWorkJson, accessToken, putCode)
                    Helper->>OrcidAPI: PUT /v3.0/{orcid}/work/{putCode}
                    OrcidAPI-->>Helper: JSON
                    Helper-->>Service: answerUpdate
                    Service->>Helper: saveOrcidDbRecord(orcidRepo, authVO, itemId, putCode, answerUpdate, 'UPDATE')
                    Helper->>OrcidRepo: saveAndFlush(orcidDbVO)
                end
            end
        end
    end

    Service-->>Controller: void
    Controller-->>Scheduler: 200 OK
```

---

## 2. Flussdiagramm (Entscheidungs- und Kontrollfluss)

```mermaid
flowchart TD
    A([Start: GET /orcid/getVerifiedOrcidAuthorizations]) --> B[OrcidRestController.getVerifiedOrcidAuthorizations]
    B --> C[OrcidAuthorizationRepository.findByVerifiedTrue]
    
    C --> D{Verifizierte Autoren vorhanden?}
    D -- Nein --> RetEnd([Return: 200 OK])
    D -- Ja --> E[Schleife: Nächster verifizierter Autor]
    
    E --> F["Publikationen abfragen<br/>↳ OrcidHelper.doOrcidQuery(...)"]
    F --> G[Klassifizierung: Item in OrcidRepository vorhanden?]
    
    G --> H{putCode in DB vorhanden?}
    H -- Nein --> I[Zu searchResultNew hinzufügen]
    H -- Ja --> J[Zu searchResultUpdate & putCodesByItemId hinzufügen]
    
    I & J --> K{Neue Records vorhanden?}
    
    K -- Ja --> L["Aufteilung in Batches (max. 100 Items)<br/>↳ OrcidHelper.convertToOrcid(...)<br/>↳ POST an ORCID: OrcidHelper.createWork(...)<br/>↳ DB speichern: OrcidHelper.saveOrcidDbRecords(..., CREATE)"]
    K -- Nein --> M{Update Records vorhanden?}
    L --> M
    
    M -- Ja --> N["Für jedes Record einzeln:<br/>↳ PutCode in Work-JSON setzen<br/>↳ PUT an ORCID: OrcidHelper.updateWork(...)<br/>↳ DB speichern: OrcidHelper.saveOrcidDbRecord(..., UPDATE)"]
    M -- Nein --> O{Weitere Autoren?}
    N --> O
    
    O -- Ja --> E
    O -- Nein --> RetEnd
```

---

## 3. Schritt-für-Schritt-Übersicht

1. **Controller-Einstieg & Ladevorgang**:
   - `OrcidRestController.getVerifiedOrcidAuthorizations()` stößt die Synchronisierung der Publikationen aller verifizierten Autoren an.
   - `OrcidAuthorizationRepository.findByVerifiedTrue()` lädt alle Benutzer mit aktiver, bestätigter ORCID-Verbindung (`verified = true`).

2. **Abfrage relevanter Publikationen**:
   - Für jeden Autor wird `OrcidHelper.doOrcidQuery(...)` ausgeführt.
   - `PubItemService.searchDetailed(...)` führt eine Elasticsearch-Suche nach allen Publikationen der zugehörigen CoNE-Autoren-ID (`coneIdAuthor`) durch.

3. **Klassifizierung in neue vs. geänderte Datensätze**:
   - Die Suchergebnisse werden iteriert und gegen `OrcidRepository.findByConeIdAuthorAndItemId(...)` geprüft:
     - **Neu (`searchResultNew`)**: Noch kein Eintrag in `OrcidRepository` vorhanden oder noch kein `putCode` vergeben.
     - **Update (`searchResultUpdate`)**: Eintrag mit existierendem `putCode` in der Datenbank vorhanden.

4. **Batch-Verarbeitung neuer Publikationen (`CREATE`)**:
   - `searchResultNew` wird in Blöcke von maximal 100 Datensätzen aufgeteilt (Vorgabe der ORCID-Bulk-API).
   - Jeder Block wird via `OrcidHelper.convertToOrcid(...)` in die ORCID-Bulk-Payload konvertiert.
   - `OrcidHelper.createWork(...)` sendet den Batch per HTTP-POST an `/v3.0/{orcid}/works`.
   - Die Antwort wird geparst (`OrcidHelper.parseBulkResponse`) und alle `putCode`s werden via `OrcidHelper.saveOrcidDbRecords(...)` im `OrcidRepository` mit Status `CREATE` persistiert.

5. **Einzelverarbeitung geänderter Publikationen (`UPDATE`)**:
   - `searchResultUpdate` wird konvertiert und in einzelne Werke aufgeteilt (`OrcidHelper.getWorksBySourceWorkId`).
   - Da die ORCID-API Updates nicht als Bulk-Payload erlaubt, wird jeder Datensatz einzeln abgearbeitet:
     - Der zugehörige `putCode` wird in den JSON-Knoten eingefügt (`OrcidHelper.setPutCode`).
     - `OrcidHelper.updateWork(...)` führt einen HTTP-PUT gegen `/v3.0/{orcid}/work/{putCode}` aus.
     - `OrcidHelper.saveOrcidDbRecord(...)` aktualisiert den Status und das Änderungsdatum im `OrcidRepository` mit Status `UPDATE`.

6. **Abschluss**:
   - Nach Abarbeitung aller Autoren liefert der Controller HTTP 200 OK zurück.
