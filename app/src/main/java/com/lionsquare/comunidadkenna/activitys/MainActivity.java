package com.lionsquare.comunidadkenna.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.firebase.MyFirebaseInstanceIdService;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;

import org.json.JSONException;

import thebat.lib.validutil.ValidUtils;

public class MainActivity extends AppCompatActivity {
    public static final String INTENT_FILTER_SPLAH = "INTENT_FILTER_SPLASH";
    private ValidUtils validUtils;
    private Preferences preferences;
    private DialogGobal dialogGobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validUtils = new ValidUtils();
        dialogGobal = new DialogGobal(this);

        preferences = new Preferences(this);

        // TODO: 15/08/2017 avisa de cuando el token ya esta listo
        IntentFilter filterDeleteMmember = new IntentFilter();
        filterDeleteMmember.addAction(INTENT_FILTER_SPLAH);
        registerReceiver(receiverToken, filterDeleteMmember);

        if (!preferences.getToken().equals("")) {
            Intent menu = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(menu);
            finish();
            return;
        }
        if (!FirebaseInstanceId.getInstance().getToken().equals("")) {
            Intent menu = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(menu);
            finish();
            return;
        }


        if (ValidUtils.isNetworkAvailable(this)) {
            // do whatever you want to do IF internet is AVAILABLE
        } else {
            dialogGobal.sinInternet(this);
        }


        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("location_pin.json");
        animationView.loop(true);
        animationView.playAnimation();


    }

    private BroadcastReceiver receiverToken = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String json = intent.getExtras().getString("json");
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiverToken);

    }

}
