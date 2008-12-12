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

package de.mpg.escidoc.pubman.viewItem.ui;

import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.ApplicationBean;
import de.mpg.escidoc.pubman.ui.ContainerPanelUI;
import de.mpg.escidoc.pubman.ui.HTMLElementUI;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.PubItemVOWrapper;
import de.mpg.escidoc.services.common.valueobjects.publication.PubItemVO;

/**
 * UI for viewing items in a brief context.
 *
 * @author: Tobias Schraut, created 30.08.2007
 * @version: $Revision: 1646 $ $LastChangedDate: 2007-12-05 17:48:05 +0100 (Mi, 05 Dez 2007) $
 */
public class ViewItemShortUI extends ContainerPanelUI
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ViewItemShortUI.class);

    private HTMLElementUI htmlElement = new HTMLElementUI();

    /**
     * Default constructor.
     */
    public ViewItemShortUI()
    {

    }

    /**
     * For JSF 1.2.
     *
     * @param context FacesContext.
     *
     * @return State object.
     */
    public Object processSaveState(final FacesContext context)
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }

    /**
     * For JSF 1.2.
     * @param context FacesContext.
     * @param state State object.
     */
    public void processRestoreState(final FacesContext context, final Object state)
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within
        // this component. We assume that the saved tree will have been restored 'behind' the children we put into it
        // from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--)
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }

    /**
     * Public constructor.
     *
     * @param pubItemVOWrapper Wrapper with item.
     */
    public ViewItemShortUI(final PubItemVOWrapper pubItemVOWrapper)
    {
        ApplicationBean applicationBean = (ApplicationBean) FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .getApplicationMap()
                .get(ApplicationBean.BEAN_NAME);

        this.getChildren().clear();
        this.setId(CommonUtils.createUniqueId(this));

        // *** CREATORS ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));

        HtmlOutputText lblCreators = new HtmlOutputText();
        bindComponentLabel(lblCreators, "ViewItemShort_lblCreators");
        this.getChildren().add(lblCreators);

        this.getChildren().add(htmlElement.getEndTag("div"));

        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));

        this.getChildren()
                .add(CommonUtils.getTextElementConsideringEmpty(getCreators(pubItemVOWrapper.getValueObject())));

        this.getChildren().add(htmlElement.getEndTag("div"));

        // *** DATES ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));

        HtmlOutputText lblDates = new HtmlOutputText();
        bindComponentLabel(lblDates, "ViewItemShort_lblDates");
        this.getChildren().add(lblDates);

        this.getChildren().add(htmlElement.getEndTag("div"));

        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));

        boolean empty = true;
        empty = displayDate(
                pubItemVOWrapper
                        .getValueObject()
                        .getMetadata()
                        .getDatePublishedInPrint(), "ViewItem_lblDatePublishedInPrint", empty);
        empty = displayDate(
                pubItemVOWrapper
                        .getValueObject()
                        .getMetadata()
                        .getDatePublishedOnline(), "ViewItem_lblDatePublishedOnline", empty);
        empty = displayDate(
                pubItemVOWrapper
                        .getValueObject()
                        .getMetadata()
                        .getDateAccepted(), "ViewItem_lblDateAccepted", empty);
        empty = displayDate(
                pubItemVOWrapper
                        .getValueObject()
                        .getMetadata()
                        .getDateSubmitted(), "ViewItem_lblDateSubmitted", empty);
        empty = displayDate(
                pubItemVOWrapper
                        .getValueObject()
                        .getMetadata()
                        .getDateModified(), "ViewItem_lblDateModified", empty);
        empty = displayDate(
                pubItemVOWrapper
                        .getValueObject()
                        .getMetadata()
                        .getDateCreated(), "ViewItem_lblDateCreated", empty);

        this.getChildren().add(htmlElement.getEndTag("div"));

        // *** GENRE ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle odd"));

        HtmlOutputText lblGenre = new HtmlOutputText();
        bindComponentLabel(lblGenre, "ViewItemShort_lblGenre");
        this.getChildren().add(lblGenre);

        this.getChildren().add(htmlElement.getEndTag("div"));

        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText odd"));

        if (pubItemVOWrapper.getValueObject().getMetadata().getGenre() != null)
        {
            HtmlOutputText valueGenre = new HtmlOutputText();
            bindComponentLabel(
                    valueGenre,
                    this.i18nHelper.convertEnumToString(pubItemVOWrapper.getValueObject().getMetadata().getGenre()));
            this.getChildren().add(valueGenre);
        }
        else
        {
            this.getChildren().add(CommonUtils.getTextElementConsideringEmpty(""));
        }

        this.getChildren().add(htmlElement.getEndTag("div"));

        // *** FILES ***
        //label
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemTitle"));

        HtmlOutputText lblFile = new HtmlOutputText();
        bindComponentLabel(lblFile, "ViewItemShort_lblFile");
        this.getChildren().add(lblFile);

        this.getChildren().add(htmlElement.getEndTag("div"));

        // value
        this.getChildren().add(htmlElement.getStartTagWithStyleClass("div", "itemText"));

        HtmlOutputText lblFiles = new HtmlOutputText();
        bindComponentValue(lblFiles, getFiles(pubItemVOWrapper.getValueObject()));
        this.getChildren().add(lblFiles);

        this.getChildren().add(htmlElement.getEndTag("div"));
    }

    /**
     * Distinguish between Persons and organization as creators and returns them formatted as string.
     * @param pubitemVo the pubitem that contains the creators
     * @return String the  formatted creators
     */
    private String getCreators(final PubItemVO pubitemVo)
    {
        StringBuffer creators = new StringBuffer();

        if (pubitemVo.getMetadata().getCreators() != null)
        {
            for (int i = 0; i < pubitemVo.getMetadata().getCreators().size(); i++)
            {
                if (pubitemVo.getMetadata().getCreators().get(i).getPerson() != null)
                {
                    if (pubitemVo.getMetadata().getCreators().get(i).getPerson().getFamilyName() != null)
                    {
                        creators.append(pubitemVo.getMetadata().getCreators().get(i).getPerson().getFamilyName());
                    }
                    if (pubitemVo
                            .getMetadata()
                            .getCreators()
                            .get(i)
                            .getPerson()
                            .getFamilyName() != null
                        && pubitemVo
                            .getMetadata()
                            .getCreators()
                            .get(i)
                            .getPerson()
                            .getGivenName() != null)
                    {
                        creators.append(", ");
                    }
                    if (pubitemVo.getMetadata().getCreators().get(i).getPerson().getGivenName() != null)
                    {
                        creators.append(pubitemVo.getMetadata().getCreators().get(i).getPerson().getGivenName());
                    }
                }
                else if (pubitemVo.getMetadata().getCreators().get(i).getOrganization() != null)
                {
                    if (pubitemVo.getMetadata().getCreators().get(i).getOrganization().getName().getValue() != null)
                    {
                        creators.append(
                                pubitemVo.getMetadata().getCreators().get(i).getOrganization().getName().getValue());
                    }
                }
                if (i < pubitemVo.getMetadata().getCreators().size() - 1)
                {
                    creators.append("; ");
                }
            }
        }
        return creators.toString();
    }

    private boolean displayDate(final String date, final String label, final boolean empty)
    {
        if (empty && date != null && !date.trim().equals(""))
        {
            HtmlOutputText lblDate = new HtmlOutputText();
            bindComponentValue(lblDate, "#{lbl." + label + "}: " + date);
            this.getChildren().add(lblDate);
            return false;
        }
        else
        {
            return empty;
        }

    }

    /**
     * This method examines the pubitem concerning its files and generates
     * a display string for the page according to the number of files detected.
     *
     * @param pubitemVo the pubitem to be examined
     * @return String the formatted String to display the occurencies of files
     */
    private String getFiles(final PubItemVO pubitemVo)
    {
        StringBuffer files = new StringBuffer();

        if (pubitemVo.getFiles() != null)
        {
            files.append(pubitemVo.getFiles().size());

            // if there is only 1 file, display "File attached", otherwise display "Files attached" (plural)
            if (pubitemVo.getFiles().size() == 1)
            {
                files.append(" #{lbl.ViewItemShort_lblFileAttached}");
            }
            else
            {
                files.append(" #{lbl.ViewItemShort_lblFilesAttached}");
            }
        }
        return files.toString();
    }
}
