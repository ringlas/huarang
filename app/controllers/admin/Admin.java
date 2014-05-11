package controllers.admin;

import controllers.*;
import models.*;
import models.Gamebook;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.*;
import java.util.Date;
import java.util.List;

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

        return ok(viewgamebook.render("View gamebook page!", gamebook));
    }

    /************************* EPISODE LOGIC *****************************/


    @Security.Authenticated(Secured.class)
    public static Result createEpisode(int gamebookId) {

        Gamebook gamebook = Gamebook.find.byId(gamebookId);

        return ok(addepisode.render("Create episode page!", gamebook));
    }

}
