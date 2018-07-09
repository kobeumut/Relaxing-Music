package com.teknasyon.rahatlaticisesler.Controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.teknasyon.rahatlaticisesler.Adapter.CategoryAdapter;
import com.teknasyon.rahatlaticisesler.CustomAppCompatActivity;
import com.teknasyon.rahatlaticisesler.Model.Categories;
import com.teknasyon.rahatlaticisesler.Network.RetrofitClient;
import com.teknasyon.rahatlaticisesler.Network.Service;
import com.teknasyon.rahatlaticisesler.R;
import com.teknasyon.rahatlaticisesler.Util.BlurTransformation;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends CustomAppCompatActivity {

    private ArrayList<Categories> data;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_library:

                    return true;
                case R.id.navigation_favorite:
                    Intent intent = new Intent(LibraryActivity.this, MusicActivity.class);
                    intent.putExtra("category", -1);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Paper.init(this);
        Bitmap blurredBitmap = BlurTransformation.blur( this, R.drawable.background);
        ImageView background_view = findViewById(R.id.background);
        background_view.setImageBitmap(blurredBitmap);
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CategoryAdapter(data);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        adapter.setEmptyView(R.layout.loading_view,(ViewGroup) recyclerView.getParent());



        Service service = RetrofitClient.getClient().create(Service.class);
        service.categories().enqueue(new Callback<ArrayList<Categories>>() {
            @Override
            public void onResponse(Call<ArrayList<Categories>> call, Response<ArrayList<Categories>> response) {
                data = response.body();

                setData();


            }

            @Override
            public void onFailure(Call<ArrayList<Categories>> call, Throwable t) {

            }
        });


        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_library);


    }

    private void setData() {

        adapter.setNewData(data);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                Intent intent = new Intent(LibraryActivity.this, MusicActivity.class);
                intent.putExtra("category", data.get(position).getId());
                startActivity(intent);

            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigation.setSelectedItemId(R.id.navigation_library); //Eğer kütüphane ekranından favorilere geçiş olursa ve bu ekrana geri dönülürse tekrar kitaplık ikonunun seçili olması için eklendi.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
