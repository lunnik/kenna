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
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.adapter.CommentAdapter;
import com.lionsquare.comunidadkenna.adapter.PagerPetAdapter;
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLostStatusBinding;
import com.lionsquare.comunidadkenna.model.CommentDatum;
import com.lionsquare.comunidadkenna.model.FolioPet;
import com.lionsquare.comunidadkenna.model.Pet;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LostStatusActivity extends AppCompatActivity implements Callback<FolioPet>, CommentAdapter.ClickListener {
    ActivityLostStatusBinding binding;
    private Preferences preferences;
    private DialogGobal dialogGobal;
    private Context context;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_status);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost_status);
        context = this;
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        initSetUp();
        getFolioPet();
    }

    void initSetUp(){
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

        }


        binding.titleToolbar.setText("");
    }

    void getFolioPet() {
        dialogGobal.progressIndeterminateStyle();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<FolioPet> call = serviceApi.getFolioLostPet(preferences.getEmail(), preferences.getToken());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<FolioPet> call, Response<FolioPet> response) {
        dialogGobal.dimmis();
        if (response.body().getSuccess() == 1) {
            FolioPet fl = response.body();
            Pet pet = fl.getPet();

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
    }

    @Override
    public void onFailure(Call<FolioPet> call, Throwable t) {
        dialogGobal.dimmis();
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
