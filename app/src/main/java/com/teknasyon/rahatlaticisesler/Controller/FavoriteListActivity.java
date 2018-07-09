package com.teknasyon.rahatlaticisesler.Controller;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.teknasyon.rahatlaticisesler.Model.Music;
import com.teknasyon.rahatlaticisesler.Adapter.MusicAdapter;
import com.teknasyon.rahatlaticisesler.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import co.mobiwise.library.ProgressLayout;
import io.paperdb.Paper;

public class FavoriteListActivity extends AppCompatActivity {


    private ArrayList<Music> data;
    private HashMap<Integer, Music> hData = new HashMap<>();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_library:
                    data.get(1).setVolume(0.5f);

                    return true;
                case R.id.navigation_favorite:
                    data.get(1).setVolume(1.0f);
                    return true;
            }
            return false;
        }
    };

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("songs.html");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    private void addData(Music m) {
        MediaPlayer new1 = new MediaPlayer();
        new1.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            new1.setDataSource(m.getURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.add(new Music(m.getId(), m.getTitle(), m.getCategory(), new1, m.getURL(), false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Paper.init(this);

        data = new ArrayList<>();
        Music[] mList = new Gson().fromJson(loadJSONFromAsset(this), Music[].class);
        for (Music m :
                mList) {
            addData(m);
        }


        MusicAdapter adapter = new MusicAdapter(data);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
//        progressLayout = (ProgressLayout) findViewById(R.id.progressLayout);


        adapter.notifyDataSetChanged();
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                final MediaPlayer mMediaPlayer = data.get(position).getMp();
                final ProgressLayout p = view.getRootView().findViewWithTag(data.get(position).getId());
                if (view.getId() == R.id.categoryImage){
                    final ImageView playButton = view.findViewById(R.id.categoryImage);


                    if (!mMediaPlayer.isPlaying()) {
                        playButton.setBackgroundResource(R.drawable.ic_pause);
                        final Thread t = new Thread() {
                            @Override
                            public void run() {


                                mMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                                    @Override
                                    public void onSeekComplete(MediaPlayer mp) {
                                        if (mp == mMediaPlayer) {
                                            p.setCurrentProgress(data.get(position).getPauseTime());
                                            p.start();
                                        }
                                    }
                                });

                                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        if (mp == mMediaPlayer) {
                                            Log.d("duration", String.valueOf(mMediaPlayer.getDuration()));

                                            Log.d("second", String.valueOf((int) TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getDuration())));
                                            if (data.get(position).getPauseTime() > 0) {
                                                mMediaPlayer.seekTo((int) TimeUnit.SECONDS.toMillis(data.get(position).getPauseTime()));
                                                p.setCurrentProgress(data.get(position).getPauseTime());
                                            } else {
                                                p.setMaxProgress((int) TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getDuration()));

                                            }
                                            p.start();
                                            mMediaPlayer.start();

                                        }
                                    }
                                });
                                //bir kere müziği dinletmeye hazırlaması yeterli daha sonra start ile müziğin çalmasına devam edilir.
                                if (data.get(position).getPauseTime() == 0 && !data.get(position).isClickedForStart()) {
                                    mMediaPlayer.prepareAsync();
                                } else {
                                    mMediaPlayer.start();
                                    p.start();
                                }
                                data.get(position).setClickedForStart(true); //Bir kez prepareasync yapıldıysa hazır olmadan birden fazla tıklanıp patlaması engellendi.

                                data.get(position).setMp(mMediaPlayer);


                            }
                        };
                        t.start();
                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
//                            mMediaPlayer.reset();
                                if (mp == mMediaPlayer) {
                                    playButton.setBackgroundResource(R.drawable.ic_play);
                                    data.get(position).setPauseTime(0);
                                    data.get(position).setMp(mMediaPlayer);
                                    p.cancel();
                                    t.interrupt();
                                }
                            }
                        });

                    } else {
                        playButton.setBackgroundResource(R.drawable.ic_play);
                        mMediaPlayer.pause();
                        p.stop();
                        data.get(position).setPauseTime((int) TimeUnit.MILLISECONDS.toSeconds(mMediaPlayer.getCurrentPosition()));


                    }


                    Log.d("click", "onItemClick: " + view);
                    Toast.makeText(FavoriteListActivity.this, "onItemClick" + position, Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            //TODO: position check and add in hashmap with position then start currentpostion
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {


            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Music m : data) {
            if (m.getMp() != null) {
                if (m.getMp().isPlaying()) {
                    m.getMp().stop();
                }
                m.getMp().release();
            }
        }

    }
}
