package com.example.yoshi.decisivealertapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by CH.PUSHPA SAI on 11-02-2017.
 */
public class Alert extends Activity {
    MyDatabase mydb = new MyDatabase(this);
    HomeActivity obj = new HomeActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder a_builder = new AlertDialog.Builder(Alert.this);
        a_builder.setMessage("Do you want to ON the Decisive Alert!!!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            mydb.updateSettings("Settings", "manual", "yes");
                            Toast.makeText(Alert.this, "Manual mode turned on", Toast.LENGTH_SHORT).show();
                            obj.silentModeOn();
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
                        //Log.i("msgA", "onCreate5");
                    }
                }) ;
        AlertDialog alert = a_builder.create();
        alert.setTitle("Alert !!!");
        alert.show();
    }
}
