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
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.pubman.easySubmission;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.PubFileVOPresentation;
import de.mpg.escidoc.services.common.valueobjects.ContextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;

/**
 * Fragment class for the easy submission. This class provides all functionality for editing, saving and submitting a
 * PubItem within the easy submission process.
 *
 * @author: Tobias Schraut, created 04.04.2008
 * @version: $Revision$ $LastChangedDate$
 */
public class EasySubmissionSessionBean extends FacesBean
{
    public static final String BEAN_NAME = "EasySubmissionSessionBean";
    private static Logger logger = Logger.getLogger(EasySubmissionSessionBean.class);
    
    public static final String SUBMISSION_METHOD_MANUAL = "MANUAL";
    public static final String SUBMISSION_METHOD_FETCH_IMPORT = "FETCH_IMPORT";
    
    public static final String IMPORT_METHOD_EXTERNAL = "EXTERNAL";
    public static final String IMPORT_METHOD_BIBTEX = "BIBTEX";
    
    public static final String DATE_PUBLISHED_IN_PRINT = "DATE_PUBLISHED_IN_PRINT";
    
    public static final String ES_STEP1 = "STEP1";
    public static final String ES_STEP2 = "STEP2";
    public static final String ES_STEP3 = "STEP3";
    public static final String ES_STEP4 = "STEP4";
    public static final String ES_STEP5 = "STEP5";
    
    
    private String currentSubmissionMethod = SUBMISSION_METHOD_MANUAL;
    
    private String currentSubmissionStep = ES_STEP1;
    
    private String currentDateType = DATE_PUBLISHED_IN_PRINT;
    
    private String importMethod = EasySubmissionSessionBean.IMPORT_METHOD_EXTERNAL;
    
    private OrganizationVO currentlySelecting = null;
    
    private ContextVO context;
    
    //private PubItemVO currentItem;
    
    private List<PubFileVOPresentation> files = new ArrayList<PubFileVOPresentation>();
    
    private List<PubFileVOPresentation> locators = new ArrayList<PubFileVOPresentation>();
    
    private String selectedDate;
    
    //For import service
    private String currentExternalServiceType = "";
    private String currentFTLabel = "";
    private HtmlSelectOneRadio radioSelectFulltext = new HtmlSelectOneRadio();


	private boolean importSourceRefresh = false;
    public SelectItem[] FULLTEXT_OPTIONS;
    public SelectItem[] EXTERNAL_SERVICE_OPTIONS;
    
    private String genreBundle = "Genre_ARTICLE";

	/**
     * Public constructor.
     */
    public EasySubmissionSessionBean()
    {
        this.currentSubmissionStep = ES_STEP1;
        this.importSourceRefresh = false;

    }
    
	public String getCurrentSubmissionMethod() {
		return currentSubmissionMethod;
	}

	public void setCurrentSubmissionMethod(String currentSubmissionMethod) {
		this.currentSubmissionMethod = currentSubmissionMethod;
	}

	public String getCurrentSubmissionStep() {
		return currentSubmissionStep;
	}

	public void setCurrentSubmissionStep(String currentSubmissionStep) {
		this.currentSubmissionStep = currentSubmissionStep;
	}

	public ContextVO getContext() {
		return context;
	}

	public void setContext(ContextVO context) {
		this.context = context;
	}


	/*public PubItemVO getCurrentItem() {
		return currentItem;
	}


	public void setCurrentItem(PubItemVO currentItem) {
		this.currentItem = currentItem;
	}*/

	public List<PubFileVOPresentation> getFiles() {
		return files;
	}

	public void setFiles(List<PubFileVOPresentation> files) {
		this.files = files;
	}

	public List<PubFileVOPresentation> getLocators() {
		return locators;
	}

	public void setLocators(List<PubFileVOPresentation> locators) {
		this.locators = locators;
	}

	public String getCurrentDateType() {
		return currentDateType;
	}

	public void setCurrentDateType(String currentDateType) {
		this.currentDateType = currentDateType;
	}

	public String getImportMethod() {
		return importMethod;
	}

	public void setImportMethod(String importMethod) {
		this.importMethod = importMethod;
	}

	public String getCurrentExternalServiceType() {
		return currentExternalServiceType;
	}

	public void setCurrentExternalServiceType(String currentExternalServiceType) {
		this.currentExternalServiceType = currentExternalServiceType;
	}

	public OrganizationVO getCurrentlySelecting() {
		return currentlySelecting;
	}

	public void setCurrentlySelecting(OrganizationVO currentlySelecting) {
		this.currentlySelecting = currentlySelecting;
	}


	public String getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(String selectedDate) {
		this.selectedDate = selectedDate;
	}

	public String getCurrentFTLabel() {
		return currentFTLabel;
	}

	public void setCurrentFTLabel(String currentFTLabel) {
		this.currentFTLabel = currentFTLabel;
	}

	public boolean isImportSourceRefresh() {
		return importSourceRefresh;
	}


	public void setImportSourceRefresh(boolean importSourceRefresh) {
		this.importSourceRefresh = importSourceRefresh;
	}

	public SelectItem[] getFULLTEXT_OPTIONS() {
		return FULLTEXT_OPTIONS;
	}

	public void setFULLTEXT_OPTIONS(SelectItem[] fulltext_options) {
		FULLTEXT_OPTIONS = fulltext_options;
	}
	
	public SelectItem[] getEXTERNAL_SERVICE_OPTIONS() {
		return EXTERNAL_SERVICE_OPTIONS;
	}


	public void setEXTERNAL_SERVICE_OPTIONS(SelectItem[] external_service_options) {
		EXTERNAL_SERVICE_OPTIONS = external_service_options;
	}
	
	public HtmlSelectOneRadio getRadioSelectFulltext() {
		return radioSelectFulltext;
	}

	public void setRadioSelectFulltext(HtmlSelectOneRadio radioSelectFulltext) {
		this.radioSelectFulltext = radioSelectFulltext;
	}

	public String getGenreBundle() {
		return genreBundle;
	}

	public void setGenreBundle(String genreBundle) {
		this.genreBundle = genreBundle;
	}

}
