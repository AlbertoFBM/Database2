package com.aserrano.database2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;


public class WordListOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = WordListOpenHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "wordlist";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "word_entries";
    public static final String KEY_ID = "_id";
    public static final String KEY_WORD = "word";


    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE + " (" +
             KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
             KEY_WORD + " TEXT)";

    private SQLiteDatabase mWritableDB;
    private SQLiteDatabase mReadableDB;

    public WordListOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
        fillDatabaseWithData(db);
    }

    private void fillDatabaseWithData(SQLiteDatabase db) {

        String[] words = {"Android", "Adapter", "ListView", "AsyncTask",
                "Android Studio", "SQLiteDatabase", "SQLOpenHelper",
                "Data model", "ViewHolder","Android Performance",
                "OnClickListener"};

        //Create a container for the data
        ContentValues values = new ContentValues();

        for (int i = 0; i < words.length; i++) {
            values.put(KEY_WORD, words[i]);
            db.insert(TABLE, null, values);
        }
    }

    @SuppressLint("Range")
    public WordItem query(int position){

        String query = "SELECT * FROM " + TABLE +
                " ORDER BY " + KEY_WORD + " ASC " +
                "LIMIT " + position + ",1";

        Cursor cursor = null;

        WordItem entry = new WordItem();

        try {
            if (mReadableDB == null){
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.rawQuery(query, null);

            cursor.moveToFirst();

            entry.setmId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));

            entry.setmWord(cursor.getString(cursor.getColumnIndex(KEY_WORD)));

        }catch(Exception e){

            Log.d(TAG, "Exception:"+ e);

        }finally {

            cursor.close();

            return entry;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(WordListOpenHelper.class.getName(),
                "Upgrading database from version " +
                oldVersion + " to " + newVersion +
                        ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long insert(String word){

        long newId = 0;

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, word);

        try {

            if(mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }

            newId = mWritableDB.insert(TABLE, null, values);

        }catch (Exception e){

            Log.d(TAG, "Insert Exception: "+ e);
        }
        return newId;
    }

    public long count(){
        if(mReadableDB == null){
            mReadableDB = getReadableDatabase();
        }
        return DatabaseUtils.queryNumEntries(mReadableDB, TABLE);
    }

    public int delete(int id){
        int deleted = 0;

        try{
            if (mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }

            deleted = mWritableDB.delete(TABLE,
                    KEY_ID + " = ?", new String[]{String.valueOf(id)});

        }catch(Exception e){
            Log.d(TAG, "DELETE EXCEPTION: "+ e);
        }

        return deleted;
    }

    public int update(int id, String word){
        int nNumberOfRowsUpdated = -1;

        try{
            if(mWritableDB == null){
                mWritableDB = getWritableDatabase();
            }

            ContentValues values = new ContentValues();

            values.put(KEY_WORD, word);

            nNumberOfRowsUpdated = mWritableDB.update(TABLE,
                    values,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(id)});

        }catch (Exception e){
            Log.d(TAG, "UPDATE EXCEPTION: " + e);
        }
        return nNumberOfRowsUpdated;
    }


    public Cursor search(String word) {

        String[] columns = new String[]{KEY_WORD};
        String selection = KEY_WORD + " LIKE ?";
        word = "%" + word + "%";
        String[] selectionArgs = new String[]{word};

        Cursor cursor = null;

        try{

            if(mReadableDB == null){
                mReadableDB = getReadableDatabase();
            }
            cursor = mReadableDB.query(TABLE, columns, selection, selectionArgs, null, null, null);

        }catch (Exception e){
            Log.d(TAG, "SEARCH EXCEPTION! " + e);
        }
        return cursor;
    }
}
