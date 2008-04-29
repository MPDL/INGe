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
package de.mpg.escidoc.pubman.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

/**
 * Class for Internationalization settings.
 *
 * @author: Tobias Schraut, created 04.07.2007
 * @version: $Revision: 23 $ $LastChangedDate: 2007-12-05 15:47:07 +0100 (Mi, 05 Dez 2007) $ Revised by ScT: 20.08.2007
 */
public class InternationalizationHelper
{

    public static final String BEAN_NAME = "InternationalizationHelper";
    private static Logger logger = Logger.getLogger(InternationalizationHelper.class);
    public static final String LABEL_BUNDLE = "Label";
    public static final String MESSAGES_BUNDLE = "Messages";
    public static final String HELP_PAGE_DE = "help/eSciDoc_help_de.html";
    public static final String HELP_PAGE_EN = "help/eSciDoc_help_en.html";
    private String selectedHelpPage;
    
    public List<String> test = new ArrayList<String>();
    
    
    
    Locale userLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();

    public InternationalizationHelper()
    {
        if (userLocale.getLanguage().equals("de"))
        {
            selectedHelpPage = HELP_PAGE_DE;
        }
        else
        {
            selectedHelpPage = HELP_PAGE_EN;
        }
    }

    // Getters and Setters
    public String getSelectedLabelBundle()
    {
        return LABEL_BUNDLE + "_" + userLocale.getLanguage();
    }

    public String getSelectedMessagesBundle()
    {
        return MESSAGES_BUNDLE + "_" + userLocale.getLanguage();
    }

    public String getSelectedHelpPage()
    {
        return selectedHelpPage;
    }

    public void toggleLocale(ActionEvent event)
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        //
        // toggle the locale
        Locale locale = null;
        Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
        String language = (String) map.get("language");
        String country = (String) map.get("country");
        try
        {
            locale = new Locale(language, country);
            fc.getViewRoot().setLocale(locale);
            //Locale.setDefault(locale);
            userLocale = locale;
            logger.debug("New locale: " + language + "_" + country + " : " + locale);
        }
        catch (Exception e)
        {
            logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
        }
        if (language.equals("de"))
        {
            selectedHelpPage = HELP_PAGE_DE;
        }
        else
        {
            selectedHelpPage = HELP_PAGE_EN;
        }
    }

    public Locale getUserLocale()
    {
        return userLocale;
    }

    public void setUserLocale(final Locale userLocale)
    {
        this.userLocale = userLocale;
    }

    public List<String> getTest()
    {
        if (test.isEmpty())
        {
            test.add("AAA");
            test.add("BBB");
        }
        return test;
    }

    public void setTest(List<String> test)
    {
        this.test = test;
    }
}
