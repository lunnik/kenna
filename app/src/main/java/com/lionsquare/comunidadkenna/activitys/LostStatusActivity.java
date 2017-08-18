package com.lionsquare.comunidadkenna.activitys;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLostStatusBinding;
import com.lionsquare.comunidadkenna.model.FolioPet;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LostStatusActivity extends AppCompatActivity implements Callback<FolioPet> {
    ActivityLostStatusBinding binding;
    private Preferences preferences;
    private DialogGobal dialogGobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_status);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost_status);
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);

        getFolioPet();
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
    }

    @Override
    public void onFailure(Call<FolioPet> call, Throwable t) {
        dialogGobal.dimmis();
    }
}
