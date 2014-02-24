package controllers;

import models.Bar;
import play.*;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;

import views.html.*;

import java.util.List;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("My app is ready!"));
    }

    public static Result add() {
        Bar bar = Form.form(Bar.class).bindFromRequest().get();
        bar.save();

        return redirect(routes.Application.index());
    }

    public static Result getBars() {
        List<Bar> bars = new Model.Finder(String.class, Bar.class).all();

        return ok(Json.toJson(bars));
    }

}
