package com.lionsquare.kenna.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.lionsquare.kenna.R;
import com.lionsquare.kenna.databinding.ActivityLocationPickerBinding;

public class LocationPickerActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLocationPickerBinding binding;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PERMIS_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_picker);
        binding.placeSearchDialogOkTV.setOnClickListener(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission();
        } else {
            binding.placeSearchDialogOkTV.setEnabled(true);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission();
        } else {
            binding.placeSearchDialogOkTV.setEnabled(true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    Log.e("lat", String.valueOf(latLng.latitude));
                    Log.e("lng", String.valueOf(latLng.longitude));
                    //MapModel mapModel = new MapModel(latLng.latitude + "", latLng.longitude + "");
                    //ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime() + "", mapModel);
                    //  mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                } else {
                    //PLACE IS NULL
                    Log.e("error", "sdfsgrfger");
                }
            }
        }

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
    public void onClick(View v) {
        if (binding.placeSearchDialogOkTV.isEnabled())
            locationPlacesIntent();
        else
            showSnackBar();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verifyPermission() {
        int writePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            binding.placeSearchDialogOkTV.setEnabled(false);
        } else {
            //saveComments();
            binding.placeSearchDialogOkTV.setEnabled(true);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showSnackBar();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMIS_LOCATION);
        }
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(binding.alpRoot, R.string.permission_location, Snackbar.LENGTH_INDEFINITE);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMIS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //saveComments();
                binding.placeSearchDialogOkTV.setEnabled(true);
            } else {
                showSnackBar();
                binding.placeSearchDialogOkTV.setEnabled(false);
            }
        }
    }

}
