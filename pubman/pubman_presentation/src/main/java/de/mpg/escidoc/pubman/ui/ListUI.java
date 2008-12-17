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
package de.mpg.escidoc.pubman.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.component.UIComponentBase;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.ValueObjectWrapper;
import de.mpg.escidoc.services.common.valueobjects.ValueObject;

/**
 * Superclass for lists with a paginator and all functions to navigate through them.
 *
 * @author: Thomas Diebäcker, created 30.08.2007
 * @version: $Revision$ $LastChangedDate$
 */
public abstract class ListUI extends ContainerPanelUI implements ActionListener, ValueChangeListener
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ListUI.class);
    private static final int MAX_NUMBER_OF_PAGE_BUTTONS = 9; // should not be less than 4!
    private static final int NUMBER_OF_LAST_BUTTONS_IF_MAX_IS_EXCEEDED = 2; // should not be greater than
    // MAX_NUMBER_OF_PAGE_BUTTONS!
    public static final int SHOW_ITEMS_PER_PAGE_DEFAULT = 0; // this is the index for SHOW_ITEMS_PER_PAGE[] not the
    // value itself!
    // Inserted by FrM, 8.11.07: Set number of selected items for delete confirmation.
    protected HtmlInputHidden numberSelectedObjects = new HtmlInputHidden();
    // the lists of objects
    private List<? extends ValueObjectWrapper> allObjects = new ArrayList<ValueObjectWrapper>();
    private List<? extends ValueObjectWrapper> objectsToDisplay = new ArrayList<ValueObjectWrapper>();
    /** the page that is currently shown */
    protected int currentPage = 1;
    protected String actionMethodForTitle = null;
    private boolean singleView = false;
    private UIPaginatorControl paginatorControlTop = new UIPaginatorControl();
    private UIPaginatorControl paginatorControlBottom = new UIPaginatorControl();
    // constants for comboBoxes
    public static final int SHOW_ITEMS_PER_PAGE[] = { 10, 25, 50, 100, 250 };
    public static final SelectItem[] SHOW_SELECTITEMS = new SelectItem[] {
            new SelectItem("0", new Integer(SHOW_ITEMS_PER_PAGE[0]).toString()),
            new SelectItem("1", new Integer(SHOW_ITEMS_PER_PAGE[1]).toString()),
            new SelectItem("2", new Integer(SHOW_ITEMS_PER_PAGE[2]).toString()),
            new SelectItem("3", new Integer(SHOW_ITEMS_PER_PAGE[3]).toString()),
            new SelectItem("4", new Integer(SHOW_ITEMS_PER_PAGE[4]).toString()) };

    /**
     * Default constructor.
     */
    public ListUI()
    {
    }

    /**
     * Public constructor.
     * 
     * @param allObjects the list of objects that should be displayed in this ListUI
     */
    public ListUI(List<? extends ValueObjectWrapper> allObjects)
    {
        this(allObjects, false, null);
    }

    /**
     * Public constructor.
     * 
     * @param allObjects the list of objects that should be displayed in this ListUI
     */
    public ListUI(List<? extends ValueObjectWrapper> allObjects, String actionMethodForTitle)
    {
        this(allObjects, false, actionMethodForTitle);
    }

    /**
     * Public constructor.
     * 
     * @param allObjects the list of objects that should be displayed in this ListUI
     * @param singleView defines wether only one item of the list should be shown at once or if the number of items
     *            shown can be choosen from the comboBox
     */
    public ListUI(List<? extends ValueObjectWrapper> allObjects, boolean singleView, String actionMethodForTitle)
    {
        this.singleView = singleView;
        this.actionMethodForTitle = actionMethodForTitle;
        // Set allObjects through set method, so the objects to display will be also set.
        // This method has to be called first as the creation of the page buttons depends on it!
        this.setAllObjects(allObjects);
        this.panTitleBar.getChildren().add(1, this.paginatorControlTop);
        this.panFooter.getChildren().add(0, this.paginatorControlBottom);
        this.numberSelectedObjects.setId("noso");
        this.numberSelectedObjects.setValue(getNumberOfSelectedObjects());
        this.panFooter.getChildren().add(1, this.numberSelectedObjects);
    }

    /**
     * Instanciates a new single item and adds it to the container for display.
     * 
     * @param valueObjectWrapper the wrapper with the ValueObject which should be displayed in the UI
     * @return the ContainerPanelUI in which the new Item is displayed
     */
    protected abstract ContainerPanelUI displayObject(ValueObjectWrapper valueObjectWrapper);

    /**
     * Displays all objects that are currently in the list of objects to display.
     */
    protected void displayObjects()
    {
        // clear the lists
        this.panContainer.getChildren().clear();
        // display every single object; object is added to container by implementing subclass
        for (int i = 0; i < this.objectsToDisplay.size(); i++)
        {
            this.displayObject(this.objectsToDisplay.get(i));
        }
    }

    /**
     * Calculates the number of pages that are needed with the currently selected values.
     * 
     * @return the number of pages
     */
    private int calculateNumberOfPages()
    {
        BigDecimal numberOfObjects = new BigDecimal(this.allObjects.size());
        BigDecimal numberOfObjectsPerPage = new BigDecimal(this.calculateNumberOfObjectsPerPage());
        BigDecimal numberOfPageButtons = numberOfObjects.divide(numberOfObjectsPerPage, BigDecimal.ROUND_UP);
        return numberOfPageButtons.intValue();
    }

    /**
     * Calculates the objects that should be displayed.
     */
    private List<? extends ValueObjectWrapper> calculateFirstObjectsToDisplay()
    {
        List<? extends ValueObjectWrapper> calculatedFirstObjectsToDisplay = new ArrayList<ValueObjectWrapper>();
        if (this.allObjects.size() > 0)
        {
            int numberOfObjectsDisplayable = Math.min(this.allObjects.size(), this.calculateNumberOfObjectsPerPage());
            calculatedFirstObjectsToDisplay = this.allObjects.subList(0, numberOfObjectsDisplayable);
        }
        return calculatedFirstObjectsToDisplay;
    }

    /**
     * Returns the number of objects as chosen in the ComboBox cboNumberOfItemsToShow or returns the first Step
     * (default: 10) in the constants if the number has not been chosen yet (e.g. the page is still initializing).
     * 
     * @return the numberOfObjectsPerPage
     */
    private int calculateNumberOfObjectsPerPage()
    {
        if (this.singleView)
        {
            // show only one item per page when single view mode is enabled
            return 1;
        }
        else
        {
            int numberOfObjectsPerPage = ListUI.SHOW_ITEMS_PER_PAGE[ListUI.SHOW_ITEMS_PER_PAGE_DEFAULT];
            if (this.paginatorControlTop.cboNumberOfItemsToShow.getValue() != null)
            {
                int value = new Integer((String)this.paginatorControlTop.cboNumberOfItemsToShow.getValue());
                numberOfObjectsPerPage = ListUI.SHOW_ITEMS_PER_PAGE[value];
            }
            return numberOfObjectsPerPage;
        }
    }

    /**
     * Calculates the objects that should be displayed on a certain page.
     * 
     * @param page
     */
    private List<? extends ValueObjectWrapper> calculateObjectsForPage(int page)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Calculating objects for page No.: " + page + "...");
        }
        List<? extends ValueObjectWrapper> calculatedObjectsForPage = new ArrayList<ValueObjectWrapper>();
        if (this.allObjects.size() > 0)
        {
            int numberOfObjectsPerPage = this.calculateNumberOfObjectsPerPage();
            int indexOfFirstObject = (page - 1) * numberOfObjectsPerPage;
            int indexOfLastObject = indexOfFirstObject + numberOfObjectsPerPage - 1;
            if (indexOfLastObject >= this.allObjects.size())
            {
                indexOfLastObject = this.allObjects.size() - 1;
            }
            calculatedObjectsForPage = this.allObjects.subList(indexOfFirstObject, indexOfLastObject + 1);
        }
        return calculatedObjectsForPage;
    }

    /**
     * Calculates the page where an object will be displayed.
     * 
     * @param object the object for wihich the page should be calculated
     * @return the page where the object would be displayed
     */
    private int calculatePageForObject(ValueObjectWrapper object)
    {
        BigDecimal positionOfObject = new BigDecimal(this.allObjects.indexOf(object) + 1);
        BigDecimal numberOfObjectsPerPage = new BigDecimal(this.calculateNumberOfObjectsPerPage());
        BigDecimal page = new BigDecimal(0);
        page.setScale(0, BigDecimal.ROUND_UP);
        page = positionOfObject.divide(numberOfObjectsPerPage, BigDecimal.ROUND_UP);
        page = page.setScale(0, BigDecimal.ROUND_UP);
        if (logger.isDebugEnabled())
        {
            logger.debug("Calculated page for object: " + page);
        }
        return page.intValue();
    }

    protected List<? extends ValueObjectWrapper> getAllObjects()
    {
        return this.allObjects;
    }

    /**
     * Sets all Objects in the lists and initially set the first objects to be displayed.
     * 
     * @param allObjects
     */
    protected void setAllObjects(List<? extends ValueObjectWrapper> allObjects)
    {
        this.allObjects = allObjects;
        this.setObjectsToDisplay(1, this.calculateFirstObjectsToDisplay());
        this.paginatorControlTop.lblItemCount.setValue(new Integer(this.allObjects.size()).toString());
        this.paginatorControlBottom.lblItemCount.setValue(new Integer(this.allObjects.size()).toString());
    }

    protected List<? extends ValueObjectWrapper> getObjectsToDisplay()
    {
        return this.objectsToDisplay;
    }

    /**
     * Sets the objects that should be displayed. This list is displayed be the method displayObjects(). Also sets the
     * current page and disables back and forward buttons if needed.
     * 
     * @param page
     * @param objectsToDisplay
     */
    private void setObjectsToDisplay(int page, List<? extends ValueObjectWrapper> objectsToDisplay)
    {
        this.currentPage = page;
        // disable/enable back and forward buttons, depending on the current page
        this.paginatorControlTop.btBack.setDisabled(currentPage == 1);
        this.paginatorControlTop.btBack.setStyleClass(this.paginatorControlTop.btBack.isDisabled() ? "disabled" : null);
        this.paginatorControlTop.btForward.setDisabled(currentPage == this.calculateNumberOfPages());
        this.paginatorControlTop.btForward
                .setStyleClass(this.paginatorControlTop.btForward.isDisabled() ? "disabled" : null);
        this.paginatorControlTop.btFirst.setDisabled(currentPage == 1);
        this.paginatorControlTop.btFirst
                .setStyleClass(this.paginatorControlTop.btFirst.isDisabled() ? "disabled" : null);
        this.paginatorControlTop.btLast.setDisabled(currentPage == this.calculateNumberOfPages());
        this.paginatorControlTop.btLast.setStyleClass(this.paginatorControlTop.btLast.isDisabled() ? "disabled" : null);
        this.paginatorControlBottom.btBack.setDisabled(this.paginatorControlTop.btBack.isDisabled());
        this.paginatorControlBottom.btBack.setStyleClass(this.paginatorControlTop.btBack.getStyleClass());
        this.paginatorControlBottom.btForward.setDisabled(this.paginatorControlTop.btForward.isDisabled());
        this.paginatorControlBottom.btForward.setStyleClass(this.paginatorControlTop.btForward.getStyleClass());
        this.paginatorControlBottom.btFirst.setDisabled(this.paginatorControlTop.btFirst.isDisabled());
        this.paginatorControlBottom.btFirst.setStyleClass(this.paginatorControlTop.btFirst.getStyleClass());
        this.paginatorControlBottom.btLast.setDisabled(this.paginatorControlTop.btLast.isDisabled());
        this.paginatorControlBottom.btLast.setStyleClass(this.paginatorControlTop.btLast.getStyleClass());
        // refreshes the page buttons
        this.refreshPageButtons();
        this.objectsToDisplay = objectsToDisplay;
    }

    /**
     * Creates a single PageButton with the given text.
     * 
     * @param value the text that should be displayed in the button
     * @return a PageButton for the Paginator
     */
    private HtmlCommandButton createPageButton(String value)
    {
        HtmlCommandButton btPage = new HtmlCommandButton();
        btPage.setId(CommonUtils.createUniqueId(btPage));
        btPage.setValue(value);
        btPage.setImmediate(true);
        btPage.addActionListener(this);
        if (value.compareTo((new Integer(this.currentPage)).toString()) == 0)
        {
            btPage.setDisabled(true);
            btPage.setStyleClass("disabled");
        }
        return btPage;
    }

    /**
     * Creates PageButtons for the Paginator.
     * 
     * @return List of PageButtons
     */
    private ArrayList<UIComponentBase> createPageButtons()
    {
        ArrayList<UIComponentBase> pageButtons = new ArrayList<UIComponentBase>();
        int numberOfPages = this.calculateNumberOfPages();
        if (numberOfPages < ListUI.MAX_NUMBER_OF_PAGE_BUTTONS)
        {
            // number of pages does not exceed the maximum number of pages, so we simply create one row of page buttons
            for (int i = 1; i <= numberOfPages; i++)
            {
                pageButtons.add(this.createPageButton(new Integer(i).toString()));
            }
        }
        else
        {
            // the number of pages exceed the number of maximum pages, so we have to split the page buttons
            pageButtons.addAll(this.createSplittedPageButtons());
        }
        return pageButtons;
    }

    /**
     * Create a splitted row of page buttons.
     * 
     * @return a row of page buttons
     */
    private ArrayList<UIComponentBase> createSplittedPageButtons()
    {
        ArrayList<UIComponentBase> leftRowButtons = new ArrayList<UIComponentBase>();
        BigDecimal numberOfLeftRowButtons = new BigDecimal(ListUI.MAX_NUMBER_OF_PAGE_BUTTONS).subtract(new BigDecimal(
                ListUI.NUMBER_OF_LAST_BUTTONS_IF_MAX_IS_EXCEEDED));
        int numberOfPages = this.calculateNumberOfPages();
        BigDecimal middleOfLeftRowButtons = numberOfLeftRowButtons.divide(new BigDecimal(2));
        middleOfLeftRowButtons = middleOfLeftRowButtons.setScale(0, BigDecimal.ROUND_UP);
        // create the left row of page buttons (before the "...")
        int firstPage = (new BigDecimal(this.currentPage).subtract(middleOfLeftRowButtons)).intValue() + 1;
        if (firstPage < 1)
        {
            firstPage = 1;
        }
        int lastPage = firstPage + numberOfLeftRowButtons.intValue() - 1;
        // correction if we run into the buttons of the right row (behind the "...")
        int maxPage = numberOfPages - ListUI.NUMBER_OF_LAST_BUTTONS_IF_MAX_IS_EXCEEDED;
        int diffToMax = lastPage - maxPage;
        if (diffToMax > 0)
        {
            firstPage = firstPage - diffToMax;
            lastPage = firstPage + numberOfLeftRowButtons.intValue() - 1;
        }
        // add a "..." in front of the left row if neccessary
        if (firstPage > 1)
        {
            HtmlOutputText threeDots = new HtmlOutputText();
            threeDots.setId(CommonUtils.createUniqueId(threeDots));
            threeDots.setValue("...");
            leftRowButtons.add(threeDots);
        }
        // add the buttons of the left row
        for (int i = firstPage; i <= lastPage; i++)
        {
            leftRowButtons.add(this.createPageButton(new Integer(i).toString()));
        }
        // create the right row of page buttons
        // add a "..." in front of the right row if neccessary
        if (diffToMax < 0)
        {
            HtmlOutputText threeDots = new HtmlOutputText();
            threeDots.setId(CommonUtils.createUniqueId(threeDots));
            threeDots.setValue("...");
            leftRowButtons.add(threeDots);
        }
        // add the buttons of the right row
        for (int i = (numberOfPages - (ListUI.NUMBER_OF_LAST_BUTTONS_IF_MAX_IS_EXCEEDED - 1)); i <= numberOfPages; i++)
        {
            leftRowButtons.add(this.createPageButton(new Integer(i).toString()));
        }
        return leftRowButtons;
    }

    /**
     * Sorts the object list.
     * 
     * @param comparator for sorting
     * @param sortOrder the sorting order
     */
    public void sortObjectList(Comparator comparator, boolean sortOrderAscending)
    {
        // sort ascending or descending
        if (sortOrderAscending)
        {
            Collections.sort(this.allObjects, comparator);
        }
        else
        {
            Collections.sort(this.allObjects, Collections.reverseOrder(comparator));
        }
        // refresh the display of the list
        this.displayObjects();
    }

    /**
     * Action handler for user actions.
     * 
     * @param event the event of the action
     */
    public void processAction(ActionEvent event)
    {
        logger.debug("PROCESSACTION:" + event);
        if (event.getComponent() == this.paginatorControlTop.btFirst
                || event.getComponent() == this.paginatorControlBottom.btFirst)
        {
            // move to first page
            this.setObjectsToDisplay(1, this.calculateObjectsForPage(1));
        }
        else if (event.getComponent() == this.paginatorControlTop.btBack
                || event.getComponent() == this.paginatorControlBottom.btBack)
        {
            // move back one page
            int previousPage = this.currentPage - 1;
            if (previousPage < 1)
            {
                previousPage = this.calculateNumberOfPages();
            }
            this.setObjectsToDisplay(previousPage, this.calculateObjectsForPage(previousPage));
        }
        else if (event.getComponent() == this.paginatorControlTop.btForward
                || event.getComponent() == this.paginatorControlBottom.btForward)
        {
            // move forward one page
            int nextPage = this.currentPage + 1;
            if (nextPage > this.calculateNumberOfPages())
            {
                nextPage = 1;
            }
            this.setObjectsToDisplay(nextPage, ListUI.this.calculateObjectsForPage(nextPage));
        }
        else if (event.getComponent() == this.paginatorControlTop.btLast
                || event.getComponent() == this.paginatorControlBottom.btLast)
        {
            // move to last page
            int lastPage = this.calculateNumberOfPages();
            this.setObjectsToDisplay(lastPage, this.calculateObjectsForPage(lastPage));
        }
        else if (event.getComponent().getParent() == this.paginatorControlTop.panPageButtons
                || event.getComponent().getParent() == this.paginatorControlBottom.panPageButtons)
        {
            // get the page that should be displayed
            Object valueOfButton = ((HtmlCommandButton)event.getComponent()).getValue();
            int page = new Integer(valueOfButton.toString());
            // refresh list of objects to display
            this.setObjectsToDisplay(page, this.calculateObjectsForPage(page));
        }
        // display objects newly
        this.displayObjects();
    }

    /**
     * ValueChange handler for comboBoxes.
     * 
     * @param event the event of the ValueChange
     */
    public void processValueChange(ValueChangeEvent event)
    {
        if (event.getComponent() == this.paginatorControlTop.cboNumberOfItemsToShow
                || event.getComponent() == this.paginatorControlBottom.cboNumberOfItemsToShow)
        {
            // synchronize paginators
            if (event.getComponent() == this.paginatorControlTop.cboNumberOfItemsToShow)
            {
                this.paginatorControlBottom.cboNumberOfItemsToShow
                        .setValue(this.paginatorControlTop.cboNumberOfItemsToShow.getValue());
            }
            else if (event.getComponent() == this.paginatorControlBottom.cboNumberOfItemsToShow)
            {
                this.paginatorControlTop.cboNumberOfItemsToShow
                        .setValue(this.paginatorControlBottom.cboNumberOfItemsToShow.getValue());
            }
            // refresh PageButtons
            this.refreshPageButtons();
            // get the first object of the old display list
            int page = 0;
            if (this.objectsToDisplay.size() > 0)
            {
                page = this.calculatePageForObject(this.objectsToDisplay.get(0));
            }
            // refresh list of objects to display
            this.setObjectsToDisplay(page, this.calculateObjectsForPage(page));
            // display objects newly
            this.displayObjects();
        }
    }

    /**
     * Refreshes the page buttons.
     */
    private void refreshPageButtons()
    {
        this.paginatorControlTop.panPageButtons.getChildren().clear();
        this.paginatorControlTop.panPageButtons.getChildren().addAll(this.createPageButtons());
        this.paginatorControlBottom.panPageButtons.getChildren().clear();
        this.paginatorControlBottom.panPageButtons.getChildren().addAll(this.createPageButtons());
    }

    /**
     * Returns all valueObjects that are currently selected.
     * 
     * @return all valueObjects that are currently selected
     */
    public List<? extends ValueObject> getSelectedObjects()
    {
        List<ValueObject> selectedObjects = new ArrayList<ValueObject>();
        for (int i = 0; i < this.allObjects.size(); i++)
        {
            if (this.allObjects.get(i).getSelected())
            {
                selectedObjects.add(this.allObjects.get(i).getValueObject());
            }
        }
        return selectedObjects;
    }

    /**
     * Returns the number of valueObjects that are currently selected. Author: FrM, 7.11.2007
     * 
     * @return the number of valueObjects that are currently selected
     */
    public int getNumberOfSelectedObjects()
    {
        int result = 0;
        for (int i = 0; i < this.allObjects.size(); i++)
        {
            if (this.allObjects.get(i).getSelected())
            {
                result++;
            }
        }
        return result;
    }

    public HtmlInputHidden getNumberSelectedObjects()
    {
        return numberSelectedObjects;
    }

    public void setNumberSelectedObjects(HtmlInputHidden numberSelectedObjects)
    {
        this.numberSelectedObjects = numberSelectedObjects;
    }
}
