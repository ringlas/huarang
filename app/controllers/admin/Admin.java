package controllers.admin;

import controllers.*;
import models.*;
import models.Gamebook;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.nio.file.Files;

import scala.util.parsing.combinator.testing.Str;
import scalax.io.support.FileUtils;
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

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");

        String pictureName = "";

        Gamebook gamebook = gamebookForm.get();

        if (picture != null) {
            String fileName = picture.getFilename();
            String contentType = picture.getContentType();

            if(!contentType.equals("image/png") && !contentType.equals("image/jpeg")){
                flash("error", "Невалидно разширение на файл!");
                return redirect(routes.Admin.listGamebooks());
            }

            File file = picture.getFile();

            try {
                file.renameTo(new File("public/images/covers/" + fileName));
            } catch (Exception e) {
                flash("error", "Възникна проблем с преместването на файла!");
                return redirect(routes.Admin.listGamebooks());
            }
            pictureName = fileName;
        }

        // set all fields
        Date currentDate = new Date();
        gamebook.setDateCreated(currentDate.toString());
        gamebook.setUser(Integer.parseInt(session().get("user_id")));

        // change book cover only if such was uploaded
        if(picture != null) {
            gamebook.setPicture("images/covers/" + pictureName);
        }

        if(gamebook.getId() > 0) {

            if(gamebookForm.hasErrors()){

                flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
                return redirect(routes.Admin.editGamebook(gamebook.getId()));
            }

            gamebook.update();
        }
        else {

            if(gamebookForm.hasErrors()){

                flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
                return redirect(routes.Admin.createGamebook());
            }

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

        for (int i = 0; i < episodes.size(); i++) {
            String str = episodes.get(i).getText();
            str = str.replaceAll("###(\\d+)###", "<a href='#episode$1' class='tooltipped' data-toggle='tooltip' data-placement='top' title='Връзка към свързания епизод.' class='btn btn-primary'>$1</a>");
            episodes.get(i).setText(str);
        }

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

    /************************* USER LOGIC *****************************/

    @Security.Authenticated(Secured.class)
    public static Result listUsers() {

        List<User> users = User.find.all();

        return ok(listusers.render("List all users page!", users));
    }

    @Security.Authenticated(Secured.class)
    public static Result viewUser(int id) {

        User user = User.find.byId(id);
        return ok(viewuser.render("View user details page!", user));
    }

    @Security.Authenticated(Secured.class)
    public static Result createUser() {

        return ok(adduser.render("Add user page!"));
    }

    @Security.Authenticated(Secured.class)
    public static Result editUser(int id) {

        User user = User.find.byId(id);

        return ok(edituser.render("Edit user page!", user));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveUser() {

        Form<User> userForm = Form.form(User.class).bindFromRequest();
        DynamicForm requestData = Form.form().bindFromRequest();

        User user = userForm.get();
        int userId = user.getId();
        String password = user.getPassword();
        boolean changedPassword = false;

        if(password != null && !password.isEmpty()) {
            String confirmPassword = requestData.get("confirm_password");
            if(!password.equals(confirmPassword)) {
                flash("error", "Паролите не съвпадат!");
                return redirect(routes.Admin.listUsers());
            }
            else {
                // Encrypt the user password with SHA-1 algorithm and Application SALT
                user.setPassword(Crypto.sign(user.getPassword()));
                changedPassword = true;
            }
        }

        if(userId != 0) {
            // Update the user
            if(!changedPassword) {
                user.setPassword(null);
            }
            user.update();
            flash("success", "Успешни промени!");
            return redirect(routes.Admin.editUser(userId));

        } else {
            // Save the user
            user.save();
            flash("success", "Успешни промени!");
            return redirect(routes.Admin.listUsers());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteUser(int user_id) {
        User user = User.find.byId(user_id);

        if(user != null) {
            try {
                user.delete();
                flash("success", "Успешно изтрихте записа от базата данни.");
                return redirect(routes.Admin.listUsers());

            } catch (Exception e) {
                flash("error", "Възникна грешка. Моля, опитайте отново.");
                return redirect(routes.Admin.listUsers());
            }
        }
        else {
            flash("error", "Не съществува запис такова id.");
            return redirect(routes.Admin.listUsers());
        }
    }

    /************************* TEST LOGIC *****************************/

    @Security.Authenticated(Secured.class)
    public static Result listTests() {

        List<Test> tests = Test.find.all();
        List points = new ArrayList();

        for(int i = 0; i < tests.size(); i++) {
            List<Question> questions = Question.find.where()
                    .eq("test_id", tests.get(i).getId())
                    .findList();
            points.add(i, questions.size());
        }

        return ok(listtests.render("List all tests page!", tests, points));
    }

    @Security.Authenticated(Secured.class)
    public static Result viewTest(int id) {

        Test test = Test.find.byId(id);

        List<Question> questions = Question.find.where()
                .eq("test_id", id)
                .findList();

        return ok(viewtest.render("View test details page!", test, questions));
    }

    @Security.Authenticated(Secured.class)
    public static Result createTest() {

        List<Gamebook> gamebooks = Gamebook.find.all();

        return ok(addtest.render("Add test page!", gamebooks));
    }

    @Security.Authenticated(Secured.class)
    public static Result editTest(int id) {

        Test test = Test.find.byId(id);

        List<Gamebook> gamebooks = Gamebook.find.all();

        return ok(edittest.render("Edit test page!", gamebooks, test));
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteTest(int id) {
        Test test = Test.find.byId(id);

        if(test != null) {
            try {
                test.delete();
                flash("success", "Успешно изтрихте записа от базата данни.");
                return redirect(routes.Admin.listTests());

            } catch (Exception e) {
                flash("error", "Възникна грешка. Моля, опитайте отново.");
                return redirect(routes.Admin.listTests());
            }
        }
        else {
            flash("error", "Не съществува запис такова id.");
            return redirect(routes.Admin.listTests());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result saveTest() {

        Form<Test> testForm = Form.form(Test.class).bindFromRequest();

        if(testForm.hasErrors()){

            flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
            return redirect(routes.Admin.createTest());
        }

        int gamebook_id = Integer.parseInt(testForm.data().get("gamebook_id"));

        Test test = testForm.get();
        test.setGamebook(gamebook_id);

        if(test.getId() != 0) {
            // Update the test
            test.update();
            flash("success", "Успешни промени!");
            return redirect(routes.Admin.listTests());

        } else {
            // Save the test
            test.save();
            flash("success", "Успешни промени!");
            return redirect(routes.Admin.listTests());
        }
    }

    /************************* QUESTION LOGIC *****************************/

    @Security.Authenticated(Secured.class)
    public static Result createQuestion(int test_id) {

        Test test = Test.find.byId(test_id);

        return ok(addquestion.render("Add question page!", test));
    }

    @Security.Authenticated(Secured.class)
    public static Result editQuestion(int id) {

        Question question = Question.find.byId(id);

        return ok(editquestion.render("Edit question page!", question));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveQuestion(int test_id) {

        Form<Question> questionForm = Form.form(Question.class).bindFromRequest();

        if(questionForm.hasErrors()){

            flash("error", "Грешно попълнена форма. Моля, опитайте пак.");
            return redirect(routes.Admin.createTest());
        }

        Test test = Test.find.byId(test_id);

        Question question = questionForm.get();

        question.setTest(test);

        if(question.getId() != 0) {
            // Update the question
            question.update();
            flash("success", "Успешни промени!");
            return redirect(routes.Admin.viewTest(test_id));

        } else {
            // Save the question
            question.save();
            flash("success", "Успешни промени!");
            return redirect(routes.Admin.viewTest(test_id));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result deleteQuestion(int id) {
        Question question = Question.find.byId(id);

        int test_id = question.getTest().getId();
        question.setTest(null);

        if(question != null) {
            try {
                question.delete();
                flash("success", "Успешно изтрихте записа от базата данни.");
                return redirect(routes.Admin.viewTest(test_id));

            } catch (Exception e) {
                flash("error", "Възникна грешка. Моля, опитайте отново.");
                return redirect(routes.Admin.viewTest(test_id));
            }
        }
        else {
            flash("error", "Не съществува запис такова id.");
            return redirect(routes.Admin.viewTest(test_id));
        }
    }

    /************************* IMPORT BOOK LOGIC *****************************/

    @Security.Authenticated(Secured.class)
    public static Result importGamebook() {
        return ok(importbook.render("lqllq!"));
    }

    @Security.Authenticated(Secured.class)
    public static Result saveImportGamebook() {
    	final Map<String, String[]> values = request().body().asFormUrlEncoded();
    	Gamebook book = new Gamebook();

    	book.setTitle( values.get("title")[0] );
    	book.setAuthor( values.get("author")[0] );
    	book.setYear(Integer.parseInt(values.get("year")[0]));
    	book.setUser(Integer.parseInt(session().get("user_id")));
    	book.setIntro(values.get("intro")[0]);
    	book.setDateCreated((new Date()).toString());
    	book.save();

		int id =  book.getId();
        String text = values.get("episodes")[0];

        if(text.startsWith(",,,")){
            text = text.substring(3);
        }

		String[] eps = text.split(",,,");
		for( int i = 0; i < eps.length; i++ ) {
			Episode ep = new Episode();
			ep.setText( eps[i] );
			ep.setGamebook( id );
			ep.setNumber( i + 1 );
			ep.save();
		}

    	return ok("Книгата е добавена успешно!");
    }
}
