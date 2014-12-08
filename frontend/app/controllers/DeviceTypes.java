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

import java.util.List;
import java.util.ArrayList;

public class DeviceTypes extends Controller {

  final static Form<DeviceType> dtForm = Form.form(DeviceType.class);

  public static Result display() {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    return ok(deviceTypes.render(DeviceType.getAll(username), dtForm));
  }

  public static Result create() {
    Form<DeviceType> dt = dtForm.bindFromRequest();
    ObjectNode json = Json.newObject();
    try {
      int i = 0;
      while (dt.field("sensorTypeUri"+i).value() != null && !dt.field("sensorTypeUri"+i).value().isEmpty()) {
        i += 1;
      }

      String deviceTypeUri = dt.field("deviceTypeUri").value();
      String manufacturer = dt.field("manufacturer").value();
      String version = dt.field("version").value();
      String owner = session("username");

      List<String> sensorTypeUriList = new ArrayList<String>();
      for (int j = 0; j < i; j++) {
        sensorTypeUriList.add(dt.field("sensorTypeUri"+j).value());
      }

      json.put("deviceTypeUri", deviceTypeUri);
      json.put("owner", owner);
      json.put("manufacturer", manufacturer);
      json.put("version", version);

      ArrayNode an = json.arrayNode();
      for (String sensorTypeUri : sensorTypeUriList) {
        an.add(sensorTypeUri);
      }

      json.put("sensorTypeUris", an);

      JsonNode response = DeviceType.create(owner, json);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return redirect("/deviceTypes");
  }

  public static Result delete() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String requestUser = session("username");
    String deviceTypeUri = df.field("uriHolder").value();

    DeviceType.delete(requestUser, deviceTypeUri);

    return redirect("/deviceTypes");
  }
}
