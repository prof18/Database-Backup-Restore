package com.prof.dbtest.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.prof.dbtest.Data.Exam;
import com.prof.dbtest.Data.Student;

import java.util.ArrayList;

/**
 * Created by marco on 9/25/16.
 **/

public class DBHelper extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String DATABASE_NAME = "studentsManager";

    //Data Type
    private static final String TEXT = " TEXT ";
    private static final String INTEGER = " INTEGER ";

    //Tables Name
    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_EXAMS =  "exams";

    //STUDENTS Table - column name
    private static final String STUD_ID = "id";
    private static final String STUD_NAME = "name";
    private static final String STUD_SURNAME = "surname";
    private static final String STUD_BORN = "bornDate";

    //EXAMS Table - column name
    private static final String EX_ID = "id";
    private static final String EX_STUDENT = "student";
    private static final String EX_VAL = "evaluation";

    //Table Create Statement

    //Students table
    private static final String CREATE_TABLE_STUDENTS = "CREATE TABLE " + TABLE_STUDENTS + " ( " +
            STUD_ID + INTEGER + "," +
            STUD_NAME + TEXT + "," +
            STUD_SURNAME + TEXT + "," +
            STUD_BORN + INTEGER + "," +
            "PRIMARY KEY (" + STUD_ID + ")" +
            ")";

    //Exams Table
    private static final String CREATE_TABLE_EXAMS = "CREATE TABLE " + TABLE_EXAMS + " ( " +
            EX_ID + INTEGER + "," +
            EX_STUDENT + INTEGER + "," +
            EX_VAL + INTEGER + "," +
            "PRIMARY KEY (" + EX_ID + "," + EX_STUDENT + ")," +
            "FOREIGN KEY (" + EX_STUDENT + ") REFERENCE (" + TABLE_STUDENTS + "(" + STUD_ID + ")" +
            ")";

    //Table Delete Statement

    //Students Table
    private static final String DELETE_STUDENTS = "DROP TABLE IF EXIST " + TABLE_STUDENTS;

    //Exams Table
    private static final String DELETE_EXAMS = "DROP TABLE IF EXIST " + TABLE_EXAMS;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables
        db.execSQL(CREATE_TABLE_STUDENTS);
        db.execSQL(CREATE_TABLE_EXAMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //reinizialize db
        db.execSQL(DELETE_STUDENTS);
        db.execSQL(DELETE_EXAMS);

        onCreate(db);
    }

    //create a new student
    public long addStudent(Student stud) {

        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(STUD_ID,stud.getId());
        values.put(STUD_NAME,stud.getName());
        values.put(STUD_SURNAME,stud.getSurname());
        values.put(STUD_BORN,stud.getBorn());

        // Create a new map of values, where column names are the keys
        long id = db.insert(TABLE_STUDENTS,null,values);

        return id;
    }

    //create a new exam
    public long addExam(Exam exam) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EX_ID,exam.getId());
        values.put(EX_STUDENT,exam.getStudent());
        values.put(EX_VAL,exam.getEvaluation());

        long id = db.insert(TABLE_EXAMS,null,values);

        return id;
    }

    //get all students
    public ArrayList<Student> getAllStudent() {

        ArrayList<Student> students = new ArrayList<>();
        //select all query
        String query = "SELECT * FROM " + TABLE_STUDENTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        if (c.moveToFirst()) {
            while(c.moveToNext()) {

                Student student = new Student();
                student.setId(c.getInt(c.getColumnIndex(STUD_ID)));
                student.setName(c.getString(c.getColumnIndex(STUD_NAME)));
                student.setSurname(c.getString(c.getColumnIndex(STUD_SURNAME)));
                student.setBorn(c.getLong(c.getColumnIndex(STUD_BORN)));

                students.add(student);
            }
        }
        c.close();
        return students;
    }

    //get all exams
    public ArrayList<Exam> getAllExam() {

        ArrayList<Exam> exams = new ArrayList<>();
        //select all query
        String query = "SELECT * FROM " + TABLE_EXAMS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        if (c.moveToFirst()) {
            while(c.moveToNext()) {

                Exam e = new Exam();
                e.setId(c.getInt(c.getColumnIndex(EX_ID)));
                e.setStudent(c.getInt(c.getColumnIndex(EX_STUDENT)));
                e.setEvaluation(c.getInt(c.getColumnIndex(EX_VAL)));

                exams.add(e);
            }
        }
        c.close();
        return exams;
    }

    //select single student
    public Student getStudent(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_STUDENTS + " WHERE " + STUD_ID + " = " + id;

        Cursor c = db.rawQuery(query,null);
        if(c != null)
            c.moveToFirst();

        Student stud = new Student();
        stud.setId(c.getInt(c.getColumnIndex(STUD_ID)));
        stud.setName(c.getString(c.getColumnIndex(STUD_NAME)));
        stud.setSurname(c.getString(c.getColumnIndex(STUD_SURNAME)));
        stud.setBorn(c.getLong(c.getColumnIndex(STUD_BORN)));

        c.close();
        return stud;
    }

    //select single exam
    public Exam getExam(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXAMS + " WHERE " + EX_ID + " = " + id;

        Cursor c = db.rawQuery(query,null);
        if ( c!= null)
            c.moveToFirst();

        Exam e = new Exam();
        e.setId(c.getInt(c.getColumnIndex(EX_ID)));
        e.setStudent(c.getInt(c.getColumnIndex(EX_STUDENT)));
        e.setEvaluation(c.getInt(c.getColumnIndex(EX_VAL)));

        c.close();
        return e;
    }

    //update a student
    public int updateStudent(Student stud) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values  = new ContentValues();
        values.put(STUD_ID,stud.getId());
        values.put(STUD_NAME,stud.getName());
        values.put(STUD_SURNAME,stud.getSurname());
        values.put(STUD_BORN,stud.getBorn());

        return db.update(TABLE_STUDENTS,values,STUD_ID + " = ? ", new String[] {String.valueOf(stud.getId())});
    }

    //update an exam
    public int updateExam(Exam e) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EX_ID,e.getId());
        values.put(EX_STUDENT,e.getStudent());
        values.put(EX_VAL,e.getEvaluation());

        return db.update(TABLE_EXAMS,values,EX_ID + " = ? ", new String[] {String.valueOf(e.getId())});
    }


    //delete a student
    public void deleteStud(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_STUDENTS,STUD_ID + " = ? ", new String[] {String.valueOf(id)});
    }

    //delete an exam
    public void deleteExam(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_EXAMS,EX_ID + " = ? ", new String[] {String.valueOf(id)});

    }

    //close database
    public void closeDB() {

        SQLiteDatabase db = this.getReadableDatabase();

        if (db != null && db.isOpen())
            db.close();
    }

}
