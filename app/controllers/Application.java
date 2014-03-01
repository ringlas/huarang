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
        if(session().get("username") != null){
            flash("notice", "Вече сте влезли в системата.");
            return redirect(routes.Application.home());
        }
        return ok(login.render(Form.form(Login.class)));
    }

    public static Result logout(){
        session().clear();
        flash("success", "Успешно излязохте от системата.");
        return redirect(routes.Application.login());
    }

    public static Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
            return badRequest(login.render(loginForm));
        } else {

            User user = User.find.where()
                    .eq("username", loginForm.get().username)
                    .findUnique();

            session().clear();
            session("username", user.getUsername());
            session("user_id", Integer.toString(user.getId()));

            return redirect(routes.Application.home());
        }
    }

    public static Result createUser() {
        Form<User> userForm = Form.form(User.class).bindFromRequest();

        if(userForm.hasErrors()){

            flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
            return redirect(routes.Application.login());
        }
        User user = userForm.get();
        user.save();
        flash("success", "Успешно създадохте нов потребител.");
        return redirect(routes.Application.login());
    }

    @Security.Authenticated(Secured.class)
    public static Result home() {
        return ok(home.render("Home page!"));
    }

//    public static Result index() {
//
//        return ok(index.render(1));
//    }

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
