package com.teknasyon.rahatlaticisesler.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.teknasyon.rahatlaticisesler.Adapter.BottomBarAdapter;
import com.teknasyon.rahatlaticisesler.Adapter.CategoryAdapter;
import com.teknasyon.rahatlaticisesler.Controller.MainActivity;
import com.teknasyon.rahatlaticisesler.Model.Categories;
import com.teknasyon.rahatlaticisesler.Network.RetrofitClient;
import com.teknasyon.rahatlaticisesler.Network.Service;
import com.teknasyon.rahatlaticisesler.R;

import java.util.ArrayList;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Umut ADALI on 25.06.2018.
 */

public class LibraryFragment extends Fragment{
    public static final String TAG = LibraryFragment.class.getSimpleName();
    private Integer categoryID = 1;
    private ArrayList<Categories> data;
    private CategoryAdapter adapter;
    private RecyclerView recyclerView;
    private View notDataView;
    private Integer favoriCategoryID = -1;
    private static final String ARG_COLOR = "color";
    private int color;


    public LibraryFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            color = getArguments().getInt(ARG_COLOR);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_square, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_square_recycler);


        init();



        return rootView;
    }

    private void init() {
//        Bitmap blurredBitmap = BlurTransformation.blur( getContext(), R.drawable.background);
//        ImageView background_view = findViewById(R.id.background);
        categoryID = getActivity().getIntent().getIntExtra("category", -1);
//        background_view.setImageBitmap(blurredBitmap);        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setBackgroundColor(getLighterColor(color));
        adapter = new CategoryAdapter(data);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        adapter.setEmptyView(R.layout.loading_view, (ViewGroup) recyclerView.getParent());
        adapter.openLoadAnimation();
        Service service = RetrofitClient.getClient().create(Service.class);
        service.categories().enqueue(new Callback<ArrayList<Categories>>() {
            @Override
            public void onResponse(Call<ArrayList<Categories>> call, Response<ArrayList<Categories>> response) {
                if(response.isSuccessful()){
                    data = response.body();

                    setData();
                }else {
                    Toast.makeText(getContext(),"Servis ile alakalı sorun yaşanıyor...", Toast.LENGTH_SHORT).show();
                }



            }

            @Override
            public void onFailure(Call<ArrayList<Categories>> call, Throwable t) {
                Toast.makeText(getContext(),"Veri çekmede sorun yaşanıyor, bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {

        adapter.setNewData(data);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, final int position) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                MusicFragment musicFragment = new MusicFragment();
////                transaction.replace(R.id.viewpager,musicFragment);
//                Bundle args = new Bundle();
//                args.putInt("category", data.get(position).getId());
//                musicFragment.setArguments(args);
//                transaction.addToBackStack("library");
//                transaction.commit();

                ViewPager pager = view.getRootView().findViewById(R.id.viewpager);
                BottomBarAdapter pagerAdapter = new BottomBarAdapter(getFragmentManager());
                pagerAdapter.addFragments(createMusicFragment(R.color.bottomtab_1,data.get(position).getId()));
                pagerAdapter.addFragments(createMusicFragment(R.color.bottomtab_1,-1));

                pager.setAdapter(pagerAdapter);
                MainActivity.selectedFragment= "Music";

                pager.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

        adapter.notifyDataSetChanged();
    }
    @NonNull
    private LibraryFragment createFavoriteFragment(int color) {
        LibraryFragment fragment = new LibraryFragment();
        fragment.setArguments(passFragmentArguments(fetchColor(color),-2));
        return fragment;
    }
    @NonNull
    private MusicFragment createMusicFragment(int color,int categoryID){
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(passFragmentArguments(fetchColor(color),categoryID));

        return fragment;
    }
    @NonNull
    private Bundle passFragmentArguments(int color,int categoryID) {
        Bundle bundle = new Bundle();
        bundle.putInt("color", color);
        bundle.putInt("category", categoryID);
        return bundle;
    }
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(getContext(), color);
    }

    /**
     * Updates {@link RecyclerView} background color upon changing Bottom Navigation item.
     *
     * @param color to apply to {@link RecyclerView} background.
     */
    public void updateColor(int color) {
        recyclerView.setBackgroundColor(getLighterColor(color));
    }

    /**
     * Facade to return colors at 30% opacity.
     *
     * @param color
     * @return
     */
    private int getLighterColor(int color) {
        return Color.argb(30,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

}
