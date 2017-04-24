package com.example.ludvig.medandraord;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Ludde on 2017-04-20.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    //Some code from https://developer.android.com/guide/topics/data/data-storage.html#db

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Wordlists";

    //Default wordlists
    String[] easywords = {"Jultomten", "Hus", "Föräldrar", "Skola", "Jobb", "Mat", "Ko", "Ägg", "Snygg", "Cykla", "Strumpor"};
    String[] mediumwords = {"Solglasögon", "Landskap", "Marijuana", "Dagdröm", "Arbetsplats"};
    String[] hardwords = {"Nepotism", "Dedikerad", "Skonsam", "Förklara", "Extremt", "Omen"};

    SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("IN SQLITEHELPER THING");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("IN SQLITEHELPER ONCREATE");
        createTable("Easy", db);
        createTable("Medium", db);
        createTable("Hard", db);
        insertBulk("Easy", easywords, db);
        insertBulk("Medium", mediumwords, db);
        insertBulk("Hard", hardwords, db);
    }

    public void createTable(String tablename, SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + tablename + " (" +
                "WORD TEXT PRIMARY KEY);";
        db.execSQL(query);
        System.out.println("CRETED TABLE " + tablename + "(UNLESS IT EXISTED)");
    }

    public void deleteTable(String tablename) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tablename + ";");
        db.close();
    }

    public void insertWord(String table, String word) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT OR IGNORE INTO " + table + "(WORD) VALUES ('" + word + "');";
        db.execSQL(query);
    }

    public void updateWord(String table, String oldWord, String newWord) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + table + " SET WORD = '" + newWord + "' WHERE WORD = '" + oldWord + "';";
        db.execSQL(query);
    }

    public void insertBulk(String table, String[] words, SQLiteDatabase db) {
        for (String w : words) {
            db.execSQL("INSERT OR IGNORE INTO " + table + "(WORD) VALUES('" + w + "');");
        }
    }

    public ArrayList<String> getWordsFromTable(String table) {
        ArrayList<String> words = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + table;
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String word = c.getString(c.getColumnIndex("WORD"));
                words.add(word);
                c.moveToNext();
            }
        }
        c.close();
        return words;
    }

    public ArrayList<String> getTableNames() {
        ArrayList<String> tableArr = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String s = c.getString(c.getColumnIndex("name"));
                if (!s.equals("android_metadata"))
                    tableArr.add(s);
                c.moveToNext();
            }
        }
        c.close();
        return tableArr;
    }

    public void removeWordFromTable(String table, String word) {
        String query = "DELETE FROM " + table + " WHERE word='" + word+"';";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        System.out.println(word + " deleted from " + table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
