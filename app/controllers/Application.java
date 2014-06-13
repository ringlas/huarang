package controllers;

import models.User;
import play.*;
import play.data.Form;
import play.data.*;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;
import play.libs.Crypto;

import views.html.*;
import views.html.huarang.*;

import java.util.List;

public class Application extends Controller {

    public static Result login() {
        if(session().get("username") != null){
            flash("notice", "Вече сте влезли в системата.");
            return redirect(routes.Library.dashboard());
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
            session("role", user.getRole());

            return redirect(routes.Library.dashboard());
        }
    }

    public static Result createUser() {
        Form<User> userForm = Form.form(User.class).bindFromRequest();
        DynamicForm requestData = Form.form().bindFromRequest();

        if(userForm.hasErrors()){
            flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
            return redirect(routes.Application.login());
        }

        User user = userForm.get();
        String password = user.getPassword();
        String confirmPassword = requestData.get("confirm_password");

        if(password.isEmpty() || !password.equals(confirmPassword)) {
            flash("error", "Паролите не съвпадат!");
            return redirect(routes.Application.login());
        }

        // Set the default group to all registered users as public
        user.setRole("public");
        // Encrypt the user password with SHA-1 algorithm and Application SALT
        user.setPassword(Crypto.sign(user.getPassword()));
        // Save the user
        user.save();

        flash("success", "Успешно създадохте нов потребител.");
        return redirect(routes.Application.login());
    }

    @Security.Authenticated(Secured.class)
    public static Result home() {
        return ok(home.render("Home page!"));
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

    public static Result map() {
        return ok(map.render());
    }

}
