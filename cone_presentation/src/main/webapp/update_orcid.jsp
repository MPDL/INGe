<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="de.mpg.mpdl.inge.cone.Querier" %>
<%@ page import="de.mpg.mpdl.inge.cone.QuerierFactory" %>
<%@ page import="de.mpg.mpdl.inge.cone.TreeFragment" %>
<%@ page import="de.mpg.mpdl.inge.cone.LocalizedString" %>
<%@ page import="de.mpg.mpdl.inge.cone.LocalizedTripleObject" %>
<%@ page import="de.mpg.mpdl.inge.cone.web.Login" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<%
// https://ihr-server.de/cone/update_orcid.jsp?uri=persons/12345&orcid=0000-0002-1825-0097
    request.setCharacterEncoding("UTF-8");
    String uri = request.getParameter("uri");
    String orcid = request.getParameter("orcid");
    String message = "";
    String error = "";

    if (uri != null && orcid != null && !uri.isEmpty() && !orcid.isEmpty()) {
        boolean loggedIn = Login.getLoggedIn(request);
        Object authorized = request.getSession().getAttribute("edit_open_vocabulary");

        if (loggedIn && authorized != null) {
            try {
                Querier querier = QuerierFactory.newQuerier(loggedIn);
                String modelName = "persons";
                TreeFragment person = querier.details(modelName, uri, "*");

                if (person.exists()) {
                    String identifierUri = "http://purl.org/dc/elements/1.1/identifier";
                    String idTypeUri = "http://www.w3.org/2001/XMLSchema-instance type";
                    String valueUri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#value";

                    List<LocalizedTripleObject> identifiers = person.get(identifierUri);
                    boolean found = false;
                    boolean changed = false;

                    if (identifiers != null) {
                        for (LocalizedTripleObject obj : identifiers) {
                            if (obj instanceof TreeFragment) {
                                TreeFragment idFragment = (TreeFragment) obj;
                                List<LocalizedTripleObject> types = idFragment.get(idTypeUri);
                                if (types != null && !types.isEmpty()) {
                                    for (LocalizedTripleObject typeObj : types) {
                                        if (typeObj.toString().equals("ORCID")) {
                                            found = true;
                                            List<LocalizedTripleObject> currentValues = idFragment.get(valueUri);
                                            String currentVal = (currentValues != null && !currentValues.isEmpty()) ? currentValues.get(0).toString() : "";

                                            if (!currentVal.equals(orcid)) {
                                                // Gefunden: ORCID vorhanden, Wert unterscheidet sich -> ersetzen
                                                List<LocalizedTripleObject> newValues = new ArrayList<LocalizedTripleObject>();
                                                newValues.add(new LocalizedString(orcid));
                                                idFragment.put(valueUri, newValues);
                                                changed = true;
                                            } else {
                                                // Wert ist bereits identisch
                                                message = "Die ORCID ist bereits aktuell (" + orcid + "). Keine Änderung notwendig.";
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            if (found) break;
                        }
                    }

                    if (!found) {
                        // Nicht gefunden: Neuen ORCID-Identifier anlegen
                        TreeFragment newIdFragment = new TreeFragment();
                        List<LocalizedTripleObject> types = new ArrayList<LocalizedTripleObject>();
                        types.add(new LocalizedString("ORCID"));
                        newIdFragment.put(idTypeUri, types);

                        List<LocalizedTripleObject> values = new ArrayList<LocalizedTripleObject>();
                        values.add(new LocalizedString(orcid));
                        newIdFragment.put(valueUri, values);

                        if (identifiers == null) {
                            identifiers = new ArrayList<LocalizedTripleObject>();
                            person.put(identifierUri, identifiers);
                        }
                        identifiers.add(newIdFragment);
                        changed = true;
                    }

                    if (changed) {
                        // Speichern durch Löschen und Neu-Erstellen
                        querier.delete(modelName, uri);
                        querier.create(modelName, uri, person);
                        message = "ORCID erfolgreich aktualisiert für URI: " + uri;
                    }
                } else {
                    error = "Person mit URI '" + uri + "' wurde in CoNE nicht gefunden.";
                }
            } catch (Exception e) {
                error = "Ein technischer Fehler ist aufgetreten: " + e.getMessage();
            }
        } else if (!loggedIn) {
            error = "Sie sind nicht eingeloggt. Bitte loggen Sie sich zuerst ein.";
        } else {
            error = "Sie haben keine Berechtigung, Einträge in diesem Vokabular zu bearbeiten.";
        }
    }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>CoNE - ORCID Update</title>
    <style type="text/css">
        body { font-family: Arial, sans-serif; margin: 40px; }
        .success { color: #28a745; font-weight: bold; }
        .error { color: #dc3545; font-weight: bold; }
        .form-group { margin-bottom: 15px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"] { width: 100%; max-width: 500px; padding: 8px; box-sizing: border-box; }
        input[type="submit"] { padding: 10px 20px; cursor: pointer; background-color: #007bff; color: white; border: none; border-radius: 4px; }
        input[type="submit"]:hover { background-color: #0056b3; }
    </style>
</head>
<body>
    <h1>ORCID für Person-Objekt aktualisieren</h1>
    <p>Geben Sie die URI der Person und den neuen ORCID-Wert ein.</p>

    <% if (!message.isEmpty()) { %>
        <p class="success"><%= message %></p>
    <% } %>
    <% if (!error.isEmpty()) { %>
        <p class="error"><%= error %></p>
    <% } %>

    <form method="post" action="update_orcid.jsp">
        <div class="form-group">
            <label for="uri">Person URI (z. B. persons/12345):</label>
            <input type="text" id="uri" name="uri" value="<%= (uri != null) ? uri : "" %>" />
        </div>
        <div class="form-group">
            <label for="orcid">Neuer ORCID-Wert:</label>
            <input type="text" id="orcid" name="orcid" value="<%= (orcid != null) ? orcid : "" %>" placeholder="0000-0000-0000-0000" />
        </div>
        <input type="submit" value="Änderung speichern" />
    </form>

    <div style="margin-top: 20px;">
        <a href="index.jsp">Zurück zur CoNE Startseite</a>
    </div>
</body>
</html>
