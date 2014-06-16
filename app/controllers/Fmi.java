package controllers;

import models.CharacterSheet;
import models.Episode;
import models.Gamebook;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.Date;
import java.util.List;

import views.html.*;

/**
 * Created by vik_a_000 on 14-5-4.
 */
public class Fmi extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result map() {
        return ok(map.render());
    }

}
