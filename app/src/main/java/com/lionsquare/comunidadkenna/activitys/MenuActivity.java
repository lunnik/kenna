package com.lionsquare.comunidadkenna.activitys;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
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

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;


import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding;
import com.lionsquare.comunidadkenna.fragments.HomeFragment;
import com.lionsquare.comunidadkenna.fragments.ProfileUserFragment;
import com.lionsquare.comunidadkenna.fragments.RegisterPetFragment;
import com.lionsquare.comunidadkenna.fragments.WallPetFragment;


import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
import com.lionsquare.comunidadkenna.utils.DialogGobal;

import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.multiphotopicker.photopicker.activity.PickImageActivity;

import java.util.HashMap;


public class MenuActivity extends AbstractAppActivity implements View.OnClickListener
        , HomeFragment.OnFragmentInteractionListener {


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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission();
        } else {
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }
        listFragment = new HashMap<>();

        currentFragment = HomeFragment.newInstace();
        goFragment(currentFragment, HomeFragment.TAG);
        // TODO: 21/09/2017 se guadar el fragmento con el tag para que no se vuleva a crear
        listFragment.put(HomeFragment.TAG, currentFragment);
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
                    currentFragment = returnFragment(HomeFragment.TAG);
                    if (validationFragment(currentFragment)) {
                        goFragment(currentFragment, HomeFragment.TAG);
                    }
                    return true;

                case R.id.navigation_notifications:
                    currentFragment = returnFragment(WallPetFragment.TAG);
                    if (validationFragment(currentFragment)) {
                        goFragment(currentFragment, WallPetFragment.TAG);
                    } else {
                        retutnListPet();
                    }
                    return true;
                case R.id.navigation_profile:
                    currentFragment = returnFragment(ProfileUserFragment.TAG);
                    if (validationFragment(currentFragment)) {
                        goFragment(currentFragment, ProfileUserFragment.TAG);

                    }
                    return true;
            }
            return false;
        }

    };

    // TODO: 26/09/2017 evita que el fragneto se vuela instanciar
    Fragment returnFragment(String tag) {
        Fragment lFragment = null;
        if (null == getSupportFragmentManager().findFragmentByTag(tag)) {
            if (tag.equals(HomeFragment.TAG)) {
                lFragment = HomeFragment.newInstace();
            } else if (tag.equals(WallPetFragment.TAG)) {
                lFragment = WallPetFragment.newInstace();
            } else if (tag.equals(ProfileUserFragment.TAG)) {
                lFragment = ProfileUserFragment.newInstance();
            }
            return lFragment;
        } else {
            return getSupportFragmentManager().findFragmentByTag(tag);

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
        Snackbar snackbar = Snackbar.make(binding.amClRoot, R.string.permission_location, Snackbar.LENGTH_INDEFINITE);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REGISTER_PET_LOST) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
            fragment.onActivityResult(requestCode, resultCode, data);
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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    // TODO: 26/09/2017 una vez instaciado el fragmento puedes regresar el item 0 de la lista
    void retutnListPet() {
        WallPetFragment recFragment = (WallPetFragment) getSupportFragmentManager().findFragmentById(R.id.fl_main_container);
        recFragment.returnFisrtItem();
      /* if (null != recFragment && recFragment.isInLayout()) {
       }*/
    }
}
