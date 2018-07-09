package com.teknasyon.rahatlaticisesler.Controller;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.teknasyon.rahatlaticisesler.Adapter.BottomBarAdapter;
import com.teknasyon.rahatlaticisesler.Fragments.LibraryFragment;
import com.teknasyon.rahatlaticisesler.Fragments.MusicFragment;
import com.teknasyon.rahatlaticisesler.Fragments.NoSwipePager;
import com.teknasyon.rahatlaticisesler.Model.Event;
import com.teknasyon.rahatlaticisesler.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.paperdb.Paper;


/**
 * Created by Umut ADALI on 25.06.2018.
 */


public class MainActivity extends AppCompatActivity {

    private final int[] colors = {R.color.bottomtab_0, R.color.bottomtab_1};

    public static String selectedFragment;
    private NoSwipePager viewPager;
    public static AHBottomNavigation bottomNavigation;
    private BottomBarAdapter pagerAdapter;
    private boolean notificationVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Paper.init(this);
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Rahatlatıcı Sesler");

        setupViewPager();

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        setupBottomNavBehaviors();
        setupBottomNavStyle();


        addBottomNavigationItems();
        bottomNavigation.setCurrentItem(0);


        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
//                fragment.updateColor(ContextCompat.getColor(MainActivity.this, colors[position]));
                if (position == 1) {
                    setupViewPager();
                }
                if (!wasSelected)
                    viewPager.setCurrentItem(position);

                return true;
            }
        });

        ArrayList<Integer> favoriteSongs = Paper.book().read("favoriteSongs");
        if (favoriteSongs != null && !favoriteSongs.isEmpty()){
            updateNavbarNotification(String.valueOf(favoriteSongs.size()));
        }

    }

    private void setupViewPager() {
        viewPager = (NoSwipePager) findViewById(R.id.viewpager);
        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(createFragment(R.color.bottomtab_0));
        pagerAdapter.addFragments(createMusicFragment(R.color.bottomtab_1, -1));

        viewPager.setAdapter(pagerAdapter);
    }


    @NonNull
    private LibraryFragment createFragment(int color) {
        LibraryFragment libraryFragment = new LibraryFragment();
        libraryFragment.setArguments(passFragmentArguments(fetchColor(color), -2));
        return libraryFragment;
    }

    @NonNull
    private MusicFragment createMusicFragment(int color, int categoryID) {
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(passFragmentArguments(fetchColor(color), categoryID));
        return fragment;
    }

    @NonNull
    private Bundle passFragmentArguments(int color, int categoryID) {
        Bundle bundle = new Bundle();
        bundle.putInt("color", color);
        bundle.putInt("category", categoryID);
        return bundle;
    }

    @Subscribe //Eventbus ile navbar favori badge sayısı arttırılıyor
    public void onNotifyData(Event e) {
        if (e.where.equals("navbarNotify")) {
            updateNavbarNotification(e.message);
        }
    }

    private void updateNavbarNotification(final String count) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AHNotification notification = new AHNotification.Builder()
                        .setText(count)
                        .setBackgroundColor(Color.YELLOW)
                        .setTextColor(Color.BLACK)
                        .build();
                // Adding notification to last item.

                bottomNavigation.setNotification(notification, bottomNavigation.getItemsCount() - 1);

                notificationVisible = true;
            }
        }, 1000);
    }


    public void setupBottomNavBehaviors() {
        bottomNavigation.setTranslucentNavigationEnabled(false);
    }

    /**
     * Adds styling properties to {@link AHBottomNavigation}
     */
    private void setupBottomNavStyle() {
        /*
        Set Bottom Navigation colors. Accent color for active item,
        Inactive color when its view is disabled.
        Will not be visible if setColored(true) and default current item is set.
         */
        bottomNavigation.setDefaultBackgroundColor(Color.WHITE);
        bottomNavigation.setAccentColor(fetchColor(R.color.bottomtab_0));
        bottomNavigation.setInactiveColor(fetchColor(R.color.bottomtab_item_resting));

        // Colors for selected (active) and non-selected items.
        bottomNavigation.setColoredModeColors(Color.WHITE,
                fetchColor(R.color.bottomtab_item_resting));

        //  Enables Reveal effect
        bottomNavigation.setColored(true);

        //  Displays item Title always (for selected and non-selected items)
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
    }


    /**
     * Adds (items) {@link AHBottomNavigationItem} to {@link AHBottomNavigation}
     * Also assigns a distinct color to each Bottom Navigation item, used for the color ripple.
     */
    private void addBottomNavigationItems() {
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.navbar_library, R.drawable.ic_dashboard_black_24dp, colors[0]);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.navbar_favorites, R.drawable.star_on, colors[1]);

        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);

    }


    /**
     * Simple facade to fetch color resource, so I avoid writing a huge line every time.
     *
     * @param color to fetch
     * @return int color value.
     */
    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null) {
            super.onBackPressed();
        } else if (selectedFragment.equals("Music")) {
            setupViewPager();
            selectedFragment = "Library";

        } else if (selectedFragment.equals("Library")) {
            super.onBackPressed();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(new Event())) // Eğer kayıtlıysa tekrar kayıt olmaması için
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
