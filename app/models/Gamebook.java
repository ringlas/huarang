package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by vik_a_000 on 14-5-4.
 */

@Entity
public class Gamebook extends Model {

    @Id
    private int id;

    @Column(name="title", length = 255)
    private String title;

    @Column(name="author", length = 255)
    private String author;

    @Column(name="year", length = 4)
    private int year;

 	@Column(columnDefinition = "TEXT")
    private String intro;

    private String dateCreated;

    private String picture;

    @ManyToOne
    private User user;

    public void setUser(int id) {
        User user = User.find.byId(id);
        this.user = user;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return this.year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setIntro(String intro) { this.intro = intro; }

    public String getIntro() { return this.intro; }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getPicture() { return picture; }

    public void setPicture(String picture) { this.picture = picture; }

    public static Finder<Integer, Gamebook> find = new Finder<Integer, Gamebook>(
            Integer.class, Gamebook.class
    );
}
