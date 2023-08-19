package com.example.sammwangi.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.sammwangi.DAOs.ProfileAccount;

import java.util.ArrayList;
import java.util.List;
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "profile_accounts.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and column names
    private static final String TABLE_PROFILE_ACCOUNTS = "profile_accounts";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ACCOUNT_TYPE = "account_type";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_REFERENCE_NUMBER = "reference_number";
    private static final String COLUMN_SUB_COUNTY = "sub_county";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_REPEAT_PASSWORD = "repeat_password";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_TOKEN_ID = "token_id";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PROFILE_ACCOUNTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ACCOUNT_TYPE + " TEXT, " +
                COLUMN_FULL_NAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_REFERENCE_NUMBER + " TEXT, " +
                COLUMN_SUB_COUNTY + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_REPEAT_PASSWORD + " TEXT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_TOKEN_ID + " TEXT, " +
                COLUMN_FILE_PATH + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    // Insert a profile account into the database
    public boolean addProfileAccount(ProfileAccount profileAccount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNT_TYPE, profileAccount.getAccountType());
        values.put(COLUMN_FULL_NAME, profileAccount.getFullName());
        values.put(COLUMN_EMAIL, profileAccount.getEmail());
        values.put(COLUMN_REFERENCE_NUMBER, profileAccount.getReferenceNumber());
        values.put(COLUMN_SUB_COUNTY, profileAccount.getSubCounty());
        values.put(COLUMN_PASSWORD, profileAccount.getPassword());
        values.put(COLUMN_REPEAT_PASSWORD, profileAccount.getRepeatPassword());
        values.put(COLUMN_USER_NAME, profileAccount.getUserName());
        values.put(COLUMN_TOKEN_ID, profileAccount.getTokenId());
        values.put(COLUMN_FILE_PATH, profileAccount.getFilePath());
        values.put(COLUMN_TIMESTAMP, profileAccount.getTimestamp());

        long insert = db.insert(TABLE_PROFILE_ACCOUNTS, null, values);
        db.close();
        return insert != -1;
    }

    // Update the file path of a profile account
    public boolean updateFilePath(int accountId, String newFilePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILE_PATH, newFilePath);

        int rowsAffected = db.update(TABLE_PROFILE_ACCOUNTS, values,
                COLUMN_ID + " = ?", new String[]{String.valueOf(accountId)});
        db.close();
        return rowsAffected > 0;
    }

    // Update the username of a profile account
    public boolean updateUserName(int accountId, String newUserName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, newUserName);

        int rowsAffected = db.update(TABLE_PROFILE_ACCOUNTS, values,
                COLUMN_ID + " = ?", new String[]{String.valueOf(accountId)});
        db.close();
        return rowsAffected > 0;
    }

    // Update the subcounty of a profile account
    public boolean updateSubCounty(int accountId, String newSubCounty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUB_COUNTY, newSubCounty);

        int rowsAffected = db.update(TABLE_PROFILE_ACCOUNTS, values,
                COLUMN_ID + " = ?", new String[]{String.valueOf(accountId)});
        db.close();
        return rowsAffected > 0;
    }

    // Delete a profile account
    public boolean deleteProfileAccount(int accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PROFILE_ACCOUNTS,
                COLUMN_ID + " = ?", new String[]{String.valueOf(accountId)});
        db.close();
        return rowsAffected > 0;
    }

    // Get all profile accounts
    public List<ProfileAccount> getAllProfileAccounts() {
        List<ProfileAccount> profileAccounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROFILE_ACCOUNTS, null, null,
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ProfileAccount profileAccount = new ProfileAccount();
                profileAccount.setId(cursor.getInt(0));
                profileAccount.setAccountType(cursor.getString(1));
                profileAccount.setFullName(cursor.getString(2));
                profileAccount.setEmail(cursor.getString(3));
                profileAccount.setReferenceNumber(cursor.getString(4));
                profileAccount.setSubCounty(cursor.getString(5));
                profileAccount.setPassword(cursor.getString(6));
                profileAccount.setRepeatPassword(cursor.getString(7));
                profileAccount.setUserName(cursor.getString(8));
                profileAccount.setTokenId(cursor.getString(9));
                profileAccount.setFilePath(cursor.getString(10));
                profileAccount.setTimestamp(cursor.getString(11));

                profileAccounts.add(profileAccount);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return profileAccounts;
    }
    // Get a profile account by ID
    public ProfileAccount getProfileAccountById(int accountId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROFILE_ACCOUNTS, null,
                COLUMN_ID + " = ?", new String[]{String.valueOf(accountId)},
                null, null, null);

        ProfileAccount profileAccount = null;
        if (cursor != null && cursor.moveToFirst()) {
            profileAccount = new ProfileAccount();
            profileAccount.setId(cursor.getInt(0));
            profileAccount.setAccountType(cursor.getString(1));
            profileAccount.setFullName(cursor.getString(2));
            profileAccount.setUserName(cursor.getString(3));
            profileAccount.setEmail(cursor.getString(4));
            profileAccount.setReferenceNumber(cursor.getString(5));
            profileAccount.setSubCounty(cursor.getString(6));
            profileAccount.setPassword(cursor.getString(7));
            profileAccount.setRepeatPassword(cursor.getString(8));
            profileAccount.setFilePath(cursor.getString(9));
            profileAccount.setTimestamp(cursor.getString(10));
            profileAccount.setTokenId(cursor.getString(11));
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return profileAccount;
    }

    // Get a profile account by reference number
    public ProfileAccount getProfileAccountByReferenceNumber(String referenceNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROFILE_ACCOUNTS, null,
                COLUMN_REFERENCE_NUMBER + " = ?", new String[]{referenceNumber},
                null, null, null);

        ProfileAccount profileAccount = null;
        if (cursor != null && cursor.moveToFirst()) {
            profileAccount = new ProfileAccount();
            profileAccount.setId(cursor.getInt(0));
            profileAccount.setAccountType(cursor.getString(1));
            profileAccount.setFullName(cursor.getString(2));
            profileAccount.setUserName(cursor.getString(3));
            profileAccount.setEmail(cursor.getString(4));
            profileAccount.setSubCounty(cursor.getString(5));
            profileAccount.setPassword(cursor.getString(6));
            profileAccount.setRepeatPassword(cursor.getString(7));
            profileAccount.setFilePath(cursor.getString(8));
            profileAccount.setTimestamp(cursor.getString(9));
            profileAccount.setTokenId(cursor.getString(10));
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return profileAccount;
    }

    public boolean updateTokenIfNotMatch(String email, String receivedToken) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TOKEN_ID, receivedToken);

        String whereClause = COLUMN_EMAIL + " = ? AND " + COLUMN_TOKEN_ID + " != ?";
        String[] whereArgs = { email, receivedToken };

        int rowsAffected = db.update(TABLE_PROFILE_ACCOUNTS, contentValues, whereClause, whereArgs);
        db.close();

        return rowsAffected > 0;
    }
}

