package com.example.hpp.movieapp;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hpp on 5/10/2015.
 */
public class movieDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle  data = getIntent().getExtras();
            Bundle arguments = new Bundle();


            String movieId = data.getString(String.valueOf(R.string.id));
            String title = data.getString(String.valueOf(R.string.title));
            String poster = data.getString(String.valueOf(R.string.poster));
            String date = data.getString(String.valueOf(R.string.date));
            String vote = data.getString(String.valueOf(R.string.vote));
            String overView = data.getString(String.valueOf(R.string.overView));


            myData v =new myData(movieId,title,poster,date,vote,overView);

            arguments.putParcelable(movieDetailFragment.Default_id,  v);

            movieDetailFragment fragment = new movieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail, fragment).commit();
        }
    }

    static class myData implements Parcelable {

        String movieId;
        String title ;
        String poster;
        String date ;
        String vote ;
        String overView;

        public myData(String movieId,String title ,String poster,String date ,String vote ,String overView ){
            this.movieId=movieId;
            this.title=title;
            this.poster=poster;
            this.date=date;
            this.vote=vote;
            this.overView=overView;

        }

        protected myData(Parcel in) {
            movieId = in.readString();
            title = in.readString();
            poster = in.readString();
            date = in.readString();
            vote = in.readString();
            overView = in.readString();
        }

        public final Creator<myData> CREATOR = new Creator<myData>() {
            @Override
            public myData createFromParcel(Parcel in) {
                return new myData(in);
            }

            @Override
            public myData[] newArray(int size) {
                return new myData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(movieId);
            dest.writeString(title);
            dest.writeString(poster);
            dest.writeString(date);
            dest.writeString(vote);
            dest.writeString(overView);
        }
    }
}
