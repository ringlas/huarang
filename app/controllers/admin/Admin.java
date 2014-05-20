package controllers.admin;

import controllers.*;
import models.*;
import models.Gamebook;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.*;
import java.util.Date;
import java.util.List;

import scala.util.parsing.combinator.testing.Str;
import views.html.admin.*;

/**
 * Created by vik_a_000 on 14-5-4.
 */
public class Admin extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result dashboard() {

        if(!session().get("role").equals("admin")){
            flash("error", "Нямате достъп до тази страница.");
            return redirect(controllers.routes.Library.dashboard());
        }

        return ok(dashboard.render("Dashboard page!"));
    }

    @Security.Authenticated(Secured.class)
    public static Result listGamebooks() {

        List<Gamebook> gamebooks = Gamebook.find.where()
                .eq("user_id", session().get("user_id"))
                .findList();

        return ok(listgamebooks.render("List gamebooks page!", gamebooks));
    }

    @Security.Authenticated(Secured.class)
    public static Result createGamebook() {

        return ok(addgamebook.render("Create gamebook page!"));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveGamebook() {
        Form<Gamebook> gamebookForm = Form.form(Gamebook.class).bindFromRequest();

        if(gamebookForm.hasErrors()){

            flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
            return redirect(routes.Admin.createGamebook());
        }

        Gamebook gamebook = gamebookForm.get();

        // set all fields
        Date currentDate = new Date();
        gamebook.setDateCreated(currentDate.toString());
        gamebook.setUser(Integer.parseInt(session().get("user_id")));

        if(gamebook.getId() > 0) {
            gamebook.update();
        }
        else {
            // Save the gamebook
            gamebook.save();
        }

        flash("success", "Записът беше запазен успешно!");
        return redirect(routes.Admin.listGamebooks());
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteGamebook(int gamebookId) {

        Gamebook gamebook = Gamebook.find.byId(gamebookId);

        if(gamebook != null) {

            try {
                gamebook.delete();
                flash("success", "Успешно изтрихте записа от базата данни.");
                return redirect(routes.Admin.listGamebooks());

            } catch (Exception e) {
                flash("error", "Възникна грешка. Моля, опитайте отново.");
                return redirect(routes.Admin.listGamebooks());
            }
        }
        else {
            flash("error", "Не съществува запис такова id.");
            return redirect(routes.Admin.listGamebooks());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result editGamebook(int gamebookId) {

        Gamebook gamebook = Gamebook.find.byId(gamebookId);

        return ok(editgamebook.render("Edit gamebook page!", gamebook));
    }

    @Security.Authenticated(Secured.class)
    public static Result viewGamebook(int gamebookId) {

        Gamebook gamebook = Gamebook.find.byId(gamebookId);

        List<Episode> episodes = Episode.find.where()
                .eq("gamebook_id", gamebookId)
                .orderBy("number asc")
                .findList();

        return ok(viewgamebook.render("View gamebook page!", gamebook, episodes));
    }

    /************************* EPISODE LOGIC *****************************/


    @Security.Authenticated(Secured.class)
    public static Result createEpisode(int gamebookId) {

        Gamebook gamebook = Gamebook.find.byId(gamebookId);

        return ok(addepisode.render("Create episode page!", gamebook));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveEpisode() {
        Form<Episode> episodeForm = Form.form(Episode.class).bindFromRequest();
        DynamicForm requestData = Form.form().bindFromRequest();

        try {
            int gamebookId = Integer.parseInt(requestData.get("gamebook_id"));

            if(episodeForm.hasErrors()){

                flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
                return redirect(routes.Admin.createEpisode(gamebookId));
            }

            Episode episode = episodeForm.get();

            // get the episode id from the submitted form
            int episodeId = episode.getId();

            // set the gamebook object
            episode.setGamebook(gamebookId);

            if(episodeId > 0) {

                episode.update();
            }
            else {

                episode.save();
            }

            flash("success", "Записът беше запазен успешно!");
            return redirect(routes.Admin.viewGamebook(gamebookId));

        } catch (Exception e){
            flash("error", "Възникна грешка! Моля, опитайте пак.");
            return redirect(routes.Admin.listGamebooks());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result editEpisode(int episodeId) {

        Episode episode = Episode.find.byId(episodeId);

        return ok(editepisode.render("Edit episode page!", episode.getGamebook(), episode));
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteEpisode(int episodeId) {

        Episode episode = Episode.find.byId(episodeId);

        if(episode != null) {

            try {
                episode.delete();
                flash("success", "Успешно изтрихте записа от базата данни.");
                return redirect(routes.Admin.viewGamebook(episode.getGamebook().getId()));

            } catch (Exception e) {
                flash("error", "Възникна грешка. Моля, опитайте отново.");
                return redirect(routes.Admin.viewGamebook(episode.getGamebook().getId()));
            }
        }
        else {
            flash("error", "Не съществува запис такова id.");
            return redirect(routes.Admin.listGamebooks());
        }
    }
}
