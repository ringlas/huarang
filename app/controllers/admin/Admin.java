package controllers.admin;

import controllers.*;
import models.*;
import models.Gamebook;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.*;


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

    public static Result createGamebook() {

        return ok(creategamebook.render("Create gamebook page!"));
    }

    public static Result saveGamebook() {
        Form<Gamebook> gamebookForm = Form.form(Gamebook.class).bindFromRequest();

        if(gamebookForm.hasErrors()){

            flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
            return redirect(routes.Admin.dashboard());
        }

        Gamebook gamebook = gamebookForm.get();

        // Save the user
        gamebook.save();

        flash("success", "Успешно създадохте нова книга-игра! Моля, започнете да попълвате нейните епизоди.");
        return redirect(routes.Admin.dashboard());
    }

}
