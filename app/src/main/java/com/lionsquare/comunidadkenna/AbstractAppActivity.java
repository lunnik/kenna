package com.lionsquare.comunidadkenna;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import com.lionsquare.comunidadkenna.activitys.LoginActivity;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.fragments.AbstractSectionFragment;

import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;

import com.lionsquare.comunidadkenna.activitys.MenuActivity;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.MyBounceInterpolator;
import com.lionsquare.comunidadkenna.utils.Preferences;

/**
 * Created by davidcordova on 21/08/15.
 */
public abstract class AbstractAppActivity extends AppCompatActivity implements
        AbstractSectionFragment.SectionFragmentCallbacks {

    public Resources res;

    public Toolbar sectionToolbar;
    public CollapsingToolbarLayout collapsingToolbar;
    public FragmentManager fragmentManager;


    public BeanSection beanSection;
    public boolean searchViewVisible = true;

    protected Preferences preferences;
    protected DbManager dbManager;
    protected DialogGobal dialogGobal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        fragmentManager = getSupportFragmentManager();

        preferences = new Preferences(this);
        dialogGobal = new DialogGobal(this);
        dbManager = new DbManager(this).open();

    }

    /**
     * Method used to create a new fragment according to the section selected
     *
     * @param fragment The fragment to create
     */
    public void goFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(null);//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

            ft.replace(R.id.fl_main_container, fragment, fragment.getClass().getName());
            ft.commit();
        }


    }

    /**
     * Method used to set the toolbar options and behaviours
     *
     * @param sectionToolbar the toolbar
     */
    public void setupToolbar(final Toolbar sectionToolbar) {
        //Empty implementation
    }

    /**
     * AbstractSectionFragment SectionFragmentCallbacks interface methods
     */

    /**
     * Method used to update the Toolbar of {@link MenuActivity} with the section
     * selected by the user
     *
     * @param beanSection A bean that contains the colors and title of the section selected
     */
    @Override
    public void updateSectionToolbar(BeanSection beanSection, Toolbar sectionToolbar) {
        this.sectionToolbar = sectionToolbar;
        this.beanSection = beanSection;
        collapsingToolbar = null;

        sectionToolbar.setTitle(beanSection.sectionNameId);
        sectionToolbar.setBackgroundColor(res.getColor(beanSection.sectionColorPrimaryId));

        setupToolbar(sectionToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(res.getColor(beanSection.sectionColorPrimaryDarkId));
        }
    }

    /**
     * Method used to update the Toolbar of {@link MenuActivity} with the section
     * selected by the user
     *
     * @param beanSection             A bean that contains the colors and title of the section selected
     * @param collapsingToolbarLayout The CollapsingToolbarLayout of the section selected in case the section has one
     * @param sectionToolbar          The toolbar of the section selected
     */
    @Override
    public void updateSectionToolbar(BeanSection beanSection, CollapsingToolbarLayout collapsingToolbarLayout, Toolbar sectionToolbar) {
        this.sectionToolbar = sectionToolbar;
        this.collapsingToolbar = collapsingToolbarLayout;
        this.beanSection = beanSection;

        collapsingToolbarLayout.setTitle(res.getString(beanSection.sectionNameId));

        setupToolbar(sectionToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setStatusBarColor(res.getColor(beanSection.sectionColorPrimaryDarkId));
        }

    }

    /**
     * metodo para cerrar seccion {@link MenuActivity}
     */
    @Override
    public void stateSession() {
        logOut();
    }


    /*
    * ciclo de viva
    * */


    @Override
    protected void onStart() {
        super.onStart();
        if (Kenna.mGoogleApiClient != null) {
            Kenna.mGoogleApiClient.connect();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("mumero de f", String.valueOf(getFragmentManager().getBackStackEntryCount()));
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


    /**
     * este metodo hace logout de la secion con la
     * que allas hecho login
     */

    void logOut() {
        if (preferences.getTypeLogin() == Kenna.Google) {
            Auth.GoogleSignInApi.signOut(Kenna.mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            preferences.closeProfile();
                            dbManager.clearUser();
                            Intent intent = new Intent(AbstractAppActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
        }
        if (preferences.getTypeLogin() == Kenna.Facebook) {
            LoginManager.getInstance().logOut();
            preferences.closeProfile();
            dbManager.clearUser();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    protected void animateButton(View view) {
        // Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        double animationDuration = 2.0 * 1000;
        myAnim.setDuration((long) animationDuration);

        // Use custom animation interpolator to achieve the bounce effect
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.20, 20.0);

        myAnim.setInterpolator(interpolator);

        // Animate the button
        view.startAnimation(myAnim);
        //playSound();

        // Run button animation again after it finished
        myAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                //animateButton();
            }
        });
    }


}
