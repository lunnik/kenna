package com.lionsquare.comunidadkenna.activitys;

import android.databinding.DataBindingUtil;
import android.databinding.adapters.FrameLayoutBindingAdapter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.databinding.ActivityDetailsLostBinding;
import com.lionsquare.comunidadkenna.model.Pet;
import com.lionsquare.comunidadkenna.model.PetLost;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

public class DetailsLostActivity extends AppCompatActivity {
    ActivityDetailsLostBinding binding;
    private PetLost pl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_lost);
        if (getIntent().getExtras() != null) {
            pl = (PetLost) getIntent().getExtras().getParcelable("model");
        }

        initSetUp();
    }


    void initSetUp() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

        }


       /* RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.toolbar.getLayoutParams();
        params.setMargins(0, getStatusBarHeight(), 0, 0);
        binding.toolbar.setLayoutParams(params);*/

        //binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
       /* RelativeLayout.LayoutParams pFrame = (RelativeLayout.LayoutParams) binding.adlContent.getLayoutParams();
        pFrame.setMargins(0, getStatusBarHeight() + getToolBarHeight(), 0, 0);
        binding.adlContent.setLayoutParams(pFrame);*/
        //binding.adlContent.setPadding(0, getStatusBarHeight() + getToolBarHeight(), 0, 0);

        binding.titleToolbar.setText(pl.getNamePet());
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this,  binding.adlContent);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));


        binding.adlTvName.setText(pl.getNamePet());
        binding.adlTvBreed.setText(pl.getBreed());
        binding.adlTvData.setText(pl.getTimestamp());
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


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    int getToolBarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }


}
