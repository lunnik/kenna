package com.lionsquare.comunidadkenna.activitys;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.lionsquare.comunidadkenna.adapter.SpinnerCustomAdapter;
import com.lionsquare.comunidadkenna.api.RBParseo;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLostBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.model.Breed;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.SpinnerObject;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.task.FileFromBitmap;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.widgets.NumberTextWatcher;
import com.lionsquare.multiphotopicker.photopicker.activity.PickImageActivity;
import com.odn.selectorimage.view.ImageSelectorActivity;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import thebat.lib.validutil.ValidUtils;


public class LostRegisterActivity extends AppCompatActivity implements
        OnMapReadyCallback, Callback<Response>, View.OnClickListener, AdapterView.OnItemSelectedListener, FileFromBitmap.CommunicationChannel {

    private GoogleMap googleMap;

    ActivityLostBinding binding;
    private DbManager dbManager;
    private DialogGobal dialogGobal;
    private static final int PLACE_PICKER_REQUEST = 1;
    private double lat, lng;
    private User user;
    public List<MultipartBody.Part> files;
    private ImagePetAdapter imagePetAdapter;
    private String breed;

    public ArrayList<SpinnerObject> CustomListViewValuesArr;


    private static final int READ_STORAGE_CODE = 1001;
    private static final int WRITE_STORAGE_CODE = 1002;

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

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        //binding.titleToolbar.setText("Registrar perdida");


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        binding.alBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 27/07/2017 contexto , num de fotos, moddo simpelo multiple ver camara, y preview
                //ImageSelectorActivity.start(LostRegisterActivity.this, 5, 1, false, false, true);
                openImagePickerIntent();
            }
        });
        binding.alTxtMoney.addTextChangedListener(new NumberTextWatcher(binding.alTxtMoney, "#,###"));

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

        binding.alSpBreed.setOnItemSelectedListener(this);

        binding.alBtnChangeLoc.setOnClickListener(this);


        lat = user.getLat();
        lng = user.getLng();

        CustomListViewValuesArr = new ArrayList<SpinnerObject>();
        List<String> listBreed = Breed.breedList();
        for (int i = 0; i < listBreed.size(); i++) {
            CustomListViewValuesArr.add(i, new SpinnerObject(listBreed.get(i)));
        }
        Resources res = getResources();
        SpinnerCustomAdapter adapter = new SpinnerCustomAdapter(this, R.layout.spinner_dropdown, CustomListViewValuesArr, res);
        binding.alSpBreed.setAdapter(adapter);
       /* ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Breed.breedList());
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.alSpBreed.setAdapter(dataAdapter);*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_pet, menu);
        // MenuItem item = menu.findItem(R.id.action_est_tec);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send) {
            if (ValidUtils.isNetworkAvailable(this))
                sendData();
            else dialogGobal.sinInternet(this);
            return true;
        }

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                    updateLoc(latLng);
                } else {
                    Log.e("error", "sdfsgrfger");

                }
            }
        }

        if (resultCode == RESULT_OK && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            ArrayList<String> images = data.getExtras().getStringArrayList(PickImageActivity.KEY_DATA_RESULT);
            if (images != null && !images.isEmpty()) {
                StringBuilder sb = new StringBuilder("");
                for (int i = 0; i < images.size(); i++) {
                    sb.append("Photo" + (i + 1) + ":" + images.get(i));
                    sb.append("\n");
                }
                Log.e("images", sb.toString());

            }
            // TODO: 30/08/2017 este asyntask es para reducir el tamaño de lafotos
            FileFromBitmap fileFromBitmap = new FileFromBitmap(images, this);
            fileFromBitmap.setmCommChListner(this);
            fileFromBitmap.execute();

         /*   for (int pos = 0; pos < images.size(); pos++) {
                String item = images.get(pos);
                File file = new File(item);
                RequestBody file1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part1 = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), file1);
                files.add(part1);
            }*/

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
            new MaterialDialog.Builder(this)
                    .title(R.string.send)
                    .content(R.string.se_envio_la_alerta_correctamente)
                    .cancelable(false)
                    .positiveText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    })
                    .show();
        } else if (response.body().getSuccess() == 2) {
            new MaterialDialog.Builder(this)
                    .title(R.string.error)
                    .content(response.body().getMessage())
                    .cancelable(false)
                    .positiveText(R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .show();
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(this);
        } else {
            new MaterialDialog.Builder(this)
                    .title(R.string.error)
                    .content(R.string.ocurrio_un_error_al_procesar_tu_solicitud)
                    .cancelable(true)
                    .positiveText(R.string.reintentar)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (ValidUtils.isNetworkAvailable(LostRegisterActivity.this))
                                sendData();
                            else
                                dialogGobal.sinInternet(LostRegisterActivity.this);
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        dialogGobal.dimmis();
        dialogGobal.errorConexionFinish(this);
        Log.e("error", t + "");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.al_btn_change_loc:
                locationPlacesIntent();
                break;
        }
    }

    private void sendData() {

        // Reset errors.
        binding.alTxtNamePet.setError(null);
        binding.alTxtMoney.setError(null);


        // Store values at the time of the login attempt.
        String namePet = binding.alTxtNamePet.getText().toString();


        String reward = "0";
        String money = "0";

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (breed.equals("")) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            binding.alSpBreed.startAnimation(shake);
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(namePet)) {
            binding.alTxtNamePet.setError(getString(R.string.error_field_required));
            focusView = binding.alTxtNamePet;
            cancel = true;
        } else if (files.isEmpty()) {
            dialogGobal.setDialogContent(getResources().getString(R.string.faltan_datos), getResources().getString(R.string.debes_agregar_al_menos_una_foto), false);
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            binding.alBtnPhoto.startAnimation(shake);
            cancel = true;
        }

        if (binding.alCbReward.isChecked()) {
            reward = "1";
            String s = binding.alTxtMoney.getText().toString();
            Log.e("money", s);
            if (s.length() > 0) {
                money = binding.alTxtMoney.getText().toString();

                int length = money.length();
                String result = "";
                for (int i = 0; i < length; i++) {
                    Character character = money.charAt(i);
                    if (Character.isDigit(character)) {
                        result += character;
                    }
                }
                money = result.substring(0, result.length() - 2);
            } else {
                binding.alTxtMoney.setError(getString(R.string.error_field_required));
                focusView = binding.alTxtMoney;
                cancel = true;
            }


        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            dialogGobal.progressIndeterminateStyle();

            ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
            Call<Response> call = serviceApi.sendReportLostPet(
                    RBParseo.parseoText(user.getEmail()),
                    RBParseo.parseoText(user.getToken()),
                    RBParseo.parseoText(String.valueOf(lat)),
                    RBParseo.parseoText(String.valueOf(lng)),
                    RBParseo.parseoText(binding.alTxtNamePet.getText().toString()),
                    RBParseo.parseoText(breed),
                    RBParseo.parseoText(reward),
                    RBParseo.parseoText(money),
                    files,
                    RBParseo.parseoText(String.valueOf(Calendar.getInstance().getTime()))
            );
            call.enqueue(this);
        }
    }

    private void openImagePickerIntent() {

        Intent mIntent = new Intent(this, PickImageActivity.class);
        mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 5);
        mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1);
        startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            breed = "";
        } else {
            breed = parent.getItemAtPosition(position).toString();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void setCommunication(List<MultipartBody.Part> files, List<String> images) {
        this.files = files;
        imagePetAdapter = new ImagePetAdapter(LostRegisterActivity.this, images);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(LostRegisterActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.alRvImage.setLayoutManager(horizontalLayoutManagaer);
        binding.alRvImage.setAdapter(imagePetAdapter);
    }
}
