package com.teknasyon.rahatlaticisesler.Adapter;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.teknasyon.rahatlaticisesler.Model.Categories;
import com.teknasyon.rahatlaticisesler.Model.Music;
import com.teknasyon.rahatlaticisesler.R;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * Created by Umut ADALI on 22.06.2018.
 */
public class CategoryAdapter extends BaseQuickAdapter<Categories, BaseViewHolder> {
    public CategoryAdapter(ArrayList<Categories> data) {
        super(R.layout.library_view, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, final Categories item) {
        viewHolder.setText(R.id.categoryTitle, item.getCategory());
        Glide.with(mContext).load(item.getImage()).apply(RequestOptions.circleCropTransform()).into((ImageView) viewHolder.getView(R.id.categoryImage));

    }
}