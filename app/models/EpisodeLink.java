package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;

/**
 * Created by victor on 14-2-28.
 */
@Entity
public class EpisodeLink extends Model {

    @Id
    private int id;

    @ManyToOne
    private Episode episode;

    private String text;

    @ManyToOne
    private Episode goToEpisode;

    public int getId(){
        return this.id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getEpisode() {
        return this.episode.getId();
    }

    public int getGoToEpisode() {
        return this.goToEpisode.getId();
    }

    public static Finder<Integer,EpisodeLink> find = new Finder<Integer,EpisodeLink>(
            Integer.class, EpisodeLink.class
    );
}
