package com.movies.ahmedamir.mymovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.movies.ahmedamir.mymovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Ahmed Amir on 8/13/2016.
 */
public class ImageAdapter extends ArrayAdapter<Movie>{

    private Context mContext;
    private ArrayList<Movie> mGridData = new ArrayList<Movie>();

    public ImageAdapter(Context mContext, ArrayList<Movie> mGridData) {
        super(mContext, 0, mGridData);
        this.mContext = mContext;

        this.mGridData = mGridData;
    }

    public ArrayList<Movie> getmGridData() {
        return mGridData;
    }

    public void setmGridData(ArrayList<Movie> data){
        mGridData = data;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View imageView;
        if (convertView == null) {

            imageView = LayoutInflater.from(mContext).inflate(R.layout.movie_image,parent,false);

        } else {
             imageView=  convertView;
        }
        Movie item = mGridData.get(position);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+item.getPoster_path()).into((ImageView)imageView);
        return imageView;
    }

}






