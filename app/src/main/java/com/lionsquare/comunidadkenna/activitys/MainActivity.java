package com.lionsquare.comunidadkenna.activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.firebase.MyFirebaseInstanceIdService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("location_pin.json");
        animationView.loop(true);
        animationView.playAnimation();
       /* finish();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);*/
    }
}
