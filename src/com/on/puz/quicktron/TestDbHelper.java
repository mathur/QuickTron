package com.on.puz.quicktron;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TestDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "scoreItems";

    // scores table name
    private static final String TABLE_ITEMS = "items";

    // scores Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ANSWERKEY = "isAnswerKey";
    private static final String KEY_TESTNAME = "testName";
    private static final String KEY_SCORES = "scores";
    private static final String KEY_EMAIL = "email";

    private static TestDbHelper dbInstance = null; 
    /*
     * Constructor that sets up the TestDbHelper object
     *
     * @param context provides Android access to the general information about the app, in this case
     * it is provided to the database
     */
    public static TestDbHelper getInstance(Context context) {
        
        // Use the application context, which will ensure that you 
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (dbInstance == null) {
        	dbInstance = new TestDbHelper(context.getApplicationContext());
        }
        return dbInstance;
      }

    private TestDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * Method that is run when the SQLite Database connection is first initiated
     *
     * @param db is the database to be created (if it doesn't already exist) or connected to
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // created the sql query of type string, only create the table if it doesn't already exist
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS + " ( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_ANSWERKEY
                + " INTEGER, " + KEY_TESTNAME + " TEXT, " + KEY_SCORES + " TEXT, " + KEY_EMAIL + " TEXT)";
        db.execSQL(sql);
    }

    /*
     * Method that is called when the data in the table needs to be updated
     *
     * @param db is the database that needs to be updated
     * @param oldV is the old version of the database
     * @param newV is the new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        // Create tables again
        onCreate(db);
    }

    /*
     * Method that is called when the user tries to add a new test to the database
     *
     * @param item is the item being added
     */
    public void addTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ANSWERKEY, test.isAnswerKey());
        values.put(KEY_TESTNAME, test.getName());
        values.put(KEY_SCORES, test.getScoresString());
        values.put(KEY_EMAIL, test.getEmail());

        // Inserting Row
        db.insert(TABLE_ITEMS, null, values);
        db.close(); // Closing database connection
    }

    /*
     * Method that is called when the user tries to remove an already existing item in the database
     *
     * @param item is the item being removed
     */
    public void removeTest(Test test) {
        // open the database for writing
        SQLiteDatabase db = this.getWritableDatabase();

        String testId= "" + test.getId();
        db.delete(TABLE_ITEMS, KEY_ID + "=" + testId, null);

        db.close();
    }

    /*
     * Method that gets all tests with the specified test name
     *
     * @return an ArrayList of tests with the specified name
     */
    public ArrayList<Test> getTestsSameName(String name) {

        String sql = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_TESTNAME + "=" + name;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<Test> testList = new ArrayList<Test>();

        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(cursor.getInt(0));
                test.setIsAnswerKey(cursor.getInt(1));
                test.setTestName(cursor.getString(2));
                test.setScores(cursor.getString(3));
                test.setEmail(cursor.getString(4));
                testList.add(test);
            } while (cursor.moveToNext());
        }
        return testList;
    }

    /*
     * Method that gets all tests with the specified test name AND is the answer key
     *
     * @return the Test object
     */
    public Test getTestAnswerKey(String name) {

        String sql = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_TESTNAME + "=" + name + " AND KEY_ANSWERKEY = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(cursor.getInt(0));
                test.setIsAnswerKey(cursor.getInt(1));
                test.setTestName(cursor.getString(2));
                test.setScores(cursor.getString(3));
                test.setEmail(cursor.getString(4));
                return test;
            } while (false);
        }
        return null;
    }

    /*
     * Method that gets all tests with the specified test name AND is NOT the answer key
     *
     * @return an ArrayList of test objects that are student responses
     */
    public ArrayList<Test> getTestStudents(String name) {

        String sql = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_TESTNAME + "=" + name + " AND KEY_ANSWERKEY = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<Test> testList = new ArrayList<Test>();

        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(cursor.getInt(0));
                test.setIsAnswerKey(cursor.getInt(1));
                test.setTestName(cursor.getString(2));
                test.setScores(cursor.getString(3));
                test.setEmail(cursor.getString(4));
                testList.add(test);
            } while (cursor.moveToNext());
        }
        return testList;
    }
    /*
     * Method that gets all test names
     *
     * @return an ArrayList of strings which are the names of all the tests
     */
    public ArrayList<String> getTestNames() {

        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + TABLE_ITEMS + ")";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<String> nameList = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            do {
                nameList.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        ArrayList cleanedNames = new ArrayList();

        HashSet hs = new HashSet();
        hs.addAll(cleanedNames);
        cleanedNames.clear();
        cleanedNames.addAll(hs);

        return cleanedNames;
    }
}
