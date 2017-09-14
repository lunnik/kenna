package com.lionsquare.comunidadkenna.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import android.widget.ImageView;


import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;


import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding;
import com.lionsquare.comunidadkenna.fragments.ProfileUserFragment;
import com.lionsquare.comunidadkenna.fragments.RegisterPetFragment;
import com.lionsquare.comunidadkenna.fragments.WallPetFragment;


import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.task.FileFromBitmap;
import com.lionsquare.comunidadkenna.utils.DialogGobal;

import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.multiphotopicker.photopicker.activity.PickImageActivity;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import thebat.lib.validutil.ValidUtils;

public class MenuActivity extends AbstractAppActivity implements View.OnClickListener, Callback<Response> {
    ActivityMenuBinding binding;

    private static final int PERMISS_WRITE_EXTERNAL_STORAGE = 1;
    private static final int REGISTER_PET_LOST = 1011;

    private Preferences preferences;
    private DialogGobal dialogGobal;


    private Fragment currentFragment;


    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        initSetUp();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }


    void initSetUp() {
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        binding.amIvLostpet.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission();
        } else {
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }
        binding.blurredView.setBackgroundResource(R.drawable.back_menu);
        binding.blurredView.setAdjustViewBounds(true);
        binding.blurredView.setScaleType(ImageView.ScaleType.CENTER);


        binding.amBtnProfile.setOnClickListener(this);
        binding.amBtnLost.setOnClickListener(this);
        binding.amBtnWall.setOnClickListener(this);
        binding.amIvLostpet.setOnClickListener(this);


        if (ValidUtils.isNetworkAvailable(this)) {
            binding.amLavLoader.setVisibility(View.VISIBLE);
            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<Response> call = serviceApi.checkinStatusFolio(preferences.getEmail(), preferences.getToken());
            call.enqueue(this);
        } else {
            dialogGobal.sinInternet(this);
        }

        currentFragment = ProfileUserFragment.newInstance();
        goFragment(currentFragment);
        binding.navigation.setItemBackgroundResource(R.color.news_color_primary);
    }

    @Override
    public void setupToolbar(final Toolbar sectionToolbar) {
        setSupportActionBar(sectionToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //sectionToolbar.setNavigationIcon(R.drawable.ic_menu);

        if (collapsingToolbar != null) {
            collapsingToolbar.setExpandedTitleTextAppearance(android.R.style.TextAppearance_Medium);
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    currentFragment = ProfileUserFragment.newInstance();
                    if (validationFragment(currentFragment))
                        changeFragmente(currentFragment, R.color.news_color_primary);


                    return true;
                case R.id.navigation_dashboard:
                    currentFragment = RegisterPetFragment.newInstance();
                    if (validationFragment(currentFragment)) {
                        changeFragmente(currentFragment, R.color.register_color_primary);
                    }
                    return true;
                case R.id.navigation_notifications:
                    currentFragment = WallPetFragment.newInstace();
                    if (validationFragment(currentFragment)) {
                        changeFragmente(currentFragment, R.color.wall_pet_color_primary);
                    }
                    return true;
            }
            return false;
        }

    };


    void changeFragmente(Fragment fragment, int color) {
        goFragment(fragment);
        binding.navigation.setItemBackgroundResource(color);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(color));
        }
    }


    boolean validationFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
        if (currentFragment == null) {
            //carga del primer fragment justo en la carga inicial de la app
            return true;
        } else if (!currentFragment.getClass().getName().equalsIgnoreCase(fragment.getClass().getName())) {
            //currentFragment no concide con newFragment
            return true;

        } else {
            //currentFragment es igual a newFragment
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        Intent iMenu = null;
        switch (v.getId()) {
            case R.id.am_btn_profile:

                break;
            case R.id.am_btn_lost:
                iMenu = new Intent(this, LostRegisterActivity.class);
                startActivityForResult(iMenu, REGISTER_PET_LOST);
                break;
            case R.id.am_btn_wall:
                iMenu = new Intent(this, WallPetActivity.class);
                startActivity(iMenu);
                break;
            case R.id.am_iv_lostpet:
                iMenu = new Intent(this, PetLossListActivity.class);
                startActivity(iMenu);
                break;
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //saveComments();
                //binding.placeSearchDialogOkTV.setEnabled(true);
                //checkoutLogin();
            } else {
                //binding.placeSearchDialogOkTV.setEnabled(false);
                showSnackBar();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verifyPermission() {
        int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            //binding.placeSearchDialogOkTV.setEnabled(false);
        } else {
            //saveComments();
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSnackBar();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISS_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(binding.amRlContent, R.string.permission_location, Snackbar.LENGTH_INDEFINITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        snackbar.setAction("Configurar", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", "com.lionsquare.kenna", null);
                intent.setData(uri);
                startActivity(intent);
            }
        }).show();
    }

    @Override
    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
        binding.amLavLoader.setVisibility(View.GONE);
        if (response.body().getSuccess() == 1) {
            binding.amIvLostpet.setVisibility(View.VISIBLE);
            animateButton(binding.amIvLostpet);
        } else if (response.body().getSuccess() == 2) {
            // no hay folios
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(this);
        }
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        binding.amLavLoader.setVisibility(View.GONE);
        Log.e("error", t + "");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REGISTER_PET_LOST) {
            initSetUp();
        } else {

        }
        // TODO: 14/09/2017 es para madar al fragmento el result
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
                    fragment.onActivityResult(requestCode, resultCode, data);
                } else {
                    Log.e("error", "sdfsgrfger");

                }
            }
        }


        if (resultCode == RESULT_OK && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
            fragment.onActivityResult(requestCode, resultCode, data);

        }



    }


    @Override
    public void setSearchViewVisible(boolean visible) {

    }


}
