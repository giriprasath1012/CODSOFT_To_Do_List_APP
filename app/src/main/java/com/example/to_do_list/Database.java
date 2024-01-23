package com.example.to_do_list;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {

        String qry1="create table details(id INTEGER PRIMARY KEY AUTOINCREMENT ,title text,date text,time text,content text,createdate text,status text)";
        db.execSQL(qry1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertitem(String title, String date, String time, String content) {
        // Format the date and time parameters to the desired output format
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        String formattedDateStr = "";
        String formattedTimeStr = "";

        try {
            // Parse the input date and time
            Date parsedDate = inputDateFormat.parse(date);
            Date parsedTime = inputTimeFormat.parse(time);

            // Format the parsed date and time to the desired output format
            if (parsedDate != null) {
                formattedDateStr = outputDateFormat.format(parsedDate);
            }

            if (parsedTime != null) {
                formattedTimeStr = outputTimeFormat.format(parsedTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Insert the formatted date and time into the database
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("date", formattedDateStr);
        cv.put("time", formattedTimeStr);
        cv.put("content", content);
        cv.put("createdate", new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date()));
        cv.put("status", "Pending");

        SQLiteDatabase db = getWritableDatabase();
        db.insert("details", null, cv);
        db.close();
    }




    public ArrayList getallitem()
    {
        ArrayList<String> arr = new ArrayList<>();
        SQLiteDatabase db=getReadableDatabase();
        Cursor c =db.rawQuery("select * from details ",null);
        if(c.moveToFirst())
        {
            do
            {
                arr.add(c.getString(0)+"$"+c.getString(1)+"$"+c.getString(2)+"$"+c.getString(3)+"$"+c.getString(4)+"$"+c.getString(5)+"$"+c.getString(6));
            }
            while(c.moveToNext());
        }
        db.close();
        return arr;
    }

    public void deleteitem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("details", "id=?", new String[]{Integer.toString(id)});
        db.close();
    }

    public void updatestatus(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Completed");
        db.update("details", values, "id=?", new String[]{Integer.toString(id)});
        db.close();
    }

    public void updateitem(int id,String updatedtitle, String updatedcontent,String updateddate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", updatedtitle);
        values.put("content", updatedcontent);
        values.put("date", updateddate);
        db.update("details", values, "id=?", new String[]{Integer.toString(id)});
        db.close();

    }
}
