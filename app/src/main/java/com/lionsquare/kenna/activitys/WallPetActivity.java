package com.lionsquare.kenna.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;


import com.lionsquare.kenna.R;


import com.lionsquare.kenna.adapter.PetLostAdapter;
import com.lionsquare.kenna.databinding.ActivityWallPetBinding;
import com.lionsquare.kenna.model.Pet;
import com.lionsquare.kenna.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;


public class WallPetActivity extends AppCompatActivity implements PetLostAdapter.ClickListener {

    ActivityWallPetBinding binding;
    PetLostAdapter petLostAdapter;
    private List<Pet> petList;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wall_pet);
        context = this;
        petList = new ArrayList<>();


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
        //StatusBarUtil.setMargin(this, findViewById(R.id.gifview));
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
}
