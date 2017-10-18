package com.lionsquare.comunidadkenna.activitys

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment

import android.os.Bundle

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View


import com.android.vending.billing.IInAppBillingService
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.lionsquare.comunidadkenna.AbstractAppActivity
import com.lionsquare.comunidadkenna.R


import com.lionsquare.comunidadkenna.databinding.ActivityMenuBinding
import com.lionsquare.comunidadkenna.fragments.HomeFragment
import com.lionsquare.comunidadkenna.fragments.ProfileUserFragment
import com.lionsquare.comunidadkenna.fragments.RegisterPetFragment
import com.lionsquare.comunidadkenna.fragments.WallPetFragment


import com.lionsquare.comunidadkenna.fragments.bean.BeanSection
import com.lionsquare.comunidadkenna.utils.DialogGobal

import com.lionsquare.comunidadkenna.utils.Preferences
import com.lionsquare.multiphotopicker.photopicker.activity.PickImageActivity

import java.util.HashMap


class MenuActivity : AbstractAppActivity(), View.OnClickListener, HomeFragment.OnFragmentInteractionListener {

    private lateinit var currentFragment: Fragment

    internal var mService: IInAppBillingService? = null

    internal var mServiceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = IInAppBillingService.Stub.asInterface(service)
        }
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                currentFragment = returnFragment(HomeFragment.TAG)
                if (validationFragment(currentFragment)) {
                    goFragment(currentFragment, HomeFragment.TAG)
                }
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_notifications -> {
                currentFragment = returnFragment(WallPetFragment.TAG)
                if (validationFragment(currentFragment)) {
                    goFragment(currentFragment, WallPetFragment.TAG)
                } else {
                    retutnListPet()
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
                currentFragment = returnFragment(ProfileUserFragment.TAG)
                if (validationFragment(currentFragment)) {
                    goFragment(currentFragment, ProfileUserFragment.TAG)

                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.`package` = "com.android.vending"
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        preferences = Preferences(this)
        dialogGobal = DialogGobal(this)
        initSetUp()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mService != null) {
            unbindService(mServiceConn)
        }
    }


    internal fun initSetUp() {
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission()
        } else {
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }

        currentFragment = HomeFragment.newInstace()
        goFragment(currentFragment, HomeFragment.TAG)
        // TODO: 21/09/2017 se guadar el fragmento con el tag para que no se vuleva a crear

    }

    override fun setupToolbar(sectionToolbar: Toolbar) {
        super.setupToolbar(sectionToolbar)
        setSupportActionBar(sectionToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //sectionToolbar.setNavigationIcon(R.drawable.ic_menu);

        if (collapsingToolbar != null) {
            collapsingToolbar!!.setExpandedTitleTextAppearance(android.R.style.TextAppearance_Medium)

        }

    }

    // TODO: 26/09/2017 evita que el fragneto se vuela instanciar
     fun returnFragment(tag: String): Fragment{
        var lFragment: Fragment? = null
        if (null == supportFragmentManager.findFragmentByTag(tag)) {
            if (tag == HomeFragment.TAG) {
                lFragment = HomeFragment.newInstace()
            } else if (tag == WallPetFragment.TAG) {
                lFragment = WallPetFragment.newInstace()
            } else if (tag == ProfileUserFragment.TAG) {
                lFragment = ProfileUserFragment.newInstance()
            }
            return lFragment!!
        } else {
            return supportFragmentManager.findFragmentByTag(tag)

        }


    }


    internal fun validationFragment(fragment: Fragment?): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fl_main_container)
        return if (currentFragment == null) {
            //carga del primer fragment justo en la carga inicial de la app
            true
        } else if (!currentFragment.javaClass.name.equals(fragment!!.javaClass.name, ignoreCase = true)) {
            //currentFragment no concide con newFragment
            true

        } else {
            //currentFragment es igual a newFragment
            false
        }
    }


    override fun onClick(v: View) {

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //saveComments();
                //binding.placeSearchDialogOkTV.setEnabled(true);
                //checkoutLogin();
            } else {
                //binding.placeSearchDialogOkTV.setEnabled(false);
                showSnackBar()
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun verifyPermission() {
        val writePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
            //binding.placeSearchDialogOkTV.setEnabled(false);
        } else {
            //saveComments();
            //binding.placeSearchDialogOkTV.setEnabled(true);
            //checkoutLogin();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showSnackBar()
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISS_WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(binding.amClRoot, R.string.permission_location, Snackbar.LENGTH_INDEFINITE)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.primaryColor))
        snackbar.setAction("Configurar") {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", "com.lionsquare.kenna", null)
            intent.data = uri
            startActivity(intent)
        }.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == REGISTER_PET_LOST) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fl_main_container)
            fragment.onActivityResult(requestCode, resultCode, data)
        } else {

        }
        // TODO: 14/09/2017 es para madar al fragmento el result
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                if (place != null) {
                    val latLng = place.latLng
                    val fragment = supportFragmentManager.findFragmentById(R.id.fl_main_container)
                    fragment.onActivityResult(requestCode, resultCode, data)
                } else {
                    Log.e("error", "sdfsgrfger")

                }
            }
        }


        if (resultCode == Activity.RESULT_OK && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            val fragment = supportFragmentManager.findFragmentById(R.id.fl_main_container)
            fragment.onActivityResult(requestCode, resultCode, data)

        }


    }


    override fun setSearchViewVisible(visible: Boolean) {

    }


    override fun onFragmentInteraction(uri: Uri) {

    }


    // TODO: 26/09/2017 una vez instaciado el fragmento puedes regresar el item 0 de la lista
    internal fun retutnListPet() {
        val recFragment = supportFragmentManager.findFragmentById(R.id.fl_main_container) as WallPetFragment
        recFragment.returnFisrtItem()
        /* if (null != recFragment && recFragment.isInLayout()) {
       }*/
    }

    companion object {


        private val PERMISS_WRITE_EXTERNAL_STORAGE = 1
        private val REGISTER_PET_LOST = 1011
    }
}
