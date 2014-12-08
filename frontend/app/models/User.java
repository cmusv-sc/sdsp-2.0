package models;

import util.*;

import com.fasterxml.jackson.databind.*;
import com.google.gson.Gson;

import java.util.*;

public class User {

  private String userId;
  private String email;
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String[] ownedUserGroupIds;
  private String[] memberUserGroupIds;
  private String[] deviceGroupIds;

  public String getUserId() {
    return this.userId;
  }

  public String getEmail() {
    return this.email;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String newPassword) {
    this.password = newPassword;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public String[] getOwnedUserGroupIds() {
    return this.ownedUserGroupIds;
  }

  public String[] getMemberUserGroupIds() {
    return this.memberUserGroupIds;
  }

  public String[] getDeviceGroupIds() {
    return this.deviceGroupIds;
  }

  public static final String GET_USER = util.Constants.HOSTNAME + util.Constants.GET_USER_ROUTE;
  public static final String AUTHENTICATE_USER = util.Constants.HOSTNAME + util.Constants.AUTHENTICATE_USER_ROUTE;
  public static final String CREATE_USER = util.Constants.HOSTNAME + util.Constants.CREATE_USER_ROUTE;
  public static final String CHANGE_PASSWORD = util.Constants.HOSTNAME + util.Constants.CHANGE_PASSWORD_ROUTE;

  public static User get(String username) {
    final JsonNode json = APICall.callAPI(GET_USER + "/" + username);

    if (json == null || json.has("error")) {
      return null;
    }

    return new Gson().fromJson(json.toString(), User.class);
  }

  public static JsonNode authenticate(JsonNode jsonData) {
    return APICall.postAPI(AUTHENTICATE_USER, jsonData);
  }

  public static JsonNode create(JsonNode jsonData) {
    return APICall.postAPI(CREATE_USER, jsonData);
  }

  public static JsonNode changePassword(JsonNode jsonData) {
    return APICall.postAPI(CHANGE_PASSWORD, jsonData);
  }
}
