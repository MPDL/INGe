package de.mpg.mpdl.inge.model.types;

import de.mpg.mpdl.inge.model.valueobjects.AdminDescriptorVO;

/**
 * Interface to indicate whether an {@link AdminDescriptorVO} holds information on how to validate
 * an item from that context.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface Validatable {
  public String getValidationSchema();
}
