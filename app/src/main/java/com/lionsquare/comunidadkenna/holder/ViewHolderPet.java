package com.lionsquare.comunidadkenna.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lionsquare.comunidadkenna.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by edgararana on 22/07/17.
 */

public class ViewHolderPet extends RecyclerView.ViewHolder {

    public CircleImageView civPet;
    public ImageView ivPet;
    public TextView tvNamePet;
    public TextView tvBreed;
    public FrameLayout root;
    public ProgressBar progressBar;

    public ViewHolderPet(View view) {
        super(view);
        civPet = (CircleImageView) view.findViewById(R.id.ip_civ_profile);
        ivPet = (ImageView) view.findViewById(R.id.ip_iv_pet);
        tvNamePet = (TextView) view.findViewById(R.id.ip_txt_name_pet);
        tvBreed = (TextView) view.findViewById(R.id.ip_txt_breed);
        root = (FrameLayout) view.findViewById(R.id.ip_root);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

    }
}

