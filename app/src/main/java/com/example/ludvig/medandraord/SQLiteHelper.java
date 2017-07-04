package com.example.ludvig.medandraord;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SQLiteHelper extends SQLiteOpenHelper {

    //Some code from https://developer.android.com/guide/topics/data/data-storage.html#db

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Wordlists";

    //Default wordlists that comes with the app
    static final List<String> defaultListNames = Arrays.asList("Lätt", "Medel", "Svår", "English");
    private String[] easywords = {"Jultomten", "Hus", "Föräldrar", "Skola", "Jobb", "Mat", "Ko", "Ägg", "Snygg", "Cykla", "Strumpor"};
    private String[] mediumwords = {"Solglasögon", "Landskap", "Marijuana", "Dagdröm", "Arbetsplats", "Nåd", "Råd", "Attraktiv", "Galen", "Ingenting", "Nätverk"};
    private String[] hardwords = {"Nepotism", "Dedikerad", "Skonsam", "Förklara", "Extremt", "Omen", "Konstform", "Aktuellt", "Alibi"};
    private String[] englishwords = {"Random", "Human", "Gas", "Politics", "Genocide", "Arbitrary", "Parents", "Handsome", "Star", "Moonlight", "Alibi"};

    SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("IN SQLITEHELPER THING");
    }


    //Database is created with the default lists inserted
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("IN SQLITEHELPER ONCREATE");
        createTable("Lätt", db);
        createTable("Medel", db);
        createTable("Svår", db);
        createTable("English", db);
        insertBulk("Lätt", easywords, db);
        insertBulk("Medel", mediumwords, db);
        insertBulk("Svår", hardwords, db);
        insertBulk("English", englishwords, db);
    }

    boolean createTable(String tablename, SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + tablename + " (" +
                "WORD TEXT PRIMARY KEY);";
        try {
            db.execSQL(query);
            System.out.println("CRETED TABLE " + tablename + "(UNLESS IT EXISTED)");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to create table: " + tablename);
            return false;
        }
    }

    void deleteTable(String tablename) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tablename + ";");
        db.close();
    }

    void insertWord(String table, String word) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT OR IGNORE INTO " + table + "(WORD) VALUES (?);";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, word);
        statement.executeInsert();
    }

    void updateWord(String table, String oldWord, String newWord) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + table + " SET WORD=? WHERE WORD=?;";
        SQLiteStatement statement = db.compileStatement(query);
        statement.bindString(1, newWord);
        statement.bindString(2, oldWord);
        int rowsAff = statement.executeUpdateDelete();
        System.out.println("Update affected " + rowsAff + " rows.");
    }

    private void insertBulk(String table, String[] words, SQLiteDatabase db) {
        for (String w : words) {
            db.execSQL("INSERT OR IGNORE INTO " + table + "(WORD) VALUES('" + w + "');");
        }
    }

    //Query all words from a table and return as arraylist to be used in game.
    ArrayList<String> getWordsFromTable(String table) {
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

    //Query the tablenames, to show in various places in the app. (Since tablename = Name of wordlist)
    ArrayList<String> getTableNames() {
        ArrayList<String> tableArr = new ArrayList<>();
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

    void removeWordFromTable(String table, String word) {
        String query = "DELETE FROM " + table + " WHERE word='" + word + "';";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        System.out.println(word + " deleted from " + table);
    }

    void mergeTables(String table1, String table2) {
        SQLiteDatabase db = getWritableDatabase();
        String newname = table1 + "" + table2;
        //Create new destination table
        createTable(newname, db);
        //Join lists, insert into new.
        String query = "INSERT INTO " + newname + " SELECT * FROM " + table1 + " UNION SELECT * FROM " + table2 + ";";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
