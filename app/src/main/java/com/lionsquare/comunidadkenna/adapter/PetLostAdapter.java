package com.lionsquare.comunidadkenna.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.holder.LoadHolder;
import com.lionsquare.comunidadkenna.holder.ViewHolderPet;
import com.lionsquare.comunidadkenna.model.Pet;


import java.util.List;


/**
 * Created by edgararana on 22/07/17.
 */

public class PetLostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final int TYPE_LOAD = 0;
    public final int TYPE_IMAGE = 1;


    OnLoadMoreListener loadMoreListener;
    boolean isLoading = false, isMoreDataAvailable = true;

    private int lastPosition = -1;


    private List<Pet> petList;
    private Context context;
    private ClickListener clickListener;



    public PetLostAdapter(Context context, List<Pet> petList) {
        this.petList = petList;
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_IMAGE) {
            View view = inflater.inflate(R.layout.item_pet, parent, false);
            return new ViewHolderPet(view);
        } else {
            return new LoadHolder(inflater.inflate(R.layout.row_load, parent, false));
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (petList.get(position).getType() == 0) {
            return TYPE_LOAD;
        } else if (petList.get(position).getType() == 1) {
            return TYPE_IMAGE;
        }
        return 0;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Pet Pet = petList.get(position);

        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore(position);

        }
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_from_bottom);
            holder.itemView.startAnimation(animation);
        }
        lastPosition = position;


    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        if (holder instanceof ViewHolderPet) {
            ViewHolderPet viewHolderVideo = (ViewHolderPet) holder;
            super.onViewRecycled(viewHolderVideo);
        }

    }


    @Override
    public int getItemCount() {
        return petList.size();
    }


    // TODO: 27/04/2017 intaface para comincar que cargue mas elementos
    public interface OnLoadMoreListener {
        void onLoadMore(int position);

    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }


    // TODO: 27/04/2017 inteface de comuniacion cuando haces click
    public interface ClickListener {
        void itemClicked(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }


}

