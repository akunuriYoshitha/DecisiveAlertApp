package com.svecw.da.decisivealert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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
        try {
            MyDatabase mydb = new MyDatabase(context);
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                incomingNumber = incomingNumber.replace(" ", "");
                if (mydb.getSettingsData("Settings", "Mode").equals("Meeting"))
                    changePhoneMode(context, mydb, incomingNumber, AudioManager.RINGER_MODE_SILENT, AudioManager.RINGER_MODE_VIBRATE);
                else
                    changePhoneMode(context, mydb, incomingNumber, AudioManager.RINGER_MODE_VIBRATE, AudioManager.RINGER_MODE_NORMAL);
                return;
            }

            if (mydb.getSettingsData("Settings", "manual").equals("yes")) {
                String phoneNumber;
                int p = 0;
                phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                p = 1;
                if(p == 1) {
                    try {
                        Intent intent1  = new Intent(context, AlertOutCall.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        MyDatabase mydb = new MyDatabase(context);
        AudioManager audio = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if (mydb.getSettingsData("Settings", "manual").equals("no")) {
            switch(audio.getRingerMode()){
                case AudioManager.RINGER_MODE_SILENT:
                    try {
                        Intent intent1  = new Intent(context, Alert.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
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
                    break;
            }
            return;
        }
    }

    public  void changePhoneMode (Context context, MyDatabase mydb, String incomingNumber, final int mode1, final int mode2)
    {
        if (mydb.getSettingsData("Settings", "manual").equals("yes"))
        {
            Cursor customContacts = mydb.getCustomContacts();
            if (mydb.getSettingsData("Settings", "Calls").equals("custom"))
            {
                while (customContacts.moveToNext())
                {
                    String customContact = customContacts.getString(0).replace(" ", "");
                    if (customContact.equals(incomingNumber))
                    {
                        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setRingerMode(mode2);
                        audioManager.setRingerMode(mode2);
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
                        audioManager.setRingerMode(mode2);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                audioManager.setRingerMode(mode1);
                            }
                        }, 30000);
                    }
                    count += 1;
                    Boolean updateVal =  mydb.updateCallersData(incomingNumber, String.valueOf(count));
                    insert = 1;
                }
            }
            if (insert == 0)
            {
                int res = mydb.insertCallers(incomingNumber, "1");
            }
            insert = 0;
            if ((mydb.getSettingsData("Settings", "sendSMS").equals("All")))
            {
                sendSMS(context, incomingNumber, mydb.getSettingsData("Settings", "SMSText"));
            }
            else if ((mydb.getSettingsData("Settings", "sendSMS").equals("Favourites")))
            {
                Cursor favContacts = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "starred=?",
                        new String[]{"1"}, null);
                while (favContacts.moveToNext())
                {
                    if (incomingNumber.replace(" ", "").equals(favContacts.getString(favContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace(" ", "")))
                    {
                        sendSMS(context, incomingNumber, mydb.getSettingsData("Settings", "SMSText"));
                        return;
                    }
                }
            }
        }
    }

    protected void sendSMS(Context context, String contacts, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contacts, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
