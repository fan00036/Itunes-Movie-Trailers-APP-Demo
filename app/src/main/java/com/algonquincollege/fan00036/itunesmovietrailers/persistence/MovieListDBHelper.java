package com.algonquincollege.fan00036.itunesmovietrailers.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by helen on 2015-12-04.
 */
//TODO: #Lab Persistence
/**
 * Purpose:
 *   a) create the database
 *   b) the onUpgrade( ) method will simply delete all existing data and re-create the table
 *   c) define several constants for the table name and the table columns.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
public class MovieListDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_MOVIE_LIST = "itunes_movie_list";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESCRTIPTON = "description";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_PUBDATE = "pubdate";
    public static final String COLUMN_TITLE = "title";

    private static final String DATABASE_NAME = "movielist.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_MOVIE_LIST + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " varchar(100) not null, "
            + COLUMN_LINK + " varchar(100) not null, "
            + COLUMN_DESCRTIPTON + " varchar(1000) not null, "
            + COLUMN_PUBDATE + " varchar(50) not null"
            + ");";

    /**
     * Construct a new instance of MovieListDBHelper for the given context.
     *
     * @param context
     */
    public MovieListDBHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( DATABASE_CREATE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w( MovieListDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data" );
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_MOVIE_LIST );
        onCreate( db );
    }
}
