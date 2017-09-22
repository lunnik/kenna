package com.lionsquare.comunidadkenna.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.LostRegisterActivity;
import com.lionsquare.comunidadkenna.activitys.PetLossListActivity;
import com.lionsquare.comunidadkenna.activitys.WallPetActivity;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.FragmentHomeBinding;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import thebat.lib.validutil.ValidUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends AbstractSectionFragment implements Callback<Response> {

    private OnFragmentInteractionListener mListener;

    private FragmentHomeBinding binding;
    private static final int REGISTER_PET_LOST = 1011;
    public static final String TAG = HomeFragment.class.getName();

    public static HomeFragment newInstace() {
        HomeFragment newsFragment = new HomeFragment();
        Bundle arguments = new Bundle();
        newsFragment.setArguments(arguments);
        newsFragment.setRetainInstance(true);
        return newsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();

        beanSection = new BeanSection();
        beanSection.sectionNameId = R.string.inicio;
        beanSection.sectionColorPrimaryId = R.color.home_color_primary;
        beanSection.sectionColorPrimaryDarkId = R.color.home_color_primary_dark;

        preferences = new Preferences(activity);
        dialogGobal = new DialogGobal(activity);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, null, false);
        initSetUp();
        return binding.getRoot();
    }

    void initSetUp() {

        sectionFragmentCallbacks.updateSectionStatusBar(beanSection);
        binding.amIvLostpet.setVisibility(View.GONE);

        binding.blurredView.setBackgroundResource(R.drawable.back_menu);
        binding.blurredView.setAdjustViewBounds(true);
        binding.blurredView.setScaleType(ImageView.ScaleType.CENTER);



        binding.amBtnLost.setOnClickListener(this);
        binding.amIvLostpet.setOnClickListener(this);


        if (ValidUtils.isNetworkAvailable(activity)) {
            binding.amLavLoader.setVisibility(View.VISIBLE);
            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<Response> call = serviceApi.checkinStatusFolio(preferences.getEmail(), preferences.getToken());
            call.enqueue(this);
        } else {
            dialogGobal.sinInternet(activity);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == activity.RESULT_OK && requestCode == REGISTER_PET_LOST) {
            initSetUp();
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        Log.e("si esta el click ", String.valueOf(v.getId()));
        Intent iMenu = null;
        switch (v.getId()) {

            case R.id.am_btn_lost:
                iMenu = new Intent(activity, LostRegisterActivity.class);
                startActivityForResult(iMenu, REGISTER_PET_LOST);
                break;

            case R.id.am_iv_lostpet:
                iMenu = new Intent(activity, PetLossListActivity.class);
                startActivity(iMenu);
                break;
        }

    }


    /**
     * recibe si es que exite un reporte activo
     */
    @Override
    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
        binding.amLavLoader.setVisibility(View.GONE);
        if (response.body().getSuccess() == 1) {
            binding.amIvLostpet.setVisibility(View.VISIBLE);
            animateButton(binding.amIvLostpet);
        } else if (response.body().getSuccess() == 2) {
            // no hay folios
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(activity);
        }
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        binding.amLavLoader.setVisibility(View.GONE);
        Log.e("error", t + "");
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
