package de.mpg.escidoc.pubman.appbase;

import javax.faces.component.UIComponent;

/**
 *
 * This interface defines the implementing class as internationalized.
 * All localized messages should be fetched through the getMessage method.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public interface Internationalized
{
    /**
     * Returns the localized message to a given placeholder.
     *
     * @param placeholder A string representing a message in the resource bundles.
     *
     * @return The according localized message.
     */
    String getMessage(String placeholder);

    /**
     * Returns the localized label to a given placeholder.
     *
     * @param placeholder A string representing a label in the resource bundles.
     *
     * @return The according localized label.
     */
    String getLabel(String placeholder);

    /**
     * Bind a localized label to a JSF component value.
     *
     * @param component The JSF component.
     * @param placeholder The label that should be localized.
     */
    void bindComponentLabel(UIComponent component, String placeholder);
}
