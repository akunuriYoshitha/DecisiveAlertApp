package com.svecw.da.decisivealert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by yoshi on 12/1/2016.
 */

public class SetContacts extends AppCompatActivity {
    MyDatabase mydb;
    TextView nobody;
    TextView customContacts;
    TextView editList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_contacts);
        nobody = (TextView) findViewById(R.id.nobody);
        customContacts = (TextView) findViewById(R.id.customContacts);
        editList = (TextView) findViewById(R.id.editList);
        mydb = new MyDatabase(SetContacts.this);
        nobody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.updateSettings("Settings", "Calls", "nobody");
                Intent intent = new Intent(SetContacts.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        customContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.updateSettings("Settings", "Calls", "custom");
                editList.setVisibility(View.VISIBLE);


            }
        });
        editList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetContacts.this, CustomContacts.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home : Intent intent = new Intent(SetContacts.this, MainActivity.class);
                startActivity(intent);
                break;
            default : return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
