package com.lionsquare.comunidadkenna.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

import retrofit2.Call;
import retrofit2.Callback;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener, Callback<Response> {
    ActivityMenuBinding binding;
    private static final int PERMISS_WRITE_EXTERNAL_STORAGE = 1;

    private Preferences preferences;
    private DialogGobal dialogGobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu);
        StatusBarUtil.darkMode(this);
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        initSetUp();
    }

    void initSetUp() {
        binding.amIvLostpet.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission();
        } else {
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }
        binding.blurredView.setBackgroundResource(R.drawable.back_menu);
        binding.blurredView.setScaleType(ImageView.ScaleType.FIT_XY);


        binding.amBtnProfile.setOnClickListener(this);
        binding.amBtnLost.setOnClickListener(this);
        binding.amBtnWall.setOnClickListener(this);
        binding.amIvLostpet.setOnClickListener(this);

        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<Response> call = serviceApi.checkinStatusFolio(preferences.getEmail(), preferences.getToken());
        call.enqueue(this);

    }

    @Override
    public void onClick(View v) {
        Intent iMenu = null;
        switch (v.getId()) {
            case R.id.am_btn_profile:
                iMenu = new Intent(this, ProfileActivity.class);
                break;
            case R.id.am_btn_lost:
                iMenu = new Intent(this, LostRegisterActivity.class);
                break;
            case R.id.am_btn_wall:
                iMenu = new Intent(this, WallPetActivity.class);
                break;
            case R.id.am_iv_lostpet:
                iMenu = new Intent(this, LostStatusActivity.class);
                break;
        }

        if (iMenu != null)
            startActivity(iMenu);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //saveComments();
                //binding.placeSearchDialogOkTV.setEnabled(true);
                //checkoutLogin();
            } else {
                //binding.placeSearchDialogOkTV.setEnabled(false);
                showSnackBar();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void verifyPermission() {
        int writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            //binding.placeSearchDialogOkTV.setEnabled(false);
        } else {
            //saveComments();
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSnackBar();
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISS_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(binding.amClRoot, R.string.permission_location, Snackbar.LENGTH_INDEFINITE);
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
    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
        if (response.body().getSuccess() == 1) {
            binding.amIvLostpet.setVisibility(View.VISIBLE);
        } else if (response.body().getSuccess() == 2) {
            // no hay folios
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(this);
        }
    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
      
    }
}
