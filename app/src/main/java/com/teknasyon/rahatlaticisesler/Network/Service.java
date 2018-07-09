package com.teknasyon.rahatlaticisesler.Network;

import com.teknasyon.rahatlaticisesler.Model.Categories;
import com.teknasyon.rahatlaticisesler.Model.Music;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Umut ADALI on 22.06.2018.
 */

    public interface Service {
        @GET("musics.html")
        Call<ArrayList<Music>> musics();

        @GET("categories.html")
        Call<ArrayList<Categories>> categories();
    }

