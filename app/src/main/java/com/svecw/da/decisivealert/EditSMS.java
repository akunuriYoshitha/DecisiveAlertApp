package com.svecw.da.decisivealert;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditSMS extends AppCompatActivity {

    MyDatabase mydb = new MyDatabase(this);
    EditText newMsg;
    ImageButton saveSMS;
    ListView existingMsgs;
    ArrayList<String> msgList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sms);

        newMsg = (EditText) findViewById(R.id.newMsg);
        saveSMS = (ImageButton) findViewById(R.id.saveSMS);
        existingMsgs = (ListView) findViewById(R.id.existing_msgs_view);
        saveSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newMsg.getText().length() == 0)
                {
                    Toast.makeText(EditSMS.this, "Please enter your message properly...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (mydb.insertSMS(newMsg.getText().toString()) == 1) {
                        msgList.add(newMsg.getText().toString());
                        adapter = new ArrayAdapter<String>(EditSMS.this, android.R.layout.simple_list_item_1, msgList);
                        existingMsgs.setAdapter(adapter);
                        mydb.updateSettings("Settings", "SMSText", newMsg.getText().toString());
                        Intent intent = new Intent(EditSMS.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }


            }
        });
        registerForContextMenu(existingMsgs);
        Cursor result = mydb.getSMS();
        while (result.moveToNext())
        {
            msgList.add(result.getString(0));
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);
        existingMsgs.setAdapter(adapter);

        existingMsgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mydb.updateSettings("Settings", "SMSText", existingMsgs.getItemAtPosition(position).toString());
                Intent intent = new Intent(EditSMS.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_contacts_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.delete_option)
        {
            if (mydb.getSettingsData("Settings", "SMSText").equals(existingMsgs.getItemAtPosition(info.position).toString()))
            {
                Toast.makeText(EditSMS.this, "Current message cannot be deleted...", Toast.LENGTH_SHORT).show();
                return true;
            }
            mydb.deleteSMS(existingMsgs.getItemAtPosition(info.position).toString());
            msgList.remove(info.position);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
