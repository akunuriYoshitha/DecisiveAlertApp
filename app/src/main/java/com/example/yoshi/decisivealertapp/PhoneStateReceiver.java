package com.example.yoshi.decisivealertapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by yoshi on 12/9/2016.
 */

public class PhoneStateReceiver extends BroadcastReceiver {
    private AudioManager audioManager;
    Switch sw1;
    int insert = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "phone state receiver active", Toast.LENGTH_SHORT).show();
        try {

            MyDatabase mydb = new MyDatabase(context);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d("pppp", incomingNumber);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                incomingNumber = incomingNumber.replace(" ", "");
                if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
                    changePhoneMode(context, mydb, incomingNumber, AudioManager.RINGER_MODE_SILENT, AudioManager.RINGER_MODE_VIBRATE);
                else
                    changePhoneMode(context, mydb, incomingNumber, AudioManager.RINGER_MODE_VIBRATE, AudioManager.RINGER_MODE_NORMAL);

            }

            if (mydb.getSettingsData("Settings", "manual").equals("yes")) {
                String phoneNumber;
                int p = 0;
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                //AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                p = 1;
                //Log.i("msg", "onReceive3");
                //Log.d(PhoneStateReceiver.class.getSimpleName(), intent.toString() + ", call to: " + phoneNumber);
                Toast.makeText(context, "Outgoing call catched: " + phoneNumber, Toast.LENGTH_LONG).show();
                //Log.i("msg", "onReceive4");

                if(p == 1) {
                    try {
                        Log.i("msg", "onReceive51");
                        Intent intent1  = new Intent(context, AlertOutCall.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Log.i("msg", "onReceive6");
                        context.startActivity(intent1);
                        Log.i("msg", "onReceive7");
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(context, "Intended to outgoingcall receiver", Toast.LENGTH_SHORT).show();

        MyDatabase mydb = new MyDatabase(context);




        Log.i("msg", "i am in onReceive");
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
//        Toast.makeText(context, "Working on audio services", Toast.LENGTH_SHORT).show();
        if (mydb.getSettingsData("Settings", "manual").equals("no")) {
            switch(audio.getRingerMode()){
                case AudioManager.RINGER_MODE_SILENT:
                    Toast.makeText(context, "phone is in silent mode", Toast.LENGTH_SHORT).show();
                    Log.i("msg", "silent");
                    try {
                        Intent intent1  = new Intent(context, Alert.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }
                    catch (Exception e){
                        Log.d("msg", "Error in generating alert");
                        e.printStackTrace();
                    }
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    Toast.makeText(context, "phone is in vibrate mode", Toast.LENGTH_SHORT).show();
                    Log.i("msg", "vibrate");
//                    Toast.makeText(context, "phone is in vibrate mode....", Toast.LENGTH_SHORT).show();

                    try {
                        Intent intent1  = new Intent(context, Alert.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    Toast.makeText(context, "phone is in Normal mode", Toast.LENGTH_SHORT).show();
                    Log.i("msg", "normal");
                    break;
            }
        }

        Log.i("msg", "i am in onReceive1");




    }

    public  void changePhoneMode (Context context, MyDatabase mydb, String incomingNumber, final int mode1, final int mode2)
    {
        if (mydb.getSettingsData("Settings", "manual").equals("yes"))
        {
//            Toast.makeText(context, "custom contacts reading....", Toast.LENGTH_SHORT).show();
            Cursor customContacts = mydb.getCustomContacts();
//            Toast.makeText(context, "custom contacts read", Toast.LENGTH_SHORT).show();
            if (mydb.getSettingsData("Settings", "Calls").equals("custom"))
            {
//                Toast.makeText(context, "In custom contacts mode", Toast.LENGTH_SHORT).show();
                while (customContacts.moveToNext())
                {
//                    Toast.makeText(context, "Incoming num : " + incomingNumber, Toast.LENGTH_SHORT).show();


//                    Log.d("gggg", incomingNumber);
//                    Log.d("gggg", customContacts.getString(0));
                    String customContact = customContacts.getString(0).replace(" ", "");
//                    Toast.makeText(context, "Custom Contact : " + customContact, Toast.LENGTH_SHORT).show();
                    if (customContact.equals(incomingNumber))
                    {
//                        Toast.makeText(context, "Both are equal", Toast.LENGTH_SHORT).show();
                        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT)
                            Toast.makeText(context, "silent audio manager", Toast.LENGTH_SHORT).show();
                        audioManager.setRingerMode(mode2);
                        audioManager.setRingerMode(mode2);
                        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
                            Toast.makeText(context, "vibrate audio manager", Toast.LENGTH_SHORT).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                audioManager.setRingerMode(mode1);
                            }
                        }, 30000);
                        return;
                    }
                }

            }

            Cursor data = mydb.getCallersData();
            while (data.moveToNext())
            {
                if (data.getString(0).equals(incomingNumber)) {
                    int count = Integer.parseInt(data.getString(1));
                    if ((Integer.parseInt(mydb.getSettingsData("Settings", "numOfCalls"))) <= count + 1)
                    {
                        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(mode2);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                audioManager.setRingerMode(mode1);
                            }
                        }, 30000);
                    }
                    Log.d("llll", "Count = "+ String.valueOf(count));
                    count += 1;
//                                Log.d("pppp", String.valueOf(count));
                    Boolean updateVal =  mydb.updateCallersData(incomingNumber, String.valueOf(count));
                    insert = 1;
                    if (updateVal == true)
                        Toast.makeText(context, String.valueOf(count), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(context, "Data updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context, "Data updation error", Toast.LENGTH_SHORT).show();
                    Log.d("llll", "Count updated to " + String.valueOf(count));

                }
            }
            if (insert == 0)
            {
                int res = mydb.insertCallers(incomingNumber, "1");
                if (res == 1)
                    Toast.makeText(context, "Data inserted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
            insert = 0;
            if ((mydb.getSettingsData("Settings", "sendSMS").equals("yes")))
            {
                sendSMS(incomingNumber, mydb.getSettingsData("Settings", "SMSText"));
            }
        }
    }

    protected void sendSMS(String contacts, String msg) {

        //for (int i = 0; i < contacts.size(); i++) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contacts, null, msg, null, null);
//            Toast.makeText(context, "Message Sent",
//                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
//            Toast.makeText(getApplicationContext(), ex.getMessage(),
//                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }
}
