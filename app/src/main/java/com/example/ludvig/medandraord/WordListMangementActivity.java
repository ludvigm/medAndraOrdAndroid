package com.example.ludvig.medandraord;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Ludde on 2017-04-20.
 */
public class WordListMangementActivity extends AppCompatActivity {


    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_management);

        SQLiteHelper sql = new SQLiteHelper(this);
        ArrayList<String> tablenames = sql.getTableNames();
        ListView listview = (ListView) findViewById(R.id.listview_all_wordlists);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tablenames);

        if (listview != null) {
            listview.setAdapter(adapter);
            registerForContextMenu(listview);
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(parent.getContext(), EditWordListActivity.class);
                intent.putExtra("tablename", selectedItem);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, del_list, 0, "Remove");
    }

    final static int del_list = 1;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String listSelected = (String) adapter.getItem(info.position);
        switch (item.getItemId()) {
            case del_list:
                adapter.remove(listSelected);
                deleteList(listSelected);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void newListButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a name for new list");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                adapter.add(name);
                createNewList(name);
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


    private void createNewList(String name) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.createTable(name, sql.getWritableDatabase());
    }

    private void deleteList(String name) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.deleteTable(name);

    }
}
