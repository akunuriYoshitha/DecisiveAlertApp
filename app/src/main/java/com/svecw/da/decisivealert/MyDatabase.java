package com.svecw.da.decisivealert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by yoshi on 12/4/2016.
 */

public class MyDatabase extends SQLiteOpenHelper{
    SQLiteDatabase db;

    public MyDatabase(Context context) {
        super(context, "MyDatabase", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table CustomContacts(Number text primary key, name text);");
        db.execSQL("create table Callers(MobileNum text primary key, count text);");

        db.execSQL("create table Settings(setting_name text primary key, selected_value text);");

        db.execSQL("INSERT INTO Settings VALUES ('manual', '')");
        db.execSQL("INSERT INTO Settings VALUES ('numOfCalls', '3')");
        db.execSQL("INSERT INTO Settings VALUES ('sendSMS', 'None')");
        db.execSQL("INSERT INTO Settings VALUES ('SMSText', 'Busy!!! Please call later...')");
        db.execSQL("INSERT INTO Settings VALUES ('Calls', 'nobody')");
        db.execSQL("INSERT INTO Settings VALUES ('Mode', 'Outdoor')");

        db.execSQL("create table SMS(sms_text primary key);");

        db.execSQL("INSERT INTO SMS values ('Busy!!! Please call later...')");
        db.execSQL("INSERT INTO SMS values ('Sorry... Unable to get you now')");
        db.execSQL("INSERT INTO SMS values ('In Meeting')");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists CustomContacts");
        db.execSQL("drop table if exists Settings");
        db.execSQL("drop table if exists Callers");
        db.execSQL("drop table if exists SMS");
        onCreate(db);

    }



    public int insertSMS (String sms_text)
    {
        Log.d("mmmm", " inside databse");
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("sms_text", sms_text);
        if (db.insert("SMS", null, cv) != -1)
            return 1;
        else
            return 0;
    }

    public Cursor getSMS ()
    {
        db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from SMS;", null);
        return result;
    }
    public int deleteSMS(String sms)
    {
        Log.d("llll", "In delete method");
        db = this.getWritableDatabase();
        int result = db.delete("SMS", "sms_text = ?", new String[] {sms});
        Log.d("llll", "executed query");
        if (result > 0)
            return 1;
        return 0;
    }

    public int insertCustomContacts (String Number, String name)
    {
        Log.d("mmmm", " inside databse");
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Number", Number);
        cv.put("name", name);
        if (db.insert("CustomContacts", null, cv) != -1)
            return 1;
        else
            return 0;
    }

    public int deleteCustomContacts (String name)
    {
        Log.d("llll", "In delete method");
        db = this.getWritableDatabase();
        int result = db.delete("CustomContacts", "name = ?", new String[] {name});
        Log.d("llll", "executed query");
        if (result > 0)
            return 1;
        return 0;
    }
    public void truncateCustomContacts()
    {
        db = this.getWritableDatabase();
        db.execSQL("delete from CustomContacts");
        Log.d("pppp", "Table truncated");
    }



    public Cursor getCustomContacts ()
    {
        db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from CustomContacts;", null);
        return result;
    }



    public  int insertCallers(String MobileNumber, String count)
    {
        Log.d("llll", "Insert callers called");
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("MobileNum", MobileNumber);
        cv.put("count", count);
        if (db.insert("Callers", null, cv) != -1)
            return 1;
        else
            return 0;
    }



    public boolean updateCallersData(String MobileNum, String count)
    {
        db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("count", count);
        String where = "MobileNum = '" + MobileNum + "'";
        db.update("Callers", cv, where, null);

        //String[] whereArgs = new String[] {String.valueOf(id)};

        int result = db.update("Callers", cv, where, null);
//        Log.d("hhhh", String.valueOf(count));
        if (result > 0)
            return true;
        return false;

    }

    public Cursor getCallersData ()
    {
        db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select MobileNum, count from Callers;", null);
        return result;
    }


    public void truncateCallers()
    {
        db = this.getWritableDatabase();
        db.execSQL("delete from Callers");
        Log.d("pppp", "Table truncated");
    }

    public Cursor getAllSettings ()
    {
        db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from Settings;", null);
        return result;
    }

    public void truncateSettings()
    {
        db = this.getWritableDatabase();
        db.execSQL("delete from Settings");
        Log.d("pppp", "Table truncated");
    }

    public String getSettingsData(String table_name, String setting_name)
    {
        db = this.getReadableDatabase();
        String[] columns = {"selected_value"};
        String condition =  "setting_name like ?";
        String[] value = {setting_name};
//        ArrayList<String> usernameslist = new ArrayList<String>();
        Cursor cursor = db.query(table_name, columns, condition, value, null, null, null);
        Log.d("llll", "select query executed");
//        Cursor cursor = db.rawQuery("select selected from " + table_name + "where setting = '" + setting + "'", null);

        if (cursor.moveToFirst())
        {
//            Log.d("llll", cursor.getString(0));
            return cursor.getString(0);
        }
        return null;
    }

    public boolean updateSettings( String Table_name, String setting_name, String Value)
    {
        Log.d("llll", "function called to change the manual");
        db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("selected_value", Value);
        String where = "setting_name = '" + setting_name + "'";
        //String[] whereArgs = new String[] {String.valueOf(id)};

        int count = db.update(Table_name, cv, where, null);
        Log.d("llll", String.valueOf(count));
        Log.d("llll", "updation successfull");
        return true;

    }

}
