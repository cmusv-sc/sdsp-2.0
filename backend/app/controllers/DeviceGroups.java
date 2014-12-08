package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.DeviceGroup;
import models.Device;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class DeviceGroups extends Controller {

  public static Result get(String deviceGroupOwner, String deviceGroupName) {
    if (deviceGroupOwner == null || deviceGroupName == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    DeviceGroup dg = DeviceGroup.get(deviceGroupOwner, deviceGroupOwner, deviceGroupName);
    if (dg == null) {
      return notFound();
    }

    String ret = new Gson().toJson(dg);
    return ok(ret);
  }
  /*
  public static Result getById(String deviceGroupId) {
    if (deviceGroupId == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    DeviceGroup dg = DeviceGroup.get("", deviceGroupId);
    if (dg == null) {
      return notFound();
    }

    String ret = new Gson().toJson(dg);
    return ok(ret);
  }*/

  public static Result getAll(String deviceGroupOwner) {
    if (deviceGroupOwner == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    List<DeviceGroup> deviceGroups = DeviceGroup.getAll(deviceGroupOwner, deviceGroupOwner);
    String ret = new Gson().toJson(deviceGroups);
    return ok(ret);
  }

  public static Result getDevices(String deviceGroupOwner, String deviceGroupName) {
    if (deviceGroupOwner == null || deviceGroupName == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    List<Device> devices = DeviceGroup.getDevices(deviceGroupOwner, deviceGroupOwner, deviceGroupName);
    String ret = new Gson().toJson(devices);
    return ok(ret);
  }

  public static Result addDevice(String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    if (deviceGroupOwner == null || deviceGroupName == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    DeviceGroup dg = DeviceGroup.addDevice(deviceGroupOwner, deviceGroupOwner, deviceGroupName, deviceUri);
    if (dg == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result deleteDevice(String deviceGroupOwner, String deviceGroupName, String deviceUri) {
    if (deviceGroupOwner == null || deviceGroupName == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    DeviceGroup dg = DeviceGroup.deleteDevice(deviceGroupOwner, deviceGroupOwner, deviceGroupName, deviceUri);
    if (dg == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result create() {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    if (!jsonRequest.has("deviceGroupOwner") || !jsonRequest.has("deviceGroupName")) {
      return badRequest();
    }
    String username = jsonRequest.findPath("deviceGroupOwner").asText();
    DeviceGroup dgRequest = new DeviceGroup(username, jsonRequest.findPath("deviceGroupName").asText());
    DeviceGroup dg = DeviceGroup.create(username, dgRequest);

    if (dg == null) {
      return badRequest();
    }

    return ok();
  }

  public static Result delete(String deviceGroupOwner, String deviceGroupName) {
    if (deviceGroupOwner == null || deviceGroupName == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    DeviceGroup dg = DeviceGroup.delete(deviceGroupOwner, deviceGroupOwner, deviceGroupName);
    if (dg == null) {
      return badRequest();
    }

    return ok();
  }
}
