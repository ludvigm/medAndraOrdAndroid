package com.example.ludvig.medandraord;

import java.io.Serializable;

class Team implements Serializable {


    private String teamName;
    private int score;

    public Team() {

    }

    Team(String name, int score) {
        teamName = name;
        this.score = score;
    }

    void giveScore(int score) {
        this.score += score;
    }

    //getters setters
    String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
