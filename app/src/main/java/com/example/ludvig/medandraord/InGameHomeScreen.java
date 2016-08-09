package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class InGameHomeScreen extends AppCompatActivity {

    LinkedList<String> words;
    LinkedList<Team> teams;
    TextView wordDisplay;
    TextView timerDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game_home_screen);

        Intent intent = getIntent();
        int numberOfTeams = intent.getIntExtra("numberOfPlayers",2);
        int diff = intent.getIntExtra("difficulty",2);

        System.out.println("players: " + numberOfTeams);
        System.out.println("diff: " + diff);


        wordDisplay = (TextView) findViewById(R.id.wordDisplay);
        timerDisplay = (TextView) findViewById(R.id.timerDisplay);

        switch(diff) {
            case 0: words = getWordsFromResources(R.array.easy_words);
                break;
            case 1: words = getWordsFromResources(R.array.default_words);
                break;
            case 2: words = getWordsFromResources(R.array.hard_words);
                break;
        }
        Collections.shuffle(words);
        teams = createTeams(numberOfTeams,0,false);
        generateHomeScreen();




    }


    private LinkedList<String> getWordsFromResources(int id) {
        return new LinkedList<>(Arrays.asList(getResources().getStringArray(id)));
    }

    private LinkedList<Team> createTeams(int numberOfTeams, int skips, boolean customTeamNames) {
        LinkedList<Team> teams = new LinkedList<>();
        if(!customTeamNames) {
            for (; numberOfTeams > 0; numberOfTeams--) {
                Team t = new Team("Team " + numberOfTeams, skips,0);
                teams.addFirst(t);
            }
        } else {
            //if user wants custom teamnames..
        }

        return teams;
    }

    private void generateHomeScreen() {
        TableLayout table = (TableLayout) findViewById(R.id.inGameHomeScreen_tableLayout);
        for(Team t : teams) {
            TextView name = new TextView(this);
            name.setText(t.getTeamName());
            TextView score = new TextView(this);
            score.setText(""+t.getScore());
            TableRow row = new TableRow(this);
            row.addView(name);
            row.addView(score);
            table.addView(row);

        }

    }

}
