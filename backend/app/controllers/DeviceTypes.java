package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.DeviceType;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class DeviceTypes extends Controller {

  public static Result getAll(String requestUser) {
    if (requestUser == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    List<DeviceType> dtList = DeviceType.getAll(requestUser);
    String ret = new Gson().toJson(dtList);
    return ok(ret);
  }

  public static Result create(String requestUser) {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    if (!jsonRequest.has("deviceTypeUri") || !jsonRequest.has("owner") || !jsonRequest.has("sensorTypeUris") || !jsonRequest.findPath("sensorTypeUris").isArray() 
        || !jsonRequest.has("manufacturer") || !jsonRequest.has("version")) {
      return badRequest();
    }

    try {
      String deviceTypeUri = jsonRequest.findPath("deviceTypeUri").asText();
      String owner = jsonRequest.findPath("owner").asText();
      JsonNode sensorTypeUrisNode = jsonRequest.findPath("sensorTypeUris");
      List<String> sensorTypeUriList = new ArrayList<String>();
      for (JsonNode st : sensorTypeUrisNode) {
        sensorTypeUriList.add(st.asText());
      }
      String[] sensorTypeUris = sensorTypeUriList.toArray(new String[sensorTypeUriList.size()]);
      String manufacturer = jsonRequest.findPath("manufacturer").asText();
      String version = jsonRequest.findPath("version").asText();

      DeviceType deviceTypeRequest = new DeviceType(deviceTypeUri, owner, sensorTypeUris, manufacturer, version);
      DeviceType deviceType = DeviceType.create(requestUser, deviceTypeRequest);
      if (deviceType == null) {
        return badRequest();
      }
    } catch (Exception e) {
      return badRequest();
    }
    return ok();
  }

  public static Result delete(String requestUser, String deviceTypeUri) {
    if (requestUser == null || deviceTypeUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    DeviceType st = DeviceType.delete(requestUser, deviceTypeUri);
    if (st == null) {
      return badRequest();
    }
    return ok();
  }
}
