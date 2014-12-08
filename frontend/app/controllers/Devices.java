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

public class Devices extends Controller {

  final static Form<Device> deviceForm = Form.form(Device.class);

  public static Result display(String deviceUri) {
    /*
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    return ok(deviceGroups.render(DeviceGroup.getAll(username), dgForm));
    */
    return ok();
  }

  public static Result createPage() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String requestUser = session("username");
    String deviceTypeUri = df.field("deviceTypeUri").value();

    DeviceType deviceType = DeviceType.get(requestUser, deviceTypeUri);

    return ok(createDevice.render(deviceType, deviceForm));
  }

  public static Result delete() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String requestUser = session("username");
    String deviceUri = df.field("deviceUri").value();

    Device.delete(requestUser, deviceUri);
    return redirect("/deviceGroup/Personal");
  }

  public static Result create() {
    Form<Device> device = deviceForm.bindFromRequest();

    ObjectNode json = Json.newObject();
    try {
      int i = 0;
      while (device.field("sensorUri"+i).value() != null && !device.field("sensorUri"+i).value().isEmpty()) {
        i += 1;
      }

      String owner = session("username");
      String deviceUri = device.field("deviceUri").value();
      String deviceTypeUri = device.field("deviceTypeUri").value();
      String deviceUserDefinedFields = device.field("deviceUserDefinedFields").value();

      DeviceType dt = DeviceType.get(owner, deviceTypeUri);
      String[] sensorTypeUris = dt.getSensorTypeUris();

      if (i != sensorTypeUris.length) {
        return redirect("/deviceGroup/Personal"); 
      }

      json.put("sensorCount", i);
      json.put("deviceUri", deviceUri);
      json.put("deviceTypeUri", deviceTypeUri);
      json.put("owner", owner);
      json.put("deviceUserDefinedFields", deviceUserDefinedFields);

      for (int j = 0; j < i; j++) {
        ObjectNode sensor = json.objectNode();

        sensor.put("sensorUri", device.field("sensorUri"+j).value());
        sensor.put("sensorTypeUri", sensorTypeUris[j]);
        sensor.put("deviceUri", deviceUri);
        sensor.put("owner", owner);
        sensor.put("sensorUserDefinedFields", "");
        
        json.put("sensor"+j, sensor);
      }

      JsonNode response = Device.create(owner, json);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return redirect("/deviceGroup/Personal");
  }
}
