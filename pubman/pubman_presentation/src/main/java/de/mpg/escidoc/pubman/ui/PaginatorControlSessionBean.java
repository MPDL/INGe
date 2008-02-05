package de.mpg.escidoc.pubman.ui;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;
import javax.faces.model.SelectItem;

/**
 * This PaginatorControlSessionBean is used as counterpart for the paginatorControlTop.jspf
 * and  paginatorControlBottom.jspf and can be used to handle navigation through a list 
 * of items or objects. The only information needed to initialize this bean is the 
 * number of items the list contains. The values for the number of items per page to be
 * displayed as dropDown can be set using the numberOfItemsPerPageValues.
 * The number of pageButtons displayed is currently limited to seven 
 * (first - prev - page1 ... page7 - next - last)  and when possible, the button 
 * representing the current page will be marked and placed in the middle.   
 * 
 * Your list bean can register to the PaginatorControlSessionBean as a ValueChangeListener
 * to get informed, when the value representing the current page selected changes.
 * 
 * @author Mario Wagner
 */
public class PaginatorControlSessionBean
{
	public static final String BEAN_NAME = "PaginatorControlSessionBean";
	
	/** the number of items */
	private int itemCount;
	
	/** selected number of items to show per page */
	private int numberOfItemsPerPage;
	
	/** the number of pages and current page, min 1 */
	private int numberOfPages, currentPage;
	
	/** 
	 * the cureent value of this paginator is the combination 
	 * of numberOfPages and the currentPage to be displayed 
	 */
	private Integer[] currentValue = new Integer[]{new Integer(1), new Integer(1)};
	
	/** the number of items per page */
	private Integer[] numberOfItemsPerPageValues = new Integer[]{10, 25, 50, 100, 250};

	/** visibility flags for the page buttons */
	private boolean[] pageButtonVisible;
	
	/** display value for the page buttons */
	private String[] pageButtonText;
	
	/** the page button values */
	private int[] pageButtonValue;
	
	private HtmlSelectOneMenu cboNumberOfItemsPerPageTop = new HtmlSelectOneMenu();
	private HtmlSelectOneMenu cboNumberOfItemsPerPageBottom = new HtmlSelectOneMenu();

	private List<ValueChangeListener> vclList = new ArrayList<ValueChangeListener>();
	
	public PaginatorControlSessionBean()
	{
		setItemCount(100);
		processModelAndDisplayUpdate();
	}
	
	public void addValueChangeListener(ValueChangeListener vcl)
	{
		if (! vclList.contains(vcl))
		{
			vclList.add(vcl);
		}
	}

	public void removeValueChangeListener(ValueChangeListener vcl)
	{
		if (vclList.contains(vcl))
		{
			vclList.remove(vcl);
		}
	}
	
	/**
	 * Any registered ValueChangeListener will be informed on changes in the 
	 * value containing the Integer[numberOfItemsPerPage, currentPage]
	 *
	 */
	private void informValueChangeListener()
	{
		Object oldValue = new Integer[]{new Integer(currentValue[0].intValue()), new Integer(currentValue[1].intValue())};
		currentValue[0] = new Integer(numberOfItemsPerPage);
		currentValue[1] = new Integer(currentPage);
		ValueChangeEvent vce = new ValueChangeEvent(cboNumberOfItemsPerPageTop, oldValue, currentValue);
		for (ValueChangeListener vcl : vclList)
		{
			vcl.processValueChange(vce);
		}
	}
	
	/**
	 * ValueChangeListener method for the paginatorControl.jspf to get 
	 * informed when user changes the number of items to show per page 
	 * @param event
	 */
	public void processNumberOfItemsPerPageChanged(ValueChangeEvent event)
	{
		numberOfItemsPerPage = (Integer)event.getNewValue();
		if (event.getComponent().equals(cboNumberOfItemsPerPageTop))
		{
			cboNumberOfItemsPerPageBottom.setValue(numberOfItemsPerPage);
		}
		else if (event.getComponent().equals(cboNumberOfItemsPerPageBottom))
		{
			cboNumberOfItemsPerPageTop.setValue(numberOfItemsPerPage);
		}
		numberOfPages = itemCount / numberOfItemsPerPage;
		if (numberOfPages < 1)
		{
			numberOfPages = 1;
		}
		if (currentPage > numberOfPages)
		{
			currentPage = numberOfPages;
		}
		if (currentPage < 1)
		{
			currentPage = 1;
		}
		processModelAndDisplayUpdate();
		
		// enforce rendering of the response
//		FacesContext context = FacesContext.getCurrentInstance();
//	    context.renderResponse();
	}

	/**
	 * ActionListener method which will be invoked, when a paginators pageButton has 
	 * been pressed. A integer attribute is required for the currentPage evaluation.  
	 * @param event
	 */
	public void pageButtonPressed(ActionEvent event)
	{
		String currButton = (String)event.getComponent().getAttributes().get("pageButton");
		int button = Integer.parseInt(currButton);
		if (button < pageButtonValue.length)
		{
			currentPage = pageButtonValue[button];
		}
	}
	
	/**
	 * Navigation method to be called after the pageButtonPressed actionListener.
	 * Handles the internal model update and leads navigation back to redisplay the 
	 * current view.
	 * @return null
	 */
	public String pageButtonAction()
	{
		processModelAndDisplayUpdate();
		return null;
	}

