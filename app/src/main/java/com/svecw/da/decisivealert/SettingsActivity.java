package com.svecw.da.decisivealert;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

/**
 * Created by yoshi on 12/1/2016.
 */

public class SettingsActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{
    HomeActivity homeActivity = new HomeActivity();
    FirebaseAuth firebaseAuth;
    Firebase parent;
    Firebase childSettings;

    AudioManager myAudioManager;
    MyDatabase mydb = new MyDatabase(SettingsActivity.this);
    TextView numCalls1, numCalls2;
    TextView calls1, calls2, calls3;
    Spinner mode, sms_option;
//    Switch sw1;
    Button smsTextView;
    ImageButton smsTextEdit;
//    EditText smsText;
    ArrayAdapter<CharSequence> mode_adapter;
    ArrayAdapter<CharSequence> smsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        calls1 = (TextView) findViewById(R.id.calls1);
        calls2 = (TextView) findViewById(R.id.calls2);
        calls3 = (TextView) findViewById(R.id.calls3);

        numCalls1 = (TextView) findViewById(R.id.numCalls1);
        numCalls2 = (TextView) findViewById(R.id.numCalls2);
        sms_option = (Spinner) findViewById(R.id.sms_switch);
        smsTextView = (Button) findViewById(R.id.smsTextButton);
        smsTextEdit = (ImageButton) findViewById(R.id.smsEdit);
        firebaseAuth = FirebaseAuth.getInstance();




        numCalls2.setText(mydb.getSettingsData("Settings", "numOfCalls") + " > ");
        smsTextView.setText(mydb.getSettingsData("Settings", "SMSText"));
        calls2.setText(mydb.getSettingsData("Settings", "Calls") + " > ");
        mode = (Spinner) findViewById(R.id.mode);
        sms_option = (Spinner) findViewById(R.id.sms_switch);
        smsAdapter = ArrayAdapter.createFromResource(this, R.array.sms_options, android.R.layout.simple_spinner_item);
        smsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sms_option.setAdapter(smsAdapter);
        String sendSMS = mydb.getSettingsData("Settings", "sendSMS");
        sms_option.setSelection(smsAdapter.getPosition(sendSMS));
        sms_option.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mydb.updateSettings("Settings", "sendSMS", parent.getItemAtPosition(position).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        mode_adapter = ArrayAdapter.createFromResource(this, R.array.mode, android.R.layout.simple_spinner_item);
        mode_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mode.setAdapter(mode_adapter);
        String mode_value = mydb.getSettingsData("Settings", "Mode");
        mode.setSelection(mode_adapter.getPosition(mode_value));

        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mydb.updateSettings("Settings", "Mode", parent.getItemAtPosition(position).toString());

                if (mydb.getSettingsData("Settings", "manual").equals("yes"))
                {
                    if (position == 0)
                    {
                        silentModeOn();
                    }

                    else
                    {
                        vibratetModeOn();
                    }


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        calls1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SetContacts.class);
                startActivity(intent);
            }
        });
        calls2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SetContacts.class);
                startActivity(intent);
            }
        });
        calls3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SetContacts.class);
                startActivity(intent);
            }
        });
        numCalls2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        numCalls1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });

        smsTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, EditSMS.class);
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
            case R.id.action_home :
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", "no");
                startActivity(intent);
                break;
            default : return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void show()
    {

        final Dialog d = new Dialog(SettingsActivity.this);
        d.setTitle("Number of Calls");

        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.set_number_picker);
        Button b2 = (Button) d.findViewById(R.id.cancel_number_picker);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.num_calls_picker);
        np.setMaxValue(15);
        np.setMinValue(2);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.updateSettings("Settings", "numOfCalls",String.valueOf(np.getValue()));
                numCalls2.setText(String.valueOf(np.getValue()) + " > ");
                d.dismiss();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        d.show();


    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public void silentModeOn()
    {
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.setRingerMode(RINGER_MODE_SILENT);

    }



    public void vibratetModeOn()
    {
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.setRingerMode(RINGER_MODE_VIBRATE);
    }

    public void normalModeOn()
    {
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.setRingerMode(RINGER_MODE_NORMAL);
        myAudioManager.setStreamVolume(AudioManager.STREAM_RING,myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING),0);
    }
}
