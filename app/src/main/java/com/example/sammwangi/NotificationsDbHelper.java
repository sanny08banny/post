package com.example.sammwangi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NotificationsDbHelper extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "ID";

    public static final String NOTIFICATIONS_TABLE = "NOTIFICATIONS_TABLE";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final String COLUMN_BODY = "BODY";
    public static final String COLUMN_TIME_RECEIVED = "TIME_RECEIVED";
    public static final String COLUMN_TIME_SENT = "TIME_SENT";
    public static final String COLUMN_DATE_RECEIVED = "DATE_RECEIVED";
    public static final String COLUMN_DATE_SENT = "DATE_SENT";

    public NotificationsDbHelper(@Nullable Context context) {
        super(context, "sam_mwangi_notifications.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTables = "CREATE TABLE " + NOTIFICATIONS_TABLE + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_NOTIFICATION_TYPE + " TEXT, " +
                COLUMN_BODY + " TEXT, " +
                COLUMN_TIME_RECEIVED + " TEXT, " +
                COLUMN_TIME_SENT + " TEXT, " +
                COLUMN_DATE_RECEIVED + " TEXT, " +
                COLUMN_DATE_SENT + " TEXT " + ")";
        db.execSQL(createTables);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE);
        // Create new table
        onCreate(db);
    }

    public boolean addNotification(NotificationsItem notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, notification.getTitle());
        contentValues.put(COLUMN_NOTIFICATION_TYPE, notification.getNotificationType());
        contentValues.put(COLUMN_BODY, notification.getBody());
        contentValues.put(COLUMN_TIME_RECEIVED, notification.getTimeReceived());
        contentValues.put(COLUMN_TIME_SENT, notification.getTimeSent());
        contentValues.put(COLUMN_DATE_RECEIVED, notification.getDateReceived());
        contentValues.put(COLUMN_DATE_SENT, notification.getDateSent());
        long insert = db.insert(NOTIFICATIONS_TABLE, null, contentValues);
        db.close();
        return insert != -1;
    }

    public boolean deleteNotification(NotificationsItem notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(notification.getId())};
        int deleteCount = db.delete(NOTIFICATIONS_TABLE, whereClause, whereArgs);
        db.close();
        return deleteCount > 0;
    }

    public List<NotificationsItem> getAllNotifications() {
        List<NotificationsItem> returnAll = new ArrayList<>();
        String queryString = "SELECT * FROM " + NOTIFICATIONS_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                int notificationId = cursor.getInt(0);
                String title = cursor.getString(1);
                String notificationType = cursor.getString(2);
                String body = cursor.getString(3);
                String timeReceived = cursor.getString(4);
                String timeSent = cursor.getString(5);
                String dateReceived = cursor.getString(6);
                String dateSent = cursor.getString(7);

                NotificationsItem notification = new NotificationsItem();
                notification.setId(notificationId);
                notification.setTitle(title);
                notification.setNotificationType(notificationType);
                notification.setBody(body);
                notification.setTimeReceived(timeReceived);
                notification.setTimeSent(timeSent);
                notification.setDateReceived(dateReceived);
                notification.setDateSent(dateSent);

                returnAll.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnAll;
    }
}
