package com.movies.ahmedamir.mymovies.models;

import com.movies.ahmedamir.mymovies.models.Trailer;

/**
 * Created by Ahmed Amir on 9/4/2016.
 */
public class TrailersJson {
    private int id;
    private Trailer[] results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Trailer[] getResults() {
        return results;
    }

    public void setResults(Trailer[] results) {
        this.results = results;
    }
}
