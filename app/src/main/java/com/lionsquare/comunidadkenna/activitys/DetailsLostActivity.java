package com.lionsquare.comunidadkenna.activitys;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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

        }
        binding.titleToolbar.setText(pl.getNamePet());

        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.adlContent);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));
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

}
