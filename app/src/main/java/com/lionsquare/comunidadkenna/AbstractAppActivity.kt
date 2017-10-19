package com.lionsquare.comunidadkenna


import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status

import com.lionsquare.comunidadkenna.activitys.LoginActivity
import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding
import com.lionsquare.comunidadkenna.db.DbManager
import com.lionsquare.comunidadkenna.fragments.AbstractSectionFragment

import com.lionsquare.comunidadkenna.fragments.HomeFragment
import com.lionsquare.comunidadkenna.fragments.ProfileUserFragment
import com.lionsquare.comunidadkenna.fragments.WallPetFragment
import com.lionsquare.comunidadkenna.fragments.bean.BeanColor
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection

import com.lionsquare.comunidadkenna.activitys.MenuActivity
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.MyBounceInterpolator
import com.lionsquare.comunidadkenna.utils.Preferences


/**
 * Created by edgararana on 21/08/15.
 */
abstract class AbstractAppActivity : AppCompatActivity(), AbstractSectionFragment.SectionFragmentCallbacks {

    lateinit var res: Resources

    lateinit var sectionToolbar: Toolbar
    var collapsingToolbar: CollapsingToolbarLayout? = null
    lateinit var fragmentManager: FragmentManager


    lateinit var beanSection: BeanSection
    var searchViewVisible : Boolean? =true

    protected lateinit var preferences: Preferences
    protected lateinit var dbManager: DbManager
    protected lateinit var dialogGobal: DialogGobal
    protected lateinit var binding: ActivityMenuBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        res = resources
        fragmentManager = supportFragmentManager

