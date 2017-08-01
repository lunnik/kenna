package com.lionsquare.comunidadkenna.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lionsquare.comunidadkenna.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        finish();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
