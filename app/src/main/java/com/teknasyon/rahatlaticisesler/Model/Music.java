package com.teknasyon.rahatlaticisesler.Model;

import android.media.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by Umut ADALI on 22.06.2018.
 */

public class Music {
    private Integer id;
    private String title;
    private Integer category;
    private MediaPlayer mp = new MediaPlayer();
    private Integer pauseTime = 0;
    private boolean clickedForStart = false;
    private boolean favorite = false;
    private Float volume = 1.0f;
    private String URL;

    public Music(Integer id, String title, Integer category, MediaPlayer mp, String URL, Boolean favorite) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.mp = mp;
        this.favorite = favorite;
        this.URL = URL;
    }
    public Music (){

    }
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
        mp.setVolume(volume,volume);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isClickedForStart() {
        return clickedForStart;
    }

    public void setClickedForStart(boolean clickedForStart) {
        this.clickedForStart = clickedForStart;
    }

    public Integer getPauseTime() {
        return pauseTime;
    }

    /**
     * Thats second
     * @param pauseTime is Second
     */
    public void setPauseTime(Integer pauseTime) {
        this.pauseTime = pauseTime;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public void setMp(MediaPlayer mp) {
        this.mp = mp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

}