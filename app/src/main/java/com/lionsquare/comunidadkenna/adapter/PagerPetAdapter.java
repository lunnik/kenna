package com.lionsquare.comunidadkenna.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lionsquare.comunidadkenna.R;

import java.util.List;

/**
 * Created by EDGAR ARANA on 10/08/2017.
 */

public class PagerPetAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    List<String> urlImage;

    public PagerPetAdapter(Context context, List<String> urlImage) {
        mContext = context;
        this.urlImage = urlImage;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return urlImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_vp_pet, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.ivp_iv_pet);
        Glide.with(mContext).load(urlImage.get(position)).centerCrop().into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}