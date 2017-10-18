package com.lionsquare.comunidadkenna.fragments

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.ProgressBar


import com.airbnb.lottie.LottieAnimationView
import com.lionsquare.comunidadkenna.AbstractAppActivity
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.activitys.MenuActivity
import com.lionsquare.comunidadkenna.db.DbManager
import com.lionsquare.comunidadkenna.fragments.bean.BeanColor
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.MyBounceInterpolator
import com.lionsquare.comunidadkenna.utils.Preferences
import com.marcoscg.infoview.InfoView

/**
 * Created by edgararana on 05/08/15.
 */
abstract class AbstractSectionFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    var mainView: View? = null
    var appBarLayout: AppBarLayout? = null
    var toolbar: Toolbar? = null
    var srlSectionRefresh: SwipeRefreshLayout? = null
    var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    var beanSection: BeanSection? = null
    var sectionFragmentCallbacks: SectionFragmentCallbacks? = null
    var pbSection: ProgressBar? = null
    var llSectionError: LinearLayout? = null
    var llNoSectionResults: LinearLayout? = null
    var rvSection: RecyclerView? = null
    protected var infoView: InfoView? = null

    var activity: AbstractAppActivity? = null

    var res: Resources? = null


    /**
     * Method used to return the value of the flag
     * flgShowError
     *
     * @return the value of flgShowError
     */
    var flgShowError = false
        private set
    var cache = false
    var refreshing = false

    protected var preferences: Preferences? = null
    protected var dbManager: DbManager? = null
    protected var dialogGobal: DialogGobal? = null


    /**
     * Interface to comunicate the current fragment with the [MenuActivity]
     */
    interface SectionFragmentCallbacks {
        /**
         * Method used to update the Toolbar of [MenuActivity] with the section
         * selected by the user
         *
         * @param beanSection    A bean that contains the colors and title of the section selected
         * @param sectionToolbar The toolbar of the section selected
         */
        fun updateSectionToolbar(beanSection: BeanSection, sectionToolbar: Toolbar)

        /**
         * Method used to update the Toolbar of [MenuActivity] with the section
         * selected by the user
         *
         * @param beanSection             A bean that contains the colors and title of the section selected
         * @param collapsingToolbarLayout The CollapsingToolbarLayout of the section selected in case the section has one
         * @param sectionToolbar          The toolbar of the section selected
         */
        fun updateSectionToolbar(beanSection: BeanSection, collapsingToolbarLayout: CollapsingToolbarLayout, sectionToolbar: Toolbar)


        /**
         * Este metodo es para solo actulizar el color del activity
         */
        fun updateSectionColor(beanColor: BeanColor)


        /**
         * Method used to show/hide the [android.support.v7.widget.SearchView]
         * in the toolbar
         *
         * @param visible true if the serachView should be visible, false otherwise
         */
        fun setSearchViewVisible(visible: Boolean)

        /**
         * este metodo espara cerraar la secion sea de facebook o google
         */
        fun stateSession()


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
     * Method used to show an indeterminated progress bar
     * while the data is being downloaded
     */
    fun showProgress() {
        pbSection!!.visibility = View.VISIBLE
        if (flgShowError)
            hideError()
    }

    /**
     * Method used to hide an indeterminated progress bar
     * once the data has been downloaded
     */
    fun hideProgress() {
        pbSection!!.visibility = View.GONE
    }

    /**
     * Method used to show an error message
     */
    fun showError() {
        flgShowError = true
        llSectionError!!.visibility = View.VISIBLE
        rvSection!!.visibility = View.GONE
    }


    /**
     * Method used to hide an error message
     */
    fun hideError() {
        flgShowError = false
        llSectionError!!.visibility = View.GONE
        rvSection!!.visibility = View.VISIBLE
    }

    /**
     * Method used to show a message when
     * a search query didn't bring back results
     */
    fun showNoResults() {
        llNoSectionResults!!.visibility = View.VISIBLE
        rvSection!!.visibility = View.GONE
    }

    fun hideNoResults() {
        llNoSectionResults!!.visibility = View.GONE
        rvSection!!.visibility = View.VISIBLE
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val a: Activity
        if (context is Activity) {
            a = context
            try {
                sectionFragmentCallbacks = a as SectionFragmentCallbacks
            } catch (e: ClassCastException) {
                throw ClassCastException(activity!!.toString() + " must implement SectionFragmentCallbacks")
            }

        }

    }


    override fun onDetach() {
        super.onDetach()
        sectionFragmentCallbacks = null
    }

    override fun onRefresh() {
        //Empty implementation
    }

    /**
     * Se hace el cambio interno de los fragmentos  si es que a si lo pide la navegacion
     *
     * @param fragment intancia del fragmento
     * @param tag      el identifiacor de ese fragmento
     */
    protected fun changeFragment(fragment: Fragment, tag: String) {
        val ft = activity!!.fragmentManager.beginTransaction()
        ft.addToBackStack(tag)//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
        ft.replace(R.id.fl_main_container, fragment)
        ft.commit()
    }

    /**
     * animacion de interpolacion para las vistas
     */
    protected fun animateButton(view: View) {
        // Load the animation
        val myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce)
        val animationDuration = 2.0 * 1000
        myAnim.duration = animationDuration.toLong()

        // Use custom animation interpolator to achieve the bounce effect
        val interpolator = MyBounceInterpolator(0.20, 20.0)

        myAnim.interpolator = interpolator

        // Animate the button
        view.startAnimation(myAnim)
        //playSound();

        // Run button animation again after it finished
        myAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {

            }

            override fun onAnimationRepeat(arg0: Animation) {}

            override fun onAnimationEnd(arg0: Animation) {
                //animateButton();
            }
        })
    }


}
