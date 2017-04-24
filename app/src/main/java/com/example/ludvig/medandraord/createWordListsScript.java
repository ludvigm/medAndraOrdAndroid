package com.example.ludvig.medandraord;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ludde on 2017-04-20.
 */
public class createWordListsScript extends Activity{

    String[] easywords = {"Jultomten", "Hus", "Föräldrar", "Skola", "Jobb", "Mat", "Ko", "Ägg", "Snygg", "Cykla", "Strumpor"};
    String[] mediumwords = {"Solglasögon","Landskap","Marijuana", "Dagdröm","Arbetsplats"};
    String[] hardwords = {"Nepotism", "Dedikerad", "Skonsam", "Förklara", "Extremt", "Omen"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
