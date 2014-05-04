package models;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by vik_a_000 on 14-3-1.
 */
@Entity
public class CharacterSheetHuarang extends Model {

    @Id
    private int id;

    private int energy;

    private int wisdom;

    private int mind;

    private int stamina;

    private String specialty;

    private String mastery;

    private String legendary;

    private boolean bonusReceived;

    private int currentEpisode;

    private String dateCreated;

    @Lob
    @Column(name="text", length = 512)
    private String codewords;

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

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void setWisdom(int wisdom) {
        this.wisdom = wisdom;
    }

    public int getWisdom() {
        return this.wisdom;
    }

    public void setMind(int mind) {
        this.mind = mind;
    }

    public int getMind() {
        return this.mind;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getStamina() {
        return this.stamina;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getSpecialty() {
        return this.specialty;
    }

    public void setMastery(String mastery) {
        this.mastery = mastery;
    }

    public String getMastery() {
        return this.mastery;
    }

    public void setLegendary(String legendary) {
        this.legendary = legendary;
    }

    public String getLegendary() {
        return this.legendary;
    }

    public void setCodewords(String codewords) {
        this.codewords = codewords;
    }

    public String getCodewords() {
        return this.codewords;
    }

    public void setBonusReceived(boolean bonusReceived) { this.bonusReceived = bonusReceived; }

    public boolean getBonusReceived() { return this.bonusReceived; }

    public void setCurrentEpisode(int currentEpisode) { this.currentEpisode = currentEpisode; }

    public int getCurrentEpisode() { return this.currentEpisode; }

    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public String getDateCreated() { return this.dateCreated; }

    public static Finder<Integer, CharacterSheetHuarang> find = new Finder<Integer, CharacterSheetHuarang>(
            Integer.class, CharacterSheetHuarang.class
    );
}
