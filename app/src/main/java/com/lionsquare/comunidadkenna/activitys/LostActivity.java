package com.lionsquare.comunidadkenna.activitys;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.CompoundButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.adapter.ImagePetAdapter;
import com.lionsquare.comunidadkenna.api.RBParseo;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLostBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.odn.selectorimage.view.ImageSelectorActivity;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class LostActivity extends AppCompatActivity implements OnMapReadyCallback, Callback<Response>, View.OnClickListener {

    private GoogleMap googleMap;

    ActivityLostBinding binding;
    private DbManager dbManager;
    private DialogGobal dialogGobal;
    private static final int PLACE_PICKER_REQUEST = 1;
    private double lat, lng;
    private User user;
    private List<MultipartBody.Part> files;
    private ImagePetAdapter imagePetAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost);
        dbManager = new DbManager(this).open();
        dialogGobal = new DialogGobal(this);
        user = dbManager.getUser();
        files = new ArrayList<>();

        initSetUp();
    }

    void initSetUp() {

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        binding.alBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 27/07/2017 contexto , num de fotos, moddo simpelo multiple ver camara, y preview
                ImageSelectorActivity.start(LostActivity.this, 5, 1, false, false, true);
            }
        });

        if (binding.alCbReward.isChecked())
            binding.alTxtMoney.setVisibility(View.VISIBLE);
        else
            binding.alTxtMoney.setVisibility(View.GONE);

        binding.alCbReward.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    binding.alTxtMoney.setVisibility(View.VISIBLE);
                else
                    binding.alTxtMoney.setVisibility(View.GONE);
            }
        });

        binding.alBtnChangeLoc.setOnClickListener(this);
        binding.alBtnSend.setOnClickListener(this);

        lat = user.getLat();
        lng = user.getLng();


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
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);

            imagePetAdapter = new ImagePetAdapter(LostActivity.this, images);

            LinearLayoutManager horizontalLayoutManagaer
                    = new LinearLayoutManager(LostActivity.this, LinearLayoutManager.HORIZONTAL, false);
            binding.alRvImage.setLayoutManager(horizontalLayoutManagaer);
            binding.alRvImage.setAdapter(imagePetAdapter);

            for (int pos = 0; pos < images.size(); pos++) {
                String item = images.get(pos);
                File file = new File(item);
                RequestBody file1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part1 = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), file1);
                files.add(part1);
            }
            binding.alBtnPhoto.setText("Cambiar fotos");
            //startActivity(new Intent(this,SelectResultActivity.class).putExtra(SelectResultActivity.EXTRA_IMAGES,images));
        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    updateLoc(latLng);
                } else {
                    Log.e("error", "sdfsgrfger");

                }
            }
        }

    }

    void updateLoc(LatLng latLng) {
        googleMap.clear();
        Marker marker = googleMap.addMarker(
                new MarkerOptions().position(latLng));

        Circle mCircle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(500)
                .strokeColor(getResources().getColor(R.color.blue_circul))
                .strokeWidth(3)
                .fillColor(getResources().getColor(R.color.blue_circul))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.animateCamera(cameraUpdate);
        lat = latLng.latitude;
        lng = latLng.longitude;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
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
        this.googleMap.setMyLocationEnabled(false);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                addMaker();

            }
        };

        handler.postDelayed(r, 1000);
    }

    void addMaker() {
        googleMap.clear();
        User user = dbManager.getUser();
        LatLng latLng = new LatLng(user.getLat(), user.getLng());
        Marker marker = googleMap.addMarker(
                new MarkerOptions().position(latLng));

        Circle mCircle = googleMap.addCircle(new CircleOptions()
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

    @Override
    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
        dialogGobal.dimmis();
        if (response.body().getSuccess() == 1) {
            dialogGobal.correctSend(this);
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.error)
                    .content(R.string.ocurrio_un_error_al_procesar_tu_solicitud)
                    .cancelable(true)
                    .positiveText(R.string.reintentar)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            sendData();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        dialogGobal.dimmis();
        dialogGobal.errorConexionFinish(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_btn_change_loc:
                locationPlacesIntent();
                break;
            case R.id.al_btn_send:
                sendData();
                break;
        }
    }

    private void sendData() {

        // Reset errors.
        binding.alTxtNamePet.setError(null);
        binding.alTxtBreed.setError(null);
        binding.alBtnPhoto.setError(null);

        // Store values at the time of the login attempt.
        String namePet = binding.alTxtNamePet.getText().toString();
        String breed = binding.alTxtBreed.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (breed.equals("")) {
            binding.alTxtBreed.setError(getString(R.string.error_field_required));
            focusView = binding.alTxtBreed;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(namePet)) {
            binding.alTxtNamePet.setError(getString(R.string.error_field_required));
            focusView = binding.alTxtNamePet;
            cancel = true;
        } else if (files.isEmpty()) {
            binding.alBtnPhoto.setError(getString(R.string.error_field_photo));
            focusView = binding.alBtnPhoto;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            dialogGobal.progressIndeterminateStyle();

            String reward = "0";
            String money = "0";
            if (binding.alCbReward.isChecked()) {
                reward = "1";
                money = binding.alTxtMoney.getText().toString();
            }


            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<Response> call = serviceApi.sendReportLostPet(
                    RBParseo.parseoText(user.getEmail()),
                    RBParseo.parseoText(user.getToken()),
                    RBParseo.parseoText(String.valueOf(lat)),
                    RBParseo.parseoText(String.valueOf(lng)),
                    RBParseo.parseoText(binding.alTxtNamePet.getText().toString()),
                    RBParseo.parseoText(binding.alTxtBreed.getText().toString()),
                    RBParseo.parseoText(reward),
                    RBParseo.parseoText(money),
                    files
            );
            call.enqueue(this);
        }
    }


}
