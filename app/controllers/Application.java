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
    
    public static String fixLinks(String paragraph, List<Link> links)
    {
        int len = links.size();
        for(int i = 0; i< len; i++)
        {
            Link l = links.get(i);
            String str = String.format("<a href=\"gamebook/display?id=epNumber%s\">%s</a>", links.get(i).go_to_episode_number, l.link_text);
            paragraph = paragraph.replaceAll("###\\d+###", str);
        }
        return paragraph;
    }

}
