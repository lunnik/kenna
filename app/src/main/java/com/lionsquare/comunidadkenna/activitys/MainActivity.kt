package com.lionsquare.comunidadkenna.activitys

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.airbnb.lottie.LottieAnimationView
import com.android.vending.billing.IInAppBillingService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.iid.FirebaseInstanceId
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.firebase.MyFirebaseInstanceIdService
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences

import org.json.JSONException

import thebat.lib.validutil.ValidUtils

class MainActivity : AppCompatActivity() {

    private var preferences: Preferences? = null
    private var dialogGobal: DialogGobal? = null

    private val receiverToken = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //String json = intent.getExtras().getString("json");
            val i = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(i)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialogGobal = DialogGobal(this)

        preferences = Preferences(this)


        // TODO: 15/08/2017 avisa de cuando el token ya esta listo
        val filterDeleteMmember = IntentFilter()
        filterDeleteMmember.addAction(INTENT_FILTER_SPLAH)
        registerReceiver(receiverToken, filterDeleteMmember)

        // TODO: 17/08/2017 salta el login despuesde  de logout
        if (preferences!!.token != "") {
            val menu = Intent(this@MainActivity, SplashActivity::class.java)
            startActivity(menu)
            finish()
            return
        }
        // TODO: 17/08/2017 si ya existe un  token pasar a login
        if (FirebaseInstanceId.getInstance().token != null) {
            if (FirebaseInstanceId.getInstance().token != "") {
                val menu = Intent(this@MainActivity, SplashActivity::class.java)
                startActivity(menu)
                finish()
                return

            }
        }


        if (!ValidUtils.isNetworkAvailable(this)) {
            dialogGobal!!.sinInternet(this)
        }


        val animationView = findViewById(R.id.animation_view) as LottieAnimationView
        animationView.setAnimation("location_pin.json")
        animationView.loop(true)
        animationView.playAnimation()


    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(receiverToken)

    }

    companion object {
        val INTENT_FILTER_SPLAH = "INTENT_FILTER_SPLASH"
    }


}
