package com.lionsquare.comunidadkenna.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.holder.LoadHolder;
import com.lionsquare.comunidadkenna.holder.ViewHolderImgePet;
import com.lionsquare.comunidadkenna.holder.ViewHolderPet;
import com.lionsquare.comunidadkenna.model.Pet;

import java.util.List;

/**
 * Created by EDGAR ARANA on 07/08/2017.
 */

public class ImagePetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<String> petList;
    private Context context;
    private ClickListener clickListener;


    public ImagePetAdapter(Context context, List<String> petList) {
        this.petList = petList;
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_image_pet, parent, false);
        return new ViewHolderImgePet(view);


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolderImgePet viewHolderImgePet = (ViewHolderImgePet) holder;
        Glide.with(context).load(petList.get(position)).centerCrop().override(120, 120).into(viewHolderImgePet.imageView);

    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        if (holder instanceof ViewHolderImgePet) {
            ViewHolderImgePet viewHolderVideo = (ViewHolderImgePet) holder;
            super.onViewRecycled(viewHolderVideo);
        }

    }


    @Override
    public int getItemCount() {
        return petList.size();
    }


    // TODO: 27/04/2017 inteface de comuniacion cuando haces click
    public interface ClickListener {
        void itemClicked(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}