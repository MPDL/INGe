/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/

package de.mpg.escidoc.services.cone.mss;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.jrdf.graph.Triple;

public class TripleSearchBean
{
    private List<Triple> tripleList;
    private List<String[]> modelList;
    private HtmlDataTable modelTable, triplesTable;
    private String[] rowData;
    private Triple tripleRow;
    private File file2display;
    private int selectedRowId = -1;
    private String subject;
    private String predicate;
    private String object;
    private String selectedModel, selectedModelType, selectedFile, selectedTriple;
    private String allowedActions;
    private TripleSearch ts;
    private static final Logger log = Logger.getLogger(TripleSearchBean.class);
    ResourceBundle mts = ResourceBundle.getBundle("properties.mulgara");

    public String getSelectedModel()
    {
        return selectedModel;
    }

    public void setSelectedModel(String selectedModel)
    {
        this.selectedModel = selectedModel;
    }

    public List<String[]> getModelList()
    {
        return modelList;
    }

    public void setModelList(List<String[]> modelList)
    {
        this.modelList = modelList;
    }

    public String getObject()
    {
        return object;
    }

    public void setObject(String object)
    {
        this.object = object;
    }

    public String getPredicate()
    {
        return predicate;
    }

    public void setPredicate(String predicate)
    {
        this.predicate = predicate;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public List<Triple> getTripleList()
    {
        return tripleList;
    }

    public void setTripleList(List<Triple> tripleList)
    {
        this.tripleList = tripleList;
    }

    public void subjectChanged(ValueChangeEvent vce)
    {
        this.setSubject((String)vce.getNewValue());
        System.out.println("subject: " + vce.getNewValue().toString());
    }

    public void predicateChanged(ValueChangeEvent vce)
    {
        this.setPredicate((String)vce.getNewValue());
        System.out.println("predicate: " + vce.getNewValue().toString());
    }

    public void objectChanged(ValueChangeEvent vce)
    {
        this.setObject((String)vce.getNewValue());
        System.out.println("object: " + vce.getNewValue().toString());
    }

    public void selectedModelChanged(ValueChangeEvent vce)
    {
        this.setSelectedModel((String)vce.getNewValue());
        System.out.println("selected Model: " + vce.getNewValue().toString());
    }

    public void selectedModelTypeChanged(ValueChangeEvent vce)
    {
        this.setSelectedModelType((String)vce.getNewValue());
        System.out.println("selected Model Type: " + vce.getNewValue().toString());
    }

    public void selectedFileChanged(ValueChangeEvent vce)
    {
        this.setSelectedFile((String)vce.getNewValue());
        System.out.println("selected File: " + vce.getNewValue().toString());
    }

    public void queryTriples()
    {
        ts = new TripleSearch();
        log.info("querying for: " + subject + " " + predicate + " " + object + " in " + selectedModel);
        tripleList = ts.queryGraph(selectedModel, subject, predicate, object);
        if (tripleList.size() <= 0)
        {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "", "querying for: " + subject + " " + predicate + " "
                            + object + " in " + selectedModel + " did not return any result !"));
        }
    }

    public void insertTriples()
    {
        ts = new TripleSearch();
        ts.updateTriple(1, selectedModel, subject, predicate, object);
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", "inserting: " + subject + " " + predicate + " "
                        + object + " into " + selectedModel));
    }

    public void deleteTriples()
    {
        ts = new TripleSearch();
        ts.updateTriple(0, selectedModel, subject, predicate, object);
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", "deleting: " + subject + " " + predicate + " "
                        + object + " from " + selectedModel));
    }

    public void createModel()
    {
        ts = new TripleSearch();
        ts.updateModel(1, selectedModel, selectedModelType, null);
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", "creating: " + selectedModel + " of type "
                        + selectedModelType));
    }

    public void deleteModel()
    {
        ts = new TripleSearch();
        ts.updateModel(0, selectedModel, null, null);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", "deleting: " + selectedModel));
    }

    public void backupModel()
    {
        ts = new TripleSearch();
        ts.updateModel(2, selectedModel, null, selectedFile);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", "saving: " + selectedModel + " to " + selectedFile));
    }

    public void restoreModel()
    {
        ts = new TripleSearch();
        ts.updateModel(4, selectedModel, null, selectedFile);
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "", "restoring: " + selectedModel + " from "
                        + selectedFile));
    }

    public List<String[]> listModels()
    {
        ts = new TripleSearch();
        modelList = ts.getModelList();
        return modelList;
    }

    public void listModelDetails()
    {
        selectedRowId = modelTable.getRowIndex();
        rowData = (String[])modelTable.getRowData();
        selectedModel = rowData[0];
        selectedModelType = rowData[1];
        allowedActions = rowData[2];
        log.info("row id: " + selectedRowId + " row data: " + selectedModel);
    }

    public void listTripleDetails()
    {
        selectedRowId = triplesTable.getRowIndex();
        tripleRow = (Triple)triplesTable.getRowData();
        if (selectedModelType.equals("FileSystemModel"))
        {
            try
            {
                String line;
                StringBuffer sb = new StringBuffer();
                file2display = new File(new URI(tripleRow.getSubject().toString()));
                BufferedReader in = new BufferedReader(new FileReader(file2display));
                while ((line = in.readLine()) != null)
                {
                    sb.append(line);
                    sb.append("\n");
                }
                selectedTriple = sb.toString();
                in.close();
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            selectedTriple = tripleRow.toString();
        }
        log.info("row id: " + selectedRowId + " row data: " + tripleRow.getSubject() + " " + tripleRow.getPredicate()
                + " " + tripleRow.getObject());
    }

    public String[] getRowData()
    {
        return rowData;
    }

    public void setRowData(String[] rowData)
    {
        this.rowData = rowData;
    }

    public int getSelectedRowId()
    {
        return selectedRowId;
    }

    public void setSelectedRowId(int selectedRowId)
    {
        this.selectedRowId = selectedRowId;
    }

    public HtmlDataTable getModelTable()
    {
        return modelTable;
    }

    public void setModelTable(HtmlDataTable modelTable)
    {
        this.modelTable = modelTable;
    }

    public String getAllowedActions()
    {
        return allowedActions;
    }

    public void setAllowedActions(String allowedActions)
    {
        this.allowedActions = allowedActions;
    }

    public String getSelectedModelType()
    {
        return selectedModelType;
    }

    public void setSelectedModelType(String selectedModelType)
    {
        this.selectedModelType = selectedModelType;
    }

    public String getSelectedFile()
    {
        return selectedFile;
    }

    public void setSelectedFile(String selectedFile)
    {
        this.selectedFile = selectedFile;
    }

    public HtmlDataTable getTriplesTable()
    {
        return triplesTable;
    }

    public void setTriplesTable(HtmlDataTable triplesTable)
    {
        this.triplesTable = triplesTable;
    }

    public Triple getTripleRow()
    {
        return tripleRow;
    }

    public void setTripleRow(Triple tripleRow)
    {
        this.tripleRow = tripleRow;
    }

    public String getSelectedTriple()
    {
        return selectedTriple;
    }

    public void setSelectedTriple(String selectedTriple)
    {
        this.selectedTriple = selectedTriple;
    }

    public File getFile2display()
    {
        return file2display;
    }

    public void setFile2display(File file2display)
    {
        this.file2display = file2display;
    }
}
