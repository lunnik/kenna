package com.lionsquare.comunidadkenna.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.holder.ViewHolderComment;
import com.lionsquare.comunidadkenna.holder.ViewHolderOwnPet;
import com.lionsquare.comunidadkenna.model.CommentDatum;
import com.lionsquare.comunidadkenna.model.FolioPet;
import com.lionsquare.comunidadkenna.model.Pet;

import java.util.List;

/**
 * Created by EDGAR ARANA on 22/08/2017.
 */

public class OwnPetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<FolioPet> folioPets;
    private Context context;
    private ClickListener clickListener;


    public OwnPetAdapter(Context context, List<FolioPet> folioPets) {
        this.folioPets = folioPets;
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_own_pet, parent, false);
        return new ViewHolderOwnPet(view);


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderOwnPet viewHolderOwnPet = (ViewHolderOwnPet) holder;
        FolioPet fp = folioPets.get(position);
        Pet pet = fp.getPet();

        Glide.with(context).load(pet.getImages().get(0)).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                viewHolderOwnPet.progressBar.setVisibility(View.GONE);

                return false;
            }
        }).into(viewHolderOwnPet.ivPet);
        viewHolderOwnPet.tvNamePet.setText(pet.getNamePet());


        if (pet.getStatus() == 1) {
            viewHolderOwnPet.tvStatus.setText("Activo");

        }


    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        if (holder instanceof ViewHolderComment) {
            ViewHolderOwnPet viewHolderOwnPet = (ViewHolderOwnPet) holder;
            super.onViewRecycled(viewHolderOwnPet);
        }

    }


    @Override
    public int getItemCount() {
        return folioPets.size();
    }


    // TODO: 27/04/2017 inteface de comuniacion cuando haces click
    public interface ClickListener {
        void itemClicked(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}