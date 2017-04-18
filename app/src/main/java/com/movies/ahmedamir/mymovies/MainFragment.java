package com.movies.ahmedamir.mymovies;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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

import com.google.gson.Gson;
import com.movies.ahmedamir.mymovies.models.JSONres;
import com.movies.ahmedamir.mymovies.models.Movie;
import com.movies.ahmedamir.mymovies.models.RealmMovie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private ImageAdapter imageAdapter;
    private GridView gridView;
    private Realm realm;
    private RealmAdapter realmAdapter;
    private static boolean fav;

    public MainFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null){
            getAllMovies();
        }


        this.setHasOptionsMenu(true);
    }



    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){

        inflater.inflate(R.menu.menu_movie,menu);

    }



    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        if(id==R.id.action_popular){

            editor.putString("sortBy","popular");
            editor.apply();
            fav=false;
            getAllMovies();
            return true;
        }
        if(id==R.id.action_top_rated){

            editor.putString("sortBy","top_rated");
            editor.apply();
            fav=false;
            getAllMovies();
            return true;
        }
        if(id==R.id.action_fav) {


            fillGridWithFavs();
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillGridWithFavs() {
        RealmConfiguration config = new RealmConfiguration.Builder(getActivity()).build();
        realm = Realm.getInstance(config);
        fav = true;
        RealmResults<RealmMovie> results = realm.where(RealmMovie.class).findAll();
        realmAdapter = new RealmAdapter(getActivity(),results);
        gridView.setAdapter(realmAdapter);
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void getAllMovies(){
        if(!isOnline()){
            Toast toast = Toast.makeText(getActivity(), "No Internet Connection, try again later", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        FetchAllMoviesTask task = new FetchAllMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy =  prefs.getString("sortBy","popular");
        task.execute(sortBy);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        gridView = (GridView) rootView.findViewById(R.id.gridView);


        imageAdapter = new ImageAdapter(getActivity(),new ArrayList<Movie>());
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie m;
                Intent intent = new Intent(getActivity(),MovieActivity.class);
                if(fav==false)
                m = imageAdapter.getItem(i);
                else{
                    RealmMovie rm = realmAdapter.getItem(i);
                    m = new Movie();
                    m.setTitle(rm.getTitle());
                    m.setId(rm.getId());
                    m.setOverview(rm.getOverview());
                    m.setRelease_date(rm.getRelease_date());
                    m.setVote_average(rm.getVote_average());
                    intent.putExtra("image",rm.getImage());
                }

                intent.putExtra("movie",m);
                ((MainActivity)getActivity()).checkTwoPane(intent);

            }
        });

        if(savedInstanceState!=null) {
            int index = savedInstanceState.getInt("Position");
            ArrayList<Movie> data = savedInstanceState.getParcelableArrayList("DataSet");
            if(data==null){
                fillGridWithFavs();
                return rootView;
            }

            imageAdapter = new ImageAdapter(getActivity(), data);
            gridView.setAdapter(imageAdapter);
            gridView.smoothScrollToPosition(index);
        }

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        gridView =(GridView) getActivity().findViewById(R.id.gridView);

        int index = gridView.getFirstVisiblePosition();
        if(fav){
            super.onSaveInstanceState(outState);
            return;
        }
        outState.putParcelableArrayList("DataSet",((ImageAdapter)gridView.getAdapter()).getmGridData());
        outState.putInt("Position",index);
        super.onSaveInstanceState(outState);
    }



    public class FetchAllMoviesTask extends AsyncTask<String,Void,Movie[]>{

        @Override
        protected Movie[] doInBackground(String... strings) {
            if (strings.length == 0)
                return null;


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JSON = null;
            try {

                urlConnection = null;
                reader = null;

                final String base_url = "http://api.themoviedb.org/3/movie/";

                Uri uri = Uri.parse(base_url + strings[0] + "?").buildUpon().appendQueryParameter("api_key", "37f2f2d4389babe127aae52ce0c20940").build();

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
                Log.e(this.getClass().getSimpleName(), "Didnt get Data");
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
            JSONres r = gson.fromJson(JSON,JSONres.class);
            Movie[] movies = r.getResults();
            return movies;

        }

        @Override
        protected void onPostExecute(Movie[] movies) {

            if(movies!=null){
                imageAdapter.clear();
                imageAdapter.addAll(movies);
                gridView.setAdapter(imageAdapter);
            }
            else{
                Log.e(this.getClass().getSimpleName(),"Movies are null");
            }

        }
    }

}
