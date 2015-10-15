package com.example.hpp.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hpp on 1/10/2015.
 */
public class MovieAdapter extends BaseAdapter {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Context context;
    private final List<String> urls = new ArrayList<String>();
    private static LayoutInflater inflater=null;
    private static boolean TAG;
    public MovieAdapter(Context context,String [] path) {
        this.context = context;

        if(path==null){
            TAG=true;
        }else {
            Collections.addAll(urls, path);
        }
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecordHolder holder = null;

        if (convertView == null) {
            holder = new RecordHolder();
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
            holder.imageItem= (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);

        }else {
            holder = (RecordHolder) convertView.getTag();


        }

        if (TAG){
            holder.imageItem.setImageResource(R.drawable.ic_launcher);

        }else {
            String url = getItem(position);

            Log.e(LOG_TAG, " URL " + url);

            Picasso.with(context).load(url).into(holder.imageItem);
        }


        return convertView;
    }

    @Override
    public int getCount() {
        if (TAG){
            return 0;
        }
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        if (TAG){
            return null;
        }
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class RecordHolder {
        ImageView imageItem;

    }
}
