package com.example.hpp.movieapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
 * Created by hpp on 1/10/2015.
 */
public class mainFragment extends Fragment {



    static String[] movieId,moviePosterPath,movieTitle,movieReleaseDate,movieVoteAverage,movieOverview;
    private static final String my_key = "50bce7edbac33ed4f4c7757ee875026d";
    private static String movieJsonStr = null;

    MovieAdapter MovieAdapter;
    public static GridView gridView;
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */


        public void onItemSelected(movieDetail.myData data);
    }

    public mainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.grid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.rating) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.pref_key),"vote_count");
            editor.apply();
            updateMovie();
            return true;

        }
        if (id == R.id.popular) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.pref_key),"POPULARITY");
            editor.apply();
            updateMovie();
            return true;

        }
        if (id == R.id.favorite) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.pref_key),"Favorite");
            editor.apply();
            favorite_movie();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort = sharedPrefs.getString(
                getString(R.string.pref_key),
                getString(R.string.pref_popular_default));
        if (sort.equals("Favorite")){
            favorite_movie();

        }else{
            updateMovie();

        }

    }


    public void favorite_movie(){

        ArrayList<MyFavorite> database=new ArrayList<>();

        SQLit getData = new SQLit(getActivity(),null,null,1);


        database = getData.databaseToString();
        if (database.size() >= 0) {

            movieId = new String[database.size()];
            movieTitle = new String[database.size()];
            movieOverview = new String[database.size()];
            movieReleaseDate = new String[database.size()];
            moviePosterPath = new String[database.size()];
            movieVoteAverage = new String[database.size()];
            for (int i = 0; i < database.size(); i++) {
                movieId[i] = database.get(i).movieId;
                movieTitle[i] = database.get(i).movieTitle;
                movieOverview[i] = database.get(i).movieOverview;
                movieReleaseDate[i] = database.get(i).movieReleaseDate;
                moviePosterPath[i] = database.get(i).moviePosterPath;
                movieVoteAverage[i] = database.get(i).movieVoteAverage;


            }

            if (MainActivity.Flage) {
                movieDetail.myData d = new movieDetail.myData(movieId[0], movieTitle[0], moviePosterPath[0],
                        movieReleaseDate[0], movieVoteAverage[0],
                        movieOverview[0]);
                ((Callback) getActivity()).onItemSelected(d);
            }

            MovieAdapter = new MovieAdapter(getActivity(), moviePosterPath);
            gridView.setAdapter(MovieAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  movieDetail.myData d = new movieDetail.myData(movieId[position], movieTitle[position], moviePosterPath[position],
                            movieReleaseDate[position], movieVoteAverage[position],
                            movieOverview[position]);
                    ((Callback) getActivity()).onItemSelected(d);

                }
            });
        }else{
           Toast.makeText(getActivity(), "there is no favorite movie", Toast.LENGTH_LONG).show();
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_fragment, container, false);


        gridView= (GridView) rootView.findViewById(R.id.gridView);

        return rootView;
    }







    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sorting = prefs.getString(getString(R.string.pref_key),
                getString(R.string.pref_popular_default));

        movieTask.execute("POPULARITY");
    }

    class FetchMovieTask extends AsyncTask<String, Void, Void> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private ProgressDialog progressdialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String title = "Loading....";
            String message = "data loading";

//            progressdialog = ProgressDialog.show(getActivity(), title, message);

        }

        @Override
        protected Void doInBackground(String... params) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
            String SORT_PARAM = "sort_by";
            String API_PARAM = "api_key";
            String sortParam = params[0] + ".desc";



            try {

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sortParam)
                        .appendQueryParameter(API_PARAM, my_key)
                        .build();

                URL url = new URL(uri.toString());

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
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v("haaaaaaay ^_^", "that's my data" + movieJsonStr);
                getMovieDataFromJson(movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
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

        private void getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {


            JSONObject movieJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");


            movieId = new String[movieArray.length()];
            movieTitle = new String[movieArray.length()];
            movieOverview = new String[movieArray.length()];
            movieReleaseDate = new String[movieArray.length()];
            moviePosterPath = new String[movieArray.length()];
            movieVoteAverage = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movie = movieArray.getJSONObject(i);

                movieId[i] = movie.getString("id");
                movieTitle[i] = movie.getString("original_title");
                movieOverview[i] = movie.getString("overview");
                movieReleaseDate[i] = movie.getString("release_date");
                moviePosterPath[i] = "http://image.tmdb.org/t/p/w500" + movie.getString("poster_path");
                movieVoteAverage[i] = movie.getString("vote_average");

            }
            if (MainActivity.Flage) {
                movieDetail.myData d = new movieDetail.myData(movieId[0], movieTitle[0], moviePosterPath[0],
                        movieReleaseDate[0], movieVoteAverage[0],
                        movieOverview[0]);
                ((Callback) getActivity()).onItemSelected(d);
            }


        }


        @Override
        protected void onPostExecute(Void aVoid) {
            if (movieJsonStr !=null) {
                MovieAdapter = new MovieAdapter(getActivity(), moviePosterPath);
                gridView.setAdapter(MovieAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        movieDetail.myData  d = new movieDetail.myData(movieId[position], movieTitle[position], moviePosterPath[position],
                                movieReleaseDate[position], movieVoteAverage[position],
                                movieOverview[position]);
                        ((Callback) getActivity()).onItemSelected(d);

                    }
                });
            }else {
                Toast.makeText(getActivity(), " no internet !! ", Toast.LENGTH_LONG).show();
            }
//            progressdialog.dismiss();
        }
    }




}
