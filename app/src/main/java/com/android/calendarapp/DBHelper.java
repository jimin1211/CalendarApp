package com.android.calendarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {
    final static String TAG = "SQLiteDB";

    public DBHelper(Context context) {
        super(context, UserContract.DB_NAME,null, UserContract.DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,getClass().getName()+"onCreate()");
        db.execSQL(UserContract.User.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.i(TAG,getClass().getName() +".onUpgrade()");
        db.execSQL(UserContract.User.DELETE_TABLE);
        onCreate(db);
    }

    public void insertUserBySQL(String memo) {
        try {
            String sql = String.format(
                    "INSERT INTO %s (%s, %s) VALUES(NULL,'%s')",
                    UserContract.User.TABLE_NAME,
                    UserContract.User._ID,
                    UserContract.User.KEY_MEMO,
                    memo
            );

            getWritableDatabase().execSQL(sql);
        } catch (SQLException e) {
            Log.e(TAG,"Error in inserting recordes");
        }
    }

    public Cursor getAllUsersBySQL() {
        String sql = "Select * FROM " + UserContract.User.TABLE_NAME;
        return getReadableDatabase().rawQuery(sql,null);
    }

    public void deleteUserBySQL(String _id) {
        try{
            String sql = String.format(
                    "DELETE FROM %s WHERE %s = %s",
                    UserContract.User.TABLE_NAME,
                    UserContract.User._ID,
                    _id
            );
        }catch (SQLException e) {
            Log.e(TAG,"Error in deleting recordes");
        }
    }

    public long insertUserByMethod(String memo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.User.KEY_MEMO, memo);

        return db.insert(UserContract.User.TABLE_NAME,null,values);
    }

    public Cursor getAllUsersByMethod() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(UserContract.User.TABLE_NAME,null,null,null,null,null,null);
    }

    public long deleteUserByMethod(String _id) {
        SQLiteDatabase db = getWritableDatabase();

        String whereClause = UserContract.User._ID +" = ?";
        String[] whereArgs ={_id};
        return db.delete(UserContract.User.TABLE_NAME, whereClause, whereArgs);
    }

    public long updateUserByMethod(String _id, String memo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.User.KEY_MEMO, memo);

        String whereClause = UserContract.User._ID +" = ?";
        String[] whereArgs ={_id};

        return db.update(UserContract.User.TABLE_NAME, values, whereClause, whereArgs);
    }


}
