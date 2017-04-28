package com.example.ludvig.medandraord;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class InGameHomeScreen extends AppCompatActivity {

    //Settings
    String wordListTableName;
    int skips;
    int numberOfTeams;
    int scoreLimit;
    int timeLimit;

    //Current game values
    ArrayList<Team> teams;
    int currentTeamIndex;
    ArrayList<String> words;


    boolean resumeAfterOnCreateFlag = false;
    boolean scoreLimitReached = false;

    Button startButton;
    TextView lastRoundTextView;

    ForegroundService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game_home_screen);

        System.out.println("ONCREATE..");
        if (savedInstanceState == null)
            System.out.println("savedinstance was null...");

        startButton = (Button) findViewById(R.id.ingamehomescreen_startbutton);
        lastRoundTextView = (TextView) findViewById(R.id.lastround_textview);

        resumeAfterOnCreateFlag = true;
        currentTeamIndex = 0;

        Intent intent = getIntent();
        numberOfTeams = intent.getIntExtra("numberOfTeams", 2);
        wordListTableName = intent.getStringExtra("wordlist");
        skips = intent.getIntExtra("skips", 0);
        scoreLimit = intent.getIntExtra("scorelimit", 25);
        timeLimit = intent.getIntExtra("timelimit", 30);

        //GET WORDLIST FROM SQL
        SQLiteHelper sql = new SQLiteHelper(this);
        words = sql.getWordsFromTable(wordListTableName);
        Collections.shuffle(words);
        teams = createTeams(numberOfTeams, skips, false);

        //Service up
        System.out.println("Service up?");
        Intent i = new Intent(this, ForegroundService.class);
        startService(i);
        bindService(i, connection, Context.BIND_AUTO_CREATE);

        drawHomeScreen(false);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName cName, IBinder binder) {
            service = ((ForegroundService.MyBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName cName) {
            service = null;
        }
    };


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
        if(connection != null) {
            unbindService(connection);
            System.out.println("service unbound.");
            service.stopService();
        }
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
    public void onBackPressed() {
        System.out.println("Back press in ingamehomescreen");
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RESUME called..");
        Intent intent = getIntent();

        //If activity resumed from ongoing notification nothing should happend.
        //Activity is resumed the "normal way", when a team has played and the user is returned to ingamehomescreen
        String from = intent.getStringExtra("from");
        if (from == null || !from.equals("service")) {
            if (!resumeAfterOnCreateFlag) {
                System.out.println("in flag==false..");

                //Give team score..
                Team currTeam = teams.get(currentTeamIndex);
                currTeam.giveScore(intent.getIntExtra("scoreAcquired", 0));

                //If team's score is >= scorelimit
                if (currTeam.getScore() >= scoreLimit) {
                    scoreLimitReached = true;
                    lastRoundTextView.setVisibility(View.VISIBLE);
                }

                //Remove words used from current game wordlist.
                removeUsedWords(intent.getStringArrayListExtra("usedWords"));

                //Next team..
                //If a team has reached the scorelimit the application should allow the current round to finish,
                //So that each team has had the same number of chances to collect score.
                if (scoreLimitReached) {
                    currentTeamIndex += 1;
                    //If limit reached + last team has played its final round, game is over. (Unless there is a tie)
                    if (currentTeamIndex >= numberOfTeams) {
                        gameOver();
                    } else {
                        drawHomeScreen(false);
                    }
                } else {
                    currentTeamIndex = (currentTeamIndex + 1) % teams.size();
                    drawHomeScreen(false);
                }
            }
        }
    }

    private void gameOver() {
        startButton.setEnabled(false);
        drawHomeScreen(true);
    }


    private void removeUsedWords(ArrayList<String> usedWords) {
        try {
            if (!usedWords.isEmpty()) {
                words.removeAll(usedWords);
            }
        } catch (NullPointerException e) {
            System.out.println("usedWords array null..");
        }
    }

    private ArrayList<Team> createTeams(int numberOfTeams, int skips, boolean customTeamNames) {
        ArrayList<Team> teams = new ArrayList<>();
        if (!customTeamNames) {
            for (; numberOfTeams > 0; numberOfTeams--) {
                Team t = new Team("Team " + numberOfTeams, 0);
                teams.add(0, t);
            }
        } else {
            //if user wants custom teamnames..
        }

        return teams;
    }

    private void drawHomeScreen(boolean gameover) {
        TableLayout table = (TableLayout) findViewById(R.id.inGameHomeScreen_tableLayout);
        table.removeAllViews();
        for (Team t : teams) {
            TextView name = new TextView(this);
            name.setText(t.getTeamName());
            TextView score = new TextView(this);
            score.setText("" + t.getScore());
            TableRow row = new TableRow(this);
            row.addView(name);
            row.addView(score);
            table.addView(row);
        }

        if (gameover) {
            TextView nextTeamText = (TextView) findViewById(R.id.textView_teamnext);
            Team team = getWinner();
            lastRoundTextView.setVisibility(View.INVISIBLE);
            nextTeamText.setText(team.getTeamName() + " Wins!");
        } else {
            TextView nextTeamText = (TextView) findViewById(R.id.textView_teamnext);
            nextTeamText.setText(teams.get(currentTeamIndex).getTeamName() + "'s turn");
        }
    }

    private Team getWinner() {
        Team highest = teams.get(0);
        for (Team t : teams.subList(1, teams.size())) {
            if (t.getScore() > highest.getScore())
                highest = t;
        }
        return highest;
    }

    public void onStartClicked(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putStringArrayListExtra("words", words);
        intent.putExtra("skips", skips);
        startActivity(intent);
    }

}