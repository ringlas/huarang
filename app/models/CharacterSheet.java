package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by vik_a_000 on 14-5-4.
 */

@Entity
public class CharacterSheet extends Model {

    @Id
    private int id;

    private int currentEpisode;

    private String dateCreated;

    @Lob
    @Column(name="codewords", length = 512)
    private String codewords;

    @Lob
    @Column(name="notes", length = 512)
    private String notes;

    @ManyToOne
    private User user;

    @ManyToOne
    private Gamebook gamebook;

    public void setUser(int id) {
        User user = User.find.byId(id);
        this.user = user;
    }

    public void setGamebook(int id) {
        Gamebook gamebook = Gamebook.find.byId(id);
        this.gamebook = gamebook;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setCodewords(String codewords) {
        this.codewords = codewords;
    }

    public String getCodewords() {
        return this.codewords;
    }

    public void setCurrentEpisode(int currentEpisode) { this.currentEpisode = currentEpisode; }

    public int getCurrentEpisode() { return this.currentEpisode; }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public String getDateCreated() { return this.dateCreated; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    public static Finder<Integer, CharacterSheet> find = new Finder<Integer, CharacterSheet>(
            Integer.class, CharacterSheet.class
    );

}
