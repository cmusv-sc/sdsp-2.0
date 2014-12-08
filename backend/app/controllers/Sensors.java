package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.Sensor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class Sensors extends Controller {

  public static Result get(String requestUser, String sensorUri) {
    if (requestUser == null || sensorUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    Sensor s = Sensor.get(requestUser, sensorUri);
    if (s == null) {
      return badRequest();
    }
    String ret = new Gson().toJson(s);
    return ok(ret);
  }

  public static Result gets(String requestUser, String deviceUri) {
    if (requestUser == null || deviceUri == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    List<Sensor> sensors = Sensor.gets(requestUser, deviceUri);
    String ret = new Gson().toJson(sensors);
    return ok(ret);
  }
}
