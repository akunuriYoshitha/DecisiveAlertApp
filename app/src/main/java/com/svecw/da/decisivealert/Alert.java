package com.svecw.da.decisivealert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by CH.PUSHPA SAI on 11-02-2017.
 */
public class Alert extends Activity {
    MyDatabase mydb = new MyDatabase(this);
    AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder a_builder = new AlertDialog.Builder(Alert.this);
        a_builder.setMessage("Do you want to ON Decisive Alert!!!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            mydb.updateSettings("Settings", "manual", "yes");
                            Toast.makeText(Alert.this, "Decisive Alert turned on", Toast.LENGTH_SHORT).show();
                            if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
                            {
                                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            }
                            else
                            {
                                audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }) ;
        AlertDialog alert = a_builder.create();
        alert.setTitle("Alert !!!");
        alert.show();
    }
}
