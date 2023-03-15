package util;

import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import java.util.Arrays;

public class AssertJsonWrapper {

    //TODO: How to handle strict/non-strict mode?

    /**
     * Wrapper method for JSONAssert.assertEquals(). <br>
     * Assert that two Json-Strings are equal, ignoring certain Json-Fields.
     *
     * @param expectedJson the expected Json as String
     * @param actualJson   the actual Json as String
     * @param ignoreFields the Json-Fields to ignore
     */
    public static void assertEquals(String expectedJson, String actualJson, String... ignoreFields) {
        Customization[] customizations = Arrays.stream(ignoreFields).map(i -> new Customization(i, (o1, o2) -> true)).toArray(Customization[]::new);
        CustomComparator ignoreFieldsComparator = new CustomComparator(JSONCompareMode.STRICT, customizations);

        JSONAssert.assertEquals(expectedJson, actualJson, ignoreFieldsComparator);
    }

}
