package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.Form;
import play.data.DynamicForm;
import play.libs.Json;

import views.html.*;

import models.*;

import util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SensorTypes extends Controller {

  final static Form<SensorType> stForm = Form.form(SensorType.class);

  public static Result display() {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    return ok(sensorTypes.render(SensorType.getAll(username), stForm));
  }

  public static Result create() {
    Form<SensorType> st = stForm.bindFromRequest();

    ObjectNode json = Json.newObject();
    try {
      String sensorTypeUri = st.field("sensorTypeUri").value();
      String owner = session("username");
      String sensorCategory = st.field("sensorCategory").value();
      String manufacturer = st.field("manufacturer").value();
      String version = st.field("version").value();
      String minValueString = st.field("minimumValue").value();
      String maxValueString = st.field("maximumValue").value();
      minValueString = minValueString.isEmpty() ? "0.0" : minValueString;
      maxValueString = maxValueString.isEmpty() ? "0.0" : maxValueString;
      double minimumValue = Double.parseDouble(minValueString);
      double maximumValue = Double.parseDouble(maxValueString);
      String unit = st.field("unit").value();
      String interpreter = st.field("interpreter").value();
      
      json.put("sensorTypeUri", sensorTypeUri);
      json.put("owner", owner);
      json.put("sensorCategory", sensorCategory);
      json.put("manufacturer", manufacturer);
      json.put("version", version);
      json.put("minimumValue", minimumValue);
      json.put("maximumValue", maximumValue);
      json.put("unit", unit);
      json.put("interpreter", interpreter);
      JsonNode response = SensorType.create(owner, json);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return redirect("/sensorTypes");
  }

  public static Result delete() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String requestUser = session("username");
    String sensorTypeUri = df.field("uriHolder").value();

    SensorType.delete(requestUser, sensorTypeUri);

    return redirect("/sensorTypes");
  }
}
