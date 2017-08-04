package com.lionsquare.comunidadkenna.activitys;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

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
import com.lionsquare.comunidadkenna.api.RBParseo;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLostBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.odn.selectorimage.view.ImageSelectorActivity;


import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.R.attr.editable;


public class LostActivity extends AppCompatActivity implements OnMapReadyCallback, Callback<Response>, View.OnClickListener {

    private GoogleMap googleMap;

    ActivityLostBinding binding;
    private DbManager dbManager;
    private DialogGobal dialogGobal;
    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost);
        dbManager = new DbManager(this).open();
        dialogGobal = new DialogGobal(this);
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
        initSetUp();
    }

    void initSetUp() {
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
            for (int i = 0; i < images.size(); i++) {
                Log.e("image", images.get(i));
            }
            sendData(images);
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
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        dialogGobal.dimmis();
    }

    void sendData(ArrayList<String> images) {
        dialogGobal.progressIndeterminateStyle();
        List<MultipartBody.Part> files = new ArrayList<>();
        for (int pos = 0; pos < images.size(); pos++) {
            String item = images.get(pos);
            File file = new File(item);
            RequestBody file1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part1 = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), file1);
            files.add(part1);
        }

        User user = dbManager.getUser();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<Response> call = serviceApi.sendReportLostPet(
                RBParseo.parseoText(user.getEmail()),
                RBParseo.parseoText(user.getToken()),
                RBParseo.parseoText(String.valueOf(user.getLng())),
                RBParseo.parseoText(String.valueOf(user.getLng())),
                RBParseo.parseoText("pet"),
                RBParseo.parseoText("breed"),
                RBParseo.parseoText("1"),
                RBParseo.parseoText("100"),
                files
        );
        call.enqueue(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_btn_change_loc:
                locationPlacesIntent();
                break;
        }
    }
}
