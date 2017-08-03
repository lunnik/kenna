package com.lionsquare.comunidadkenna.activitys;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.api.RBParseo;
import com.lionsquare.comunidadkenna.api.ServiceApi;
import com.lionsquare.comunidadkenna.databinding.ActivityLostBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.model.Response;
import com.lionsquare.comunidadkenna.model.User;
import com.odn.selectorimage.view.ImageSelectorActivity;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class LostActivity extends AppCompatActivity implements OnMapReadyCallback, Callback<Response> {

    private GoogleMap googleMap;

    ActivityLostBinding binding;
    private DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lost);
        dbManager = new DbManager(this).open();
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
        initSetUp();
    }

    void initSetUp() {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            for (int i = 0; i < images.size(); i++) {
                Log.e("image", images.get(i));
            }
            sendData(images);
            //startActivity(new Intent(this,SelectResultActivity.class).putExtra(SelectResultActivity.EXTRA_IMAGES,images));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

    }

    @Override
    public void onFailure(Call<Response> call, Throwable t) {

    }

    void sendData(ArrayList<String> images) {

        List<MultipartBody.Part> files = new ArrayList<>();
        for (int pos = 0; pos < images.size(); pos++) {
            String item = images.get(pos);
            File file = new File(item);
            RequestBody file1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part1 = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), file1);
            //RequestBody to_server = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            //filestosend.put("photo_" + 0 + String.valueOf(pos + 1), to_server);
            files.add(part1);
        }

        User user = dbManager.getUser();
        ServiceApi serviceApi = ServiceApi.retrofit.create(ServiceApi.class);
        Call<Response> call = serviceApi.sendReportLostPet(
                RBParseo.parseoText(user.getEmail()),
                RBParseo.parseoText(user.getToken()),
                RBParseo.parseoText(String.valueOf(user.getLng())),
                RBParseo.parseoText(String.valueOf(user.getLng())),
                RBParseo.parseoText("pet"),
                RBParseo.parseoText("breed"),
                RBParseo.parseoText("1"),
                RBParseo.parseoText("100"),
                files
        );
        call.enqueue(this);

    }
}
