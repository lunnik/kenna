package com.lionsquare.comunidadkenna;

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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.lionsquare.comunidadkenna.fragments.AbstractSectionFragment;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;

import com.lionsquare.comunidadkenna.activitys.MenuActivity;
import com.lionsquare.comunidadkenna.utils.MyBounceInterpolator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Method used to create a new fragment according to the section selected
     *
     * @param fragment The fragment to create
     */
    public void goFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
            window.setStatusBarColor(res.getColor(beanSection.sectionColorPrimaryDarkId));
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
