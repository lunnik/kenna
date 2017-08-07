package com.lionsquare.comunidadkenna.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.lionsquare.comunidadkenna.R;

/**
 * Created by EDGAR ARANA on 07/08/2017.
 */


public class ViewHolderImgePet extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public ViewHolderImgePet(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.iip_iv_pet);
    }
}