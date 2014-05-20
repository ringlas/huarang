package models;

import play.db.ebean.Model;

import javax.persistence.*;

import java.util.List;

/**
 * Created by victor on 14-2-24.
 */
@Entity
public class Episode extends Model {

    @Id
    private int id;

    @Lob
    @Column(name="text", length = 512)
    private String text;

    @Column(name="number", length = 4)
    private int number;

    @ManyToOne
    private Gamebook gamebook;

    public void setGamebook(int id) {
        Gamebook gamebook = Gamebook.find.byId(id);
        this.gamebook = gamebook;
    }

    public Gamebook getGamebook() { return  this.gamebook; }

    public void setId(int id) { this.id = id; }

    public int getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumber() { return number; }

    public void setNumber(int number) { this.number = number; }

    @OneToMany(mappedBy = "episode", cascade=CascadeType.ALL)
    public List<EpisodeLink> episodeLinks;

    public static Finder<Integer,Episode> find = new Finder<Integer,Episode>(
            Integer.class, Episode.class
    );
}
