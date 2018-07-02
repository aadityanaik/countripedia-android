package com.halfwitdevs.countripedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BookmarkDatabaseAdapter extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Bookmarks.db";
    private static final String TABLE_BOOKMARKS = "Bookmarks";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_ALPHA2CODE = "alpha2code";
    private static final String COLUMN_COUNTRYNAME = "CountryName";

    public BookmarkDatabaseAdapter(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_BOOKMARKS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ALPHA2CODE + " VARCHAR(20), " + COLUMN_COUNTRYNAME + " VARCHAR(20));";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        onCreate(sqLiteDatabase);
    }

    public void addCountry(BookmarkObject bookmarkObject) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ALPHA2CODE, bookmarkObject.getAlpha2code());
        contentValues.put(COLUMN_COUNTRYNAME, bookmarkObject.getName());
        sqLiteDatabase.insert(TABLE_BOOKMARKS, null, contentValues);
        sqLiteDatabase.close();
    }

    public boolean checkIfPresent(String countryName) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKMARKS + " WHERE " + COLUMN_COUNTRYNAME + "=\"" + countryName + "\";";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        boolean flag;
        flag = cursor.getCount() > 0;

        cursor.close();
        sqLiteDatabase.close();

        return flag;
    }

    public void deleteCountry(String countryName) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("DELETE FROM " + TABLE_BOOKMARKS + " WHERE " + COLUMN_COUNTRYNAME + "=\"" + countryName + "\";");
        sqLiteDatabase.close();
    }

    public ArrayList<String> dataBaseToString() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKMARKS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String> display = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            display.add(cursor.getString(cursor.getColumnIndex(BookmarkDatabaseAdapter.COLUMN_COUNTRYNAME)));
            //display.append("\n");
            cursor.moveToNext();
        }
        sqLiteDatabase.close();
        return display;
    }

    public ArrayList<String[]> dataBaseToNameCode() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKMARKS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        ArrayList<String[]> display = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            String countryName = cursor.getString(cursor.getColumnIndex(BookmarkDatabaseAdapter.COLUMN_COUNTRYNAME));
            String countryCode = cursor.getString(cursor.getColumnIndex(BookmarkDatabaseAdapter.COLUMN_ALPHA2CODE));
            display.add(new String[]{countryName, countryCode});
            cursor.moveToNext();
        }
        sqLiteDatabase.close();
        return display;
    }
}
