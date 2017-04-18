package com.movies.ahmedamir.mymovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.movies.ahmedamir.mymovies.models.Movie;
import com.movies.ahmedamir.mymovies.models.RealmMovie;
import com.squareup.picasso.Picasso;

import io.realm.RealmResults;

/**
 * Created by Ahmed Amir on 9/15/2016.
 */
public class RealmAdapter extends ArrayAdapter<RealmMovie>{

    private Context context;
    private RealmResults<RealmMovie> data;

    public RealmAdapter(Context c , RealmResults<RealmMovie> r){
        super(c,0,r);
        context = c;
        data=r;
    }

    public RealmResults<RealmMovie> getData(){
        return data;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View imageView;
        if (convertView == null) {

            imageView = LayoutInflater.from(context).inflate(R.layout.movie_image,parent,false);

        } else {
            imageView=  convertView;
        }
        RealmMovie item = data.get(position);
        byte[] bytes =item.getImage();
        Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        ((ImageView)imageView).setImageBitmap(map);
        return imageView;
    }
}
