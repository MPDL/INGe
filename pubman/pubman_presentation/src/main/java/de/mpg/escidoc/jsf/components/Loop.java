package de.mpg.escidoc.jsf.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;

/**
 * Custom JSF component looping obver a given list.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Loop extends UIData
{
    private String dir;
    private String intermediate;
    private static Logger logger = Logger.getLogger(Loop.class);
    private Object[] _values;

    /**
     * Default conbstructor.
     */
    public Loop()
    {
        super();
        setRendererType(null);
        logger.debug("Constructing loop component");
    }

    public String getIntermediate()
    {
        return intermediate;
    }

    public void setIntermediate(final String intermediate)
    {
        this.intermediate = intermediate;
    }

    public String getDir()
    {
        if (null != this.dir)
        {
            return this.dir;
        }
        ValueExpression _ve = getValueExpression("dir");
        if (_ve != null)
        {
            return (String) _ve.getValue(getFacesContext().getELContext());
        }
        else
        {
            return null;
        }
    }

    public void setDir(final String dir)
    {
        this.dir = dir;
    }

    public Object saveState(FacesContext _context)
    {
        if (_values == null)
        {
            _values = new Object[4];
        }
        _values[0] = super.saveState(_context);
        _values[1] = dir;
        return _values;
    }

    public void restoreState(FacesContext _context, Object _state)
    {
        _values = (Object[]) _state;
        super.restoreState(_context, _values[0]);
        this.dir = (String) _values[1];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.faces.component.UIComponent#getFamily()
     */
    @Override
    public String getFamily()
    {
        return "eSciDoc JSF Components";
    }

    public void encodeBegin(FacesContext context) throws IOException
    {
        UIData data = (UIData) this;
        data.setRowIndex(-1);
        // Render the beginning of the table
        ResponseWriter writer = context.getResponseWriter();
        renderLoopStart(context, data, writer);
    }

    public void encodeChildren(FacesContext context) throws IOException
    {
        UIData data = (UIData) this;
        ResponseWriter writer = context.getResponseWriter();
        int processed = 0;
        int elementIndex = data.getFirst() - 1;
        int elements = data.getRows();

        boolean first = true;

        while (true)
        {
            if ((elements > 0) && (++processed > elements))
            {
                break;
            }
            // Select the current row
            data.setRowIndex(++elementIndex);
            if (!data.isRowAvailable())
            {
                break;
            }
            // Write intermediate content
            if (!first && intermediate != null)
            {
                writer.writeText(intermediate, "intermediate");
            }
            renderElementStart(data, writer);
            renderElement(context, data, writer);
            renderElementEnd(data, writer);

            first = false;
        }
        data.setRowIndex(-1);
    }

    public void encodeEnd(FacesContext context) throws IOException
    {
        UIData data = (UIData) this;
        data.setRowIndex(-1);
        renderLoopEnd(data, context.getResponseWriter());
    }

    @Override
    public boolean getRendersChildren()
    {
        return true;
    }

    protected void renderElement(FacesContext context, UIComponent table, ResponseWriter writer) throws IOException
    {
        for (Iterator<UIComponent> gkids = getChildren(table); gkids.hasNext();)
        {
            encodeRecursive(context, gkids.next());
        }
        writer.writeText("\n", table, null);
    }

    /**
     * Renders the start of a table and applies the value of <code>styleClass</code> if available and renders any pass
     * through attributes that may be specified.
     * 
     * @param context the <code>FacesContext</code> for the current request
     * @param loopComponent the table that's being rendered
     * @param writer the current writer
     * @throws IOException if content cannot be written
     */
    protected void renderLoopStart(final FacesContext context, final UIComponent loopComponent,
            final ResponseWriter writer) throws IOException
    {
    }

    /**
     * Renders the closing <code>table</code> element.
     * 
     * @param table the table that's being rendered
     * @param writer the current writer
     * @throws IOException if content cannot be written
     */
    protected void renderLoopEnd(final UIComponent table, final ResponseWriter writer) throws IOException
    {
        writer.writeText("\n", table, null);
    }

    /**
     * Renders the starting <code>tr</code> element applying any values from the <code>rowClasses</code> attribute.
     * 
     * @param table the table that's being rendered
     * @param writer the current writer
     * @throws IOException if content cannot be written
     */
    protected void renderElementStart(final UIComponent table, final ResponseWriter writer) throws IOException
    {
    }

    /**
     * Renders the closing <code>rt</code> element.
     * 
     * @param table the table that's being rendered
     * @param writer the current writer
     * @throws IOException if content cannot be written
     */
    protected void renderElementEnd(final UIComponent table, final ResponseWriter writer) throws IOException
    {
    }

    /**
     * <p>
     * Render nested child components by invoking the encode methods on those components, but only when the
     * <code>rendered</code> property is <code>true</code>.
     * </p>
     * 
     * @param context FacesContext for the current request
     * @param component the component to recursively encode
     * @throws IOException if an error occurrs during the encode process
     */
    protected void encodeRecursive(final FacesContext context, final UIComponent component) throws IOException
    {
        // suppress rendering if "rendered" property on the component is
        // false.
        if (!component.isRendered())
        {
            return;
        }
        // Render this component and its children recursively
        component.encodeBegin(context);
        if (component.getRendersChildren())
        {
            component.encodeChildren(context);
        }
        else
        {
            Iterator<UIComponent> kids = getChildren(component);
            while (kids.hasNext())
            {
                UIComponent kid = kids.next();
                encodeRecursive(context, kid);
            }
        }
        component.encodeEnd(context);
    }

    /**
     * @param component <code>UIComponent</code> for which to extract children
     * @return an Iterator over the children of the specified component, selecting only those that have a
     *         <code>rendered</code> property of <code>true</code>.
     */
    protected Iterator<UIComponent> getChildren(final UIComponent component)
    {
        int childCount = component.getChildCount();
        if (childCount > 0)
        {
            return component.getChildren().iterator();
        }
        else
        {
            return Collections.<UIComponent> emptyList().iterator();
        }
    }
}
