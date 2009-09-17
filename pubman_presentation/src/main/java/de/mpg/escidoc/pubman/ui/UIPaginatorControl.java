package de.mpg.escidoc.pubman.ui;

import java.util.ResourceBundle;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGroup;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeListener;

import de.mpg.escidoc.pubman.appbase.Internationalized;
import de.mpg.escidoc.pubman.util.CommonUtils;
import de.mpg.escidoc.pubman.util.InternationalizationHelper;

public class UIPaginatorControl extends HtmlPanelGroup implements Internationalized
{
	public static final String COMPONENT_TYPE = "de.mpg.escidoc.pubman.ui.PaginatorControl";

	private static Application application = FacesContext.getCurrentInstance().getApplication();
    private InternationalizationHelper i18nHelper = (InternationalizationHelper)application
            .getVariableResolver().resolveVariable(FacesContext.getCurrentInstance(),
                    InternationalizationHelper.BEAN_NAME);
    private ResourceBundle bundleLabel = ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle());
	
    // item list controls
	public HTMLElementUI htmlElementUI = new HTMLElementUI();
	public HtmlOutputText lblTotal = new HtmlOutputText();
	public HtmlOutputText lblItemCount = new HtmlOutputText();
	public HtmlOutputText lblShow = new HtmlOutputText();
	public HtmlOutputText lblObjectsPerPage = new HtmlOutputText();
	public HtmlCommandButton btFirst = new HtmlCommandButton();
	public HtmlCommandButton btBack = new HtmlCommandButton();
	public HtmlCommandButton btForward = new HtmlCommandButton();
	public HtmlCommandButton btLast = new HtmlCommandButton();
    public HtmlSelectOneMenu cboNumberOfItemsToShow = new HtmlSelectOneMenu();

    public HtmlPanelGroup panPageButtons = new HtmlPanelGroup();

    public UIPaginatorControl()
	{
    	this.setId(CommonUtils.createUniqueId(this));
		this.getChildren().add(this.htmlElementUI.getStartTagWithStyleClass("div", "paginator"));
		this.getChildren().add(this.htmlElementUI.getStartTagWithStyleClass("span", "paginator"));
		this.lblTotal.setId(CommonUtils.createUniqueId(this.lblTotal));
		this.lblTotal.setValue(bundleLabel.getString("ItemList_Total"));
		this.getChildren().add(this.lblTotal);
		this.lblItemCount.setId(CommonUtils.createUniqueId(this.lblItemCount));
		// value is set in setAllObjects()
		this.getChildren().add(this.lblItemCount);
		this.lblShow.setId(CommonUtils.createUniqueId(this.lblShow));
		this.lblShow.setValue(bundleLabel.getString("ItemList_Show"));
		this.getChildren().add(this.lblShow);
		this.cboNumberOfItemsToShow.setId(CommonUtils.createUniqueId(this.cboNumberOfItemsToShow));
		this.cboNumberOfItemsToShow.getChildren().clear();
		this.cboNumberOfItemsToShow.getChildren().addAll(CommonUtils.convertToSelectItemsUI(ListUI.SHOW_SELECTITEMS));
		this.cboNumberOfItemsToShow.setOnchange("submit();");
		// set the default value
		this.cboNumberOfItemsToShow.setValue(new Integer(ListUI.SHOW_ITEMS_PER_PAGE_DEFAULT).toString());
		this.getChildren().add(this.cboNumberOfItemsToShow);
		this.lblObjectsPerPage.setId(CommonUtils.createUniqueId(this.lblObjectsPerPage));
		this.lblObjectsPerPage.setValue(bundleLabel.getString("ItemList_ObjectsPerPage"));
		this.getChildren().add(this.lblObjectsPerPage);
		// hide number of items to show when UI is in single view mode
		this.btFirst.setId(CommonUtils.createUniqueId(this.btFirst));
		this.btFirst.setValue("|<");
		this.btFirst.setImmediate(true);
		this.getChildren().add(this.btFirst);
		this.btBack.setId(CommonUtils.createUniqueId(this.btBack));
		this.btBack.setValue("<");
		this.btBack.setImmediate(true);
		this.getChildren().add(this.btBack);
		this.panPageButtons.setId(CommonUtils.createUniqueId(this.panPageButtons));
		// the page buttons themselves are created and added in setAllObjects() and don't have to be added here
		this.getChildren().add(this.panPageButtons);
		this.btForward.setId(CommonUtils.createUniqueId(this.btForward));
		this.btForward.setValue(">");
		this.btForward.setImmediate(true);
		this.getChildren().add(this.btForward);
		this.btLast.setId(CommonUtils.createUniqueId(this.btLast));
		this.btLast.setValue(">|");
		this.btLast.setImmediate(true);
		this.getChildren().add(this.btLast);
		this.getChildren().add(this.htmlElementUI.getEndTag("span"));
		this.getChildren().add(this.htmlElementUI.getEndTag("div"));
	}

    public Object processSaveState(FacesContext context) 
    {
        Object superState = super.processSaveState(context);
        return new Object[] {superState, new Integer(getChildCount())};
    }
    
    public void processRestoreState(FacesContext context, Object state) 
    {
        // At this point in time the tree has already been restored, but not before our ctor added the default children.
        // Since we saved the number of children in processSaveState, we know how many children should remain within this component. 
    	// We assume that the saved tree will have been restored 'behind' the children we put into it from within the ctor.
        Object[] values = (Object[]) state;
        Integer savedChildCount = (Integer) values[1];
        for (int i = getChildCount() - savedChildCount.intValue(); i > 0; i--) 
        {
            getChildren().remove(0);
        }
        super.processRestoreState(context, values[0]);
    }

    public void setVisible(boolean singleView)
    {
	    this.lblTotal.setRendered(!singleView);
		this.lblItemCount.setRendered(!singleView);
		this.lblShow.setRendered(!singleView);
		this.cboNumberOfItemsToShow.setRendered(!singleView);
		this.lblObjectsPerPage.setRendered(!singleView);
    }
    
    public void registerActionListener(ActionListener listener)
    {
		this.btFirst.addActionListener(listener);
		this.btBack.addActionListener(listener);
		this.btForward.addActionListener(listener);
		this.btLast.addActionListener(listener);
    }

    public void registerValueChangeListener(ValueChangeListener listener)
    {
		this.cboNumberOfItemsToShow.addValueChangeListener(listener);
    }

    public void setItemCount(int itemCount)
    {
    	this.lblItemCount.setValue(new Integer(itemCount).toString());
    }
    
    public void setObjectsToDisplay(int currentPage, int numberOfPages)
    {
        this.btBack.setDisabled(currentPage == 1);
        this.btBack.setStyleClass(this.btBack.isDisabled() ? "disabled" : null);
        this.btForward.setDisabled(currentPage == numberOfPages);
        this.btForward.setStyleClass(this.btForward.isDisabled() ? "disabled" : null);
        this.btFirst.setDisabled(currentPage == 1);
        this.btFirst.setStyleClass(this.btFirst.isDisabled() ? "disabled" : null);
        this.btLast.setDisabled(currentPage == numberOfPages);
        this.btLast.setStyleClass(this.btLast.isDisabled() ? "disabled" : null);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getLabel(java.lang.String)
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedLabelBundle()).getString(placeholder);
    }

    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#getMessage(java.lang.String)
     */
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(i18nHelper.getSelectedMessagesBundle()).getString(placeholder);
    }
    
    /*
     * (non-Javadoc)
     * @see de.mpg.escidoc.pubman.appbase.Internationalized#bindComponentLabel(javax.faces.component.UIComponent, java.lang.String)
     */
    public void bindComponentLabel(UIComponent component, String placeholder)
    {
        ValueExpression value = FacesContext
            .getCurrentInstance()
            .getApplication()
            .getExpressionFactory()
            .createValueExpression(FacesContext.getCurrentInstance().getELContext(), "#{lbl." + placeholder + "}", String.class);
        component.setValueExpression("value", value); 
    }
}
