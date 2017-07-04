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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class WordListMangementActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    ArrayList<String> tableNames;
    AlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list_management);

        SQLiteHelper sql = new SQLiteHelper(this);
        tableNames = sql.getTableNames();
        ListView listview = (ListView) findViewById(R.id.listview_all_wordlists);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tableNames);

        if (listview != null) {

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    Intent intent = new Intent(parent.getContext(), EditWordListActivity.class);
                    intent.putExtra("tablename", selectedItem);
                    startActivity(intent);
                }
            });

            listview.setAdapter(adapter);
            registerForContextMenu(listview);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //Disable delete functionality for the "standard" lists that comes with the app.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String selectedItem = adapter.getItem(info.position);
        menu.add(Menu.NONE, del_list, 0, "Remove");
        if (SQLiteHelper.defaultListNames.contains(selectedItem)) {
            menu.getItem(0).setEnabled(false);
        }
    }


    final static int del_list = 1;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String listSelected = adapter.getItem(info.position);
        switch (item.getItemId()) {
            case del_list:
                promptAreYouSureDelete(listSelected);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void promptAreYouSureDelete(final String listSelected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure? The list will be lost forever!");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                adapter.remove(listSelected);
                deleteList(listSelected);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void newListButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater Li = LayoutInflater.from(this);
        final EditText edittext = (EditText) Li.inflate(R.layout.new_list_input, null);
        builder.setView(edittext);
        builder.setTitle("New list");
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

    public void mergeListsButton(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater Li = LayoutInflater.from(this);
        final LinearLayout popup = (LinearLayout) Li.inflate(R.layout.merge_lists_popup, null);

        Button accept = (Button) popup.findViewById(R.id.merge_list_accept);
        Button decline = (Button) popup.findViewById(R.id.merge_list_decline);

        //Spinners get same adapter used to list the existing word-lists.
        final Spinner spinner1 = (Spinner) popup.findViewById(R.id.merge_list_spinner1);
        spinner1.setAdapter(adapter);
        final Spinner spinner2 = (Spinner) popup.findViewById(R.id.merge_list_spinner2);
        spinner2.setAdapter(adapter);

        builder.setView(popup);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Merge lists
                String list1 = (String) spinner1.getSelectedItem();
                String list2 = (String) spinner2.getSelectedItem();
                if (list1.equals(list2)) {
                    Toast.makeText(popup.getContext(), "Cannot merge list with itself!", Toast.LENGTH_SHORT).show();
                } else {
                    mergeLists(list1, list2);
                    dialog.dismiss();
                }
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Canceled
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
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

    private void mergeLists(String list1, String list2) {
        SQLiteHelper sql = new SQLiteHelper(this);
        sql.mergeTables(list1, list2);
        adapter.add(list1 + "" + list2);
    }
}
