package de.mpg.mpdl.inge.model.valueobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;

public class ValueObjectTrimTest {

  @Test
  public void testTrimStringsDuringCleanup() throws Exception {
    PersonVO person = new PersonVO();
    person.setGivenName("  John  ");
    person.setFamilyName("  Doe  ");

    person.cleanup();

    assertEquals("John", person.getGivenName());
    assertEquals("Doe", person.getFamilyName());
  }

  @Test
  public void testTrimToStringEmptySetToNullDuringCleanup() throws Exception {
    PersonVO person = new PersonVO();
    person.setGivenName("    ");

    person.cleanup();

    assertNull(person.getGivenName());
  }
}
