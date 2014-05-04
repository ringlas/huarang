package controllers;

import play.mvc.Controller;
import play.mvc.*;

import views.html.*;
import views.html.huarang.*;

/**
 * Created by vik_a_000 on 14-5-4.
 */
public class Library extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result dashboard() {
        return ok(dashboard.render("Dashboard page!"));
    }

}
