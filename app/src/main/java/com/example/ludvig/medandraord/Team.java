package com.example.ludvig.medandraord;

/**
 * Created by Ludvig on 8/8/2016.
 */
public class Team {


    private String teamName;
    private int skipsLeft;
    private int score;

    public Team() {

    }

    public Team(String name, int skips, int score) {
        teamName = name;
        skipsLeft = skips;
        this.score = score;
    }

    public void incrementScore() {
        this.score++;
    }
    public void decrementSkips() {
        this.skipsLeft--;
    }


    //getters setters
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getSkipsLeft() {
        return skipsLeft;
    }

    public void setSkipsLeft(int skipsLeft) {
        this.skipsLeft = skipsLeft;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }








}
