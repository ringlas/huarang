package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Created by victor on 14-2-26.
 */
@Entity
public class User extends Model {

    @Id
    private int id;

    @Column(name="username", length = 255)
    private String username;

    @Column(name="password", length = 255)
    private String password;

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public static Finder<Integer,User> find = new Finder<Integer,User>(
            Integer.class, User.class
    );
}
