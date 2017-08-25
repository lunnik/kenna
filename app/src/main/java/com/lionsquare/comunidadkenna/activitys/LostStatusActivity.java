package com.lionsquare.comunidadkenna.activitys;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.adapter.CommentAdapter;


import com.lionsquare.comunidadkenna.databinding.ActivityLostStatusBinding;
import com.lionsquare.comunidadkenna.model.CommentDatum;
import com.lionsquare.comunidadkenna.model.FolioPet;
import com.lionsquare.comunidadkenna.model.Pet;

import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

import java.util.List;


public class LostStatusActivity extends AppCompatActivity implements CommentAdapter.ClickListener {

    private Preferences preferences;
    private DialogGobal dialogGobal;
    private Context context;
    private CommentAdapter commentAdapter;
    private FolioPet fl;
    private Pet pet;

    private ActivityLostStatusBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost_status);
        context = this;
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getParcelable("FolioPet") != null && getIntent().getExtras().getParcelable("pet") != null) {
                fl = (FolioPet) getIntent().getExtras().getParcelable("FolioPet");
                pet = (Pet) getIntent().getExtras().getParcelable("pet");
                initSetUp();

            }

            if (getIntent().getExtras().get("id") != null) {
                //dialogGobal.progressIndeterminateStyle();
                Log.e("id", String.valueOf(getIntent().getExtras().getInt("id")));


            }


        }


    }


    void initSetUp() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

        }


        binding.titleToolbar.setText("");

        Glide.with(context).load(pet.getImages().get(0)).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {


                return false;
            }
        }).into(binding.alsIvPet);
        binding.alsTvMascota.setText(pet.getNamePet());
        if (pet.getReward() == 1) {
            binding.alsTvMoney.setText("$ " + pet.getMoney());
        } else {
            binding.ll2.setVisibility(View.GONE);
        }

        if (pet.getStatus() == 1) {
            binding.alsTvEstatus.setText("Activo");

        }

        binding.alsTvTime.setText(converteTimestamp(pet.getTimestamp()));
        initRv(fl.getCommentData());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private CharSequence converteTimestamp(String mileSegundos) {
        Long time = Long.parseLong(mileSegundos) * 1000;
        String txt = (String) DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        return Character.toUpperCase(txt.charAt(0)) + txt.substring(1);
    }


    void initRv(List<CommentDatum> list) {

        commentAdapter = new CommentAdapter(context, list);
        commentAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.alsRvComment.setLayoutManager(mLayoutManager);
        binding.alsRvComment.setItemAnimator(new DefaultItemAnimator());
        binding.alsRvComment.setAdapter(commentAdapter);
    }

    @Override
    public void itemClicked(int position) {

    }
}
