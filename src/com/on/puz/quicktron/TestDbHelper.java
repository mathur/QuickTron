package com.on.puz.quicktron;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
                + " INTEGER, " + KEY_TESTNAME + " TEXT, " + KEY_SCORES + " TEXT)";
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

        String testId = "" + test.getId();
        db.delete(TABLE_ITEMS, KEY_ID + "=" + testId, null);

        db.close();
    }

    /*
     * Method that gets a test with the specified ID
     *
     * @return a test object with the contents of the test entry
     */
    public Test getTest(int id) {

        String sql = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_ID + "=" + id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(cursor.getInt(0));
                test.setIsAnswerKey(cursor.getInt(1));
                test.setTestName(cursor.getString(2));
                test.setScores(cursor.getString(3));
                return test;
            } while (cursor.moveToNext());
        } else {
            return null;
        }
    }

    /*
     * Method that gets all the items from the database and returns it in an ArrayList
     *
     * @return an ArrayList with objects of type item ordered by status
     */
    public ArrayList<Test> getAllTests() {
        ArrayList<Test> testList = new ArrayList<Test>();
        // Select all query with ordering
        String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " ORDER BY " + KEY_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Test test = new Test();
                test.setId(cursor.getInt(0));
                test.setIsAnswerKey(cursor.getInt(1));
                test.setTestName(cursor.getString(2));
                test.setScores(cursor.getString(3));
                // adding item to list
                testList.add(test);
            } while (cursor.moveToNext());
        }


        // return the now filled test list
        return testList;
    }
}
