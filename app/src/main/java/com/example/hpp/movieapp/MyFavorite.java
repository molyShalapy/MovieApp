package com.example.hpp.movieapp;

/**
 * Created by hpp on 5/10/2015.
 */
public class MyFavorite {
    public   String movieId ;
    public   String movieTitle;
    public   String movieOverview ;
    public   String movieReleaseDate ;
    public   String moviePosterPath ;
    public   String movieVoteAverage;
    public MyFavorite(String movieId, String movieTitle, String movieOverview,
                      String movieReleaseDate, String moviePosterPath, String movieVoteAverage){
        this.movieId=movieId;
        this.movieTitle=movieTitle;
        this.movieOverview=movieOverview;
        this.movieReleaseDate=movieReleaseDate;
        this.moviePosterPath=moviePosterPath;
        this.movieVoteAverage=movieVoteAverage;
    }
}
