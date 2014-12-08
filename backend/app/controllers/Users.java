package controllers;

import play.*;
import play.mvc.*;

import views.html.*;

import models.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

import java.util.*;

public class Users extends Controller {

  public static Result get(String username) {
    if (username == null) {
      return badRequest();
    }

    response().setHeader("Access-Control-Allow-Origin", "*");

    User user = User.getUser(username);
    if (user == null) {
      return notFound();
    }

    String ret = new Gson().toJson(user);
    return ok(ret);
  }

  public static Result create() {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    if (!jsonRequest.has("email") || !jsonRequest.has("username") || !jsonRequest.has("password") || !jsonRequest.has("firstName") || !jsonRequest.has("lastName")) {
      return badRequest();
    }
    User userRequest = new User(jsonRequest.findPath("email").asText().toLowerCase(), jsonRequest.findPath("username").asText().toLowerCase(), jsonRequest.findPath("password").asText(),
        jsonRequest.findPath("firstName").asText(), jsonRequest.findPath("lastName").asText());
    User user = User.create(userRequest);

    if (user == null) {
      return unauthorized();
    }
    return created();
  }

  public static Result changePassword() {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    String username = jsonRequest.findPath("username").textValue();
    String oldPassword = jsonRequest.findPath("oldPassword").textValue();
    String newPassword = jsonRequest.findPath("newPassword").textValue();

    if (username == null || oldPassword == null || newPassword == null) {
      return badRequest();
    }

    User user = User.changePassword(username, username, oldPassword, newPassword);

    if (user == null) {
      return unauthorized();
    }
    return ok();
  }

  public static Result authenticate() {
    JsonNode jsonRequest = request().body().asJson();
    if (jsonRequest == null) {
      return badRequest();
    }

    String username = jsonRequest.findPath("username").textValue();
    String password = jsonRequest.findPath("password").textValue();
    
    if (username == null || password == null) {
      return null;
    }

    User user = User.getUser(username);
    if (user != null && user.getPassword().equals(password)) {
      return ok();
    }
    return unauthorized();
  }
}
