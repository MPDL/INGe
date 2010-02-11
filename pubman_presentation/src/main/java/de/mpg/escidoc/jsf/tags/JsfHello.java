package de.mpg.escidoc.jsf.tags;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentELTag;

public class JsfHello extends UIComponentELTag
{
    // Declare a bean property for the hellomsg attribute.
    public String hellomsg = null;

    // Associate the renderer and component type.
    public String getComponentType()
    {
        return "de.mpg.escidoc.jsf.components.JsfHello";
    }

    public String getRendererType()
    {
        return null;
    }

    protected void setProperties(UIComponent component)
    {
        super.setProperties(component);
        // set hellomsg
        if (hellomsg != null)
        {
            FacesContext context = FacesContext.getCurrentInstance();
            Application app = context.getApplication();
            ValueBinding vb = app.createValueBinding(hellomsg);
            component.setValueBinding("hellomsg", vb);
        }
    }

    public void release()
    {
        super.release();
        hellomsg = null;
    }

    public void setHellomsg(String hellomsg)
    {
        this.hellomsg = hellomsg;
    }
}