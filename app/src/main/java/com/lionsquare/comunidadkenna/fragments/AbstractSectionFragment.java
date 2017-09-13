package com.lionsquare.comunidadkenna.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.activitys.MenuActivity;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;
import com.lionsquare.comunidadkenna.utils.DialogGobal;
import com.lionsquare.comunidadkenna.utils.Preferences;

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
         * Method used to show/hide the {@link android.support.v7.widget.SearchView}
         * in the toolbar
         *
         * @param visible true if the serachView should be visible, false otherwise
         */
        void setSearchViewVisible(boolean visible);
        /**
         * este metodo espara cerraar la secion sea de facebook o google
         * */
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

    public void hideNoResults(){
        llNoSectionResults.setVisibility(View.GONE);
        rvSection.setVisibility(View.VISIBLE);
    }




    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sectionFragmentCallbacks = (SectionFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SectionFragmentCallbacks");
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


}
