package com.lionsquare.comunidadkenna.activitys;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        StatusBarUtil.darkMode(this);
        initSetUp();
    }

    void initSetUp() {

        binding.blurredView.setBackgroundResource(R.drawable.back_menu);
        binding.blurredView.setAdjustViewBounds(true);
        binding.blurredView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        binding.amBtnProfile.setOnClickListener(this);
        binding.amBtnLost.setOnClickListener(this);
        binding.amBtnWall.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent iMenu = null;
        switch (v.getId()) {
            case R.id.am_btn_profile:
                iMenu = new Intent(this, ProfileActivity.class);
                break;
            case R.id.am_btn_lost:
                iMenu = new Intent(this, LostActivity.class);
                break;
            case R.id.am_btn_wall:
                iMenu = new Intent(this, WallPetActivity.class);
                break;
        }

        if (iMenu != null)
            startActivity(iMenu);


    }
}
