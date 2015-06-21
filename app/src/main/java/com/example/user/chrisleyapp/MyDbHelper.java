/**
 * Created by tharun on 26/4/15.
 */

package com.example.user.chrisleyapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDbHelper extends SQLiteOpenHelper {

    // TABLE INFORMATTION
    public static final String TABLE_NAME = "member";
    public static final String PRODUCT_ID = "_id";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String WEIGHT = "weight";

    // DATABASE INFORMATION
    static final String DB_NAME = "MEMBER.DB";
    static final int DB_VERSION = 1;

    // TABLE CREATION STATEMENT

    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "(" + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT NOT NULL ," + PRICE
            + " TEXT NOT NULL ," + WEIGHT + " TEXT NOT NULL);";

    public MyDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

}