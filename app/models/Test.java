package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by victor on 14-5-30.
 */
@Entity
public class Test extends Model {

    @Id
    private int id;

    @Column(name="title", length = 255)
    private String title;

    @ManyToOne
    private Gamebook gamebook;

//    @OneToMany(mappedBy = "Question")
//    private Question question;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Gamebook getGamebook() {
        return gamebook;
    }

    public void setGamebook(int id) {
        Gamebook gamebook = Gamebook.find.byId(id);
        this.gamebook = gamebook;
    }

    public static Finder<Integer,Test> find = new Finder<Integer,Test>(
            Integer.class, Test.class
    );
}
