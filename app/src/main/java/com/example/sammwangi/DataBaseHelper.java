package com.example.sammwangi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String PROFILES_TABLE = "PROFILES_TABLE";
    public static final String COLUMN_FULLNAME = "FULLNAME";
    public static final String COLUMN_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String COLUMN_USER_NAME = "USER_NAME";
    public static final String COLUMN_REFERENCE_NUMBER = "REFERENCE_NUMBER";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String COLUMN_REPEAT_PASSWORD = "REPEAT_PASSWORD";
    public static final String COLUMN_ID = "ID";
    private static final String COLUMN_TOKEN_ID = "TOKEN_ID";
    private static final String COLUMN_SUB_COUNTY = "SUB_COUNTY";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "sam_mwangi.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTables = "CREATE TABLE " + PROFILES_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_ACCOUNT_TYPE + " TEXT, " +
                COLUMN_FULLNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_REFERENCE_NUMBER + " TEXT, " +
                COLUMN_SUB_COUNTY + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_REPEAT_PASSWORD + " TEXT, " +
                COLUMN_TOKEN_ID + " TEXT " + ")";
        db.execSQL(createTables);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       String upgradeTable = "ALTER TABLE " + PROFILES_TABLE + " ADD COLUMN " + COLUMN_TOKEN_ID + " TEXT";
        db.execSQL(upgradeTable);
    }
    public boolean addProfile(ProfileAccount profileAccount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_FULLNAME,profileAccount.getFullName());
        contentValues.put(COLUMN_ACCOUNT_TYPE,profileAccount.getAccountType());
        contentValues.put(COLUMN_EMAIL,profileAccount.getEmail());
        contentValues.put(COLUMN_USER_NAME,profileAccount.getUserName());
        contentValues.put(COLUMN_REFERENCE_NUMBER,profileAccount.getReferenceNumber());
        contentValues.put(COLUMN_SUB_COUNTY,profileAccount.getSubCounty());
        contentValues.put(COLUMN_PASSWORD,profileAccount.getPassword());
        contentValues.put(COLUMN_REPEAT_PASSWORD,profileAccount.getRepeatPassword());
        contentValues.put(COLUMN_TOKEN_ID,profileAccount.getTokenId());
        long insert = db.insert(PROFILES_TABLE,null,contentValues);
        db.close();
        return insert != -1;
    }
    public List<ProfileAccount> getAllAccounts(){
        List<ProfileAccount> returnAll = new ArrayList<>();
        String queryString = "SELECT * FROM " + PROFILES_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.moveToFirst()){
            do {
                int profileId = cursor.getInt(0);
                String fullName = cursor.getString(3);
                String userName = cursor.getString(1);
                String accountType = cursor.getString(2);
                String email = cursor.getString(4);
                String referenceNumber = cursor.getString(5);
                String subCounty = cursor.getString(6);
                String password = cursor.getString(7);
                String repeatPassword = cursor.getString(8);
                String tokenId = cursor.getString(9);

                ProfileAccount profileAccount = new ProfileAccount(profileId,accountType, userName,fullName,email,referenceNumber, subCounty, password,repeatPassword, tokenId);
                returnAll.add(profileAccount);


            }while (cursor.moveToNext());
        }else{

        }
        db.close();
        return returnAll;
    }

    public boolean deleteProfile(ProfileAccount profileAccount) {
        SQLiteDatabase database = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = { String.valueOf(profileAccount.getId()) };
        int rowsAffected = database.delete(PROFILES_TABLE, whereClause, whereArgs);
        database.close();

        return rowsAffected > 0;
    }

    public boolean updateTokenIfNotMatch(String email, String receivedToken) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TOKEN_ID, receivedToken);

        String whereClause = COLUMN_EMAIL + " = ? AND " + COLUMN_TOKEN_ID + " != ?";
        String[] whereArgs = { email, receivedToken };

        int rowsAffected = db.update(PROFILES_TABLE, contentValues, whereClause, whereArgs);
        db.close();

        return rowsAffected > 0;
    }



}

