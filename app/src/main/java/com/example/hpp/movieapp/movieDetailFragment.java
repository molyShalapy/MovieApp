package com.example.hpp.movieapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hpp on 11/10/2015.
 */
public class movieDetailFragment extends Fragment {




    public static String movieId;
    public static String movieTitle;
    public static String movieOverview;
    public static String movieReleaseDate;
    public static String moviePosterPath;
    public static String movieVoteAverage;

    private static String videoJsonStr;
    private static String reviewJsonStr;
    public static SQLit SQLIT;
    ArrayList<MyFavorite> database = new ArrayList<>();

    Button favorite;
    TextView review;
    private static Boolean check;

    static final String Default_id = "id";

    public movieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        movieDetail.myData d = null;
        Bundle arguments = getArguments();
        if (arguments != null) {

            d = arguments.getParcelable(movieDetailFragment.Default_id);


        }
        if (d == null) {
            return null;
        }

        movieTitle = d.title;
        moviePosterPath = d.poster;
        movieReleaseDate = d.date;
        movieVoteAverage = d.vote;
        movieOverview = d.overView;
        movieId = d.movieId;

        View rootView = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        TextView title = (TextView) rootView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView2);
        TextView date = (TextView) rootView.findViewById(R.id.date);
        TextView vote = (TextView) rootView.findViewById(R.id.vote);
        TextView overView = (TextView) rootView.findViewById(R.id.overview);
        review = (TextView) rootView.findViewById(R.id.review);


        title.setText(movieTitle);


        Picasso.with(getActivity()).load(moviePosterPath).into(imageView);

        date.setText(movieReleaseDate);


        vote.setText(movieVoteAverage);

        overView.setText(movieOverview);


        Button trailer = (Button) rootView.findViewById(R.id.trailer);
        trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchVideoTask x = new fetchVideoTask();
                x.execute(movieId);
            }
        });
        favorite = (Button) rootView.findViewById(R.id.favorite);
        SQLIT = new SQLit(getActivity(), null, null, 1);
        database = SQLIT.databaseToString();
        check = false;
        for (int i = 0; i < database.size(); i++) {
            if (movieId.equals(database.get(i).movieId)) {
                favorite.setText("un favorite");
                check = true;
            }
        }

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (check == false) {
                    SQLIT.addMovie(movieId, movieTitle, movieOverview, movieReleaseDate,
                            moviePosterPath, movieVoteAverage);
                    Toast.makeText(getActivity(), "added to favorite ", Toast.LENGTH_SHORT).show();
                    favorite.setText("un favorite");
                    check = true;
                } else {
                    favorite.setText("favorite");
                    SQLIT.deleteMovie(movieId);
                    Toast.makeText(getActivity(), "remove from favorite ", Toast.LENGTH_SHORT).show();
                    check = false;
                }
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchReviewTask v = new fetchReviewTask();
        v.execute(movieId);
    }


    class fetchVideoTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = fetchVideoTask.class.getSimpleName();


        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String urlStr = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=50bce7edbac33ed4f4c7757ee875026d";

            try {

                URL url = new URL(urlStr);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                videoJsonStr = buffer.toString();
                Log.i("my vedio", videoJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private String getVideoKey(String jsonStr)
                throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);

            JSONArray movieArray = movieJson.getJSONArray("results");
            JSONObject movie = movieArray.getJSONObject(0);
            String key = movie.getString("key");
            return key;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (videoJsonStr != null) {

                try {
                    watchYoutubeVideo(getVideoKey(videoJsonStr));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(getActivity(), " no internet !! ", Toast.LENGTH_LONG).show();
            }
        }

        public void watchYoutubeVideo(String id) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + id));
                startActivity(intent);
            }
        }

    }


    class fetchReviewTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = fetchReviewTask.class.getSimpleName();


        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String urlStr = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=50bce7edbac33ed4f4c7757ee875026d";

            try {

                URL url = new URL(urlStr);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                if (urlConnection == null) {
                    return null;
                }
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                reviewJsonStr = buffer.toString();
                Log.i("hazem", reviewJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private String getReview(String jsonStr)
                throws JSONException {
            JSONObject movieJson = new JSONObject(jsonStr);

            JSONArray movieArray = movieJson.getJSONArray("results");
            JSONObject movie = movieArray.getJSONObject(0);
            String review = movie.getString("content");
            return review;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (reviewJsonStr != null) {
                try {

                    review.setText("Review : \n" + getReview(reviewJsonStr));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                if (MainActivity.Flage == false) {
                    review.setText("review : ");
                }

            }
        }
    }
}

