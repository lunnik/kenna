package com.lionsquare.kenna.activitys;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.lionsquare.kenna.R;
import com.lionsquare.kenna.databinding.ActivityLostBinding;


import java.util.ArrayList;


public class LostActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    ActivityLostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        binding.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 27/07/2017 contexto , num de fotos, moddo simpelo multiple ver camara, y preview
                ImageSelectorActivity.start(LostActivity.this, 5, 1, false, false, true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            for (int i = 0; i < images.size(); i++) {
                Log.e("image", images.get(i));
            }

            //startActivity(new Intent(this,SelectResultActivity.class).putExtra(SelectResultActivity.EXTRA_IMAGES,images));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
