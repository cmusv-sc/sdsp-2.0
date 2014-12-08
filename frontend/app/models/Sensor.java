package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class Sensor {

  private String sensorId;
  private String sensorUri;
  private String sensorTypeUri;
  private String deviceUri;
  private String owner;
  private String sensorUserDefinedFields;

  public String getSensorId() {
    return this.sensorId;
  }

  public String getSensorUri() {
    return this.sensorUri;
  }

  public String getSensorTypeUri() {
    return this.sensorTypeUri;
  }

  public String getDeviceUri() {
    return this.deviceUri;
  }

  public String getOwner() {
    return this.owner;
  }

  public String getSensorUserDefinedFields() {
    return this.sensorUserDefinedFields;
  }

  public static final String GET_SENSOR = util.Constants.HOSTNAME + util.Constants.GET_SENSOR_ROUTE;
  public static final String GET_DEVICE_SENSORS = util.Constants.HOSTNAME + util.Constants.GET_DEVICE_SENSORS_ROUTE;

  public static Sensor get(String requestUser, String sensorUri) {
    final JsonNode json = APICall.callAPI(GET_SENSOR + "/" + requestUser + "/" + sensorUri);
    if (json == null || json.has("error")) {
      return null;
    }
    return new Gson().fromJson(json.toString(), Sensor.class);
  }

  public static List<Sensor> gets(String requestUser, String deviceUri) {
    final JsonNode json = APICall.callAPI(GET_DEVICE_SENSORS + "/" + requestUser + "/" + deviceUri);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<Sensor>();
    }

    Sensor[] sensorArray = new Gson().fromJson(json.toString(), Sensor[].class);
    return Arrays.asList(sensorArray);
  }
}
