package com.lionsquare.comunidadkenna.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.DetailsLostActivity;
import com.lionsquare.comunidadkenna.activitys.MainActivity;
import com.lionsquare.comunidadkenna.activitys.MenuActivity;
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.FragmentWallPetBinding;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
import com.lionsquare.comunidadkenna.model.ListLost;
import com.lionsquare.comunidadkenna.model.Pet;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;
import com.marcoscg.infoview.InfoView;

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

    public static final String TAG = WallPetFragment.class.getName();


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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initSetUp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wall_pet, null, false);
        }
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();
        initSetUp();
        return binding.getRoot();
    }

    void initSetUp() {
        toolbar = binding.includeToolbar.pinnedToolbar;
        sectionFragmentCallbacks.updateSectionToolbar(beanSection, toolbar);

        infoView = binding.infoView;
        infoView.setTitle(getString(R.string.opps));
        infoView.setMessage(getString(R.string.eso_no_deberia_haber_ocurrido));
        infoView.setIconRes(R.drawable.ic_sad_emoji);
        infoView.setButtonText(getString(R.string.intentar_de_nuevo));
        infoView.setButtonTextColorRes(R.color.colorAccent);
        infoView.setOnTryAgainClickListener(new InfoView.OnTryAgainClickListener() {
            @Override
            public void onTryAgainClick() {
                Toast.makeText(activity, "Try again clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        if (petList.isEmpty()) {
            if (ValidUtils.isNetworkAvailable(activity)) {
                getListLost();
            } else {
                dialogGobal.sinInternet(activity);
            }
        } else {
            initRv(petList);

        }


    }

    void getListLost() {
        infoView.setProgress(true);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity) {
            a = (Activity) context;
            try {
                sectionFragmentCallbacks = (SectionFragmentCallbacks) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement SectionFragmentCallbacks");
            }
        }
    }

    @Override
    public void onResponse(Call<ListLost> call, Response<ListLost> response) {
        infoView.setVisibility(View.GONE);
        if (response.body().getSuccess() == 1) {
            petList = response.body().getListLost();
            initRv(petList);
        } else if (response.body().getSuccess() == 2) {
            dialogGobal.noMatches(activity);
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(activity);
        }


    }

    @Override
    public void onFailure(Call<ListLost> call, Throwable t) {
        infoView.setProgress(false);
        dialogGobal.errorConexionFinish(activity);
        Log.e("err", String.valueOf(t));
    }


    // TODO: 26/09/2017 regresa a la posicion o el rv
    public void returnFisrtItem() {
        binding.awRvPet.smoothScrollToPosition(0);
        //binding.awRvPet.scrollToPosition(0);
    }


}
