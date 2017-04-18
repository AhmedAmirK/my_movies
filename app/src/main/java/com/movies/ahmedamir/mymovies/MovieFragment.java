package com.movies.ahmedamir.mymovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.movies.ahmedamir.mymovies.models.Movie;
import com.movies.ahmedamir.mymovies.models.RealmMovie;
import com.movies.ahmedamir.mymovies.models.Review;
import com.movies.ahmedamir.mymovies.models.ReviewsJson;
import com.movies.ahmedamir.mymovies.models.Trailer;
import com.movies.ahmedamir.mymovies.models.TrailersJson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Ahmed Amir on 9/4/2016.
 */

public class MovieFragment extends Fragment {

    private Movie m = null;
    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;
    private ListView mtrailers;
    private ListView mreviews;
    private Realm realm;
    private Intent intent;
    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();

        if (intent != null && intent.getExtras() != null && intent.getExtras().getParcelable("movie") != null) {
            m = intent.getExtras().getParcelable("movie");
            getTrailersAndReviews();
        } else Log.e(getClass().getSimpleName(), "Intent is NULL!");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RealmConfiguration config = new RealmConfiguration.Builder(getActivity()).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getInstance(config);

        if(intent==null || m==null) {
            return null;
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);



        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(m.getTitle());

        mtrailers = (ListView) rootView.findViewById(R.id.trailers);
        trailersAdapter = new TrailersAdapter(getActivity(), new ArrayList<Trailer>());
        mtrailers.setAdapter(trailersAdapter);
        mtrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Trailer t = trailersAdapter.getItem(i);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + t.getKey()));
                startActivity(intent);

            }
        });

        mreviews = (ListView) rootView.findViewById(R.id.reviews);
        reviewsAdapter = new ReviewsAdapter(getActivity(), new ArrayList<Review>());
        mreviews.setAdapter(reviewsAdapter);
        mreviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Review r = reviewsAdapter.getItem(i);

                Intent browserintent = new Intent(Intent.ACTION_VIEW, Uri.parse(r.getUrl()));
                startActivity(browserintent);
            }
        });

        TextView overview = (TextView) rootView.findViewById(R.id.overview);
        overview.setText(m.getOverview());


        TextView release_date = (TextView) rootView.findViewById(R.id.textView3);
        release_date.setText(m.getRelease_date());


        final ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
        if(intent.hasExtra("image")){
            byte[] bytes = (byte[])intent.getExtras().get("image");
            Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            poster.setImageBitmap(map);

        }
        else Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + m.getPoster_path()).into(poster);

        TextView score = (TextView) rootView.findViewById(R.id.textView6);
        score.setText("" + m.getVote_average());

        ImageButton imageButton = (ImageButton) rootView.findViewById(R.id.star);
        if(intent.hasExtra("image")){
            imageButton.setVisibility(View.GONE);
            (rootView.findViewById(R.id.trailers_text)).setVisibility(View.GONE);
            (rootView.findViewById(R.id.reviews_text)).setVisibility(View.GONE);
            mtrailers.setVisibility(View.GONE);
            mreviews.setVisibility(View.GONE);
        }


        final RealmMovie r = realm.where(RealmMovie.class).equalTo("id",m.getId()).findFirst();
        if(r!=null){
            imageButton.setImageResource(R.mipmap.ic_star_full);
            imageButton.setTag(true);
        }
        else imageButton.setTag(false);
        assert imageButton != null;

        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if((Boolean)view.getTag()){
                    RealmMovie r = realm.where(RealmMovie.class).equalTo("id",m.getId()).findFirst();
                    realm.beginTransaction();
                    r.deleteFromRealm();
                    realm.commitTransaction();
                    Toast.makeText(getActivity(),"Removed From Favorites",Toast.LENGTH_SHORT).show();
                    view.setTag(false);
                    ((ImageButton) view).setImageResource(R.mipmap.ic_star_holo);
                }
                else {

                    realm.beginTransaction();
                    RealmMovie movie = realm.createObject(RealmMovie.class);
                    movie.setId(m.getId());
                    movie.setOverview(m.getOverview());
                    movie.setRelease_date(m.getRelease_date());
                    movie.setTitle(m.getTitle());
                    movie.setVote_average(m.getVote_average());
                    Bitmap image = ((BitmapDrawable)poster.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG,100,stream);
                    byte[] arr = stream.toByteArray();
                    movie.setImage(arr);
                    realm.commitTransaction();
                    Toast.makeText(getActivity(),"Added to Favorites",Toast.LENGTH_SHORT).show();
                    view.setTag(true);
                    ((ImageButton) view).setImageResource(R.mipmap.ic_star_full);
                }
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        realm.close();
        super.onPause();
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public void getTrailersAndReviews() {

        if(intent.hasExtra("image"))
            return;
        FetchTrailersTask task = new FetchTrailersTask();
        task.execute(m.getId());
        FetchReviewsTask task1 = new FetchReviewsTask();
        task1.execute(m.getId());

    }

    public class FetchTrailersTask extends AsyncTask<Integer, Void, Trailer[]> {
        @Override
        protected Trailer[] doInBackground(Integer... integers) {
            if (integers.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSON = null;

            try {
                final String base_url = "http://api.themoviedb.org/3/movie/";


                Uri uri = Uri.parse(base_url + integers[0] + "/videos?").buildUpon().appendQueryParameter("api_key", "37f2f2d4389babe127aae52ce0c20940").build();
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                JSON = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(this.getClass().getSimpleName(), "Error closing stream", e);
                    }
                }
            }
            Gson gson = new Gson();
            TrailersJson r = gson.fromJson(JSON, TrailersJson.class);
            Trailer[] trailers = r.getResults();
            return trailers;
        }

        @Override
        protected void onPostExecute(Trailer[] trailers) {
            if (trailers != null) {
                trailersAdapter.clear();
                trailersAdapter.addAll(trailers);
                setListViewHeightBasedOnItems(mtrailers);

            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<Integer, Void, Review[]> {
        @Override
        protected Review[] doInBackground(Integer... integers) {
            if (integers.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSON = null;

            try {
                final String base_url = "http://api.themoviedb.org/3/movie/";


                Uri uri = Uri.parse(base_url + integers[0] + "/reviews?").buildUpon().appendQueryParameter("api_key", "37f2f2d4389babe127aae52ce0c20940").build();
                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                JSON = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(this.getClass().getSimpleName(), "Error closing stream", e);
                    }
                }
            }
            Gson gson = new Gson();
            ReviewsJson r = gson.fromJson(JSON, ReviewsJson.class);
            Review[] reviews = r.getResults();
            return reviews;
        }

        @Override
        protected void onPostExecute(Review[] reviews) {
            if (reviews != null) {
                reviewsAdapter.clear();
                reviewsAdapter.addAll(reviews);
                setListViewHeightBasedOnItems(mreviews);
            }
        }

    }
}