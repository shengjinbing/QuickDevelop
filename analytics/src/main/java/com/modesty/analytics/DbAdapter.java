package com.modesty.analytics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


import com.modesty.analytics.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * SQLite database adapter.
 *
 * <p>Not thread-safe. Instances of this class should only be used
 * by a single thread.
 *
 * @author lixiang
 * @since 2018/5/18
 *
 */
/* package */ class DbAdapter {
    private static final String LOGTAG = "DbAdapter";

    enum Table {
        EVENTS ("events");

        Table(String name) {
            mTableName = name;
        }

        public String getName() {
            return mTableName;
        }

        private final String mTableName;
    }

    private static final String KEY_DATA = "data";
    private static final String KEY_CREATED_AT = "created_at";

    static final int DB_UPDATE_ERROR = -1;
    static final int DB_OUT_OF_MEMORY_ERROR = -2;
    static final int DB_UNDEFINED_CODE = -3;

    private static final String DATABASE_NAME = "tracker";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_EVENTS_TABLE =
       "CREATE TABLE " + Table.EVENTS.getName() + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        KEY_DATA + " TEXT NOT NULL, " +
        KEY_CREATED_AT + " INTEGER NOT NULL);";

    private static final String EVENTS_TIME_INDEX =
        "CREATE INDEX IF NOT EXISTS time_idx ON " + Table.EVENTS.getName() +
        " (" + KEY_CREATED_AT + ");";

    private final DatabaseHelper mDb;

    DbAdapter(Context context) {
        this(context, DATABASE_NAME);
    }

    private DbAdapter(Context context, String dbName) {
        mDb = new DatabaseHelper(context, dbName);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private final File mDatabaseFile;

        DatabaseHelper(Context context, String dbName) {
            super(context, dbName, null, DATABASE_VERSION);
            mDatabaseFile = context.getDatabasePath(dbName);
        }

        /**
         * Completely deletes the DB file from the file system.
         */
        void deleteDatabase() {
            close();
            boolean result = mDatabaseFile.delete();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Logger.v(LOGTAG, "Creating a new analytics events DB");

            db.execSQL(CREATE_EVENTS_TABLE);
            db.execSQL(EVENTS_TIME_INDEX);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Logger.v(LOGTAG, "Upgrading app, replacing Analytics events DB");

            db.execSQL("DROP TABLE IF EXISTS " + Table.EVENTS.getName());
            db.execSQL(CREATE_EVENTS_TABLE);
            db.execSQL(EVENTS_TIME_INDEX);
        }

        boolean hasEnoughSpace() {
            if (mDatabaseFile.exists()) {
                return mDatabaseFile.getUsableSpace() >= mDatabaseFile.length();
            }
            return true;
        }
    }

    /**
     * Adds a JSON string representing an event with properties or a person record
     * to the SQLiteDatabase.
     * @param j the JSON to record
     * @param table the table to insert into, either "events" or "people"
     * @return the number of rows in the table, or DB_OUT_OF_MEMORY_ERROR/DB_UPDATE_ERROR
     * on failure
     */
    int addJSON(JSONObject j, Table table) {
        // we are aware of the race condition here, but what can we do..?
        if (!this.hasEnoughSpace()) {
            Logger.e(LOGTAG, "There is not enough space left on the device to store data, so data was discarded");
            return DB_OUT_OF_MEMORY_ERROR;
        }

        final String tableName = table.getName();

        Cursor c = null;
        int count = DB_UPDATE_ERROR;

        try {
            final SQLiteDatabase db = mDb.getWritableDatabase();

            final ContentValues cv = new ContentValues();
            cv.put(KEY_DATA, j.toString());
            cv.put(KEY_CREATED_AT, System.currentTimeMillis());
            db.insert(tableName, null, cv);

            c = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            c.moveToFirst();
            count = c.getInt(0);
        } catch (final SQLiteException e) {
            Logger.e(LOGTAG, "Could not add data to table " + tableName + ". Re-initializing database.", e);

            // We assume that in general, the results of a SQL exception are
            // unrecoverable, and could be associated with an oversized or
            // otherwise unusable DB. Better to bomb it and get back on track
            // than to leave it junked up (and maybe filling up the disk.)
            if (c != null) {
                c.close();
                c = null;
            }
            mDb.deleteDatabase();
        } finally {
            if (c != null) {
                c.close();
            }
            mDb.close();
        }
        return count;
    }

    /**
     * Removes events with an _id <= last_id from table
     * @param last_id the last id to delete
     * @param table the table to remove events from, either "events" or "people"
     */
    void cleanupEvents(String last_id, Table table) {
        final String tableName = table.getName();

        try {
            final SQLiteDatabase db = mDb.getWritableDatabase();
            db.delete(tableName, "_id <= " + last_id, null);
        } catch (final SQLiteException e) {
            Logger.e(LOGTAG, "Could not clean sent records from " + tableName + ". Re-initializing database.", e);
            mDb.deleteDatabase();
        } finally {
            mDb.close();
        }
    }

    /**
     * Removes events before time.
     * @param time the unix epoch in milliseconds to remove events before
     * @param table the table to remove events from, either "events" or "people"
     */
    void cleanupEvents(long time, Table table) {
        final String tableName = table.getName();

        try {
            final SQLiteDatabase db = mDb.getWritableDatabase();
            db.delete(tableName, KEY_CREATED_AT + " <= " + time, null);
        } catch (final SQLiteException e) {
            Logger.e(LOGTAG, "Could not clean timed-out records from " + tableName + ". Re-initializing database.", e);
            mDb.deleteDatabase();
        } finally {
            mDb.close();
        }
    }

    void deleteDB() {
        mDb.deleteDatabase();
    }


    /**
     * Returns the data string to send to Analytics and the maximum ID of the row that
     * we're sending, so we know what rows to delete when a track request was successful.
     *
     * @param table the table to read the JSON from, either "events" or "people"
     * @return String array containing the maximum ID, the data string
     * representing the events (or null if none could be successfully retrieved) and the total
     * current number of events in the queue.
     */
    String[] generateDataString(Table table) {
        Cursor c = null;
        Cursor queueCountCursor = null;
        String data = null;
        String last_id = null;
        String queueCount = null;
        final String tableName = table.getName();
        final SQLiteDatabase db = mDb.getReadableDatabase();

        try {
            c = db.rawQuery("SELECT * FROM " + tableName  +
                    " ORDER BY " + KEY_CREATED_AT + " ASC LIMIT 50", null);

            queueCountCursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            queueCountCursor.moveToFirst();
            queueCount = String.valueOf(queueCountCursor.getInt(0));

            final JSONArray arr = new JSONArray();

            while (c.moveToNext()) {
                if (c.isLast()) {
                    last_id = c.getString(c.getColumnIndex("_id"));
                }
                try {
                    final JSONObject j = new JSONObject(c.getString(c.getColumnIndex(KEY_DATA)));
                    arr.put(j);
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
            }

            if (arr.length() > 0) {
                data = arr.toString();
            }
        } catch (final SQLiteException e) {
            Logger.e(LOGTAG, "Could not pull records for Analytics out of database " + tableName + ". Waiting to send.", e);

            last_id = null;
            data = null;
        } finally {
            mDb.close();
            if (c != null) {
                c.close();
            }
            if (queueCountCursor != null) {
                queueCountCursor.close();
            }
        }

        if (last_id != null && data != null) {
            final String[] ret = {last_id, data, queueCount};
            return ret;
        }
        return null;
    }

    private boolean hasEnoughSpace() {
        return mDb.hasEnoughSpace();
    }
}
