package com.ph.schedule.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.ph.schedule.bean.Schedule;

public class DBAdapter {

    // 建库信息
    public static final String DB_NAME = "schedule.db";
    private static final String DB_TABLE = "schedule";
    public static final int DB_VERSION = 1;

    // 字段名
    public static final String ID = "schedule_id";
    public static final String NAME = "schedule_name";
    public static final String TIME = "schedule_time";
    public static final String ADDRESS = "address";
    public static final String TEACHER = "teacher";

    // 建表sql语句
    public static final String DB_CREATE = "create table " + DB_TABLE + " (" +
            ID + " integer primary key autoincrement, " +
            NAME + " text not null, " +
            TIME + " text not null," +
            ADDRESS + " text not null," +
            TEACHER + " text not null);";

    private SQLiteDatabase db;
    private final Context context;
    private DBHelper dbHelper;

    public DBAdapter(Context _context) {
        context = _context;
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    /**
     * 打开数据库
     */
    public void open() {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public long insert(Schedule schedule) {
        System.out.println(DB_CREATE);
        ContentValues scheduleValues = new ContentValues();
        scheduleValues.put(NAME, schedule.getScheduleName());
        scheduleValues.put(TIME, schedule.getScheduleTime());
        scheduleValues.put(ADDRESS, schedule.getAddress());
        scheduleValues.put(TEACHER, schedule.getTeacher());
        return db.insert(DB_TABLE, null, scheduleValues);
    }

    public Schedule[] queryAll() {
        Cursor results = db.query(DB_TABLE, new String[]{ID, NAME, TIME, ADDRESS, TEACHER},
                null, null, null, null, null);
        return ConvertToBook(results);
    }

    public Schedule[] queryOne(long id) {
        Cursor results = db.query(DB_TABLE, new String[]{ID, NAME, TIME, ADDRESS, TEACHER},
                ID + "=" + id, null, null, null, null);
        return ConvertToBook(results);
    }

    private Schedule[] ConvertToBook(Cursor cursor) {
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()) {
            return null;
        }
        Schedule[] scheduleList = new Schedule[resultCounts];
        for (int i = 0; i < resultCounts; i++) {
            scheduleList[i] = new Schedule();

            cursor.moveToNext();
        }
        return scheduleList;
    }

    public long deleteAll() {
        return db.delete(DB_TABLE, null, null);
    }

    public long deleteOne(long id) {
        return db.delete(DB_TABLE, ID + "=" + id, null);
    }

    public long updateOne(long id, Schedule schedule) {
        ContentValues scheduleValues = new ContentValues();
        scheduleValues.put(ID, schedule.getScheduleId());
        scheduleValues.put(NAME, schedule.getScheduleName());
        scheduleValues.put(TIME, schedule.getScheduleTime());
        scheduleValues.put(ADDRESS, schedule.getAddress());
        scheduleValues.put(TEACHER, schedule.getTeacher());
        return db.update(DB_TABLE, scheduleValues, ID + "=" + id, null);
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i1) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
            onCreate(db);
        }
    }
}