package com.lionsquare.comunidadkenna.fragments


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lionsquare.comunidadkenna.AbstractAppActivity
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.FragmentProfileUserBinding
import com.lionsquare.comunidadkenna.db.DbManager
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection
import com.lionsquare.comunidadkenna.model.Response
import com.lionsquare.comunidadkenna.model.User
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences

import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback

/**
 * A simple [Fragment] subclass.
 */
class ProfileUserFragment : AbstractSectionFragment(), OnMapReadyCallback {

    private var coverImage: ImageView? = null

    private val textviewTitle: TextView? = null
    internal lateinit var circleImageView: CircleImageView


    private var googleMap: GoogleMap? = null
    private var mCircle: Circle? = null
    private var btnLogOut: Button? = null
    private var btnChangeLoc: Button? = null
    private var txtName: TextView? = null
    private var txtEmail: TextView? = null

    private var mapView: MapView? = null
    internal var binding: FragmentProfileUserBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity = getActivity() as AbstractAppActivity
        res = activity!!.resources

        beanSection = BeanSection()
        beanSection!!.sectionNameId = R.string.perfil
        beanSection!!.sectionColorPrimaryId = R.color.news_color_primary
        beanSection!!.sectionColorPrimaryDarkId = R.color.news_color_primary_dark
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_profile_user, null, false)
        }
        findViews()
        initSetUp(savedInstanceState)
        return binding!!.root
    }

    private fun findViews() {
        coverImage = binding!!.apIvCover
        circleImageView = binding!!.imageProfile
        btnLogOut = binding!!.logaout
        btnChangeLoc = binding!!.changeLoc
        txtName = binding!!.apTxtName
        txtEmail = binding!!.apTxtEmail
    }

    internal fun initSetUp(savedInstanceState: Bundle?) {
        preferences = Preferences(context)
        dialogGobal = DialogGobal(context)
        dbManager = DbManager(activity).open()
        appBarLayout = binding!!.ablGuidesAppbar
        toolbar = binding!!.guidesToolbar
        collapsingToolbarLayout = binding!!.collapsingToolbar

        sectionFragmentCallbacks!!.updateSectionToolbar(beanSection!!, collapsingToolbarLayout!!, toolbar!!)
        appBarLayout!!.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                //  Collapsed

                binding!!.viewToolbarShadowPreLollipop.visibility = View.VISIBLE

            } else {
                //Expanded
                binding!!.viewToolbarShadowPreLollipop.visibility = View.GONE

            }
        }
        if (mapView == null) {
            mapView = binding!!.map
            mapView!!.onCreate(savedInstanceState)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }

        if (URLUtil.isValidUrl(preferences!!.imagePerfil)) {
            Glide.with(this).load(preferences!!.imagePerfil).into(circleImageView)
        } else {
            Glide.with(this).load(R.drawable.ic_user_ic).into(circleImageView)
        }

        if (URLUtil.isValidUrl(preferences!!.cover)) {
            Glide.with(this).load(preferences!!.cover).into(coverImage!!)
        } else {
            Glide.with(this).load(R.drawable.back_login).into(coverImage!!)
        }

        btnLogOut!!.setOnClickListener(this)
        btnChangeLoc!!.setOnClickListener(this)

        txtName!!.text = preferences!!.name
        txtEmail!!.text = preferences!!.email
        //textviewTitle.setText("Perfil");
    }


    override fun onClick(v: View) {
        when (v.id) {

            R.id.change_loc -> locationPlacesIntent()
            R.id.logaout -> sectionFragmentCallbacks!!.stateSession()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap!!.uiSettings.isScrollGesturesEnabled = false
        this.googleMap!!.uiSettings.setAllGesturesEnabled(false)
        this.googleMap!!.uiSettings.isMapToolbarEnabled = false
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        this.googleMap!!.isMyLocationEnabled = false

        val handler = Handler()
        btnLogOut!!.isEnabled = false
        val r = Runnable { addMaker() }

        handler.postDelayed(r, 1000)

    }

    internal fun addMaker() {
        googleMap!!.clear()
        try {
            val user = dbManager!!.user
            val latLng = LatLng(user!!.lat!!, user.lng!!)
            val marker = googleMap!!.addMarker(
                    MarkerOptions().position(latLng))

            mCircle = googleMap!!.addCircle(CircleOptions()
                    .center(latLng)
                    .radius(500.0)
                    .strokeColor(resources.getColor(R.color.blue_circul))
                    .strokeWidth(3f)
                    .fillColor(resources.getColor(R.color.blue_circul))
            )
            googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            // TODO: 20/07/2017 Aumente el valor para acercar.
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(user.lat!!, user.lng!!), 14f)
            googleMap!!.animateCamera(cameraUpdate)
            dialogGobal!!.dimmis()
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }

        btnLogOut!!.isEnabled = true

    }

    private fun locationPlacesIntent() {
        try {

            val builder = PlacePicker.IntentBuilder()
            activity!!.startActivityForResult(builder.build(activity!!), PLACE_PICKER_REQUEST)

        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
            Log.e("error lat", e.toString())
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
            Log.e("error lat", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(context, data!!)
                if (place != null) {
                    val latLng = place.latLng
                    updateLoc(latLng)
                } else {
                    Log.e("error", "sdfsgrfger")

                }
            }
        }

    }

    fun updateLoc(latLng: LatLng) {
        dialogGobal!!.progressIndeterminateStyle()
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.updateLoc(
                preferences!!.email, preferences!!.token, latLng.latitude, latLng.longitude)
        call.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                dialogGobal!!.dimmis()
                if (response.body().success == 1) {
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    // TODO: 20/07/2017 Aumente el valor para acercar.
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
                    googleMap!!.animateCamera(cameraUpdate)
                    dbManager!!.updateLoc(dbManager!!.user!!.id, latLng.latitude, latLng.longitude)
                    googleMap!!.clear()
                    addMaker()
                } else if (response.body().success == 0) {
                    //token caduco
                    tokenDeprecated()
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Log.e("error", t.toString())
                dialogGobal!!.dimmis()
            }
        })
    }

    internal fun tokenDeprecated() {
        MaterialDialog.Builder(context)
                .title(R.string.token_deprecated)
                .content(R.string.inicar_sesion_nuevamente)
                .positiveText(R.string.volver_a_iniciar_sesion)
                .cancelable(false)
                .onPositive { dialog, which -> sectionFragmentCallbacks!!.stateSession() }
                .progressIndeterminateStyle(true)
                .show()
    }

    override fun onResume() {
        if (mapView != null)
            mapView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mapView != null)
            mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (mapView != null)
            mapView!!.onLowMemory()
    }

    companion object {


        fun newInstance(): ProfileUserFragment {
            val newsFragment = ProfileUserFragment()
            val arguments = Bundle()
            newsFragment.arguments = arguments
            newsFragment.retainInstance = true

            return newsFragment
        }

        private val PLACE_PICKER_REQUEST = 1

        val TAG = ProfileUserFragment::class.java.name
    }

}
