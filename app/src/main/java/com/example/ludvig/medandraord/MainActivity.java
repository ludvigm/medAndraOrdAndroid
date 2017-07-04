package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
