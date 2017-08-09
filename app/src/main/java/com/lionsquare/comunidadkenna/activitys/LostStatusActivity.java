package com.lionsquare.comunidadkenna.activitys;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.databinding.ActivityLostStatusBinding;

public class LostStatusActivity extends AppCompatActivity {
    ActivityLostStatusBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_status);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost_status);
    }
}
