package de.mpg.escidoc.services.common.types;

import de.mpg.escidoc.services.common.valueobjects.AdminDescriptorVO;

/**
 * Interface to indicate whether an {@link AdminDescriptorVO} holds information on how to validate an item from that context.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public interface Validatable
{
    public String getValidationSchema();
}