	private void processModelAndDisplayUpdate()
	{
		pageButtonVisible = new boolean[8];
		pageButtonText = new String[8];
		pageButtonValue = new int[8];
		if (currentPage < 4)
		{
			for (int i = 1; i < 8; i++)
			{
				pageButtonVisible[i] = i <= numberOfPages;
				pageButtonText[i] = currentPage == i ? "(" + i + ")" : "" + i;
				pageButtonValue[i] = i;
			}
		}
		else if (currentPage > numberOfPages - 4)
		{
			for (int i = 1; i < 8; i++)
			{
				pageButtonVisible[i] = numberOfPages - 7 + i > 0;
				pageButtonText[i] = currentPage == numberOfPages - 7 + i ? "(" + (numberOfPages - 7 + i) + ")" : "" + (numberOfPages - 7 + i);
				pageButtonValue[i] = numberOfPages - 7 + i;
			}
		}
		else
		{
			for (int i = 1; i < 4; i++)
			{
				pageButtonVisible[i] = currentPage - 4 + i > 0;
				pageButtonText[i] = "" + (currentPage - 4 + i);
				pageButtonValue[i] = currentPage - 4 + i;
			}
			pageButtonVisible[4] = currentPage > 0;
			pageButtonText[4] = "(" + currentPage + ")";
			pageButtonValue[4] =  currentPage;
			for (int i = 5; i < 8; i++)
			{
				pageButtonVisible[i] = currentPage - 4 + i <= numberOfPages;
				pageButtonText[i] = "" + (currentPage - 4 + i);
				pageButtonValue[i] = currentPage - 4 + i;
			}
		}
		informValueChangeListener();
	}

	/**
	 * @return the selectItems for the number of items per page dropDown
	 */
	public SelectItem[] getNumberOfItemsPerPageOptions()
	{
		SelectItem[] returnValue = new SelectItem[numberOfItemsPerPageValues.length];
		for (int i = 0; i < numberOfItemsPerPageValues.length; i++)
		{
			returnValue[i] = new SelectItem(numberOfItemsPerPageValues[i]);
		}
		return returnValue;
	}

	/**
	 * Navigation call for the firstPage button
	 * @return null to redisplay current view
	 */
	public String firstPage()
	{
		if (currentPage > 1)
		{
			// currentPage is at first position - index 1
			currentPage = 1;
			processModelAndDisplayUpdate();
		}
		return null;
	}
	
	/**
	 * Navigation call for the previousPage button
	 * @return null to redisplay current view
	 */
	public String previousPage()
	{
		if (currentPage > 1)
		{
			currentPage--;
			processModelAndDisplayUpdate();
		}
		return null;
	}

	/**
	 * Navigation call for the nextPage button
	 * @return null to redisplay current view
	 */
	public String nextPage()
	{
		if (currentPage < numberOfPages)
		{
			currentPage++;
			processModelAndDisplayUpdate();
		}
		return null;
	}

	/**
	 * Navigation call for the lastPage button
	 * @return null to redisplay current view
	 */
	public String lastPage()
	{
		if (currentPage < numberOfPages)
		{
			currentPage = numberOfPages;
			processModelAndDisplayUpdate();
		}
		return null;
	}

	
	public int getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}

	public int getItemCount()
	{
		return itemCount;
	}

	public void setItemCount(int itemCount)
	{
		this.itemCount = itemCount;
		numberOfItemsPerPage = numberOfItemsPerPageValues[0];
		currentPage = 1;
		numberOfPages = itemCount / numberOfItemsPerPage;
		if (numberOfPages < 1)
		{
			numberOfPages = 1;
		}
		processModelAndDisplayUpdate();
	}

	public int getNumberOfItemsPerPage()
	{
		return numberOfItemsPerPage;
	}

	public void setNumberOfItemsPerPage(int numberOfItemsPerPage)
	{
		// this value will be set using the valueChangeListener only !!!
		// this.numberOfItemsPerPage = numberOfItemsPerPage;
		
	}

	public int getNumberOfPages()
	{
		return numberOfPages;
	}

	public void setNumberOfPages(int numberOfPages)
	{
		this.numberOfPages = numberOfPages;
	}

	
	public boolean[] getPageButtonVisible()
	{
		return pageButtonVisible;
	}

	public String[] getPageButtonText()
	{
		return pageButtonText;
	}

	public Integer[] getNumberOfItemsPerPageValues()
	{
		return numberOfItemsPerPageValues;
	}

	/** 
	 * this method can be used to overwrite the initial values 
	 * for the number of items to show per page options
	 */
	public void setNumberOfItemsPerPageValues(Integer[] numberOfItemsPerPageValues)
	{
		this.numberOfItemsPerPageValues = numberOfItemsPerPageValues;
	}

	public HtmlSelectOneMenu getCboNumberOfItemsPerPageBottom()
	{
		return cboNumberOfItemsPerPageBottom;
	}

	public void setCboNumberOfItemsPerPageBottom(HtmlSelectOneMenu cboNumberOfItemsPerPageBottom)
	{
		this.cboNumberOfItemsPerPageBottom = cboNumberOfItemsPerPageBottom;
	}

	public HtmlSelectOneMenu getCboNumberOfItemsPerPageTop()
	{
		return cboNumberOfItemsPerPageTop;
	}

	public void setCboNumberOfItemsPerPageTop(HtmlSelectOneMenu cboNumberOfItemsPerPageTop)
	{
		this.cboNumberOfItemsPerPageTop = cboNumberOfItemsPerPageTop;
	}


	
	
}
