package com.lionsquare.comunidadkenna.activitys


import android.content.Intent

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity

import android.os.Bundle


import android.util.Log
import android.view.View

import android.widget.ImageView


import android.widget.Toast


import com.airbnb.lottie.LottieAnimationView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.Profile
import com.facebook.ProfileTracker
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.Status
import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.model.people.Person
import com.google.firebase.iid.FirebaseInstanceId
import com.lionsquare.comunidadkenna.Kenna
import com.lionsquare.comunidadkenna.R

import com.lionsquare.comunidadkenna.databinding.ActivityLoginBinding
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences

import org.json.JSONException
import org.json.JSONObject


import java.util.Arrays

import thebat.lib.validutil.ValidUtils


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private var callbackManager: CallbackManager? = null
    private var profileTracker: ProfileTracker? = null


    private var preferences: Preferences? = null

    private var binding: ActivityLoginBinding? = null
    private var token: String? = ""
    private var dialogGobal: DialogGobal? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        facebookInit()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        googleAccount()
        faceBookComponent()


    }


    private fun init() {

        preferences = Preferences(this@LoginActivity)
        dialogGobal = DialogGobal(this)


        Log.e("bolaen", preferences!!.flag.toString())
        token = FirebaseInstanceId.getInstance().token

        if (preferences!!.flag) {
            val menu = Intent(this@LoginActivity, LocationPickerActivity::class.java)
            startActivity(menu)
            finish()
        }
        if (ValidUtils.isNetworkAvailable(this)) {
        } else {
            dialogGobal!!.sinInternet(this)

        }

    }


    private fun facebookInit() {
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager!!,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {


                        val request = GraphRequest.newMeRequest(loginResult.accessToken
                        ) { `object`, response ->
                            val profile = Profile.getCurrentProfile()
                            val urlImgaeProfil = profile.getProfilePictureUri(200, 200)
                            val urlImage = urlImgaeProfil.toString()
                            Log.e("url", urlImage)

                            Log.e("LoginActivity Response ", response.toString())
                            var cover = ""
                            try {
                                cover = `object`.getJSONObject("cover").getString("source")

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                            try {

                                saveData(loginResult.accessToken.toString(),
                                        profile.name,
                                        `object`.getString("email"),
                                        urlImage,
                                        cover,
                                        Kenna.Facebook,
                                        token)


                                val menu = Intent(this@LoginActivity, LocationPickerActivity::class.java)
                                startActivity(menu)
                                finish()

                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Log.e("JSONException = ", " " + e)
                            }
                        }

                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email,cover")
                        request.parameters = parameters
                        request.executeAsync()


                    }

                    override fun onCancel() {
                        Toast.makeText(this@LoginActivity, "Login cancelado", Toast.LENGTH_LONG).show()
                        Log.e("onCancel", "Login canceled")
                    }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(this@LoginActivity, exception.message, Toast.LENGTH_LONG).show()
                        Log.e("onError", "Login error" + exception.message)
                    }
                })
    }

    private fun googleAccount() {


        binding!!.cvSignIn.setOnClickListener(this)
        binding!!.btnSignOut.setOnClickListener(this)
        binding!!.btnRevokeAccess.setOnClickListener(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope(Scopes.PROFILE))
                .requestScopes(Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .requestProfile()
                .build()

        Kenna.mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build()

        // Customzing G+ button
        //btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        //btnSignIn.setScopes(gso.getScopeArray());
    }

    private fun faceBookComponent() {
        binding!!.blurredView.setBackgroundResource(R.drawable.back_login)
        binding!!.blurredView.adjustViewBounds = true
        binding!!.blurredView.scaleType = ImageView.ScaleType.CENTER_CROP


        binding!!.cvFbLogin.setOnClickListener {
            Log.e("Presion btn facebook", "")
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("public_profile", "user_friends", "email"))
        }


        profileTracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(oldProfile: Profile, currentProfile: Profile) {

                //aqui puede acceder  a item de perdil
            }
        }

    }

    override fun onResume() {
        super.onResume()
        AppEventsLogger.activateApp(this)

    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    public override fun onPause() {
        super.onPause()
        AppEventsLogger.deactivateApp(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        profileTracker!!.stopTracking()
    }


    override fun onClick(v: View) {
        val id = v.id

        when (id) {
            R.id.cv_sign_in -> if (ValidUtils.isNetworkAvailable(this))
                signIn()
            else
                dialogGobal!!.sinInternet(this)

            R.id.btn_sign_out -> if (ValidUtils.isNetworkAvailable(this))
                signOut()
            else
                dialogGobal!!.sinInternet(this)

            R.id.btn_revoke_access -> revokeAccess()
        }
    }

    //google


    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(Kenna.mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    fun signOut() {
        Auth.GoogleSignInApi.signOut(Kenna.mGoogleApiClient).setResultCallback { updateUI(false) }
    }

    private fun revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(Kenna.mGoogleApiClient).setResultCallback { updateUI(false) }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("onConnectionFailed", "onConnectionFailed:" + connectionResult)
    }


    private fun updateUI(isSignedIn: Boolean) {
        if (isSignedIn) {
            binding!!.btnSignIn.visibility = View.GONE
            binding!!.btnSignOut.visibility = View.VISIBLE
            binding!!.btnRevokeAccess.visibility = View.VISIBLE

        } else {
            binding!!.btnSignIn.visibility = View.VISIBLE
            binding!!.btnSignOut.visibility = View.GONE
            binding!!.btnRevokeAccess.visibility = View.GONE

        }
    }


    private fun handleSignInResult(result: GoogleSignInResult) {
        // Log.d("handleSignInResult", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess) {

            val acct = result.signInAccount
            val person = Plus.PeopleApi.getCurrentPerson(Kenna.mGoogleApiClient)
            var cover = ""
            if (person != null) {

                try {
                    cover = JSONObject(person.cover.toString()).getJSONObject("coverPhoto").getString("url")
                    Log.e("cover", cover)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                Log.e("error", "Error!")
            }
            saveData(acct!!.idToken, acct.displayName, acct.email, acct.photoUrl.toString(), cover, Kenna.Google, token)

            val menu = Intent(this@LoginActivity, LocationPickerActivity::class.java)
            startActivity(menu)
            finish()

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false)
        }
    }

    internal fun saveData(token_social: String?, name: String?, emalil: String?, profile_pick: String, cover: String, typeLogin: Int, token: String?) {
        // Log.e("token save",token);
        preferences!!.setProfil(token_social, name, emalil, profile_pick, cover, typeLogin, true, token)

    }

    companion object {

        private val RC_SIGN_IN = 6
    }
}