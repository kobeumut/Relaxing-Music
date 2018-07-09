package com.teknasyon.rahatlaticisesler.Adapter;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;

import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.teknasyon.rahatlaticisesler.Model.Event;
import com.teknasyon.rahatlaticisesler.Model.Music;
import com.teknasyon.rahatlaticisesler.R;
import com.xw.repo.BubbleSeekBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.paperdb.Paper;

import static com.teknasyon.rahatlaticisesler.Controller.MainActivity.bottomNavigation;

/**
 * Created by Umut ADALI on 22.06.2018.
 */
public class MusicAdapter extends BaseItemDraggableAdapter<Music, BaseViewHolder> {
    String count = "0";
    public MusicAdapter(ArrayList<Music> data) {
        super(R.layout.card_view, data);
    }

    @Override
    protected void convert(final BaseViewHolder viewHolder, final Music item) {
        viewHolder.setText(R.id.musicTitle, item.getTitle()) //Müzik başlığı ve play butonuna tıklama event'i eklendi
                .addOnClickListener(R.id.playButton);
        viewHolder.getView(R.id.progressLayout).setTag(item.getId()); //Adaptörden mediaplayer kontrol etmek daha zor olacağı için view'a müziğin id'si tag olarak atandı
        viewHolder.getView(R.id.loadingView).setTag(-(item.getId())); //Adaptörden mediaplayer kontrol etmek daha zor olacağı için view'a müziğin id'si daha önce tag olarak atandığı için negatif durumu atandı
        LikeButton likeButton = viewHolder.getView(R.id.like_button); // Beğenme butonu adaptörden kontrolü sağlandı.
        ArrayList<Integer> fs = Paper.book().read("favoriteSongs"); //Veritabanından seçilen favori şarkılar getiriliyor.
        if (fs != null ){
            likeButton.setLiked(fs.contains(item.getId())); // Eğer daha önce beğenme işlevi yapıldıysa adaptörden seçili olarak getiriliyor
        }
        likeButton.setOnLikeListener(new OnLikeListener() { // Favori müziklerin tıklama eventi veritabanından kontrol edilip ekleniyor veya çıkarılıyor
            @Override
            public void liked(LikeButton likeButton) {
                ArrayList<Integer> favoriteSongs = Paper.book().read("favoriteSongs");
                if (favoriteSongs == null) {
                    favoriteSongs = new ArrayList<>();
                }
                if (!favoriteSongs.contains(item.getId())){
                    favoriteSongs.add(item.getId());
                }

                Paper.book().write("favoriteSongs",favoriteSongs);
                count = String.valueOf(favoriteSongs.size());
                EventBus.getDefault().postSticky(new Event("navbarNotify", count));//Favori sayısı navigation bottom bar'a iletiliyor

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                ArrayList<Integer> favoriteSongs = Paper.book().read("favoriteSongs");
                if (favoriteSongs == null) {
                    favoriteSongs = new ArrayList<>();
                }else{
                    favoriteSongs.remove(item.getId());

                }
                Paper.book().write("favoriteSongs",favoriteSongs);
                count = String.valueOf(favoriteSongs.size());
                EventBus.getDefault().postSticky(new Event("navbarNotify", count));//Favori sayısı navigation bottom bar'a iletiliyor
            }
        });

        BubbleSeekBar seekBar = viewHolder.getView(R.id.seekBar); // Müzik ses arttırma azaltma eventinin dinlendiği bölüm
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                item.setVolume(progressFloat/100); // seekbar üzerindeki değeri float değerinde 1 üzerinden olacak şekilde ayarlandı
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });

    }
}