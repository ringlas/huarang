package controllers;

import play.*;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;

import views.html.*;

import java.util.List;

public class Application extends Controller {

    public static Result login() {
        return ok(login.render("Login page!"));
    }

    public static Result home() {
        return ok(home.render("Home page!"));
    }

    public static Result index() {
        return ok(index.render("Index page!", 1));
    }
}
