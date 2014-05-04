package com.mapyo.everydayredbook.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AddedDataBaseHelper extends SQLiteOpenHelper {
    static final String DB_NAME = "added_redbook";
    static final int DB_VERSION = 1;
    static final String CREATE_TABLE = "create table added_redbook ( _id integer primary key autoincrement, added_id integer not null );";
    static final String DROP_TABLE = "drop table added_redbook;";


    public AddedDataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
