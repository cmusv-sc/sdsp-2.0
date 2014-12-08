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

public class DeviceGroups extends Controller {

  final static Form<DeviceGroup> dgForm = Form.form(DeviceGroup.class);

  public static Result display() {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    return ok(deviceGroups.render(DeviceGroup.getAll(username), dgForm));
  }

  public static Result displayGroup(String deviceGroupName) {
    String username = session("username");
    if (username == null) {
      return unauthorized();
    }
    return ok(deviceGroup.render(deviceGroupName, DeviceGroup.getDevices(username, deviceGroupName)));
  }

  public static Result create() {
    Form<DeviceGroup> dg = dgForm.bindFromRequest();

    ObjectNode json = Json.newObject();
    try {
      String deviceGroupName = dg.field("deviceGroupName").value();
      String username = session("username");


      if (deviceGroupName != null && !deviceGroupName.isEmpty()) {
        json.put("deviceGroupOwner", username);
        json.put("deviceGroupName", deviceGroupName);

        JsonNode response = DeviceGroup.create(json);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return redirect("/deviceGroups");
  }

  public static Result delete() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String deviceGroupOwner = session("username");
    String deviceGroupName = df.field("nameHolder").value();

    DeviceGroup.delete(deviceGroupOwner, deviceGroupName);

    return redirect("/deviceGroups");
  }

  public static Result addDevice() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String deviceGroupOwner = session("username");
    String deviceGroupName = df.field("nameHolder").value();
    String deviceUri = df.field("deviceUri").value();

    DeviceGroup.addDevice(deviceGroupOwner, deviceGroupName, deviceUri).toString();

    return redirect("/deviceGroup/"+deviceGroupName);
  }

  public static Result deleteDevice() {
    DynamicForm df = DynamicForm.form().bindFromRequest();
    String deviceGroupOwner = session("username");
    String deviceGroupName = df.field("nameHolder").value();
    String deviceUri = df.field("uriHolder").value();

    DeviceGroup.deleteDevice(deviceGroupOwner, deviceGroupName, deviceUri);

    return redirect("/deviceGroup/"+deviceGroupName);
  }
}
