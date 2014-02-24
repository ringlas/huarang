package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by victor on 14-2-24.
 */
@Entity
public class Bar extends Model {

    @Id
    public String id;

    public String name;
}
