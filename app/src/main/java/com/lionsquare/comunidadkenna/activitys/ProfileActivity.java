package com.lionsquare.comunidadkenna.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.lionsquare.comunidadkenna.Kenna;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity   {


    private ImageView coverImage;

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
    private DialogGobal dialogGobal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getIntent().getExtras() != null) {
            boolean checkinToken = getIntent().getExtras().getBoolean("checkin_token");
            if (checkinToken) {

            }
        }


        toolbar.setTitle("");


        setSupportActionBar(toolbar);


        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        dbManager = new DbManager(this).open();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
      /*  SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
*/

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

       // btnLogOut.setOnClickListener(this);
        //btnChangeLoc.setOnClickListener(this);

        txtName.setText(preferences.getName());
        txtEmail.setText(preferences.getEmail());
        //textviewTitle.setText("Perfil");


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


    @Override
    protected void onResume() {
        super.onResume();

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







}
