package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.Device;
import models.Sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class Devices extends Controller {

  public static Result get(String requestUser, String deviceUri) {
    if (requestUser == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    Device d = Device.get(requestUser, deviceUri);
    if (d == null) {
      return badRequest();
    }

    String ret = new Gson().toJson(d);
    return ok(ret);
  }

  public static Result create(String requestUser) {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    if (!jsonRequest.has("sensorCount") || !jsonRequest.has("owner") || !jsonRequest.has("deviceUri")  
        ||!jsonRequest.has("deviceTypeUri") || !jsonRequest.has("deviceUserDefinedFields")) {
      return badRequest();
    }

    try {
      int sensorCount = jsonRequest.findPath("sensorCount").asInt();
      for (int i = 0; i < sensorCount; i++ ) { // Not enough sensors
        if (!jsonRequest.has("sensor"+i)) {
          return badRequest();
        }
      }

      String deviceUri = jsonRequest.findPath("deviceUri").asText();
      String deviceTypeUri = jsonRequest.findPath("deviceTypeUri").asText();
      String[] sensorUris = new String[sensorCount];
      String owner = jsonRequest.findPath("owner").asText();
      String deviceUserDefinedFields = jsonRequest.findPath("deviceUserDefinedFields").asText();

      List<Sensor> sensorList = new ArrayList<Sensor>();

      for (int i = 0; i < sensorCount; i++) {
        JsonNode sensor = jsonRequest.findPath("sensor"+i);
        String sensorUri = sensor.findPath("sensorUri").asText();
        String sensorTypeUri = sensor.findPath("sensorTypeUri").asText();
        String sensorDeviceUri = sensor.findPath("deviceUri").asText();
        String sensorOwner = sensor.findPath("owner").asText();
        String sensorUserDefinedFields = sensor.findPath("sensorUserDefinedFields").asText();

        sensorUris[i] = sensorUri;
        Sensor newSensor = new Sensor(sensorUri, sensorTypeUri, sensorDeviceUri, sensorOwner, sensorUserDefinedFields);
        sensorList.add(newSensor);
      }
     
      Device newDevice = new Device(deviceUri, deviceTypeUri, sensorUris, owner, deviceUserDefinedFields);
      Device device = Device.create(owner, newDevice, sensorList);
      if (device == null) {
        return badRequest();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return badRequest();
    }
    return ok();
  }

  public static Result delete(String requestUser, String deviceUri) {
    if (requestUser == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    Device d = Device.delete(requestUser, deviceUri);
    if (d == null) {
      return badRequest();
    }
    return ok();
  }
}
