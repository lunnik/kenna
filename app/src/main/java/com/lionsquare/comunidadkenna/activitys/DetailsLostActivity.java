package com.lionsquare.comunidadkenna.activitys;

import android.databinding.DataBindingUtil;
import android.databinding.adapters.FrameLayoutBindingAdapter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.adapter.PagerPetAdapter;
import com.lionsquare.comunidadkenna.databinding.ActivityDetailsLostBinding;
import com.lionsquare.comunidadkenna.model.Pet;
import com.lionsquare.comunidadkenna.model.PetLost;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

public class DetailsLostActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityDetailsLostBinding binding;
    private PetLost pl;
    private User user;
    private PagerPetAdapter pagerPetAdapter;
    private GoogleMap googleMap;
    private Circle mCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_lost);
        if (getIntent().getExtras() != null) {
            pl = (PetLost) getIntent().getExtras().getParcelable("model");
            user = (User) getIntent().getExtras().getParcelable("user");
            Log.e("distnce", String.valueOf(pl.getDistance()));
        }

        initSetUp();
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

    void initSetUp() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

        }

        pagerPetAdapter = new PagerPetAdapter(this, pl.getImages());
        binding.adlVpPet.setAdapter(pagerPetAdapter);
        binding.adlCiIndicator.setViewPager(binding.adlVpPet);


        binding.titleToolbar.setText(pl.getNamePet());
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.adlContent);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));


        binding.adlTvName.setText(pl.getNamePet());
        binding.adlTvBreed.setText(pl.getBreed());
        binding.adlTvData.setText(pl.getTimestamp());

        if (pl.getReward() == 1) {
            binding.adlLlReward.setVisibility(View.GONE);
        }
        binding.adlTvDistace.setText("Se persio a " + pl.getDistance() + " metros de tu ubicación");

        if (pl.getUser() == null) {
            Log.e("vacio", "sfsfd");
        } else {
            Log.e("no vacio", "sdfsfds");
        }
        binding.adlTvNamePropetary.setText(user.getName());
        binding.adlTvDatos.setText(user.getEmail());
        Glide.with(this).load(user.getProfile_pick()).centerCrop().into(binding.adlCivProfile);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        addMaker();

    }

    void addMaker() {
        googleMap.clear();
        this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng latLng = new LatLng(Double.valueOf(pl.getLat()), Double.valueOf(pl.getLng()));
        Marker marker = googleMap.addMarker(
                new MarkerOptions().position(latLng));

        mCircle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(1000)
                .strokeColor(getResources().getColor(R.color.blue_circul))
                .strokeWidth(3)
                .fillColor(getResources().getColor(R.color.blue_circul))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // TODO: 20/07/2017 Aumente el valor para acercar.
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.animateCamera(cameraUpdate);


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    int getToolBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }
}
