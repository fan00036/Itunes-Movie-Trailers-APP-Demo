package com.algonquincollege.fan00036.itunesmovietrailers.persistence;

/**
 * Created by helen on 2015-12-04.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.algonquincollege.fan00036.itunesmovietrailers.domain.MovieItem;

import java.util.ArrayList;
import java.util.List;

import static com.algonquincollege.fan00036.itunesmovietrailers.Constants.LOG_TAG;

//TODO: #Lab Persistence
/**
 * Purpose: this class is the DAO (data access object).
 *
 * It maintains the database connection and supports CRUD data operations.
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: http://www.vogella.com/tutorials/AndroidSQLite/article.html
 */
public class MovieListDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MovieListDBHelper dbHelper;
    private String[] allColumns = {
            MovieListDBHelper.COLUMN_ID
            , MovieListDBHelper.COLUMN_TITLE
            , MovieListDBHelper.COLUMN_LINK
            , MovieListDBHelper.COLUMN_DESCRTIPTON
            , MovieListDBHelper.COLUMN_PUBDATE
    };

    public MovieListDataSource( Context context ) {
        dbHelper = new MovieListDBHelper( context );
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MovieItem createMovieItem(String title, String link, String description, String pubDate) {
        ContentValues values = new ContentValues();
        values.put(MovieListDBHelper.COLUMN_TITLE, title);
        values.put(MovieListDBHelper.COLUMN_LINK, link);
        values.put(MovieListDBHelper.COLUMN_DESCRTIPTON, description);
        values.put(MovieListDBHelper.COLUMN_PUBDATE, pubDate);
        Log.i(LOG_TAG, "MovieItem inserted: " + title + "link: " + link);
        long insertId = database.insert(MovieListDBHelper.TABLE_MOVIE_LIST, null,
                values);
        Cursor cursor = database.query(MovieListDBHelper.TABLE_MOVIE_LIST,
                allColumns, MovieListDBHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        MovieItem newMovieITem = cursorToMovieItem(cursor);
        cursor.close();
        return newMovieITem;
    }

    public void deleteMovieItem(MovieItem aMovieItem) {
        long id = aMovieItem.getId();
        Log.i( LOG_TAG, "MovieItem deleted with id: " + id);
        database.delete(MovieListDBHelper.TABLE_MOVIE_LIST,
                MovieListDBHelper.COLUMN_ID + " = " + id, null);
    }

    public void removeAllMovieItems() {
        Log.i( LOG_TAG, "Remove all movie items");
        database.delete(MovieListDBHelper.TABLE_MOVIE_LIST,
                null, null);
    }

    public List<MovieItem> getAllMovieItems() {
        List<MovieItem> movieList = new ArrayList<MovieItem>();

        Cursor cursor = database.query(MovieListDBHelper.TABLE_MOVIE_LIST,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MovieItem movieItem = cursorToMovieItem(cursor);
            movieList.add(movieItem);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return movieList;
    }

    public MovieItem update(MovieItem movieItem) {
        long id = movieItem.getId();
        Log.i(LOG_TAG, "MovieItem updated with id: " + id);
        ContentValues values = new ContentValues();
        values.put(MovieListDBHelper.COLUMN_TITLE, movieItem.getTitle());
        values.put(MovieListDBHelper.COLUMN_LINK, movieItem.getLink());
        values.put(MovieListDBHelper.COLUMN_DESCRTIPTON, movieItem.getDescription());
        values.put(MovieListDBHelper.COLUMN_PUBDATE, movieItem.getPubDate().toString());
        database.update(MovieListDBHelper.TABLE_MOVIE_LIST, values,
                MovieListDBHelper.COLUMN_ID + " = " + id, null);
        Cursor cursor = database.query(MovieListDBHelper.TABLE_MOVIE_LIST,
                allColumns, MovieListDBHelper.COLUMN_ID + " = " + id, null, null,
                null, null);
        cursor.moveToFirst();
        MovieItem updateMovieItem = cursorToMovieItem(cursor);
        cursor.close();
        return updateMovieItem;
    }

    private MovieItem cursorToMovieItem(Cursor cursor) {
        MovieItem movieItem = new MovieItem();
        movieItem.setId(cursor.getLong(0));
        movieItem.setTitle(cursor.getString(1));
        movieItem.setLink(cursor.getString(2));
        movieItem.setDescription(cursor.getString(3));
        movieItem.setPubDate(new java.util.Date(cursor.getString(4)));
        return movieItem;
    }
}

