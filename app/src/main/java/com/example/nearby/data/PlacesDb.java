package com.example.nearby.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Professor on 4/14/2017.
 */
public class PlacesDb extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Places.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlacesContract.FeedEntry.TABLE_NAME + " (" +
                    PlacesContract.FeedEntry._ID + " TEXT PRIMARY KEY," +
                    PlacesContract.FeedEntry.COLUMN_PHOTO + " TEXT," +
                    PlacesContract.FeedEntry.COLUMN_NAME + " TEXT," +
                    PlacesContract.FeedEntry.COLUMN_ADDRESS + " TEXT," +
                    PlacesContract.FeedEntry.COLUMN_PRICE_LEVEL + " INTEGER," +
                    PlacesContract.FeedEntry.COLUMN_LATITUDE + " REAL," +
                    PlacesContract.FeedEntry.COLUMN_LONGITUDE + " REAL," +
                    PlacesContract.FeedEntry.COLUMN_TYPE + " TEXT," +
                    PlacesContract.FeedEntry.COLUMN_RATING + " REAL)";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PlacesContract.FeedEntry.TABLE_NAME;

    public PlacesDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
