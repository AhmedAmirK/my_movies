package com.movies.ahmedamir.mymovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.movies.ahmedamir.mymovies.models.Trailer;

import java.util.ArrayList;

/**
 * Created by Ahmed Amir on 9/4/2016.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer> {

    private Context context;
    private ArrayList<Trailer> trailers;

    public TrailersAdapter(Context c, ArrayList<Trailer> trailers){
        super(c,0,trailers);
        context = c;
        this.trailers = trailers;

    }

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {

            view = LayoutInflater.from(context).inflate(R.layout.trailer_item,parent,false);

        } else {
            view=  convertView;
        }
        Trailer item = trailers.get(position);
        ((TextView)view.findViewById(R.id.trailer_name)).setText(item.getName());
        return view;
    }
}