        preferences = Preferences(this)
        dialogGobal = DialogGobal(this)
        dbManager = DbManager(this).open()

    }

    /**
     * Method used to create a new fragment according to the section selected
     *
     * @param fragment The fragment to create
     * @param tag      The tag check instance exist in FragmentManager
     */
    public fun goFragment(fragment: Fragment, tag: String) {

        /*   FragmentTransaction ft = fragmentManager.beginTransaction();
        //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ft.addToBackStack(tag);//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.fl_main_container, fragment, tag);
        ft.commit();
*/
        // TODO: 22/09/2017 estos metodos es para intentar que no se dupliquen los fragmentos y se reutilicen pero tiene un erro en los tags
        if (null == supportFragmentManager.findFragmentByTag(tag)) {
            val ft = fragmentManager.beginTransaction()
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(tag)//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            ft.replace(R.id.fl_main_container, fragment, tag)
            ft.commit()
        } else {
            val fragment1 = supportFragmentManager.findFragmentByTag(tag)
            val ft = fragmentManager.beginTransaction()
            //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(tag)//con addToBackStack al remplzar el fragmento se guada en la pila de retrocesos
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            ft.replace(R.id.fl_main_container, fragment1)
            ft.commitAllowingStateLoss()
            //ft.commit();

        }


    }

    override fun onBackPressed() {
        val index = supportFragmentManager.backStackEntryCount - 2
        var tag: String? = null
        if (index > 0) {
            val backEntry = supportFragmentManager.getBackStackEntryAt(index)
            tag = backEntry.name
        } else {
            val backEntry = supportFragmentManager.getBackStackEntryAt(0)
            tag = backEntry.name
        }


        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()

            val bottomNavigationMenu = binding!!.navigation.menu
            if (tag == HomeFragment.TAG) {
                //binding.navigation.setSelectedItemId(R.id.navigation_home);
                bottomNavigationMenu.findItem(R.id.navigation_home).isChecked = true

            }
            if (tag == WallPetFragment.TAG) {
                bottomNavigationMenu.findItem(R.id.navigation_notifications).isChecked = true
            }
            if (tag == ProfileUserFragment.TAG) {
                bottomNavigationMenu.findItem(R.id.navigation_profile).isChecked = true
            }
        } else {
            // super.onBackPressed();
            finish()
        }
    }


    /**
     * Method used to set the toolbar options and behaviours
     *
     * @param sectionToolbar the toolbar
     */
    open fun setupToolbar(sectionToolbar: Toolbar) {
        //Empty implementation
    }

    /**
     * AbstractSectionFragment SectionFragmentCallbacks interface methods
     */

    /**
     * Method used to update the Toolbar of [MenuActivity] with the section
     * selected by the user
     *
     * @param beanSection A bean that contains the colors and title of the section selected
     */
    override fun updateSectionToolbar(beanSection: BeanSection, sectionToolbar: Toolbar) {
        this.sectionToolbar = sectionToolbar
        this.beanSection = beanSection
        collapsingToolbar = null

        sectionToolbar.setTitle(beanSection.sectionNameId)
        sectionToolbar.setBackgroundColor(res.getColor(beanSection.sectionColorPrimaryId))

        setupToolbar(sectionToolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = res.getColor(beanSection.sectionColorPrimaryDarkId)
            window.navigationBarColor = resources.getColor(beanSection.sectionColorPrimaryDarkId)
        }

        binding!!.navigation.itemBackgroundResource = beanSection.sectionColorPrimaryId

    }

    /**
     * Method used to update the Toolbar of [MenuActivity] with the section
     * selected by the user
     *
     * @param beanSection             A bean that contains the colors and title of the section selected
     * @param collapsingToolbarLayout The CollapsingToolbarLayout of the section selected in case the section has one
     * @param sectionToolbar          The toolbar of the section selected
     */
    override fun updateSectionToolbar(beanSection: BeanSection, collapsingToolbarLayout: CollapsingToolbarLayout, sectionToolbar: Toolbar) {
        this.sectionToolbar = sectionToolbar
        this.collapsingToolbar = collapsingToolbarLayout
        this.beanSection = beanSection

        collapsingToolbarLayout.title = res.getString(beanSection.sectionNameId)
        setupToolbar(sectionToolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = res.getColor(beanSection.sectionColorPrimaryDarkId)
            window.navigationBarColor = resources.getColor(beanSection.sectionColorPrimaryDarkId)
        }
        binding!!.navigation.itemBackgroundResource = beanSection.sectionColorPrimaryId
    }


    override fun updateSectionColor(beanColor: BeanColor) {
        if (beanColor.ColorPrimaryId != 0 && beanColor.ColorPrimaryDarkId != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = beanColor.ColorPrimaryDarkId
                window.navigationBarColor = beanColor.ColorPrimaryDarkId
            }
            sectionToolbar.setBackgroundColor(beanColor.ColorPrimaryId)
            //binding.navigation.setItemBackgroundResource(beanColor.ColorPrimaryDarkId);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = res.getColor(beanSection.sectionColorPrimaryDarkId)
                window.navigationBarColor = resources.getColor(beanSection.sectionColorPrimaryDarkId)
            }

            binding!!.navigation.itemBackgroundResource = beanSection.sectionColorPrimaryId
            sectionToolbar.setBackgroundColor(res.getColor(beanSection.sectionColorPrimaryId))
        }

    }

    /**
     * metodo para cerrar seccion [MenuActivity]
     */
    override fun stateSession() {
        logOut()
    }


    /*
    * ciclo de viva
    * */


    override fun onStart() {
        super.onStart()
        if (Kenna.mGoogleApiClient != null) {
            Kenna.mGoogleApiClient!!.connect()
        }
    }


    /**
     * este metodo hace logout de la secion con la
     * que allas hecho login
     */

    internal fun logOut() {
        if (preferences.typeLogin == Kenna.Google) {
            Auth.GoogleSignInApi.signOut(Kenna.mGoogleApiClient).setResultCallback {
                preferences.closeProfile()
                dbManager.clearUser()
                val intent = Intent(this@AbstractAppActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        if (preferences.typeLogin == Kenna.Facebook) {
            LoginManager.getInstance().logOut()
            preferences.closeProfile()
            dbManager.clearUser()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


    protected fun animateButton(view: View) {
        // Load the animation
        val myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
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
