package com.example.ludvig.medandraord;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

        LayoutInflater Li = LayoutInflater.from(this);
        final EditText edittext = (EditText) Li.inflate(R.layout.new_list_input, null);
        builder.setView(edittext);
        builder.setTitle("Enter a name for new list");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = edittext.getText().toString();
                if (name.replaceAll(" ", "").equals("") || name.equals("")) {
                    Toast.makeText(WordListMangementActivity.this, "Name cannot be empty or contain only spaces", Toast.LENGTH_SHORT).show();
                } else {
                    createNewList(name);
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


    private void createNewList(String name) {
        SQLiteHelper sql = new SQLiteHelper(this);
        boolean worked = sql.createTable(name, sql.getWritableDatabase());
        if (worked)
            adapter.add(name);
    }

    private void deleteList(String name) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.deleteTable(name);

    }
}
