package com.movies.ahmedamir.mymovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.movies.ahmedamir.mymovies.models.Review;

import java.util.ArrayList;

/**
 * Created by Ahmed Amir on 9/5/2016.
 */
public class ReviewsAdapter extends ArrayAdapter<Review> {

    Context context;
    ArrayList<Review> reviews;

    public ReviewsAdapter(Context c, ArrayList<Review> r){
        super(c,0,r);
        context=c;
        reviews=r;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view;
        if (convertView == null) {

            view = LayoutInflater.from(context).inflate(R.layout.review_item,parent,false);

        } else {
            view=  convertView;
        }
        Review item = reviews.get(position);
        ((TextView)view.findViewById(R.id.review_content)).setText(item.getContent()+"...");
        ((TextView)view.findViewById(R.id.review_author)).setText(item.getAuthor());
        return view;
    }

}
