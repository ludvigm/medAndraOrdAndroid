package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class GameActivity extends AppCompatActivity {


    LinkedList<String> words;
    LinkedList<Team> teams;
    TextView wordDisplay;
    TextView timerDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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
        start();

    }

    private LinkedList<String> getWordsFromResources(int id) {
        return new LinkedList<>(Arrays.asList(getResources().getStringArray(id)));
    }

    private void start() {

        displayNextWord();

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerDisplay.setText(String.valueOf(millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                timerDisplay.setText("Time out!");
            }

        }.start();

        System.out.println("after timer.");

    }


    public void nextButtonClicked(View view) {
        //Give score.
        displayNextWord();
    }

    public void passButtonClicked(View view) {
        displayNextWord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayNextWord() {

        String word = words.pollFirst();
        if(word!=null) {
            wordDisplay.setText(word);
        }
        /*if(iterator.hasNext()) {
            wordDisplay.setText(iterator.next());
        } else {
            iterator = words.listIterator();
            wordDisplay.setText(iterator.next());
        }*/
    }

    private LinkedList<Team> createTeams(int numberOfTeams, int skips, boolean customTeamNames) {
        LinkedList<Team> teams = new LinkedList<>();
        for(;numberOfTeams>0; numberOfTeams--) {
            Team t = new Team("Team "+numberOfTeams,skips,0);
            teams.addLast(t);
        }
        return teams;
    }
}
