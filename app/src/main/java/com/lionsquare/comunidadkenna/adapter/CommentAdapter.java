package com.lionsquare.comunidadkenna.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.lionsquare.comunidadkenna.R;

import com.lionsquare.comunidadkenna.holder.ViewHolderComment;
import com.lionsquare.comunidadkenna.model.CommentDatum;

import java.util.List;

/**
 * Created by edgararana on 20/08/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private List<CommentDatum> commentData;
    private Context context;
    private ClickListener clickListener;


    public CommentAdapter(Context context, List<CommentDatum> commentData) {
        this.commentData = commentData;
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new ViewHolderComment(view);


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CommentDatum commentDatum = commentData.get(position);
        ViewHolderComment viewHolderComment = (ViewHolderComment) holder;
        Glide.with(context).load(commentDatum.getProfilePick()).centerCrop().override(120, 120).into(viewHolderComment.circleImageView);
        viewHolderComment.tvUser.setText(commentDatum.getName());
        viewHolderComment.tvComment.setText(commentDatum.getComment());


    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {

        if (holder instanceof ViewHolderComment) {
            ViewHolderComment viewHolderVideo = (ViewHolderComment) holder;
            super.onViewRecycled(viewHolderVideo);
        }

    }


    @Override
    public int getItemCount() {
        return commentData.size();
    }


    // TODO: 27/04/2017 inteface de comuniacion cuando haces click
    public interface ClickListener {
        void itemClicked(int position);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }


}