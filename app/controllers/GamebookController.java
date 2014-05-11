package controllers;

import models.CharacterSheetHuarang;
import models.Episode;
import play.data.Form;
import play.mvc.*;
import play.libs.Json;

import views.html.huarang.*;

import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by victor on 14-2-25.
 */
public class GamebookController extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result chooseSkill() {

        CharacterSheetHuarang characterSheetHuarang = CharacterSheetHuarang.find.where()
                .eq("user_id", session().get("user_id")).findUnique();

        if(characterSheetHuarang != null) {
            characterSheetHuarang.delete();
        }

        return ok(skills.render("Skill page"));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveSkill(){
        // Bind the request data from the form to an object
        CharacterSheetHuarang characterSheetHuarang = Form.form(CharacterSheetHuarang.class).bindFromRequest().get();
        // Set the User related to that character sheet
        characterSheetHuarang.setUser(Integer.parseInt(session().get("user_id")));

        boolean error = false;

        if(characterSheetHuarang.getMastery().equals(characterSheetHuarang.getLegendary()) ||
                characterSheetHuarang.getMastery().equals(characterSheetHuarang.getSpecialty()) ||
                        characterSheetHuarang.getSpecialty().equals(characterSheetHuarang.getLegendary())) {
            error = true;
        }

        if(error) {
            flash("danger", "Трябва да изберете 3 различни умения!");
            return redirect(routes.GamebookController.chooseSkill());
        }

        // Save it to the database
        characterSheetHuarang.save();
        return redirect(routes.GamebookController.skillBonus());
    }

    @Security.Authenticated(Secured.class)
    public static Result skillBonus() {

        List<CharacterSheetHuarang> characterSheetHuarang = CharacterSheetHuarang.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();

        int latestCSIndex = characterSheetHuarang.size()-1;
        int wisdom = characterSheetHuarang.get(latestCSIndex).getWisdom();
        int mind   = characterSheetHuarang.get(latestCSIndex).getMind();

        String bonus = "Няма бонус";

        String legendary = characterSheetHuarang.get(latestCSIndex).getLegendary();

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

        if(characterSheetHuarang.get(latestCSIndex).getBonusReceived() != true) {
            characterSheetHuarang.get(latestCSIndex).setWisdom(wisdom);
            characterSheetHuarang.get(latestCSIndex).setMind(mind);
            characterSheetHuarang.get(latestCSIndex).setBonusReceived(true);
            characterSheetHuarang.get(latestCSIndex).save();
        }

        return ok(skillbonus.render(characterSheetHuarang.get(characterSheetHuarang.size()-1), bonus));
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

        List<CharacterSheetHuarang> characterSheetHuarang = CharacterSheetHuarang.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();

        characterSheetHuarang.get(characterSheetHuarang.size()-1).setCurrentEpisode(number);
        characterSheetHuarang.get(characterSheetHuarang.size()-1).save();

        return ok(index.render(number, characterSheetHuarang.get(characterSheetHuarang.size() - 1)));
    }

    @Security.Authenticated(Secured.class)
    public static Result savedGame() {

        List<CharacterSheetHuarang> characterSheetHuarang = CharacterSheetHuarang.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();
        try {
            int number = characterSheetHuarang.get(characterSheetHuarang.size()-1).getCurrentEpisode();
            return redirect(routes.GamebookController.displayEpisode(number));
        }
        catch (Exception e) {
            return redirect(routes.GamebookController.chooseSkill());
        }
    }

    public static Result updateCharacterSheet(int number) {
        // Bind the request data from the form to an object
        CharacterSheetHuarang characterSheetHuarang = Form.form(CharacterSheetHuarang.class).bindFromRequest().get();
        // Set the User related to that character sheet
        characterSheetHuarang.setUser(Integer.parseInt(session().get("user_id")));
        // Get the request with the submitted form data
        Http.RequestBody body = request().body();
        // Update the database with that particular id
        characterSheetHuarang.update(Integer.parseInt(body.asFormUrlEncoded().get("id")[0]));
        return redirect(routes.GamebookController.displayEpisode(number));
    }

}
