
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;

import org.junit.Ignore;
import org.junit.Test;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;

public class TestJson {

  @Test
  public void getFieldValueViaGetter() throws Exception {
    ItemVersionVO pi = new ItemVersionVO();
    for (PropertyDescriptor pd : Introspector.getBeanInfo(pi.getClass()).getPropertyDescriptors()) {
      System.out.println(pd.getReadMethod());
      System.out.println(pd.getName());

      /*
       * if (pd.getReadMethod() != null && !"class".equals(pd.getName()))
       * System.out.println(pd.getReadMethod().invoke(foo));
       */
    }

  }

  @Test
  @Ignore
  public void testJson() {
    JsonObjectBuilder jsob = Json.createObjectBuilder().add("update",
        Json.createArrayBuilder()
            .add(Json.createArrayBuilder().add(getFieldStringMatchJson("context.workflow", "SIMPLE"))
                .add(getUserJson(null, null, null, "item.owner.objectId"))
                .add(getFieldStringMatchJson("item.version.state", "PENDING", "RELEASED"))
                .add(getFieldStringMatchJson("item.publicStatus", "PENDING", "SUBMITTED", "RELEASED")))
            .add(Json.createArrayBuilder().add(getFieldStringMatchJson("context.workflow", "SIMPLE"))
                .add(getUserJson("MODERATOR", "CONTEXT", "item.context.objectId", null))
                .add(getFieldStringMatchJson("item.version.state", "RELEASED"))
                .add(getFieldStringMatchJson("item.publicStatus", "PENDING", "SUBMITTED", "RELEASED")))
            .add(Json.createArrayBuilder().add(getFieldStringMatchJson("context.workflow", "STANDARD"))
                .add(getUserJson(null, null, null, "item.owner.objectId"))
                .add(getFieldStringMatchJson("item.version.state", "PENDING", "RELEASED", "IN_REVISION"))
                .add(getFieldStringMatchJson("item.publicStatus", "PENDING", "SUBMITTED", "RELEASED")))
            .add(Json.createArrayBuilder().add(getFieldStringMatchJson("context.workflow", "STANDARD"))
                .add(getUserJson("MODERATOR", "CONTEXT", "item.context.objectId", null))
                .add(getFieldStringMatchJson("item.version.state", "RELEASED", "SUBMITTED"))
                .add(getFieldStringMatchJson("item.publicStatus", "PENDING", "SUBMITTED", "RELEASED"))));
    StringWriter wr = new StringWriter();
    Map<String, Boolean> config = new HashMap<String, Boolean>();
    config.put(JsonGenerator.PRETTY_PRINTING, true);
    Json.createWriterFactory(config).createWriter(wr).writeObject(jsob.build());
    System.out.println(wr.toString());
  }

  private JsonObjectBuilder getUserJson(String role, String grantType, String grantMatchId, String ownerIdMatchField) {

    JsonObjectBuilder jsob = Json.createObjectBuilder().add("type", "user_match");
    // JsonObjectBuilder mainJsob = Json.createObjectBuilder().add("match", jsob) ;

    if (role != null) {
      jsob.add("role", role);
    }
    if (grantType != null) {
      jsob.add("grant_type", grantType);
    }
    if (grantMatchId != null) {
      jsob.add("grant_id_match_field", grantMatchId);
    }
    if (ownerIdMatchField != null) {
      jsob.add("user_id_match_field", ownerIdMatchField);
    }

    return jsob;
  }

  private JsonObjectBuilder getFieldStringMatchJson(String matchField, String... strings) {
    JsonArrayBuilder jab = Json.createArrayBuilder();
    for (String s : strings) {
      jab.add(s);
    }

    return Json.createObjectBuilder().add("type", "field_string_match").add("field", matchField).add("values", jab);
  }

  private JsonObjectBuilder getFieldFieldMatchJson(String matchField1, String matchField2) {

    return Json.createObjectBuilder().add("type", "field_field_match").add("field2", matchField1).add("field2", matchField2);
  }

}
