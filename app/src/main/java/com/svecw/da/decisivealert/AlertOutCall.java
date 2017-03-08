package com.svecw.da.decisivealert;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by CH.PUSHPA SAI on 26-01-2017.
 */
public class AlertOutCall extends Activity{
    MyDatabase mydb = new MyDatabase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder a_builder = new AlertDialog.Builder(AlertOutCall.this);
        a_builder.setMessage("Do you want to OFF the Decisive Alert!!!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            mydb.updateSettings("Settings", "manual", "no");
                            Toast.makeText(AlertOutCall.this, "Decisive Alert turned off", Toast.LENGTH_SHORT).show();
                            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
                        return;
                    }
                }) ;
        AlertDialog alert = a_builder.create();
        alert.setTitle("Alert !!!");
        alert.show();

    }
}
