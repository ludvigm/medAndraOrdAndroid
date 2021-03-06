package com.example.ludvig.medandraord;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class InGameHomeScreen extends AppCompatActivity {

    private final int reqCode = 1;
    AlertDialog dialog = null;

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
        teams = createTeams(numberOfTeams);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connection != null) {
            unbindService(connection);
            System.out.println("service unbound.");
            Intent i = new Intent(this, ForegroundService.class);
            stopService(i);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure? Going back will cause the current game to be over!");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exitGame();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            //back from share
            exitGame();
        } else {
            System.out.println("in else reqcode != 1");
        }
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

                //Update notification?
                service.updateNotification(getLeaderString());

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

    private String getLeaderString() {
        Team leader = getLeaderTeam();
        return leader.getTeamName() + " is currently in the lead with " + leader.getScore() + " points!";
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

    private ArrayList<Team> createTeams(int numberOfTeams) {
        ArrayList<Team> teams = new ArrayList<>();
        for (; numberOfTeams > 0; numberOfTeams--) {
            Team t = new Team("Lag " + numberOfTeams, 0);
            teams.add(0, t);
        }
        return teams;
    }

    private void drawHomeScreen(boolean gameover) {
        TableLayout table = (TableLayout) findViewById(R.id.inGameHomeScreen_tableLayout);
        LayoutInflater Li = LayoutInflater.from(this);

        if (table != null) {
            table.removeAllViews();
            for (Team t : teams) {
                TextView name = (TextView) Li.inflate(R.layout.team_text_view, null);
                name.setText(t.getTeamName());
                TextView score = (TextView) Li.inflate(R.layout.team_text_view, null);
                score.setText(String.format(Locale.ENGLISH, "%d", t.getScore()));
                TableRow row = new TableRow(this);
                row.addView(name);
                row.addView(score);
                table.addView(row);
            }
        }

        if (gameover) {

            //set winnertext in background.
            TextView nextTeamText = (TextView) findViewById(R.id.textView_teamnext);
            Team team = getLeaderTeam();
            lastRoundTextView.setVisibility(View.INVISIBLE);
            String winnertext = team.getTeamName() + " Wins!";
            if (nextTeamText != null) {
                nextTeamText.setText(winnertext);
            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Inflate popup
            final RelativeLayout popup = (RelativeLayout) Li.inflate(R.layout.game_over_popup, null);
            Button home = (Button) popup.findViewById(R.id.winpopup_homebtn);
            Button share = (Button) popup.findViewById(R.id.winpopup_sharebtn);

            TextView title = (TextView) popup.findViewById(R.id.popuptitletext);
            title.setText(winnertext);

            builder.setView(popup);

            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go back to home screen, discaring all game data etc.
                    exitGame();
                    dialog.dismiss();
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Share on media etc
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, "I'm being sent!!");
                    share.putExtra("requestCode", reqCode);
                    startActivityForResult(Intent.createChooser(share, "Share Text"), reqCode);

                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.show();
        } else {
            TextView nextTeamText = (TextView) findViewById(R.id.textView_teamnext);
            if (nextTeamText != null) {
                nextTeamText.setText(String.format(Locale.ENGLISH, "%s's turn", teams.get(currentTeamIndex).getTeamName()));
            }
        }
    }

    private Team getLeaderTeam() {
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

    private void exitGame() {
        Intent mainActivity = new Intent(InGameHomeScreen.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

}