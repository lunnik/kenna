package com.lionsquare.comunidadkenna.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.FragmentProfileUserBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileUserFragment extends AbstractSectionFragment implements OnMapReadyCallback {


    public  static  ProfileUserFragment newInstance() {
        ProfileUserFragment newsFragment = new ProfileUserFragment();
        Bundle arguments = new Bundle();
        newsFragment.setArguments(arguments);
        newsFragment.setRetainInstance(true);

        return newsFragment;
    }
    private ImageView coverImage;

    private Toolbar toolbar;
    private TextView textviewTitle;
    CircleImageView circleImageView;


    private GoogleMap googleMap;
    private Circle mCircle;
    private Button btnLogOut, btnChangeLoc;
    private TextView txtName, txtEmail;

    private static final int PLACE_PICKER_REQUEST = 1;


    FragmentProfileUserBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();

        beanSection = new BeanSection();
        beanSection.sectionNameId = R.string.perfil;
        beanSection.sectionColorPrimaryId = R.color.primaryColor;
        beanSection.sectionColorPrimaryDarkId = R.color.news_color_primary_dark;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_user, null, false);
        findViews();
        initSetUp();
        return binding.getRoot();
    }

    private void findViews() {
        coverImage = binding.apIvCover;
        circleImageView = binding.imageProfile;
        btnLogOut = binding.logaout;
        btnChangeLoc = binding.changeLoc;
        txtName = binding.apTxtName;
        txtEmail = binding.apTxtEmail;
    }

    void initSetUp() {
        preferences = new Preferences(getContext());
        dialogGobal = new DialogGobal(getContext());
        dbManager = new DbManager(getActivity()).open();
        appBarLayout=binding.ablGuidesAppbar;
        toolbar = binding.guidesToolbar;
        collapsingToolbarLayout=binding.collapsingToolbar;

        sectionFragmentCallbacks.updateSectionToolbar(beanSection, collapsingToolbarLayout, toolbar);
        sectionFragmentCallbacks.setSearchViewVisible(true);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_perfil);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (URLUtil.isValidUrl(preferences.getImagePerfil())) {
            Glide.with(this).load(preferences.getImagePerfil()).into(circleImageView);
        } else {
            Glide.with(this).load(R.drawable.ic_user_ic).into(circleImageView);
        }


        if (URLUtil.isValidUrl(preferences.getCover())) {
            Glide.with(this).load(preferences.getCover()).into(coverImage);
        } else {
            Glide.with(this).load(R.drawable.back_login).into(coverImage);
        }

        btnLogOut.setOnClickListener(this);
        btnChangeLoc.setOnClickListener(this);

        txtName.setText(preferences.getName());
        txtEmail.setText(preferences.getEmail());
        //textviewTitle.setText("Perfil");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.change_loc:
                locationPlacesIntent();
                break;
            case R.id.logaout:
                sectionFragmentCallbacks.stateSession();

                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.
                checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.googleMap.setMyLocationEnabled(false);

        final Handler handler = new Handler();
        btnLogOut.setEnabled(false);
        final Runnable r = new Runnable() {
            public void run() {
                addMaker();

            }
        };

        handler.postDelayed(r, 1000);

    }

    void addMaker() {
        googleMap.clear();
        try {
            User user = dbManager.getUser();
            LatLng latLng = new LatLng(user.getLat(), user.getLng());
            Marker marker = googleMap.addMarker(
                    new MarkerOptions().position(latLng));

            mCircle = googleMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(500)
                    .strokeColor(getResources().getColor(R.color.blue_circul))
                    .strokeWidth(3)
                    .fillColor(getResources().getColor(R.color.blue_circul))
            );
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // TODO: 20/07/2017 Aumente el valor para acercar.
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(user.getLat(), user.getLng()), 14);
            googleMap.animateCamera(cameraUpdate);
            dialogGobal.dimmis();
        } catch (Exception e) {
            Log.e("error", String.valueOf(e));
        }

        btnLogOut.setEnabled(true);

    }

    private void locationPlacesIntent() {
        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            getActivity().startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Log.e("error lat", String.valueOf(e));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    updateLoc(latLng);
                } else {
                    Log.e("error", "sdfsgrfger");

                }
            }
        }

    }

    public void updateLoc(final LatLng latLng) {
        dialogGobal.progressIndeterminateStyle();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<Response> call = serviceApi.updateLoc(
                preferences.getEmail(), preferences.getToken(), latLng.latitude, latLng.longitude);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                dialogGobal.dimmis();
                if (response.body().getSuccess() == 1) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    // TODO: 20/07/2017 Aumente el valor para acercar.
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                    googleMap.animateCamera(cameraUpdate);
                    dbManager.updateLoc(dbManager.getUser().getId(), latLng.latitude, latLng.longitude);
                    googleMap.clear();
                    addMaker();
                } else if (response.body().getSuccess() == 0) {
                    //token caduco
                    tokenDeprecated();
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("error", String.valueOf(t));
                dialogGobal.dimmis();
            }
        });
    }

    void tokenDeprecated() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.token_deprecated)
                .content(R.string.inicar_sesion_nuevamente)
                .positiveText(R.string.volver_a_iniciar_sesion)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sectionFragmentCallbacks.stateSession();


                    }
                })
                .progressIndeterminateStyle(true)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

}
