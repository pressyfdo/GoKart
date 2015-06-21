/**
 * Created by tharun on 26/4/15.
 */

package com.example.user.chrisleyapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class SQLController {

    private MyDbHelper dbhelper;
    private Context ourcontext;
    private SQLiteDatabase database;

    public SQLController(Context c) {

        ourcontext = c;
        dbhelper = new MyDbHelper(ourcontext);
        database = dbhelper.getWritableDatabase();

        dbhelper.onUpgrade(database,1,1);

    }

    public SQLController open() throws SQLException {
        return this;

    }

    public void close() {
        dbhelper.close();
    }

    public void insertData(String name, String price, String weight) {

        ContentValues cv = new ContentValues();
        cv.put(MyDbHelper.NAME, name);
        cv.put(MyDbHelper.PRICE, price);
        cv.put(MyDbHelper.WEIGHT, weight);
        database.insert(MyDbHelper.TABLE_NAME, null, cv);

    }

    public Cursor readEntry() {
        String[] s = new String[]{"_id","name","price","weight"};
        String[] allColumns = new String[] { MyDbHelper.PRODUCT_ID, MyDbHelper.NAME,
                MyDbHelper.PRICE, MyDbHelper.WEIGHT };

        database = dbhelper.getReadableDatabase();
        Cursor c = null;
        try {
            c = database.query(MyDbHelper.TABLE_NAME, new String[]{MyDbHelper.PRODUCT_ID, MyDbHelper.NAME, MyDbHelper.PRICE, MyDbHelper.WEIGHT}, null, null, null, null, null);
        }catch(Exception e){
            Toast toast = Toast.makeText(ourcontext, "Exception : "+e.getMessage()+" : "+e.getLocalizedMessage()+" : "+e.toString() , Toast.LENGTH_LONG);
            toast.show();

        }
        return c;

    }

}