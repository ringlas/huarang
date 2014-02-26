package controllers;

import models.Episode;
import play.*;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;

import views.html.*;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by victor on 14-2-25.
 */
public class Gamebook {

    public static Result display(int number) {

        List<Episode> episode = new Model.Finder(String.class, Episode.class)
                .where()
                    .eq("number", number)
                .findList();

        if(!episode.isEmpty()) {
            return ok(Json.toJson(episode));
        }
        else {
            return ok("Няма резултати в базата данни!");
        }
    }
}
