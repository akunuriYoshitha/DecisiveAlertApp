package com.example.yoshi.decisivealertapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by CH.PUSHPA SAI on 26-01-2017.
 */
public class AlertOutCall extends Activity{
    MyDatabase mydb = new MyDatabase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.i("msgA", "onCreate1");
        super.onCreate(savedInstanceState);
        Log.i("msgA", "onCreate2");
        Toast.makeText(AlertOutCall.this,"toast msg", Toast.LENGTH_LONG).show();
        AlertDialog.Builder a_builder = new AlertDialog.Builder(AlertOutCall.this);
        a_builder.setMessage("Do you want to OFF the Decisive Alert!!!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(AlertOutCall.this, " clicked yes button", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        try {
                            mydb.updateSettings("Settings", "manual", "no");
                            Toast.makeText(AlertOutCall.this, "Manual mode turned off", Toast.LENGTH_SHORT).show();
//                            obj.silentModeOn();
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
                        Toast.makeText(AlertOutCall.this, " clicked no button", Toast.LENGTH_SHORT).show();
                        finish();
                        //Log.i("msgA", "onCreate5");
                    }
                }) ;
        AlertDialog alert = a_builder.create();
        alert.setTitle("Alert !!!");
        alert.show();
        Log.i("msgA", "onCreate3");
    }
}
