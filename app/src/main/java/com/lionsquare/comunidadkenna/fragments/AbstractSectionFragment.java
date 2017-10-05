package com.lionsquare.comunidadkenna.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.airbnb.lottie.LottieAnimationView;
import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.activitys.MenuActivity;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.fragments.bean.BeanColor;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.MyBounceInterpolator;
import com.lionsquare.comunidadkenna.utils.Preferences;
import com.marcoscg.infoview.InfoView;

/**
 * Created by davidcordova on 05/08/15.
 */
public abstract class AbstractSectionFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener {

    public View mainView;
    public AppBarLayout appBarLayout;
    public Toolbar toolbar;
    public SwipeRefreshLayout srlSectionRefresh;
    public CollapsingToolbarLayout collapsingToolbarLayout;
    public BeanSection beanSection;
    public SectionFragmentCallbacks sectionFragmentCallbacks;
    public ProgressBar pbSection;
    public LinearLayout llSectionError;
    public LinearLayout llNoSectionResults;
    public RecyclerView rvSection;
    protected InfoView infoView;

    public AbstractAppActivity activity;

    public Resources res;


    private boolean flgShowError = false;
    public boolean cache = false;
    public boolean refreshing = false;

    protected Preferences preferences;
    protected DbManager dbManager;
    protected DialogGobal dialogGobal;


    /**
     * Interface to comunicate the current fragment with the {@link MenuActivity}
     */
    public interface SectionFragmentCallbacks {
        /**
         * Method used to update the Toolbar of {@link MenuActivity} with the section
         * selected by the user
         *
         * @param beanSection    A bean that contains the colors and title of the section selected
         * @param sectionToolbar The toolbar of the section selected
         */
        void updateSectionToolbar(BeanSection beanSection, Toolbar sectionToolbar);

        /**
         * Method used to update the Toolbar of {@link MenuActivity} with the section
         * selected by the user
         *
         * @param beanSection             A bean that contains the colors and title of the section selected
         * @param collapsingToolbarLayout The CollapsingToolbarLayout of the section selected in case the section has one
         * @param sectionToolbar          The toolbar of the section selected
         */
        void updateSectionToolbar(BeanSection beanSection, CollapsingToolbarLayout collapsingToolbarLayout, Toolbar sectionToolbar);


        /**
         * Este metodo es para solo actulizar el color del activity
         */
        void updateSectionColor(BeanColor beanColor);


        /**
         * Method used to show/hide the {@link android.support.v7.widget.SearchView}
         * in the toolbar
         *
         * @param visible true if the serachView should be visible, false otherwise
         */
        void setSearchViewVisible(boolean visible);

        /**
         * este metodo espara cerraar la secion sea de facebook o google
         */
        void stateSession();


        /**
         * Method used to handle the click on a new item
         *
         * @param beanNews the bean of the new selected
         */
        //void onNewsReadClick(BeanNews beanNews);

        /**
         * Method used to handle the click on a life time item
         * @param beanLifeTime the bean of the item selected
         */
        //void onLifeTimeMoreClick(BeanLifeTime beanLifeTime);
    }

    /**
     * Method used to return the value of the flag
     * flgShowError
     *
     * @return the value of flgShowError
     */
    public boolean getFlgShowError() {
        return flgShowError;
    }

    /**
     * Method used to show an indeterminated progress bar
     * while the data is being downloaded
     */
    public void showProgress() {
        pbSection.setVisibility(View.VISIBLE);
        if (flgShowError)
            hideError();
    }

    /**
     * Method used to hide an indeterminated progress bar
     * once the data has been downloaded
     */
    public void hideProgress() {
        pbSection.setVisibility(View.GONE);
    }

    /**
     * Method used to show an error message
     */
    public void showError() {
        flgShowError = true;
        llSectionError.setVisibility(View.VISIBLE);
        rvSection.setVisibility(View.GONE);
    }


    /**
     * Method used to hide an error message
     */
    public void hideError() {
        flgShowError = false;
        llSectionError.setVisibility(View.GONE);
        rvSection.setVisibility(View.VISIBLE);
    }

    /**
     * Method used to show a message when
     * a search query didn't bring back results
     */
    public void showNoResults() {
        llNoSectionResults.setVisibility(View.VISIBLE);
        rvSection.setVisibility(View.GONE);
    }

    public void hideNoResults() {
        llNoSectionResults.setVisibility(View.GONE);
        rvSection.setVisibility(View.VISIBLE);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity) {
            a = (Activity) context;
            try {
                sectionFragmentCallbacks = (SectionFragmentCallbacks) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement SectionFragmentCallbacks");
            }
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        sectionFragmentCallbacks = null;
    }

    @Override
    public void onRefresh() {
        //Empty implementation
    }

    /**
     * Se hace el cambio interno de los fragmentos  si es que a si lo pide la navegacion
     *
     * @param fragment intancia del fragmento
     * @param tag      el identifiacor de ese fragmento
     */
    protected void changeFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = activity.fragmentManager.beginTransaction();
        ft.addToBackStack(tag);//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.fl_main_container, fragment);
        ft.commit();
    }

    /**
     * animacion de interpolacion para las vistas
     */
    protected void animateButton(View view) {
        // Load the animation
        final Animation myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
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
