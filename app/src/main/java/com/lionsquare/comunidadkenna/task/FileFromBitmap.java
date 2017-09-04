package com.lionsquare.comunidadkenna.task;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.LostRegisterActivity;
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter;
import com.lionsquare.comunidadkenna.utils.DialogGobal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by EDGAR ARANA on 30/08/2017.
 */

public class FileFromBitmap extends AsyncTask<Void, Integer, List<MultipartBody.Part>> {

    Activity activity;

    List<String> images;
    DialogGobal dialogGobal;
    List<MultipartBody.Part> files = new ArrayList<>();
    CommunicationChannel mCommChListner = null;


    public FileFromBitmap(List<String> images, Activity activity) {
        this.images = images;
        this.activity = activity;
        dialogGobal = new DialogGobal(activity);


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // before executing doInBackground
        // update your UI
        // exp; make progressbar visible
        dialogGobal.setDialogContent(
                activity.getString(R.string.espere),
                activity.getString(R.string.procesando_imagen), true);
    }

    @Override
    protected List<MultipartBody.Part> doInBackground(Void... params) {

        for (int pos = 0; pos < images.size(); pos++) {
            String item = images.get(pos);
            File file = new File(item);
            RequestBody file1 = RequestBody.create(MediaType.parse("multipart/form-data"), savebitmap(file));
            MultipartBody.Part part1 = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), file1);
            files.add(part1);
        }

        return files;
    }


    @Override
    protected void onPostExecute(List<MultipartBody.Part> files) {
        super.onPostExecute(files);
        dialogGobal.dimmis();
        if (!files.isEmpty()) {
            if (mCommChListner != null) {
                mCommChListner.setCommunication(files, images);
            }

        } else {
            Toast.makeText(activity, "vacio", Toast.LENGTH_SHORT).show();
        }


    }

    private File savebitmap(File fileName) {
        File file = null;
        try {
            file = new Compressor(activity).compressToFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;

    }


    public interface CommunicationChannel {
        public void setCommunication(List<MultipartBody.Part> files, List<String> images);
    }


    public void setmCommChListner(CommunicationChannel mCommChListner) {
        this.mCommChListner = mCommChListner;
    }
}
