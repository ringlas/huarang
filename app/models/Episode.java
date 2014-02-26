package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Created by victor on 14-2-24.
 */
@Entity
public class Episode extends Model {

    @Id
    private int id;

    private int number;

    @Lob
    @Column(name="text", length = 512)
    private String text;

    public int getId() {
        return this.id;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Finder<Integer,Episode> find = new Finder<Integer,Episode>(
            Integer.class, Episode.class
    );
}
