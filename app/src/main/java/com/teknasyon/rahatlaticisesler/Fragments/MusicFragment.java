package com.teknasyon.rahatlaticisesler.Fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.teknasyon.rahatlaticisesler.Adapter.MusicAdapter;
import com.teknasyon.rahatlaticisesler.Model.Event;
import com.teknasyon.rahatlaticisesler.Model.Music;
import com.teknasyon.rahatlaticisesler.Network.RetrofitClient;
import com.teknasyon.rahatlaticisesler.Network.Service;
import com.teknasyon.rahatlaticisesler.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import co.mobiwise.library.ProgressLayout;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.teknasyon.rahatlaticisesler.Controller.MainActivity.bottomNavigation;

/**
 * Created by Umut ADALI on 25.06.2018.
 */

public class MusicFragment extends Fragment {
    public static final String TAG = MusicFragment.class.getSimpleName();
    private Integer categoryID = 1;
    private ArrayList<Music> data;
    private MusicAdapter adapter;
    private RecyclerView recyclerView;
    private View notDataView;
    private Integer favoriCategoryID = -1;
    private static final String ARG_COLOR = "color";
    private int color;


    public MusicFragment() {
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setBackgroundColor(getLighterColor(color));

        init();

        getDatas();

        return rootView;
    }
    private void getDatas() {

        Service service = RetrofitClient.getClient().create(Service.class);
        service.musics().enqueue(new Callback<ArrayList<Music>>() {
            @Override
            public void onResponse(Call<ArrayList<Music>> call, Response<ArrayList<Music>> response) {
                if (response.isSuccessful()){
                    ArrayList<Music> list = response.body();
                    ArrayList<Music> allSongs = Paper.book().read("AllSongs");
                    if (allSongs != null && Arrays.equals(allSongs.toArray(),list.toArray())){
                        list = allSongs;
                    }

                    ArrayList<Integer> favoriteSongs = Paper.book().read("favoriteSongs");


                    for (Music m : list) {
                        if (m.getCategory().equals(categoryID)) {
                            addData(m);
                        } else if (categoryID.equals(favoriCategoryID) && (favoriteSongs != null ? (favoriteSongs.indexOf(m.getId()) > -1) : false)) {
                            addData(m);
                        }

                    }

                    if (data.isEmpty()) {
                        adapter.setEmptyView(R.layout.empty_view, (ViewGroup) recyclerView.getParent());
                    }
                    setData();
                }else {
                    Toast.makeText(getContext(),"Servis ile alakalı sorun yaşanıyor...", Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onFailure(Call<ArrayList<Music>> call, Throwable t) {
                    Toast.makeText(getContext(),"Veri çekmede sorun yaşanıyor, bağlantınızı kontrol ediniz.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void init() {
//        Bitmap blurredBitmap = BlurTransformation.blur( getContext(), R.drawable.background);
//        ImageView background_view = findViewById(R.id.background);
        categoryID = getArguments().getInt("category");
//        background_view.setImageBitmap(blurredBitmap);        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MusicAdapter(data);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<>();
        adapter.setEmptyView(R.layout.loading_view, (ViewGroup) recyclerView.getParent());
        adapter.openLoadAnimation();


    }

    private void setData() {
        adapter.setNewData(data);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                final MediaPlayer mMediaPlayer = data.get(position).getMp();
                final ProgressLayout p = view.getRootView().findViewWithTag(data.get(position).getId());  //Adaptörden mediaplayer kontrol etmek daha zor olacağı için view'a müziğin id'si tag olarak atanarak çağırıldı
                if (view.getId() == R.id.playButton) {
                    final ConstraintLayout loadingView = view.getRootView().findViewWithTag(-(data.get(position).getId())); //Adaptörden mediaplayer kontrol etmek daha zor olacağı için view'a müziğin id'si daha önce tag olarak atandığı için negatif durumu atanarak çağırıldı
                    final ImageView playButton = view.findViewById(R.id.playButton);


                    if (!mMediaPlayer.isPlaying()) {
                        loadingView.setVisibility(View.VISIBLE);
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
                                            loadingView.setVisibility(View.GONE);
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
                                            loadingView.setVisibility(View.GONE);

                                        }
                                    }
                                });
                                //bir kere müziği dinletmeye hazırlaması yeterli daha sonra start ile müziğin çalmasına devam edilir.
                                if (data.get(position).getPauseTime() == 0 && !data.get(position).isClickedForStart()) {
                                    mMediaPlayer.prepareAsync();
                                } else {
                                    mMediaPlayer.start();
                                    p.start();
                                    loadingView.setVisibility(View.GONE);
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
                }
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            //TODO: position check and add in hashmap with position then start currentpostion
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {


            }
        });


        if (categoryID.equals(favoriCategoryID)) {
            OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
                @Override
                public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                    Toast.makeText(getContext(), "Favorilerden silmek üzeresiniz", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                }

                @Override
                public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                    ArrayList<Integer> favoriteSongs = Paper.book().read("favoriteSongs");
                    if (favoriteSongs == null) {
                        favoriteSongs = new ArrayList<>();
                    } else {
                        favoriteSongs.remove(data.get(pos).getId());
                    }
                    Paper.book().write("favoriteSongs", favoriteSongs);
                    String count = String.valueOf(favoriteSongs.size());
                    EventBus.getDefault().postSticky(new Event("navbarNotify", count));
                    Toast.makeText(getContext(), "Favorilerden başarıyla silindi", Toast.LENGTH_SHORT).show();
                    data.remove(pos);
                    adapter.notifyDataSetChanged();
                    if (data.isEmpty()) {
                        adapter.setEmptyView(R.layout.empty_view,(ViewGroup) recyclerView.getParent());
                    }
                }

                @Override
                public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
//                canvas.drawColor(ContextCompat.getColor(MusicFragment.this, R.color.colorPrimaryDark));
                }
            };
            ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
            itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);

            adapter.enableSwipeItem();
            adapter.setOnItemSwipeListener(onItemSwipeListener);
        }
        recyclerView.setHasFixedSize(true);

        adapter.notifyDataSetChanged();
    }

    private void addData(Music m) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build();
            m.getMp().setAudioAttributes(audioAttributes);
        } else {
            m.getMp().setAudioStreamType(AudioManager.STREAM_MUSIC);
        }


        try {
            m.getMp().setDataSource(m.getURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.add(new Music(m.getId(), m.getTitle(), m.getCategory(), m.getMp(), m.getURL(), false));
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
