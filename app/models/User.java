package models;

import org.springframework.beans.factory.annotation.Required;
import play.api.data.validation.ValidationError;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Crypto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 14-2-26.
 */
@Entity
public class User extends Model {

    @Id
    private int id;

    @Column(name="username", length = 255)
    @Constraints.Required
    private String username;

    @Column(name="password", length = 255)
    @Constraints.Required
    private String password;

    public int getId(){
        return this.id;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){return this.password;}

    public static String authenticate(String username, String password){
        List<User> userList = find.where()
                .eq("username", username)
                .eq("password", Crypto.sign(password))
                .findList();

        if(userList.isEmpty()){
            return null;
        }
        else {
            return userList.get(0).getUsername();
        }
    }

    public static Finder<Integer,User> find = new Finder<Integer,User>(
            Integer.class, User.class
    );

//    public List<ValidationError> validate() {
//        List<ValidationError> errors = new ArrayList<ValidationError>();
//
//        return errors.isEmpty() ? null : errors;
//    }
}
