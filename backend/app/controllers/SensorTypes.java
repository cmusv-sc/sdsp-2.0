package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.SensorType;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class SensorTypes extends Controller {

  public static Result getAll(String requestUser) {
    if (requestUser == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    List<SensorType> stList = SensorType.getAll(requestUser);
    String ret = new Gson().toJson(stList);
    return ok(ret);
  }

  public static Result create(String requestUser) {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    if (!jsonRequest.has("sensorTypeUri") || !jsonRequest.has("owner") || !jsonRequest.has("sensorCategory") || !jsonRequest.has("manufacturer")
        || !jsonRequest.has("version") || !jsonRequest.has("maximumValue") || !jsonRequest.has("minimumValue") || !jsonRequest.has("unit") || !jsonRequest.has("interpreter")) {
      return badRequest();
    }

    try {
      String sensorTypeUri = jsonRequest.findPath("sensorTypeUri").asText();
      String owner = jsonRequest.findPath("owner").asText();
      String sensorCategory = jsonRequest.findPath("sensorCategory").asText();
      String manufacturer = jsonRequest.findPath("manufacturer").asText();
      String version = jsonRequest.findPath("version").asText();
      double maximumValue = jsonRequest.findPath("maximumValue").asDouble();
      double minimumValue = jsonRequest.findPath("minimumValue").asDouble();
      String unit = jsonRequest.findPath("unit").asText();
      String interpreter = jsonRequest.findPath("interpreter").asText();

      SensorType sensorTypeRequest = new SensorType(sensorTypeUri, owner, sensorCategory, manufacturer, version, maximumValue, minimumValue, unit, interpreter);
      SensorType sensorType = SensorType.create(requestUser, sensorTypeRequest);
      if (sensorType == null) {
        return badRequest();
      }
    } catch (Exception e) {
      return badRequest();
    }
    return ok();
  }

  public static Result delete(String requestUser, String sensorTypeUri) {
    if (requestUser == null || sensorTypeUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");
    SensorType st = SensorType.delete(requestUser, sensorTypeUri);
    if (st == null) {
      return badRequest();
    }
    return ok();
  }
}
