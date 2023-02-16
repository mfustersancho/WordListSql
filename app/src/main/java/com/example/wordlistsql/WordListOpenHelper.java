package com.example.wordlistsql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class WordListOpenHelper extends SQLiteOpenHelper {
    // It's a good idea to always define a log tag like this.
    private static final String TAG = WordListOpenHelper.class.getSimpleName();

    // DB info
    private static final int DATABASE_VERSION = 1;
    private static final String WORD_LIST_TABLE = "word_entries";
    private static final String DATABASE_NAME = "wordlist";

    // Entity info
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";
    public static final String KEY_DEFINITION = "definition";

    // Entity columns
    private static final String[] COLUMNS = { KEY_ID, KEY_WORD, KEY_DEFINITION };

    // Queries -----
    // Create DB
    private static final String WORD_LIST_TABLE_CREATE = "CREATE TABLE " + WORD_LIST_TABLE + " ("
                                                            + KEY_ID + " INTEGER PRIMARY KEY, "
                                                            + KEY_WORD + " TEXT, "
                                                            + KEY_DEFINITION + " TEXT);";

    // DBAccessors
    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_LIST_TABLE_CREATE);
        fillDatabaseWidthData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + WORD_LIST_TABLE);
        onCreate(db);
    }

    private void fillDatabaseWidthData(SQLiteDatabase db) {
        String[] words = {"Android", "Adapter", "ListView", "AsyncTask",
            "Android Studio", "SQLiteDatabase", "SQLOpenHelper",
            "Data model", "ViewHolder", "Android Performance",
            "OnClickListener"};

        // Container for the data
        ContentValues values = new ContentValues();

        for(String word: words) {
            values.put(KEY_WORD, word);
            values.put(KEY_DEFINITION, "");
            db.insert(WORD_LIST_TABLE, null, values);
        }
    }

    @SuppressLint("Range")
    public WordItem query(int position) {
        // Get the nth element of the list
        String query = "SELECT * FROM " + WORD_LIST_TABLE
                + " ORDER BY " + KEY_WORD + " ASC "
                + "LIMIT " + position + ",1";

        Cursor cursor = null;
        WordItem entry = new WordItem();

        try {
            if(mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);
            cursor.moveToFirst();
            entry.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            entry.setWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));
            entry.setDefinition(cursor.getString(cursor.getColumnIndex(KEY_DEFINITION)));
        } catch (Exception e) {
            Log.d(TAG, "EXCEPTION: " + e.getMessage());
        } finally {
            cursor.close();
            return entry;
        }
    }

    public long insert(String word, String definition) {
        long newId = 0;
        if(definition == null)
            definition = "";
        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);
        values.put(KEY_DEFINITION, definition);
        try {
            if(mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            newId = mWritableDB.insert(WORD_LIST_TABLE, null, values);
        } catch (Exception e) {
            Log.d(TAG, "INSERT EXCEPTION: " + e.getMessage());
        }
        return newId;
    }

    public long count() {
        if(mReadableDB == null) {
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, WORD_LIST_TABLE);
    }

    public int delete(int id) {
        int deleted = 0;
        try {
            if(mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            deleted = mWritableDB.delete(WORD_LIST_TABLE, KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        } catch(Exception e) {
            Log.d(TAG, "DELETE EXCEPTION: " + e.getMessage());
        }
        return deleted;
    }

    public int update(int id, String word, String definition) {
        int mNumberOfRowsUpdated = -1;
        try {
            if(mWritableDB == null) {
                mWritableDB = getWritableDatabase();
            }
            ContentValues values = new ContentValues();
            values.put(KEY_WORD, word);
            values.put(KEY_DEFINITION, definition);
            mNumberOfRowsUpdated = mWritableDB.update(WORD_LIST_TABLE,
                    values,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch(Exception e) {
            Log.d(TAG, "UPDATE EXCEPTION: " + e.getMessage());
        }
        return mNumberOfRowsUpdated;
    }

    public Cursor search(String searchString) {
        String[] columns = new String[]{KEY_WORD};
        searchString = "%" + searchString + "%";
        String where = KEY_WORD + " LIKE ? ";
        String[] whereArgs = new String[]{searchString};
        Cursor cursor = null;
        try {
            if(mReadableDB == null) {
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(WORD_LIST_TABLE, columns, where, whereArgs, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }
}
