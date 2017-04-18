package com.movies.ahmedamir.mymovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;

 interface Callback {
    public void checkTwoPane(Intent i);
}

public class MainActivity extends AppCompatActivity implements Callback{

    private boolean TwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.moviecontainer) != null) {
            TwoPane=true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.moviecontainer, new MovieFragment())
                        .commit();
            }

        }else TwoPane=false;

    }

    public void checkTwoPane(Intent intent){
        if(TwoPane){
            this.setIntent(intent);
            getSupportFragmentManager().beginTransaction().replace(R.id.moviecontainer,new MovieFragment()).commit();

        }
        else startActivity(intent);
    }
}
