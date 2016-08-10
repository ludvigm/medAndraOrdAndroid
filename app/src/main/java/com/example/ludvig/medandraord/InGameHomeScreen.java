package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class InGameHomeScreen extends AppCompatActivity {

    ArrayList<String> words;
    ArrayList<Team> teams;
    int currentTeamIndex;

    int numberOfTeams;
    int diff;
    int skips;

    boolean resumeAfterOnCreateFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game_home_screen);

            System.out.println("ONCREATE..");
        if(savedInstanceState==null)
            System.out.println("savedinstance was null...");

            resumeAfterOnCreateFlag = true;
            currentTeamIndex = 0;

            Intent intent = getIntent();
            numberOfTeams = intent.getIntExtra("numberOfPlayers", 2);
            diff = intent.getIntExtra("difficulty", 2);
            skips = intent.getIntExtra("skips", 0);

            System.out.println("players: " + numberOfTeams);
            System.out.println("diff: " + diff);
            System.out.println("skips: " + skips);

            switch (diff) {
                case 0:
                    words = getWordsFromResources(R.array.easy_words);
                    break;
                case 1:
                    words = getWordsFromResources(R.array.default_words);
                    break;
                case 2:
                    words = getWordsFromResources(R.array.hard_words);
                    break;
            }
            Collections.shuffle(words);
            teams = createTeams(numberOfTeams, skips, false);

        drawHomeScreen();
    }


   /* @Override
    protected void onSaveInstanceState(Bundle state) {
        System.out.println("onsave is called..");
        super.onSaveInstanceState(state);
        state.putInt("numberOfTeams",numberOfTeams);
        state.putInt("diff",diff);
        state.putInt("skips",skips);
        state.putSerializable("teams", teams);
        state.putStringArrayList("words",words);
        state.putInt("currentTeamIndex",currentTeamIndex);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        System.out.println("OnRestore..");
        numberOfTeams = savedInstanceState.getInt("numberOfTeams");
        diff = savedInstanceState.getInt("diff");
        skips = savedInstanceState.getInt("skips");
        words = savedInstanceState.getStringArrayList("words");
        teams = (ArrayList<Team>) savedInstanceState.getSerializable("teams");
        currentTeamIndex = savedInstanceState.getInt("currentTeamIndex");
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        System.out.println("ONDESTROY was called..");
    }

    @Override
    public void onPause() {
        super.onPause();
        resumeAfterOnCreateFlag = false;
        System.out.println("PAUSE called.");
    }


    @Override
    public void onNewIntent(Intent intent) {
        System.out.println("On new intent......");
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME called..");

        if(!resumeAfterOnCreateFlag) {
            System.out.println("in flag==false..");
            Intent intent = getIntent();
            System.out.println(intent.getIntExtra("scoreAcquired",0));
            teams.get(currentTeamIndex).giveScore(intent.getIntExtra("scoreAcquired", 0));
            removeUsedWords(intent.getStringArrayListExtra("usedWords"));

            currentTeamIndex = (currentTeamIndex + 1) % teams.size();
        }
        drawHomeScreen();
    }


    private ArrayList<String> getWordsFromResources(int id) {
        return new ArrayList<>(Arrays.asList(getResources().getStringArray(id)));
    }

    private void removeUsedWords(ArrayList<String> usedWords) {
        try {
            if (!usedWords.isEmpty()) {
                words.removeAll(usedWords);
            }
        }catch(NullPointerException e) {
            System.out.println("usedWords array null..");
        }
    }

    private ArrayList<Team> createTeams(int numberOfTeams, int skips, boolean customTeamNames) {
        ArrayList<Team> teams = new ArrayList<>();
        if(!customTeamNames) {
            for (; numberOfTeams > 0; numberOfTeams--) {
                Team t = new Team("Team " + numberOfTeams,0);
                teams.add(0,t);
            }
        } else {
            //if user wants custom teamnames..
        }

        return teams;
    }

    private void drawHomeScreen() {
        TableLayout table = (TableLayout) findViewById(R.id.inGameHomeScreen_tableLayout);
        table.removeAllViews();
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

        TextView nextTeamText = (TextView) findViewById(R.id.textView_teamnext);
        nextTeamText.setText(teams.get(currentTeamIndex).getTeamName() + "'s turn");

    }

    public void onStartClicked(View view) {

        Intent intent = new Intent(this,GameActivity.class);
        intent.putStringArrayListExtra("words",words);
        intent.putExtra("skips",skips);
        startActivity(intent);
    }

}
