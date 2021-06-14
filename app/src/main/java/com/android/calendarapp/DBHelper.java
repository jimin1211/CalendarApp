package com.android.calendarapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.android.calendarapp.UserContract.DATABASEVERSION;


public class DBHelper extends SQLiteOpenHelper {
    final static String TAG = "SQLiteDB";

    public DBHelper(Context context) {
        super(context, UserContract.DB_NAME,null, UserContract.DATABASEVERSION);
        //context - DB 생성 컨텍스트(보통 MainActivity)
        //DB_NAME - DB 파일 이름
        //SQLiteDatabase.CursorFactory - 표준커서 사용시, null
        //DATABASEVERSION - DB 버전
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //DB 처음 생성 시, 호출 (테이블 생성 & 초기 레코드 삽입)
        Log.i(TAG,getClass().getName()+"onCreate()");
        db.execSQL(UserContract.User.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //DB 업그레이드 시, 호출 (기존 테이블 삭제 및 생성 / AFTER TABLE로 스키마 수정)
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
            //getWritableDatabase()의 execSQL(sql) 통해 SELECT 명령 제외한 대부분의 명령 직접 실행

        } catch (SQLException e) {
            Log.e(TAG,"Error in inserting recordes");
        }
    }

    public Cursor getAllUsersBySQL() {
        String sql = "Select * FROM " + UserContract.User.TABLE_NAME;
        return getReadableDatabase().rawQuery(sql,null);
        //getReadableDatabase()의 rawQuery() 통해 SELECT sql문 실행, 리턴
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
}
