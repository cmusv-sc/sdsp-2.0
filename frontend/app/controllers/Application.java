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

public class Application extends Controller {

  public static Result index() {
    return ok(index.render());
  }

  public static class Login {
    public String username;
    public String password;
  }

  public static Result login() {
    return ok(login.render(Form.form(Login.class)));
  }

  public static Result logout() {
    session().clear();
    flash("Success", "You have been logged out");
    return redirect(routes.Application.login());
  }

  public static Result authenticate() {
    Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

    try {
      ObjectNode on = Json.newObject();

      FormHelper.formField(loginForm, on, "username");
      FormHelper.formField(loginForm, on, "password");

      JsonNode response = User.authenticate(on);
      if (response.has("success")) {
        session().clear();
        session("username", loginForm.get().username);
        return redirect(routes.Application.index());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return badRequest(login.render(loginForm));
  }

  // TODO: change password

  public static Result register() {
    return ok(register.render(Form.form(User.class)));
  }

  public static Result registerUser() {
    Form<User> registrationForm = Form.form(User.class).bindFromRequest();

    try {
      ObjectNode on = Json.newObject();

      if (!registrationForm.field("password").value().equals(registrationForm.field("confirmPassword").value())) {
        return badRequest(register.render(Form.form(User.class)));
      }

      FormHelper.formField(registrationForm, on, "email");
      FormHelper.formField(registrationForm, on, "username");
      FormHelper.formField(registrationForm, on, "password");
      FormHelper.formField(registrationForm, on, "firstName");
      FormHelper.formField(registrationForm, on, "lastName");

      JsonNode response = User.create(on);
      if (response.has("success")) {
        return redirect(routes.Application.login());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return badRequest(register.render(Form.form(User.class)));
  }

}
