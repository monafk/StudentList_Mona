package com.waiting.studentlist.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.waiting.studentlist.sqlite.model.StudentData;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "waiting_list_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create students table
        db.execSQL(StudentData.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + StudentData.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertStudent(StudentData studentData) {

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // `id` will be inserted automatically.
        // no need to add them
        values.put(StudentData.COLUMN_STUDENT_NAME, studentData.getName());
        values.put(StudentData.COLUMN_STUDENT_COURSE, studentData.getCourse());
        values.put(StudentData.COLUMN_STUDENT_PRIORITY, studentData.getPriority());

        // insert row
        long id = db.insert(StudentData.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public StudentData getStudent(long id) {

        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(StudentData.TABLE_NAME,
                new String[]{StudentData.COLUMN_ID, StudentData.COLUMN_STUDENT_NAME, StudentData.COLUMN_STUDENT_COURSE, StudentData.COLUMN_STUDENT_PRIORITY},
                StudentData.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare studentData object
        StudentData studentData = new StudentData(
                cursor.getInt(cursor.getColumnIndex(StudentData.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_NAME)),
                cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_COURSE)),
                cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_PRIORITY)));

        // close the db connection
        cursor.close();

        //return studentData data
        return studentData;
    }

    public List<StudentData> getAllStudents() {

        List<StudentData> studentData = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + StudentData.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        // to get every student.
        if (cursor.moveToFirst()) {
            do {
                StudentData students = new StudentData();
                students.setId(cursor.getInt(cursor.getColumnIndex(StudentData.COLUMN_ID)));
                students.setName(cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_NAME)));
                students.setCourse(cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_COURSE)));
                students.setPriority(cursor.getString(cursor.getColumnIndex(StudentData.COLUMN_STUDENT_PRIORITY)));

                //Add the studentData to the list
                studentData.add(students);

            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return studentData list
        return studentData;
    }

    public int getStudentsCount() {
        String countQuery = "SELECT  * FROM " + StudentData.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public long updateStudent(StudentData studentData) {

        SQLiteDatabase db = this.getWritableDatabase();

        //Sending the new values
        ContentValues values = new ContentValues();
        values.put(StudentData.COLUMN_STUDENT_NAME, studentData.getName());
        values.put(StudentData.COLUMN_STUDENT_COURSE, studentData.getCourse());
        values.put(StudentData.COLUMN_STUDENT_PRIORITY, studentData.getPriority());

        // updating row
        long newRowUpdate = db.update(StudentData.TABLE_NAME,
                values,
                StudentData.COLUMN_ID + " =?",
                new String[]{String.valueOf(studentData.getId())});

        return newRowUpdate;
    }

    public void deleteStudent(StudentData studentData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(StudentData.TABLE_NAME, StudentData.COLUMN_ID + " = ?",
                new String[]{String.valueOf(studentData.getId())});
        db.close();
    }
}
