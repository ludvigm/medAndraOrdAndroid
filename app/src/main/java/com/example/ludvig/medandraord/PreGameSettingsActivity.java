package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class PreGameSettingsActivity extends AppCompatActivity {


    private SQLiteHelper sql;
    private Spinner diffSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game_settings);
        //Get table names from DB
        if(sql == null) {
            sql = new SQLiteHelper(this);
        }
        ArrayList<String> tableNames = sql.getTableNames();
        System.out.println(tableNames);
        diffSpinner = (Spinner) findViewById(R.id.difficulty_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,tableNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(diffSpinner != null) {
            diffSpinner.setAdapter(adapter);
        }
    }

    public void onStartClicked(View view) {

        try {
            Spinner teamsSpinner = (Spinner) findViewById(R.id.teams_spinner);
            int teams = Integer.valueOf( (String) teamsSpinner.getSelectedItem());

            String wordlist = diffSpinner.getSelectedItem().toString();

            Spinner skipsSpinner = (Spinner) findViewById(R.id.skips_spinner);
            int skips = Integer.valueOf( (String) skipsSpinner.getSelectedItem());

            Spinner scorelimitSpinner = (Spinner) findViewById(R.id.scorelimit_spinner);
            int scorelimit = Integer.valueOf( (String) scorelimitSpinner.getSelectedItem());

            Spinner timelimitSpinner = (Spinner) findViewById(R.id.timelimit_spinner);
            int timelimit = Integer.valueOf( (String) timelimitSpinner.getSelectedItem());

            Intent intent = new Intent(this,InGameHomeScreen.class);
            intent.putExtra("numberOfTeams",teams);
            intent.putExtra("wordlist", wordlist);
            intent.putExtra("skips",skips);
            intent.putExtra("scorelimit",scorelimit);
            intent.putExtra("timelimit", timelimit);
            startActivity(intent);

        }catch(NullPointerException e) {
            System.out.println("PreGameSettings onStartClicked nullpointer..");
        }

    }


}
