package com.example.yoshi.decisivealertapp;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CustomContacts extends Activity{
    ArrayAdapter<String> adapter;
    private static final int RESULT_PICK_CONTACT = 85500;
    ArrayList<String> list = new ArrayList<String>();
    ListView contactsList;
    private TextView textView1;
    private TextView textView2;
    MyDatabase mydb = new MyDatabase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_contacts);
//        textView1 = (TextView) findViewById(R.id.textView1);
//        textView2 = (TextView) findViewById(R.id.textView2);
        contactsList = (ListView) findViewById(R.id.contacts);
        registerForContextMenu(contactsList);
        Cursor contacts = mydb.getCustomContacts();
        Log.d("mmmm", "count = " + String.valueOf(contacts.getCount()));
        while (contacts.moveToNext())
        {
            Log.d("mmmm", "in while");
            list.add(contacts.getString(1));
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        contactsList.setAdapter(adapter);

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
        Log.i("oooo", ((TextView) info.targetView).getText().toString());
//        Log.d("oooo", String.valueOf(info.position));

        if (item.getItemId() == R.id.delete_option)
        {
//            Toast.makeText(this, info.position, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, contactsList.getItemAtPosition(info.position).toString(), Toast.LENGTH_SHORT).show();
            if (mydb.deleteCustomContacts(contactsList.getItemAtPosition(info.position).toString()) > 0)
                Toast.makeText(this, "deleted in database", Toast.LENGTH_SHORT).show();
            list.remove(info.position);
            adapter.notifyDataSetChanged();
//            Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();


            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    Log.e("mmmm", " pick contact success ");
                    break;
            }
        } else {
            Log.e("mmmm", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            Log.d("llll", phoneNo);
            name = cursor.getString(nameIndex);
            mydb.insertCustomContacts(phoneNo, name);
            list.add(name);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
            contactsList.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}