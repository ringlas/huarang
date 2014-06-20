package controllers;

import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.*;

import views.html.fmi.*;

/**
 * Created by vik_a_000 on 14-5-4.
 */
public class Fmi extends Controller {

    @Security.Authenticated(Secured.class)
    public static Result map() {
        return ok(map.render());
    }

    @Security.Authenticated(Secured.class)
    public static Result home() {

        Gamebook gamebook = Gamebook.find.where()
                .eq("title", "Факултетът на бъдещето")
                .findUnique();

        return ok(home.render(gamebook));
    }

    @Security.Authenticated(Secured.class)
    public static Result displayEpisode(int gamebook_id, int number) {

        CharacterSheetFmi characterSheet = CharacterSheetFmi.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", gamebook_id)
                .findUnique();

        int currentTime = characterSheet.getTime();

        switch (number) {
            case 4 : currentTime = currentTime - 2; break;
            case 5 : currentTime = currentTime - 3; break;
            case 6 : currentTime = currentTime - 3; break;
            case 8 : currentTime = currentTime - 1; break;
            case 9 : currentTime = currentTime + 1; break;
        }

        if(currentTime < 0) {
            currentTime = 0;
        }

        characterSheet.setTime(currentTime);
        characterSheet.setCurrentEpisode(number);
        characterSheet.update();

        if(currentTime == 0) {
            return redirect(routes.Fmi.test(gamebook_id));
        }

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
            return ok(home.render(gamebook));
        }
        else {
            flash("error", "Няма намерена такава книга-игра!");
            return redirect(routes.Fmi.home());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result updateCharSheet(int gamebook_id, int number) {

        try {

            CharacterSheetFmi characterSheet = Form.form(CharacterSheetFmi.class).bindFromRequest().get();

            // Set the User related to that character sheet
            characterSheet.setUser(Integer.parseInt(session().get("user_id")));
            characterSheet.setGamebook(gamebook_id);
            characterSheet.update();

            flash("success", "Дневникът ви беше обновен!");
            return redirect(routes.Fmi.displayEpisode(gamebook_id, number));

        }catch (Exception e) {

            flash("error", "Възникна грешка! Моля, опитайте пак.");
            return redirect(routes.Fmi.displayEpisode(gamebook_id, number));
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result startGame(int id, boolean replay) {

        // Check to see if player wants to continue from last milestone
        // Or start a new game all over again with new char sheet

        CharacterSheetFmi characterSheetOld = CharacterSheetFmi.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", id)
                .findUnique();

        // start over
        if(replay) {

            if(characterSheetOld != null) {
                characterSheetOld.delete();
            }

            Date currentDate = new Date();

            CharacterSheetFmi characterSheet = new CharacterSheetFmi();
            characterSheet.setCurrentEpisode(1);
            characterSheet.setGamebook(id);
            characterSheet.setDateCreated(currentDate.toString());
            characterSheet.setCodewords("");
            characterSheet.setNotes("");
            characterSheet.setTime(5);
            characterSheet.setUser(Integer.parseInt(session().get("user_id")));

            characterSheet.save();

            return redirect(routes.Fmi.displayEpisode(id, 1));
        }
        // continue
        else {

            if(characterSheetOld != null) {
                return redirect(routes.Fmi.displayEpisode(id, characterSheetOld.getCurrentEpisode()));
            }
            else {
                flash("error", "Няма намерен дневник за тази книга-игра!");
                return redirect(routes.Fmi.displayGamebook(id));
            }
        }
    }

    /**
     * Method displaying the exam test
     *
     * @param gamebook_id
     * @return
     */
    @Security.Authenticated(Secured.class)
    public static Result test(int gamebook_id) {

        CharacterSheetFmi characterSheet = CharacterSheetFmi.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", gamebook_id)
                .findUnique();

        Test test = Test.find.where()
                .eq("gamebook_id", gamebook_id)
                .findUnique();

        List<Question> questions = Question.find.where()
                .eq("test_id", test.getId())
                .findList();

        return ok(testpage.render(questions, characterSheet));
    }

    @Security.Authenticated(Secured.class)
    public static Result calculateScore(int gamebook_id) {

        DynamicForm requestData = Form.form().bindFromRequest();

        Map<String, String> data = requestData.get().getData();

        int points = calculateScore(data, gamebook_id);

        CharacterSheetFmi characterSheet = CharacterSheetFmi.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", gamebook_id)
                .findUnique();

        characterSheet.setPoints(points);
        characterSheet.update();

        return redirect(routes.Fmi.rating(gamebook_id));
    }

    @Security.Authenticated(Secured.class)
    public static Result rating(int gamebook_id) {

        CharacterSheetFmi characterSheet = CharacterSheetFmi.find.where()
                .eq("user_id", Integer.parseInt(session().get("user_id")))
                .eq("gamebook_id", gamebook_id)
                .findUnique();

        List<CharacterSheetFmi> characterSheets = CharacterSheetFmi.find.where()
                .findList();

        return ok(successpage.render(characterSheet, characterSheets));
    }

    private static int calculateScore(Map<String, String> data, int gamebook_id) {

        int points = 0;

        try {

            Test test = Test.find.where()
                    .eq("gamebook_id", gamebook_id)
                    .findUnique();

            List<Question> questions = Question.find.where()
                    .eq("test_id", test.getId())
                    .findList();

            Iterator it = data.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();

                Question question = Question.find.byId(Integer.parseInt(pairs.getKey().toString()));

                int value = Integer.parseInt(pairs.getValue().toString());

                if(question.getCorrect_answer() == value) {
                    points++;
                }

                it.remove();
            }
        }

        catch(Exception e) {
            return 0;
        }

        return points;
    }

}
