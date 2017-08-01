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
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLocationPickerBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.model.CheckoutLogin;
import com.lionsquare.comunidadkenna.model.RecoverProfile;
import com.lionsquare.comunidadkenna.model.Register;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationPickerActivity extends AppCompatActivity implements View.OnClickListener, Callback<CheckoutLogin> {
    ActivityLocationPickerBinding binding;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int PERMIS_LOCATION = 1;
    private Preferences preferences;
    private DbManager dbManager;

    private DialogGobal dialogGobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_picker);
        StatusBarUtil.darkMode(this);
        dialogGobal = new DialogGobal(this);
        initSetUp();

    }

    private void initSetUp() {
        preferences = new Preferences(this);
        dbManager = new DbManager(this).open();

        if (dbManager.getUser() != null) {
            Intent iMenu = new Intent(this, MenuActivity.class);
            startActivity(iMenu);
            finish();
        } else {
            binding.placeSearchDialogOkTV.setOnClickListener(this);
            binding.alpBtnStar.setVisibility(View.GONE);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                verifyPermission();
            } else {
                binding.placeSearchDialogOkTV.setEnabled(true);
                checkoutLogin();
            }
        }


    }

    void checkoutLogin() {
        dialogGobal.progressIndeterminateStyle();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<CheckoutLogin> call = serviceApi.checkoutEmail(preferences.getEmail());
        call.enqueue(this);
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
                    sendPrefile(latLng);
                    binding.alpBtnStar.setVisibility(View.VISIBLE);
                    binding.placeSearchDialogOkTV.setVisibility(View.GONE);
                    binding.placeSearchDialogCancelTV.setVisibility(View.GONE);
                } else {
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
        locationPlacesIntent();

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
            checkoutLogin();
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMIS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //saveComments();
                binding.placeSearchDialogOkTV.setEnabled(true);
                checkoutLogin();
            } else {
                binding.placeSearchDialogOkTV.setEnabled(false);
                showSnackBar();
            }
        }
    }

    private void sendPrefile(final LatLng latLng) {
        dialogGobal.progressIndeterminateStyle();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<Register> call = serviceApi.registerProfile(preferences.getName(), preferences.getEmail()
                , preferences.getImagePerfil(), FirebaseInstanceId.getInstance().getToken(), preferences.getTypeLogin(), latLng.latitude, latLng.longitude);

        call.enqueue(new Callback<Register>() {
            @Override
            public void onResponse(Call<Register> call, Response<Register> response) {
                dialogGobal.dimmis();
                dbManager.insertUser(preferences.getName(), preferences.getEmail(), preferences.getImagePerfil(),
                        preferences.getCover(), preferences.getTypeLogin(), preferences.getTokenSosial(), "token Fire",
                        latLng.latitude, latLng.longitude);
                Intent iMenu = new Intent(LocationPickerActivity.this, MenuActivity.class);
                startActivity(iMenu);
                finish();
            }

            @Override
            public void onFailure(Call<Register> call, Throwable t) {
                dialogGobal.dimmis();
                Log.e("error de conexion", String.valueOf(t));

                new MaterialDialog.Builder(LocationPickerActivity.this)
                        .title(R.string.error)
                        .content(R.string.ocurrio_un_error_al_contectar)
                        .cancelable(false)
                        .negativeText(R.string.intentar_otra_vez)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                sendPrefile(latLng);
                            }
                        })
                        .positiveText(R.string.salir)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                finish();
                            }
                        })
                        .progressIndeterminateStyle(true)
                        .show();
            }
        });

    }

    @Override
    public void onResponse(Call<CheckoutLogin> call, Response<CheckoutLogin> response) {
        dialogGobal.dimmis();
        if (response.body().getSuccess() == 1) {
            // TODO: 31/07/2017 actulizamos el perfil ya sea que cambio de cuanta
            if (preferences.getTypeLogin() != response.body().getType_account())
                diferenteAccount();
            else
                recoverProfileData();
        }
    }

    @Override
    public void onFailure(Call<CheckoutLogin> call, Throwable t) {
        Log.e("error de conexion", String.valueOf(t));
        dialogGobal.dimmis();
    }

    // TODO: 31/07/2017 si se accede con la misma cuenta solo se recuperan los datos del servidor con el correo
    void recoverProfileData() {
        dialogGobal.setDialog(getResources().getString(R.string.recuperando_datos));
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<RecoverProfile> call = serviceApi.recoverProfile(preferences.getEmail());
        call.enqueue(new Callback<RecoverProfile>() {
            @Override
            public void onResponse(Call<RecoverProfile> call, Response<RecoverProfile> response) {
                dialogGobal.dimmis();
                if (response.body().getSuccess() == 1) {
                    User user = response.body().getUser();
                    dbManager.insertUser(preferences.getName(), preferences.getEmail(), preferences.getImagePerfil(),
                            preferences.getCover(), preferences.getTypeLogin(), preferences.getTokenSosial(), user.getToken(),
                            user.getLat(), user.getLat());
                    Intent iMenu = new Intent(LocationPickerActivity.this, MenuActivity.class);
                    startActivity(iMenu);
                    finish();
                    dbManager.close();
                }
            }

            @Override
            public void onFailure(Call<RecoverProfile> call, Throwable t) {
                dialogGobal.dimmis();
                Log.e("error", String.valueOf(t));
                dialogGobal.errorConexionFinish(LocationPickerActivity.this);
            }
        });

    }


    // TODO: 31/07/2017 cuando es iferentes ala cuenta con la que estabas pero es le mismo coreo se aztulizan los perfiles
    void diferenteAccount() {
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<RecoverProfile> call = serviceApi.updateProfile(
                preferences.getEmail(), preferences.getName(), preferences.getImagePerfil(), FirebaseInstanceId.getInstance().getToken(), preferences.getTypeLogin());
        call.enqueue(new Callback<RecoverProfile>() {
            @Override
            public void onResponse(Call<RecoverProfile> call, Response<RecoverProfile> response) {
                if (response.body().getSuccess() == 1) {
                    User user = response.body().getUser();
                    dbManager.insertUser(preferences.getName(), preferences.getEmail(), preferences.getImagePerfil(),
                            preferences.getCover(), preferences.getTypeLogin(), preferences.getTokenSosial(), user.getToken(),
                            user.getLat(), user.getLat());
                    Intent iMenu = new Intent(LocationPickerActivity.this, MenuActivity.class);
                    startActivity(iMenu);
                    finish();
                    dbManager.close();
                }
            }

            @Override
            public void onFailure(Call<RecoverProfile> call, Throwable t) {
                dialogGobal.errorConexionFinish(LocationPickerActivity.this);
            }
        });
    }
}
