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

        CharacterSheet characterSheet = CharacterSheet.find.where()
                .eq("user_id", session().get("user_id")).findUnique();

        if(characterSheet != null) {
            characterSheet.delete();
        }

        return ok(skills.render("Skill page"));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveSkill(){
        // Bind the request data from the form to an object
        CharacterSheet characterSheet = Form.form(CharacterSheet.class).bindFromRequest().get();
        // Set the User related to that character sheet
        characterSheet.setUser(Integer.parseInt(session().get("user_id")));

        boolean error = false;

        if(characterSheet.getMastery().equals(characterSheet.getLegendary()) ||
                characterSheet.getMastery().equals(characterSheet.getSpecialty()) ||
                        characterSheet.getSpecialty().equals(characterSheet.getLegendary())) {
            error = true;
        }

        if(error) {
            flash("danger", "Трябва да изберете 3 различни умения!");
            return redirect(routes.Gamebook.chooseSkill());
        }

        // Save it to the database
        characterSheet.save();
        return redirect(routes.Gamebook.skillBonus());
    }

    @Security.Authenticated(Secured.class)
    public static Result skillBonus() {

        List<CharacterSheet> characterSheet = CharacterSheet.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();

        int latestCSIndex = characterSheet.size()-1;
        int wisdom = characterSheet.get(latestCSIndex).getWisdom();
        int mind   = characterSheet.get(latestCSIndex).getMind();

        String bonus = "Няма бонус";

        String legendary = characterSheet.get(latestCSIndex).getLegendary();

        switch (legendary) {
            case "Имунитет към отрови" :
                bonus = "+1 т. Познание";
                wisdom++;
                break;
            case "Стрелба с лък" :
                bonus = "+1 т. Съзнание";
                mind++;
                break;
            case "Бой с меч" :
                bonus = "+1 т. Съзнание";
                mind++;
                break;
            case "Самоконтрол" :
                bonus = "+1 т. Съзнание";
                mind++;
                break;
            case "Шаманизъм" :
                bonus = "+1 т. Познание";
                wisdom++;
                break;
        }

        if(characterSheet.get(latestCSIndex).getBonusReceived() != true) {
            characterSheet.get(latestCSIndex).setWisdom(wisdom);
            characterSheet.get(latestCSIndex).setMind(mind);
            characterSheet.get(latestCSIndex).setBonusReceived(true);
            characterSheet.get(latestCSIndex).save();
        }

        return ok(skillbonus.render(characterSheet.get(characterSheet.size()-1), bonus));
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

        characterSheet.get(characterSheet.size()-1).setCurrentEpisode(number);
        characterSheet.get(characterSheet.size()-1).save();

        return ok(index.render(number, characterSheet.get(characterSheet.size() - 1)));
    }

    @Security.Authenticated(Secured.class)
    public static Result savedGame() {

        List<CharacterSheet> characterSheet = CharacterSheet.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();
        try {
            int number = characterSheet.get(characterSheet.size()-1).getCurrentEpisode();
            return redirect(routes.Gamebook.displayEpisode(number));
        }
        catch (Exception e) {
            return redirect(routes.Gamebook.chooseSkill());
        }
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
