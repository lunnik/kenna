package com.lionsquare.comunidadkenna.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.lionsquare.comunidadkenna.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by EDGAR ARANA on 22/08/2017.
 */

public class ViewHolderOwnPet extends RecyclerView.ViewHolder {


    public ImageView ivPet;
    public TextView tvNamePet;
    public TextView tvStatus;
    public FrameLayout root;
    public LottieAnimationView progressBar;

    public ViewHolderOwnPet(View view) {
        super(view);
        ivPet = (ImageView) view.findViewById(R.id.iot_thumbnail);
        tvNamePet = (TextView) view.findViewById(R.id.iot_title);
        tvStatus = (TextView) view.findViewById(R.id.iot_status);
        root = (FrameLayout) view.findViewById(R.id.iop_root);
        progressBar = (LottieAnimationView) view.findViewById(R.id.iwn_progressBar);

    }
}

