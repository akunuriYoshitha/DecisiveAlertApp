package com.example.yoshi.decisivealertapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Iterator;
import java.util.Map;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    Firebase childSetting, parent, settingsData;
    private AudioManager myAudioManager;
    GoogleApiClient mGoogleApiClient;
    MyDatabase mydb = new MyDatabase(HomeActivity.this);
    ImageButton on_button, off_button;
    TextView user_msg;
    String parameter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth = FirebaseAuth.getInstance();
        parameter = getIntent().getStringExtra("EXTRA_SESSION_ID");
        on_button = (ImageButton) findViewById(R.id.on_button);
        off_button = (ImageButton) findViewById(R.id.off_button);
        user_msg = (TextView) findViewById(R.id.user_msg);
        Firebase.setAndroidContext(this);
        if (firebaseAuth.getCurrentUser() != null)
            parent = new Firebase("https://decisivealertapp.firebaseio.com/user-" + firebaseAuth.getCurrentUser().getUid());
//        startService(new Intent(this, MyService.class));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(HomeActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if (firebaseAuth.getCurrentUser() == null) {

                        if (!(parameter.equals("yes")))
                        {
                            parameter = "no";
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }

                    }
                }

        };

        if(mydb.getSettingsData("Settings", "manual").equals("yes"))
        {
            if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
            {
                off_button.setVisibility(View.VISIBLE);
                on_button.setVisibility(View.GONE);
                user_msg.setText("Click the button to put your mobile in normal mode");
                silentModeOn();
            }
            else if (mydb.getSettingsData("Settings", "Mode").equals("Outdoor"))
            {
                off_button.setVisibility(View.VISIBLE);
                on_button.setVisibility(View.GONE);
                user_msg.setText("Click the button to put your mobile in normal mode");
                vibratetModeOn();
            }

        }
        else
        {
            mydb.truncateCallers();
            if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
            {
                on_button.setVisibility(View.VISIBLE);
                off_button.setVisibility(View.GONE);
                user_msg.setText("Click the button to put your mobile in silent mode");
                normalModeOn();
            }
            else
            {
                on_button.setVisibility(View.VISIBLE);
                off_button.setVisibility(View.GONE);
                user_msg.setText("Click the button to put your mobile in vibrate mode");
                normalModeOn();
            }

        }
        on_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.updateSettings("Settings", "manual", "yes");
                if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
                {
                    off_button.setVisibility(View.VISIBLE);
                    on_button.setVisibility(View.GONE);
                    user_msg.setText("Click the button to put your mobile in normal mode");
                    silentModeOn();
                }
                else if (mydb.getSettingsData("Settings", "Mode").equals("Outdoor"))
                {
                    off_button.setVisibility(View.VISIBLE);
                    on_button.setVisibility(View.GONE);
                    user_msg.setText("Click the button to put your mobile in normal mode");
                    vibratetModeOn();
                }
            }
        });
        off_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.updateSettings("Settings", "manual", "no");
                mydb.truncateCallers();
                if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
                {
                    on_button.setVisibility(View.VISIBLE);
                    off_button.setVisibility(View.GONE);
                    user_msg.setText("Click the button to put your mobile in silent mode");
                    normalModeOn();
                }
                else
                {
                    on_button.setVisibility(View.VISIBLE);
                    off_button.setVisibility(View.GONE);
                    user_msg.setText("Click the button to put your mobile in vibrate mode");
                    normalModeOn();
                }
            }
        });

//        Intent intent = new Intent(MainActivity.this, OutgoingCallReceiver.class);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting : Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.action_save_to_cloud :
                if (firebaseAuth.getCurrentUser() == null)
                {
                    Toast.makeText(HomeActivity.this, "Kindly login to save your settings", Toast.LENGTH_SHORT).show();
                    break;
                }


//                Firebase settingData = childSetting.child("manualmode");
                Cursor settings = mydb.getAllSettings();
                while (settings.moveToNext())
                {
                    childSetting = parent.child("Settings");
                    settingsData = childSetting.child(settings.getString(0));
                    settingsData.setValue(settings.getString(1));
                }
                Cursor customContacts = mydb.getCustomContacts();
                parent.child("Custom Contacts").removeValue();
                while (customContacts.moveToNext())
                {
                    childSetting = parent.child("Custom Contacts");
                    settingsData = childSetting.child(customContacts.getString(0));
                    settingsData.setValue(customContacts.getString(1));
                }
                Toast.makeText(HomeActivity.this, "Settings saved", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_restore_settings : if (firebaseAuth.getCurrentUser() == null)
                {
                    Toast.makeText(HomeActivity.this, "Kindly login to restore your settings", Toast.LENGTH_SHORT).show();
                    break;
                }
                mydb.truncateCustomContacts();
                parent.child("Custom Contacts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> contacts = dataSnapshot.getValue(Map.class);
                        Iterator customContactsIterator = contacts.keySet().iterator();
                        while (customContactsIterator.hasNext())
                        {
                            String key = customContactsIterator.next().toString();
                            int result = mydb.insertCustomContacts(key, contacts.get(key));
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                parent.child("Settings").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, String> settings = dataSnapshot.getValue(Map.class);
                        Iterator settingsIterator = settings.keySet().iterator();
                        while (settingsIterator.hasNext())
                        {
                            String key = settingsIterator.next().toString();
                            boolean result = mydb.updateSettings("Settings", key, settings.get(key));
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

                break;
            case R.id.action_logout : if (firebaseAuth.getCurrentUser() == null)
            {
                Toast.makeText(HomeActivity.this, "Kindly login with your google account", Toast.LENGTH_LONG).show();
                break;
            }
                else
            {
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            }
            default : return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}


