package com.lionsquare.comunidadkenna.activitys;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.lionsquare.comunidadkenna.adapter.PagerPetAdapter;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityDetailsLostBinding;
import com.lionsquare.comunidadkenna.model.Pet;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.User;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.lionsquare.comunidadkenna.utils.StatusBarUtil;
import com.lionsquare.comunidadkenna.widgets.CustomToast;
import com.txusballesteros.AutoscaleEditText;
import com.wafflecopter.charcounttextview.CharCountTextView;

import retrofit2.Call;
import retrofit2.Callback;

public class DetailsLostActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, Callback<Response> {
    ActivityDetailsLostBinding binding;
    private Pet pl;
    private User user;
    private PagerPetAdapter pagerPetAdapter;
    private GoogleMap googleMap;
    private Circle mCircle;
    private AutoscaleEditText aetComment;
    private Preferences preferences;
    private DialogGobal dialogGobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details_lost);
        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        if (getIntent().getExtras() != null) {

            // TODO: 21/08/17 cuando viene de la lita
            if (getIntent().getExtras().getParcelable("model") != null && getIntent().getExtras().getParcelable("user") != null) {
                pl = (Pet) getIntent().getExtras().getParcelable("model");
                user = (User) getIntent().getExtras().getParcelable("user");
                initSetUp();

            }

            if (getIntent().getExtras().get("id") != null) {

                Log.e("id", String.valueOf(getIntent().getExtras().getInt("id")));
            }


        }

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

    void initSetUp() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");

        }

        pagerPetAdapter = new PagerPetAdapter(this, pl.getImages());
        binding.adlVpPet.setAdapter(pagerPetAdapter);
        binding.adlCiIndicator.setViewPager(binding.adlVpPet);


        binding.titleToolbar.setText(pl.getNamePet());

        CharCountTextView tvCharCount = (CharCountTextView) findViewById(R.id.tvTextCounter);
        aetComment = (AutoscaleEditText) findViewById(R.id.adl_aet_comment);
        final CardView cvSend = (CardView) findViewById(R.id.adl_cv_send);

        cvSend.setOnClickListener(this);

        tvCharCount.setEditText(aetComment);
        tvCharCount.setMaxCharacters(150); //Will default to 150 anyway (Twitter emulation)
        tvCharCount.setExceededTextColor(Color.RED); //Will default to red also
        tvCharCount.setCharCountChangedListener(new CharCountTextView.CharCountChangedListener() {
            @Override
            public void onCountChanged(int countRemaining, boolean hasExceededLimit) {
                if (hasExceededLimit) {
                    cvSend.setEnabled(false);
                } else {
                    cvSend.setEnabled(true);
                }
            }
        });
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, binding.adlContent);
        StatusBarUtil.setPaddingSmart(this, binding.toolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));


        binding.adlTvName.setText(pl.getNamePet());
        binding.adlTvBreed.setText(pl.getBreed());
        binding.adlTvData.setText(converteTimestamp(pl.getTimestamp()));

        if (pl.getReward() == 1) {
            binding.adlLlReward.setVisibility(View.GONE);
        }
        binding.adlTvDistace.setText("Se perdio a " + pl.getDistance() + " metros de tu ubicación");

        if (pl.getUser() == null) {
            Log.e("vacio", "sfsfd");
        } else {
            Log.e("no vacio", "sdfsfds");
        }
        binding.adlTvNamePropetary.setText(user.getName());
        binding.adlTvDatos.setText(user.getEmail());
        Glide.with(this).load(user.getProfile_pick()).centerCrop().into(binding.adlCivProfile);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        addMaker();

    }

    void addMaker() {
        googleMap.clear();
        this.googleMap.getUiSettings().setScrollGesturesEnabled(false);
        this.googleMap.getUiSettings().setAllGesturesEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);

        LatLng latLng = new LatLng(Double.valueOf(pl.getLat()), Double.valueOf(pl.getLng()));
        Marker marker = googleMap.addMarker(
                new MarkerOptions().position(latLng));

        mCircle = googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(1000)
                .strokeColor(getResources().getColor(R.color.blue_circul))
                .strokeWidth(3)
                .fillColor(getResources().getColor(R.color.blue_circul))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // TODO: 20/07/2017 Aumente el valor para acercar.
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.animateCamera(cameraUpdate);


    }

    private CharSequence converteTimestamp(String mileSegundos) {
        Long time = Long.parseLong(mileSegundos) * 1000;
        String txt = (String) DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        return Character.toUpperCase(txt.charAt(0)) + txt.substring(1);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adl_cv_send:
                String comment = aetComment.getText().toString();
                if (!comment.trim().isEmpty()) {
                    sendComment(comment);
                } else {
                    CustomToast.show(this, getResources().getString(R.string.mensaje_vacio), false);
                }
                break;
        }
    }

    void sendComment(String comment) {
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<Response> call = serviceApi.sendCommentPetLost(preferences.getEmail(), preferences.getToken(), pl.getId(), comment);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
        if (response.body().getSuccess() == 1) {
            dialogGobal.correctSend(this);
        } else if (response.body().getSuccess() == 2) {
            dialogGobal.setDialogContent(getResources().getString(R.string.vacio), response.body().getMessage());
        } else if (response.body().getSuccess() == 0) {
            dialogGobal.tokenDeprecated(this);
        }


    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {
        dialogGobal.errorConexion();
        Log.e("errro ", t + "");
    }
}
