package com.lionsquare.comunidadkenna.activitys;


import android.content.Intent;

import android.databinding.DataBindingUtil;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;


import android.util.Log;
import android.view.View;

import android.widget.ImageView;


import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lionsquare.comunidadkenna.Kenna;
import com.lionsquare.comunidadkenna.R;

import com.lionsquare.comunidadkenna.databinding.ActivityLoginBinding;
import com.lionsquare.comunidadkenna.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Arrays;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;

    private static final int RC_SIGN_IN = 006;


    private Preferences preferences;

    private ActivityLoginBinding binding;
    private String token = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        facebookInit();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        googleAccount();
        faceBookComponent();


    }


    private void init() {

        preferences = new Preferences(LoginActivity.this);
        Log.e("bolaen", String.valueOf(preferences.getFlag()));
        token = FirebaseInstanceId.getInstance().getToken();

        if (preferences.getFlag()) {
            Intent menu = new Intent(LoginActivity.this, LocationPickerActivity.class);
            startActivity(menu);
            finish();
        }

    }


    private void facebookInit() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {



                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        final Profile profile = Profile.getCurrentProfile();
                                        Uri urlImgaeProfil = profile.getProfilePictureUri(200, 200);
                                        final String urlImage = urlImgaeProfil.toString();
                                        Log.e("url", urlImage);

                                        Log.e("LoginActivity Response ", response.toString());
                                        String cover = "";
                                        try {
                                            cover = object.getJSONObject("cover").getString("source");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        try {

                                            saveData(String.valueOf(loginResult.getAccessToken()),
                                                    profile.getName(),
                                                    object.getString("email"),
                                                    urlImage,
                                                    cover,
                                                    Kenna.Facebook,
                                                    token);


                                            Intent menu = new Intent(LoginActivity.this, LocationPickerActivity.class);
                                            startActivity(menu);
                                            finish();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.e("JSONException = ", " " + e);
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,cover");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        Log.e("onCancel", "Login canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("onError", "Login error" + exception.getMessage());
                    }
                });
    }

    private void googleAccount() {


        binding.cvSignIn.setOnClickListener(this);
        binding.btnSignOut.setOnClickListener(this);
        binding.btnRevokeAccess.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .requestProfile()
                .build();

        Kenna.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        // Customzing G+ button
        //btnSignIn.setSize(SignInButton.SIZE_STANDARD);
        //btnSignIn.setScopes(gso.getScopeArray());
    }

    private void faceBookComponent() {
        binding.blurredView.setBackgroundResource(R.drawable.back_login);
        binding.blurredView.setAdjustViewBounds(true);
        binding.blurredView.setScaleType(ImageView.ScaleType.CENTER_CROP);


        binding.cvFbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Presion btn facebook", "");
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });


        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

                //aqui puede acceder  a item de perdil
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.cv_sign_in:
                signIn();
                break;

            case R.id.btn_sign_out:
                signOut();
                break;

            case R.id.btn_revoke_access:
                revokeAccess();
                break;
        }
    }

    //google


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(Kenna.mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    public void signOut() {
        Auth.GoogleSignInApi.signOut(Kenna.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(Kenna.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateUI(false);
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("onConnectionFailed", "onConnectionFailed:" + connectionResult);
    }


    private void updateUI(boolean isSignedIn) {
        if (isSignedIn) {
            binding.btnSignIn.setVisibility(View.GONE);
            binding.btnSignOut.setVisibility(View.VISIBLE);
            binding.btnRevokeAccess.setVisibility(View.VISIBLE);

        } else {
            binding.btnSignIn.setVisibility(View.VISIBLE);
            binding.btnSignOut.setVisibility(View.GONE);
            binding.btnRevokeAccess.setVisibility(View.GONE);

        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        // Log.d("handleSignInResult", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            Person person = Plus.PeopleApi.getCurrentPerson(Kenna.mGoogleApiClient);
            String cover = "";
            if (person != null) {

                try {
                    cover = new JSONObject(String.valueOf(person.getCover())).getJSONObject("coverPhoto").getString("url");
                    Log.e("cover", cover);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("error", "Error!");
            }
            saveData(acct.getIdToken(), acct.getDisplayName(), acct.getEmail(), String.valueOf(acct.getPhotoUrl()), cover, Kenna.Google, token);

            Intent menu = new Intent(LoginActivity.this, LocationPickerActivity.class);
            startActivity(menu);
            finish();

        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    void saveData(String token_social, String name, String emalil, String profile_pick, String cover, int typeLogin, String token) {
       // Log.e("token save",token);
        preferences.setProfil(token_social, name, emalil, profile_pick, cover, typeLogin, true, token);

    }
}