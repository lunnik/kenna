package com.lionsquare.kenna.activitys;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_picker);
        binding.placeSearchDialogOkTV.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    LatLng latLng = place.getLatLng();
                    //MapModel mapModel = new MapModel(latLng.latitude + "", latLng.longitude + "");
                    //ChatModel chatModel = new ChatModel(userModel, Calendar.getInstance().getTime().getTime() + "", mapModel);
                    //  mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(chatModel);
                } else {
                    //PLACE IS NULL
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
        }
    }

    @Override
    public void onClick(View v) {

    }
}
