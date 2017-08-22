package com.lionsquare.comunidadkenna.activitys;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.adapter.OwnPetAdapter;
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityPetLossListBinding;
import com.lionsquare.comunidadkenna.model.FolioPet;
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

public class PetLossListActivity extends AppCompatActivity implements OwnPetAdapter.ClickListener, Callback<List<FolioPet>> {
    ActivityPetLossListBinding binding;
    private OwnPetAdapter ownPetAdapter;
    private List<FolioPet> folioPets;
    private Context context;
    private Preferences preferences;
    private DialogGobal dialogGobal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_loss_list);
        context = this;
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        folioPets = new ArrayList<>();
        initSetUp();

    }

    void initSetUp() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.apllRvPetOwn);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));

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
        Call<List<FolioPet>> call = serviceApi.getFolioLostPet(preferences.getEmail(), preferences.getToken());
        call.enqueue(this);
    }


    void initRv(List<FolioPet> list) {

        ownPetAdapter = new OwnPetAdapter(context, list);
        ownPetAdapter.setClickListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        binding.apllRvPetOwn.setLayoutManager(mLayoutManager);
        binding.apllRvPetOwn.setItemAnimator(new DefaultItemAnimator());
        binding.apllRvPetOwn.setAdapter(ownPetAdapter);

    }


    @Override
    public void itemClicked(int position) {

    }

    @Override
    public void onResponse(Call<List<FolioPet>> call, Response<List<FolioPet>> response) {
        dialogGobal.dimmis();
        folioPets = response.body();
        initRv(folioPets);

    }

    @Override
    public void onFailure(Call<List<FolioPet>> call, Throwable t) {
        dialogGobal.dimmis();
        dialogGobal.errorConexion();
    }
}
