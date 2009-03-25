/*
 * CDDL HEADER START The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License. You can
 * obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for the
 * specific language governing permissions and limitations under the License. When distributing Covered Code, include
 * this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner] CDDL HEADER END
 */
/*
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH
 * and Max-Planck- Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */
package de.mpg.escidoc.services.cone.util;

import java.io.FileWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import de.mpg.escidoc.services.common.exceptions.TechnicalException;
import de.mpg.escidoc.services.common.util.ResourceUtil;

/**
 * Helper class to generate an RDF file from the initial database.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class CreateJournalRdf
{
    /**
     * Main-Method.
     * 
     * @param args No arguments needed
     * @throws Exception Any exception
     */
    public static void main(String[] args) throws Exception
    {
        new CreateJournalRdf();
    }

    /**
     * Default constructor. Contains the main routine.
     * 
     * @throws Exception Any exception
     */
    private CreateJournalRdf() throws Exception
    {
        Connection connection = getConnection();
        String sql = "SELECT * FROM qa_journals WHERE sfxid IS NOT NULL AND sfxid != ''";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        FileWriter fileWriter = new FileWriter(ResourceUtil.getResourceAsFile("src/main/resources/journals.rdf"));
        StringWriter stringWriter = new StringWriter();
        fileWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<!DOCTYPE rdf:RDF>\n"
                + "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "
                + "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:dcterms=\"http://purl.org/dc/terms/\" "
                + "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">");
        while (rs.next())
        {
            int rm = rs.getInt("rm");
            String sfxId = rs.getString("sfxid");
            String sfxTitle = rs.getString("sfxtitle");
            String eDocTitle = rs.getString("edoctitle");
            String eDocAbbrev = rs.getString("edocabbrev");
            String sfxIssn = rs.getString("sfxIssn");
            String sfxPublisher = rs.getString("sfxpublisher");
            String sfxPlace = rs.getString("sfxplace");
            System.out.println(sfxId + ": " + eDocTitle);
            fileWriter.append("\t<rdf:Description rdf:about=\"urn:sfx:");
            fileWriter.append(xmlFormat(sfxId));
            fileWriter.append("\">\n");
            fileWriter.append("\t\t<dc:identifier>");
            fileWriter.append(xmlFormat(sfxId));
            fileWriter.append("</dc:identifier>\n");
            if (eDocTitle != null && !"".equals(eDocTitle))
            {
                fileWriter.append("\t\t<dc:title>");
                fileWriter.append(xmlFormat(eDocTitle));
                fileWriter.append("</dc:title>\n");
            }
            else
            {
                throw new RuntimeException("Empty title: sfxid=" + sfxId);
            }
            if (eDocAbbrev != null && !"".equals(eDocAbbrev))
            {
                fileWriter.append("\t\t<dcterms:alternative>");
                fileWriter.append(xmlFormat(eDocAbbrev));
                fileWriter.append("</dcterms:alternative>\n");
            }
            if (sfxPublisher != null && !"".equals(sfxPublisher))
            {
                fileWriter.append("\t\t<dc:publisher>");
                fileWriter.append(xmlFormat(sfxPublisher));
                fileWriter.append("</dc:publisher>\n");
            }
            if (sfxPlace != null && !"".equals(sfxPlace))
            {
                fileWriter.append("\t\t<dcterms:publisher>");
                fileWriter.append(xmlFormat(sfxPlace));
                fileWriter.append("</dcterms:publisher>\n");
            }
            if (sfxIssn != null && !"".equals(sfxIssn))
            {
                fileWriter.append("\t\t<dcterms:identifier>");
                fileWriter.append(xmlFormat(sfxIssn));
                fileWriter.append("</dcterms:identifier>\n");
            }
            fileWriter.append("\t</rdf:Description>\n");
        }
        fileWriter.append("</rdf:RDF>\n");
        fileWriter.close();
    }

    private String xmlFormat(String str)
    {
        str = str.replace("&", "&amp;");
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        return str;
    }

    /**
     * Initialize Connection to database.
     * 
     * @throws TechnicalException Any exception.
     */
    private Connection getConnection() throws TechnicalException
    {
        try
        {
            Context ctx = new InitialContext();
            DataSource dataSource = (DataSource) ctx.lookup("Journals");
            return dataSource.getConnection();
        }
        catch (Exception e)
        {
            throw new TechnicalException(e);
        }
    }
}
