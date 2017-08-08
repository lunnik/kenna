package com.lionsquare.comunidadkenna.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;


import com.lionsquare.comunidadkenna.R;


import com.lionsquare.comunidadkenna.adapter.PetLostAdapter;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityWallPetBinding;
import com.lionsquare.comunidadkenna.model.ListLost;
import com.lionsquare.comunidadkenna.model.Pet;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WallPetActivity extends AppCompatActivity implements PetLostAdapter.ClickListener, Callback<ListLost> {

    ActivityWallPetBinding binding;
    PetLostAdapter petLostAdapter;
    private List<Pet> petList;
    private Context context;
    private Preferences preferences;
    private DialogGobal dialogGobal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wall_pet);
        context = this;
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        petList = new ArrayList<>();
        initSetUp();

    }

    void initSetUp() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        for (int i = 0; i < 20; i++) {
            petList.add(new Pet(i, "", 1));
        }
        initRv(petList);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.awRvPet);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));
        //StatusBarUtil.setMargin(this, binding.refreshLayout);

        getListLost();
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

    void getListLost() {
        dialogGobal.progressIndeterminateStyle();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<ListLost> call = serviceApi.getListPetLost(preferences.getEmail(), preferences.getToken());
        call.enqueue(this);
    }

    void initRv(List<Pet> list) {

        petLostAdapter = new PetLostAdapter(context, list);
        petLostAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.awRvPet.setLayoutManager(mLayoutManager);
        binding.awRvPet.setItemAnimator(new DefaultItemAnimator());
        binding.awRvPet.setAdapter(petLostAdapter);
        petLostAdapter.setLoadMoreListener(new PetLostAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(final int position) {
                binding.awRvPet.post(new Runnable() {
                    @Override
                    public void run() {
                        if (petList.size() > 15) {
                            int index = Integer.valueOf(petList.get(position).getId()) - 1;

                            //loadMore(index);
                        }
                    }
                });
            }
        });
    }


    @Override
    public void itemClicked(int position) {

    }

    @Override
    public void onResponse(Call<ListLost> call, Response<ListLost> response) {
        dialogGobal.dimmis();

    }

    @Override
    public void onFailure(Call<ListLost> call, Throwable t) {
        dialogGobal.dimmis();
        dialogGobal.errorConexionFinish(this);
        Log.e("err", String.valueOf(t));
    }
}
