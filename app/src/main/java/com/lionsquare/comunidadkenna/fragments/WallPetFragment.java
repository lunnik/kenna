package com.lionsquare.comunidadkenna.fragments;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.DetailsLostActivity;
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.FragmentWallPetBinding;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
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
import thebat.lib.validutil.ValidUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class WallPetFragment extends AbstractSectionFragment implements Callback<ListLost>, PetLostAdapter.ClickListener {

    FragmentWallPetBinding binding;

    public static WallPetFragment newInstace() {
        WallPetFragment wallPetFragment = new WallPetFragment();
        Bundle arguments = new Bundle();
        wallPetFragment.setArguments(arguments);
        wallPetFragment.setRetainInstance(true);
        return wallPetFragment;
    }

    PetLostAdapter petLostAdapter;
    private List<Pet> petList;
    private Preferences preferences;
    private DialogGobal dialogGobal;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();

        beanSection = new BeanSection();
        beanSection.sectionNameId = R.string.perdidos;
        beanSection.sectionColorPrimaryId = R.color.wall_pet_color_primary;
        beanSection.sectionColorPrimaryDarkId = R.color.wall_pet_color_primary_dark;

        preferences = new Preferences(activity);
        dialogGobal = new DialogGobal(activity);
        petList = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wall_pet, null, false);
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();
        initSetUp();
        return binding.getRoot();
    }

    void initSetUp() {
        toolbar = binding.includeToolbar.pinnedToolbar;
        sectionFragmentCallbacks.updateSectionToolbar(beanSection, toolbar);
        sectionFragmentCallbacks.setSearchViewVisible(true);


        if (ValidUtils.isNetworkAvailable(activity)) {
            getListLost();
        } else {
            dialogGobal.sinInternet(activity);
        }
        initRv(petList);

    }

    void getListLost() {
        dialogGobal.progressIndeterminateStyle();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<ListLost> call = serviceApi.getListPetLost(preferences.getEmail(), preferences.getToken());
        call.enqueue(this);
    }

    void initRv(List<Pet> list) {

        petLostAdapter = new PetLostAdapter(activity, list);
        petLostAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
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
    public void onClick(View v) {

    }

    @Override
    public void itemClicked(int position) {
        Intent iDetails = new Intent(activity, DetailsLostActivity.class);
        iDetails.putExtra("model", petList.get(position));
        iDetails.putExtra("user", petList.get(position).getUser());
        startActivity(iDetails);
        Pet pet = petList.get(position);


    }

    @Override
    public void onResponse(Call<ListLost> call, Response<ListLost> response) {
        dialogGobal.dimmis();
        if (response.body().getSuccess() == 1) {
            petList = response.body().getListLost();
            initRv(petList);
        } else if (response.body().getSuccess() == 2) {
            //vacio
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(activity);
        }


    }

    @Override
    public void onFailure(Call<ListLost> call, Throwable t) {
        dialogGobal.dimmis();
        dialogGobal.errorConexionFinish(activity);
        Log.e("err", String.valueOf(t));
    }
}
