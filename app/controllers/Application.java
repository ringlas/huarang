package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.data.*;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;

import views.html.*;

import java.util.List;

public class Application extends Controller {

    public static Result login() {
        return ok(login.render(Form.form(Login.class)));
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {
            session().clear();
            session("username", loginForm.get().username);
            return redirect(routes.Application.home());
        }
    }

    public static Result createUser() {

        User user = Form.form(User.class).bindFromRequest().get();
        user.save();
        return redirect(routes.Application.login());
    }

    public static Result home() {
        return ok(home.render("Home page!"));
    }

    public static Result index() {
        return ok(index.render("Index page!", 1));
    }

    public static class Login {

        public String username;
        public String password;

        public String validate() {
            if (User.authenticate(username, password) == null) {
                return "Некоректни потребителско име и/или парола";
            }
            return null;
        }

    }
}
