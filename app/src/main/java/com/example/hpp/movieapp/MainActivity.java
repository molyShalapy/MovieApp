package com.example.hpp.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements mainFragment.Callback {
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    public static boolean Flage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail) != null) {
            Flage = true;
             if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail, new movieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            Flage = false;
        }
    }

    @Override
    public void onItemSelected(movieDetail.myData data) {
        if (Flage) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(movieDetailFragment.Default_id, data);

            movieDetailFragment fragment = new movieDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, movieDetail.class);
            intent.putExtra(String.valueOf(R.string.id), data.movieId);
            intent.putExtra(String.valueOf(R.string.title), data.title);
            intent.putExtra(String.valueOf(R.string.poster), data.poster);
            intent.putExtra(String.valueOf(R.string.date), data.date);
            intent.putExtra(String.valueOf(R.string.vote), data.vote);
            intent.putExtra(String.valueOf(R.string.overView), data.overView);

            startActivity(intent);
        }

    }
}
