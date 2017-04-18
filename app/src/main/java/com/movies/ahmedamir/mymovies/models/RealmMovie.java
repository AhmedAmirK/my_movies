package com.movies.ahmedamir.mymovies.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ahmed Amir on 9/12/2016.
 */
public class RealmMovie extends RealmObject {
    private byte[] image;
    private String overview;

    private String release_date;

    @PrimaryKey
    private int id;

    private String title;


    private double vote_average;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] Image) {
        this.image =Image ;
    }




    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }
}
