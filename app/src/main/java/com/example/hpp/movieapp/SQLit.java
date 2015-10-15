package com.example.hpp.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hpp on 11/10/2015.
 */
public class SQLit extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION =10;
    private static final String DATABASE_NAME = "MovieDB.db";
    public static final String TABLE_PRODUCTS = "movies";
    public static final String movieId = "movieId";
    public static final String movieTitle = "movieTitle";
    public static final String movieOverview = "movieOverview";
    public static final String movieReleaseDate = "movieReleaseDate";
    public static final String moviePosterPath = "moviePosterPath";
    public static final String movieVoteAverage = "movieVoteAverage";
    private Context c ;

    public SQLit(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.c=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                movieId + " INTEGER PRIMARY KEY , " +
                movieTitle+ " TEXT ,"+movieOverview+ " TEXT ,"+
                movieReleaseDate+ " TEXT ,"+moviePosterPath+ " TEXT ,"+movieVoteAverage+ " TEXT "+");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addMovie(String id, String title, String overview, String releaseDate, String posterPath, String voteAverage) {

        ContentValues values = new ContentValues();
        values.put(movieId, Integer.parseInt(id.trim()));
        values.put(movieTitle, title);
        values.put(movieOverview, overview);
        values.put(movieReleaseDate, releaseDate);
        values.put(moviePosterPath, posterPath);
        values.put(movieVoteAverage, voteAverage);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        Log.i("my", "add " + values.toString());
        db.close();
    }

    // to delete from dataBase
    public void deleteMovie(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + movieId + "=\"" + id + "\";");
        Log.i("my", "delete " + id);
    }

    // to get all movies from DB

    public ArrayList<MyFavorite> databaseToString(){
        ArrayList<MyFavorite> Ids=new ArrayList<>();


        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("movieId")) != null) {
                Ids.add(new MyFavorite(c.getString(c.getColumnIndex(movieId)) ,
                        c.getString(c.getColumnIndex(movieTitle)), c.getString(c.getColumnIndex(movieOverview)),
                        c.getString(c.getColumnIndex(movieReleaseDate)) ,c.getString(c.getColumnIndex(moviePosterPath)),
                        c.getString(c.getColumnIndex(movieVoteAverage))   )  );

                Log.i("hazem","hte movie ====>>>>> "+c.getString(c.getColumnIndex(movieId)));

            }
            c.moveToNext();
        }
        db.close();
        return Ids;
    }

}
