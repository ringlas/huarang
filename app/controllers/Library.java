package controllers;

import models.CharacterSheet;
import models.Episode;
import models.Gamebook;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.*;

import views.html.*;
import views.html.huarang.*;

import java.util.Date;
import java.util.List;

/**
 * Created by vik_a_000 on 14-5-4.
 */
public class Library extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result dashboard() {

        List<Gamebook> gamebooks = Gamebook.find.where()
                .ne("title", "ХУАРАНГ И КУМИХО")
                .findList();

        return ok(dashboard.render("Dashboard page!", gamebooks));
    }

    @Security.Authenticated(Secured.class)
    public static Result displayEpisode(int gamebook_id, int number) {

        CharacterSheet characterSheet = CharacterSheet.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", gamebook_id)
                .findUnique();

        characterSheet.setCurrentEpisode(number);
        characterSheet.update();

        return ok(gamebook.render(gamebook_id, number, characterSheet));
    }

    @Security.Authenticated(Secured.class)
    public static Result generateEpisode(int gamebook_id, int number) {

        Episode episode = Episode.find.where()
                .eq("gamebook_id", gamebook_id)
                .eq("number", number)
                .findUnique();

        if(episode != null) {
            episode.setText(episode.getText().replaceAll("\\n", "<br>"));
            return ok(Json.toJson(episode));
        }
        else {
            return ok("Няма резултати в базата данни!");
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result displayGamebook(int id) {

        Gamebook gamebook = Gamebook.find.where()
                .eq("id", id)
                .findUnique();

        if(gamebook != null) {
            return ok(game.render(gamebook));
        }
        else {
            flash("error", "Няма намерена такава книга-игра!");
            return redirect(routes.Library.dashboard());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updateCharSheet(int gamebook_id, int number) {

        try {

            CharacterSheet characterSheet = Form.form(CharacterSheet.class).bindFromRequest().get();

            // Set the User related to that character sheet
            characterSheet.setUser(Integer.parseInt(session().get("user_id")));
            characterSheet.setGamebook(gamebook_id);
            characterSheet.update();

            flash("success", "Дневникът ви беше обновен!");
            return redirect(routes.Library.displayEpisode(gamebook_id, number));

        }catch (Exception e) {

            flash("error", "Възникна грешка! Моля, опитайте пак.");
            return redirect(routes.Library.displayEpisode(gamebook_id, number));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result startGame(int id, boolean replay) {

        // Check to see if player wants to continue from last milestone
        // Or start a new game all over again with new char sheet

        CharacterSheet characterSheetOld = CharacterSheet.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", id)
                .findUnique();

        // start over
        if(replay) {

            if(characterSheetOld != null) {
                characterSheetOld.delete();
            }

            Date currentDate = new Date();

            CharacterSheet characterSheet = new CharacterSheet();
            characterSheet.setCurrentEpisode(1);
            characterSheet.setGamebook(id);
            characterSheet.setDateCreated(currentDate.toString());
            characterSheet.setCodewords("");
            characterSheet.setNotes("");
            characterSheet.setUser(Integer.parseInt(session().get("user_id")));

            characterSheet.save();

            return redirect(routes.Library.displayEpisode(id, 1));
        }
        // continue
        else {

            if(characterSheetOld != null) {
                return redirect(routes.Library.displayEpisode(id, characterSheetOld.getCurrentEpisode()));
            }
            else {
                flash("error", "Няма намерен дневник за тази книга-игра!");
                return redirect(routes.Library.displayGamebook(id));
            }
        }
    }
}
