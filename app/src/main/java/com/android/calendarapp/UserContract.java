package com.android.calendarapp;

import android.provider.BaseColumns;

/** 스키마 (계약 클래스) 정의 **/
public final class UserContract {
    public static final String DB_NAME = "user.db";
    public static final int DATABASEVERSION = 1;
    private static final String TEXT_TYPE = "TEXT";
    private static final String COMMA_SEP = ",";

    private UserContract() {}

    public static class User implements BaseColumns {
        //BaseColumns 인터페이스는 _ID라는 기본 키 필드 상수로 정의함
        public static final String TABLE_NAME = "MEMO";
        public static final String KEY_MEMO = "memo";

        public static final String CREATE_RABLE =
                "CREATE TABLE"+TABLE_NAME+"("+ _ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                        KEY_MEMO + TEXT_TYPE +  " )";
        public static final String DELETE_TABLE = "DROP THABLE IF EXITS " + TABLE_NAME;
    }
}
