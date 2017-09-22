package com.lionsquare.comunidadkenna;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.lionsquare.comunidadkenna.activitys.WallPetActivity;
import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.fragments.AbstractSectionFragment;

import com.lionsquare.comunidadkenna.fragments.HomeFragment;
import com.lionsquare.comunidadkenna.fragments.ProfileUserFragment;
import com.lionsquare.comunidadkenna.fragments.WallPetFragment;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;

import com.lionsquare.comunidadkenna.activitys.MenuActivity;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.MyBounceInterpolator;
import com.lionsquare.comunidadkenna.utils.Preferences;

import java.util.ArrayList;
import java.util.HashMap;

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
    protected ActivityMenuBinding binding;
    protected HashMap<String, Fragment> listFragment;


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
    public void goFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            Log.e("tag ini", tag);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(tag);//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.fl_main_container, fragment);
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
            window.setNavigationBarColor(getResources().getColor(beanSection.sectionColorPrimaryId));
        }

        binding.navigation.setItemBackgroundResource(beanSection.sectionColorPrimaryId);

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
            window.setNavigationBarColor(getResources().getColor(beanSection.sectionColorPrimaryId));
        }
        binding.navigation.setItemBackgroundResource(beanSection.sectionColorPrimaryId);
    }


    @Override
    public void updateSectionStatusBar(BeanSection beanSection) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(res.getColor(beanSection.sectionColorPrimaryDarkId));
            window.setNavigationBarColor(getResources().getColor(beanSection.sectionColorPrimaryId));
        }
        binding.navigation.setItemBackgroundResource(beanSection.sectionColorPrimaryId);
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

        int index = getSupportFragmentManager().getBackStackEntryCount() - 2;
        String tag = null;
        Log.e("index", String.valueOf(index));
        if (index > 0) {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
            tag = backEntry.getName();

        } else {
            FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(0);
            tag = backEntry.getName();
        }
        Log.e("tag", tag);
        Log.e("mumero de f", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();

            Menu bottomNavigationMenu = binding.navigation.getMenu();
            if (tag.equals(HomeFragment.TAG)) {
                //binding.navigation.setSelectedItemId(R.id.navigation_home);
                //bottomNavigationMenu.performIdentifierAction(R.id.navigation_home, 0);
                bottomNavigationMenu.findItem(R.id.navigation_home).setChecked(true);
                Log.e("Fragment", "HomeFragment");
            }
            if (tag.equals(WallPetFragment.TAG)) {
                //binding.navigation.setSelectedItemId(R.id.navigation_notifications);
                bottomNavigationMenu.findItem(R.id.navigation_notifications).setChecked(true);
                Log.e("Fragment", "WallPetFragment");
            }
            if (tag.equals(ProfileUserFragment.TAG)) {
                //binding.navigation.setSelectedItemId(R.id.navigation_profile);
                bottomNavigationMenu.findItem(R.id.navigation_profile).setChecked(true);
                Log.e("Fragment", "ProfileUserFragment");

            }
        } else {
            //  super.onBackPressed();
            finish();
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
