package com.example.ludvig.medandraord;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void manageWordListButtonClicked(View view) {
        Intent intent = new Intent(this, WordListMangementActivity.class);
        startActivity(intent);
    }

    public void playButtonClicked(View view) {
        Intent intent = new Intent(this,PreGameSettingsActivity.class);
        startActivity(intent);
    }

}
