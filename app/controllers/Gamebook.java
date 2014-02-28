package controllers;

import models.Episode;
import models.EpisodeLink;
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

    public static Result chooseSkill() {
        return ok(skills.render("Skill page"));
    }

    public static Result skillBonus() {
        return ok(skillbonus.render("Skill bonus page"));
    }

    public static Result generateEpisode(int number) {

        Episode episode = Episode.find.byId(number);

        if(episode != null) {
            episode.setText(episode.getText().replaceAll("\\n", "<br>"));
            return ok(Json.toJson(episode));
        }
        else {
            return ok("Няма резултати в базата данни!");
        }
    }

    public static Result displayEpisode(int number) {

        return ok(index.render("Title", number));
    }

//    public static String fixLinks(String paragraph, List<EpisodeLink> links) {
//        int len = links.size();
//        for(int i = 0; i< len; i++)
//        {
//            EpisodeLink l = links.get(i);
//            String str = String.format("<a href=\"display/id=epNumber%s\">%s</a>", links.get(i).getGoToEpisode(i).getId(), l.getText());
//            paragraph = paragraph.replaceAll("###\\d+###", str);
//        }
//        return paragraph;
//    }
}
