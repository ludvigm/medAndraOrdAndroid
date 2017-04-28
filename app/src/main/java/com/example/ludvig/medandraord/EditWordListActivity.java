package com.example.ludvig.medandraord;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ludde on 2017-04-20.
 */
public class EditWordListActivity extends AppCompatActivity {

    ArrayAdapter adapter;
    String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word_list_layout);
        SQLiteHelper sql = new SQLiteHelper(this);
        Intent intent = getIntent();
        tableName = intent.getStringExtra("tablename");
        ArrayList<String> words = sql.getWordsFromTable(tableName);

        ListView listview = (ListView) findViewById(R.id.listview_single_table_wordlist);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, words);
        if (listview != null) {
            listview.setAdapter(adapter);
            registerForContextMenu(listview);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, del_word, 0, "Remove");
        menu.add(Menu.NONE, edit_word, 1, "Edit");
    }

    final static int del_word = 1;
    final static int edit_word = 2;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final String wordSelected = (String) adapter.getItem(info.position);
        switch (item.getItemId()) {
            case del_word:
                adapter.remove(wordSelected);
                removeWordFromDB(wordSelected);
                return true;
            case edit_word:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                LayoutInflater Li = LayoutInflater.from(this);
                final EditText edittext = (EditText) Li.inflate(R.layout.new_word_input, null);
                builder.setView(edittext);
                builder.setTitle("Edit word");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String wordToEdit = edittext.getText().toString();
                        if (wordToEdit.replaceAll(" ", "").equals("") || wordToEdit.equals("")) {
                            Toast.makeText(EditWordListActivity.this, "Name cannot be empty or contain only spaces", Toast.LENGTH_SHORT).show();
                        } else {
                            wordToEdit = fixCase(wordToEdit);
                            int insertindex = adapter.getPosition(wordSelected);
                            adapter.remove(wordSelected);
                            adapter.insert(wordToEdit, insertindex);
                            updateWordInDB(wordSelected, wordToEdit);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void addWord(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater Li = LayoutInflater.from(this);
        final EditText edittext = (EditText) Li.inflate(R.layout.new_word_input, null);
        builder.setView(edittext);
        builder.setTitle("Enter new word");
// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String wordToAdd = edittext.getText().toString();
                if (wordToAdd.replaceAll(" ", "").equals("") || wordToAdd.equals("")) {
                    Toast.makeText(EditWordListActivity.this, "Name cannot be empty or contain only spaces", Toast.LENGTH_SHORT).show();
                } else {
                    wordToAdd = fixCase(wordToAdd);
                    adapter.add(wordToAdd);
                    addWordToDB(wordToAdd);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void removeWordFromDB(String word) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.removeWordFromTable(tableName, word);
    }

    private void addWordToDB(String word) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.insertWord(tableName, word);
    }

    private void updateWordInDB(String oldWord, String newWord) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.updateWord(tableName, oldWord, newWord);
    }

    private String fixCase(String word) {
        String capitalfirst = word.substring(0, 1).toUpperCase();
        String rest = word.substring(1, word.length()).toLowerCase();
        String res = capitalfirst.concat(rest);
        System.out.println(word + " was fixed to " + res);
        return res;
    }

}
