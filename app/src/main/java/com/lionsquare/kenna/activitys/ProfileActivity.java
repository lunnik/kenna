package com.lionsquare.kenna.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
import com.lionsquare.kenna.Kenna;
import com.lionsquare.kenna.R;
import com.lionsquare.kenna.db.DbManager;
import com.lionsquare.kenna.model.User;
import com.lionsquare.kenna.utils.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, OnMapReadyCallback, View.OnClickListener {
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapsing;
    private ImageView coverImage;
    private FrameLayout framelayoutTitle;
    private LinearLayout linearlayoutTitle;
    private Toolbar toolbar;
    private TextView textviewTitle;
    CircleImageView circleImageView;

    private Preferences preferences;
    private DbManager dbManager;

    private GoogleMap googleMap;
    private Circle mCircle;
    private Button btnLogOut, btnChangeLoc;
    private TextView txtName, txtEmail;

    private static final int PLACE_PICKER_REQUEST = 1;

    private void findViews() {
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        collapsing = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        coverImage = (ImageView) findViewById(R.id.imageview_placeholder);
        framelayoutTitle = (FrameLayout) findViewById(R.id.framelayout_title);
        linearlayoutTitle = (LinearLayout) findViewById(R.id.linearlayout_title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textviewTitle = (TextView) findViewById(R.id.textview_title);
        circleImageView = (CircleImageView) findViewById(R.id.image_profile);
        btnLogOut = (Button) findViewById(R.id.change_loc);
        btnChangeLoc = (Button) findViewById(R.id.logaout);
        txtName = (TextView) findViewById(R.id.ap_txt_name);
        txtEmail = (TextView) findViewById(R.id.ap_txt_email);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();

        toolbar.setTitle("");
        appbar.addOnOffsetChangedListener(this);

        setSupportActionBar(toolbar);
        startAlphaAnimation(textviewTitle, 0, View.INVISIBLE);

        preferences = new Preferences(this);
        dbManager = new DbManager(this).open();
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }


        if (URLUtil.isValidUrl(preferences.getImagePerfil()))

            Glide.with(this).load(preferences.getImagePerfil()).into(circleImageView);
        else
            Glide.with(this).load(R.drawable.ic_user_ic).into(circleImageView);


        if (URLUtil.isValidUrl(preferences.getCover()))
            Glide.with(this).load(preferences.getCover()).into(coverImage);
        else
            Glide.with(this).load(R.drawable.back_login).into(coverImage);

        btnLogOut.setOnClickListener(this);
        btnChangeLoc.setOnClickListener(this);

        txtName.setText(preferences.getName());
        txtEmail.setText(preferences.getEmail());
        textviewTitle.setText("Perfil");


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Kenna.mGoogleApiClient != null) {
            Kenna.mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Start Animation again only if it is not running

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.change_loc:
                locationPlacesIntent();
                break;
            case R.id.logaout:
                if (preferences.getTypeLogin() == Kenna.Google) {
                    signOut();
                }
                if (preferences.getTypeLogin() == Kenna.Facebook) {
                    LoginManager.getInstance().logOut();
                    preferences.closeProfile();
                    dbManager.clearUser();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                break;
        }

    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(Kenna.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        preferences.closeProfile();
                        dbManager.clearUser();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(false);
        addMaker();
    }

    void addMaker() {
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


    }

    private void locationPlacesIntent() {
        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Log.e("error lat", String.valueOf(e));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    // TODO: 20/07/2017 Aumente el valor para acercar.
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
                    googleMap.animateCamera(cameraUpdate);
                    dbManager.updateLoc(dbManager.getUser().getId(), latLng.latitude, latLng.longitude);
                    googleMap.clear();
                    addMaker();
                } else {
                    Log.e("error", "sdfsgrfger");

                }
            }
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(textviewTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(linearlayoutTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
