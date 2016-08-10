package com.example.ludvig.medandraord;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Ludvig on 8/8/2016.
 */
public class Team implements Serializable {


    private String teamName;
    private int score;

    public Team() {

    }

    public Team(String name, int score) {
        teamName = name;
        this.score = score;
    }

    public void giveScore(int score) {
        this.score +=score;
    }

    //getters setters
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
