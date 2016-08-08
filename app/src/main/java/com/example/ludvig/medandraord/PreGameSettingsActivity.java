package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class PreGameSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_game_settings);

    }


    public void onStartClicked(View view) {

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);
        int index = rg.indexOfChild(findViewById(rg.getCheckedRadioButtonId()));
        int numberOfPlayers = index+2;

        Spinner diffSpinner = (Spinner) findViewById(R.id.difficulty_spinner);
        int diff = diffSpinner.getSelectedItemPosition();

        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra("numberOfPlayers",numberOfPlayers);
        intent.putExtra("difficulty", diff);
        startActivity(intent);
    }
}
