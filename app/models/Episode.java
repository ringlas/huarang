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

    public int getId() {
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @OneToMany(mappedBy = "episode", cascade=CascadeType.ALL)
    public List<EpisodeLink> episodeLinks;

    public static Finder<Integer,Episode> find = new Finder<Integer,Episode>(
            Integer.class, Episode.class
    );
}
