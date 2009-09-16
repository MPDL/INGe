package de.mpg.escidoc.jsf.tags;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentELTag;

import org.apache.log4j.Logger;

/**
 *
 * TODO Custom component tag for iterating over a given List.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Loop extends UIComponentELTag
{

    private static Logger logger = Logger.getLogger(Loop.class);

    private ValueExpression intermediate;
    private ValueExpression first;
    private ValueExpression maxElements;
    private ValueExpression value;
    private java.lang.String _var;
    private ValueExpression dir;
    
    /**
     * Default constructor.
     */
    public Loop()
    {
        logger.debug("Constructing Loop tag.");
    }

    @Override
    public String getComponentType()
    {
        return "de.mpg.escidoc.jsf.components.Loop";
    }

    @Override
    public String getRendererType()
    {
        return null;
    }

    /**
     * Set the properties/attributes of this tag.
     * @param component The UI component represented by this Tag.
     */
    protected void setProperties(final UIComponent component)
    {

        logger.debug("Setting props on " + component);

        super.setProperties(component);
        javax.faces.component.UIData data = null;
        try
        {
            data = (javax.faces.component.UIData) component;
        }
        catch (ClassCastException cce)
        {
            throw new IllegalStateException("Component " + component.toString()
                    + " not expected type.  Expected: javax.faces.component.UIData.  Perhaps you're missing a tag?");
        }
        if (first != null)
        {
            data.setValueExpression("first", first);
        }
        if (maxElements != null)
        {
            data.setValueExpression("maxElements", maxElements);
        }
        if (value != null)
        {
            data.setValueExpression("value", value);
        }
        if (intermediate != null)
        {
            data.setValueExpression("intermediate", intermediate);
        }
        data.setVar(_var);
        if (dir != null)
        {
            data.setValueExpression("dir", dir);
        }
    }

    /**
     * Release the component.
     */
    public void release()
    {
        super.release();

        // component properties
        this.first = null;
        this.maxElements = null;
        this.value = null;
        this._var = null;

        // rendered attributes
        this.dir = null;

    }

    public void setIntermediate(ValueExpression intermediate)
    {
        this.intermediate = intermediate;
    }

    public void setFirst(final ValueExpression first)
    {
        this.first = first;
    }

    public void setMaxElements(final ValueExpression maxElements)
    {
        this.maxElements = maxElements;
    }

    public void setValue(final ValueExpression value)
    {
        this.value = value;
    }

    public void setVar(final java.lang.String _var)
    {
        this._var = _var;
    }

    public void setDir(final ValueExpression dir)
    {
        this.dir = dir;
    }

}
