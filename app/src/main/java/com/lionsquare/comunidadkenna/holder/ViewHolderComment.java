package com.lionsquare.comunidadkenna.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lionsquare.comunidadkenna.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by edgararana on 20/08/17.
 */

public class ViewHolderComment extends RecyclerView.ViewHolder {
    public CircleImageView circleImageView;
    public TextView tvUser, tvComment;


    public ViewHolderComment(View itemView) {
        super(itemView);
        circleImageView = (CircleImageView) itemView.findViewById(R.id.ic_civ_profile);
        tvUser = (TextView) itemView.findViewById(R.id.ic_txt_name_user);
        tvComment = (TextView) itemView.findViewById(R.id.ic_txt_comment);
    }
}