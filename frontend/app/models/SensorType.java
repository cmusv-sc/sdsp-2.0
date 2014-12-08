package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class SensorType {

  private String sensorTypeId;
  private String sensorTypeUri;
  private int instanceCount;
  private String owner;
  private String sensorCategory;
  private String manufacturer;
  private String version;
  private double maximumValue;
  private double minimumValue;
  private String unit;
  private String interpreter;

  public String getSensorTypeId() {
    return this.sensorTypeId;
  }

  public String getSensorTypeUri() {
    return this.sensorTypeUri;
  }

  public int getInstanceCount() {
    return this.instanceCount;
  }

  public String getOwner() {
    return this.owner;
  }

  public String getSensorCategory() {
    return this.sensorCategory;
  }

  public String getManufacturer() {
    return this.manufacturer;
  }

  public String getVersion() {
    return this.version;
  }

  public double getMaximumValue() {
    return this.maximumValue;
  }

  public double getMinimumValue() {
    return this.minimumValue;
  }

  public String getUnit() {
    return this.unit;
  }

  public String getInterpreter() {
    return this.interpreter;
  }

  public static final String GET_ALL_SENSOR_TYPES = util.Constants.HOSTNAME + util.Constants.GET_ALL_SENSOR_TYPES_ROUTE;
  public static final String DELETE_SENSOR_TYPE = util.Constants.HOSTNAME + util.Constants.DELETE_SENSOR_TYPE_ROUTE;
  public static final String CREATE_SENSOR_TYPE = util.Constants.HOSTNAME + util.Constants.CREATE_SENSOR_TYPE_ROUTE;

  public static List<SensorType> getAll(String requestUser) {
    final JsonNode json = APICall.callAPI(GET_ALL_SENSOR_TYPES + "/" + requestUser);
    if (json == null || json.has("error") || !json.isArray()) {
      return new ArrayList<SensorType>();
    }

    SensorType[] sensorTypeArray = new Gson().fromJson(json.toString(), SensorType[].class);
    return Arrays.asList(sensorTypeArray);
  }

  public static JsonNode delete(String requestUser, String sensorTypeUri) {
    return APICall.callAPI(DELETE_SENSOR_TYPE + "/" + requestUser + "/" + sensorTypeUri);
  }

  public static JsonNode create(String requestUser, JsonNode jsonData) {
    return APICall.postAPI(CREATE_SENSOR_TYPE + "/" + requestUser, jsonData);
  }
}
