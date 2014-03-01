package controllers;

import models.CharacterSheet;
import models.Episode;
import models.EpisodeLink;
import models.User;
import play.*;
import play.data.Form;
import play.data.*;
import play.db.ebean.Model;
import play.mvc.*;
import play.libs.Json;

import views.html.*;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by victor on 14-2-25.
 */
public class Gamebook extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result chooseSkill() {
        return ok(skills.render("Skill page"));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveSkill(){
        // Bind the request data from the form to an object
        CharacterSheet characterSheet = Form.form(CharacterSheet.class).bindFromRequest().get();
        // Set the User related to that character sheet
        characterSheet.setUser(Integer.parseInt(session().get("user_id")));
        // Save it to the database
        characterSheet.save();
        return redirect(routes.Gamebook.skillBonus());
    }

    @Security.Authenticated(Secured.class)
    public static Result skillBonus() {

        List<CharacterSheet> characterSheet = CharacterSheet.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();

        return ok(skillbonus.render(characterSheet.get(0)));
    }

    @Security.Authenticated(Secured.class)
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

    @Security.Authenticated(Secured.class)
    public static Result displayEpisode(int number) {

        List<CharacterSheet> characterSheet = CharacterSheet.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();

        return ok(index.render(number, characterSheet.get(0)));
    }

    public static Result updateCharacterSheet(int number) {
        // Bind the request data from the form to an object
        CharacterSheet characterSheet = Form.form(CharacterSheet.class).bindFromRequest().get();
        // Set the User related to that character sheet
        characterSheet.setUser(Integer.parseInt(session().get("user_id")));
        // Get the request with the submitted form data
        Http.RequestBody body = request().body();
        // Update the database with that particular id
        characterSheet.update(Integer.parseInt(body.asFormUrlEncoded().get("id")[0]));
        return redirect(routes.Gamebook.displayEpisode(number));
    }

}
