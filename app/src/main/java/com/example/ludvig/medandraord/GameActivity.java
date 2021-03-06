package com.example.ludvig.medandraord;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private ArrayList<String> words;
    private TextView wordDisplay;
    private TextView timerDisplay;
    private int skips;

    private int scoreAcquired;
    private ArrayList<String> usedWords;

    private Button passButton;
    private Button nextButton;
    private CountDownTimer timer;

    private String nextWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();
        words = i.getStringArrayListExtra("words");
        skips = i.getIntExtra("skips", 0);

        nextButton = (Button) findViewById(R.id.nextButton);
        if (skips > 0) {
            passButton = (Button) findViewById(R.id.passButton);
            passButton.setEnabled(true);
        }

        wordDisplay = (TextView) findViewById(R.id.wordDisplay);
        timerDisplay = (TextView) findViewById(R.id.timerDisplay);
        usedWords = new ArrayList<>();
        scoreAcquired = 0;

        start();
    }

    private void start() {

        displayNextWord();

        timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerDisplay.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                goBackToHomeScreen();
            }
        }.start();
    }

    public void nextButtonClicked(View view) {
        scoreAcquired++;
        usedWords.add(nextWord);
        displayNextWord();
    }

    public void passButtonClicked(View view) {
        skips--;
        if (skips == 0)
            passButton.setEnabled(false);
        displayNextWord();
    }

    private void displayNextWord() {
        if (!words.isEmpty()) {
            nextWord = popWord();
            wordDisplay.setText(nextWord);
            usedWords.add(nextWord);
        } else {
            try {
                nextButton.setEnabled(false);
                passButton.setEnabled(false);
            } catch (NullPointerException e) {
                System.out.println("button null..");
            }
            wordDisplay.setText(R.string.outofwords);
        }
    }

    private String popWord() {
        String word = words.get(0);
        words.remove(0);
        return word;
    }

    private void goBackToHomeScreen() {
        Intent i = new Intent(GameActivity.this, InGameHomeScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putStringArrayListExtra("usedWords", usedWords);
        i.putExtra("scoreAcquired", scoreAcquired);
        scoreAcquired = 0;
        startActivity(i);
    }

    //Prevent a lot of wierd interactions with using back button while timer running...
    @Override
    public void onBackPressed(){
        if(timer != null) {
            timer.cancel();
        }
        goBackToHomeScreen();
    }
}
