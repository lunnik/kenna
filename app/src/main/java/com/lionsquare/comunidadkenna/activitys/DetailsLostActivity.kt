package com.lionsquare.comunidadkenna.activitys

import android.databinding.DataBindingUtil
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.CardView
import android.text.format.DateUtils
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.adapter.PagerPetAdapter
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.ActivityDetailsLostBinding
import com.lionsquare.comunidadkenna.model.Pet
import com.lionsquare.comunidadkenna.model.Response
import com.lionsquare.comunidadkenna.model.User
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences
import com.lionsquare.comunidadkenna.utils.StatusBarUtil
import com.lionsquare.comunidadkenna.widgets.CustomToast
import com.txusballesteros.AutoscaleEditText
import com.wafflecopter.charcounttextview.CharCountTextView

import retrofit2.Call
import retrofit2.Callback
import thebat.lib.validutil.ValidUtils

class DetailsLostActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, Callback<Response> {
    internal lateinit var binding: ActivityDetailsLostBinding
    private var pl: Pet? = null
    private var user: User? = null
    private var pagerPetAdapter: PagerPetAdapter? = null
    private var googleMap: GoogleMap? = null
    private var mCircle: Circle? = null
    private var aetComment: AutoscaleEditText? = null
    private var preferences: Preferences? = null
    private var dialogGobal: DialogGobal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityDetailsLostBinding>(this, R.layout.activity_details_lost)
        preferences = Preferences(this)
        dialogGobal = DialogGobal(this)
        if (intent.extras != null) {

            // TODO: 21/08/17 cuando viene de la lita
            if (intent.extras.getParcelable<Parcelable>("model") != null && intent.extras.getParcelable<Parcelable>("user") != null) {
                pl = intent.extras.getParcelable<Parcelable>("model") as Pet?
                user = intent.extras.getParcelable<Parcelable>("user") as User?
                initSetUp()

            }

            if (intent.extras.get("id") != null) {
                if (ValidUtils.isNetworkAvailable(this)) {

                    dialogGobal!!.progressIndeterminateStyle()
                    val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
                    val call = serviceApi.getPetIndividul(preferences!!.email, preferences!!.token, intent.extras.getInt("id"))
                    call.enqueue(object : Callback<Pet> {
                        override fun onResponse(call: Call<Pet>, response: retrofit2.Response<Pet>) {
                            dialogGobal!!.dimmis()

                            try {
                                pl = response.body()
                                user = pl!!.user
                                initSetUp()
                            } catch (e: Exception) {
                                dialogGobal!!.errorConexion()
                            }

                        }

                        override fun onFailure(call: Call<Pet>, t: Throwable) {
                            dialogGobal!!.dimmis()
                            dialogGobal!!.errorConexion()
                            Log.e("error", t.toString() + "")
                        }
                    })

                } else
                    dialogGobal!!.sinInternet(this)


            }


        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    internal fun initSetUp() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setTitle("")

        }

        pagerPetAdapter = PagerPetAdapter(this, pl!!.images)
        binding.adlVpPet.setAdapter(pagerPetAdapter)
        binding.adlCiIndicator.setViewPager(binding.adlVpPet)


        binding.titleToolbar.setText(pl!!.namePet)

        val tvCharCount = findViewById(R.id.tvTextCounter) as CharCountTextView
        aetComment = findViewById(R.id.adl_aet_comment) as AutoscaleEditText
        val cvSend = findViewById(R.id.adl_cv_send) as CardView

        cvSend.setOnClickListener(this)

        tvCharCount.setEditText(aetComment!!)
        tvCharCount.setMaxCharacters(150) //Will default to 150 anyway (Twitter emulation)
        tvCharCount.setExceededTextColor(Color.RED) //Will default to red also
        tvCharCount.setCharCountChangedListener {
            countRemaining, hasExceededLimit ->
            if (hasExceededLimit) {
                cvSend.isEnabled = false
            } else {
                cvSend.isEnabled = true
            }
        }
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, binding.adlContent)
        StatusBarUtil.setPaddingSmart(this, binding.toolbar)
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview))


        binding.adlTvName.setText(pl!!.namePet)
        binding.adlTvBreed.setText(pl!!.breed)
        binding.adlTvData.setText(converteTimestamp(pl!!.timestamp))

        if (pl!!.reward == 1) {
            binding.adlLlReward.setVisibility(View.GONE)
        }
        binding.adlTvDistace.setText("Se perdio a " + pl!!.distance + " metros de tu ubicación")


        binding.adlTvNamePropetary.setText(user!!.name)
        binding.adlTvDatos.setText(user!!.email)
        Glide.with(this).load(user!!.profile_pick).centerCrop().into(binding.adlCivProfile)

        if (googleMap != null) {
            addMaker()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        if (pl!!.lat != null && pl!!.lng != null) addMaker()


    }

    internal fun addMaker() {
        googleMap!!.clear()
        this.googleMap!!.uiSettings.isScrollGesturesEnabled = false
        this.googleMap!!.uiSettings.setAllGesturesEnabled(false)
        this.googleMap!!.uiSettings.isMapToolbarEnabled = false

        val latLng = LatLng(java.lang.Double.valueOf(pl!!.lat)!!, java.lang.Double.valueOf(pl!!.lng)!!)
        val marker = googleMap!!.addMarker(
                MarkerOptions().position(latLng))

        mCircle = googleMap!!.addCircle(CircleOptions()
                .center(latLng)
                .radius(1000.0)
                .strokeColor(resources.getColor(R.color.blue_circul))
                .strokeWidth(3f)
                .fillColor(resources.getColor(R.color.blue_circul))
        )
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        // TODO: 20/07/2017 Aumente el valor para acercar.
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        googleMap!!.animateCamera(cameraUpdate)


    }

    private fun converteTimestamp(mileSegundos: String): CharSequence {
        val time = java.lang.Long.parseLong(mileSegundos) * 1000
        val txt = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) as String
        return Character.toUpperCase(txt[0]) + txt.substring(1)
    }


    val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }


    internal val toolBarHeight: Int
        get() {
            var actionBarHeight = 0
            val tv = TypedValue()
            if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
            }
            return actionBarHeight
        }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.adl_cv_send -> {
                val comment = aetComment!!.text.toString()
                if (!comment.trim { it <= ' ' }.isEmpty()) {
                    if (ValidUtils.isNetworkAvailable(this))
                        sendComment(comment)
                    else
                        dialogGobal!!.sinInternet(this)
                } else {
                    CustomToast.show(this, resources.getString(R.string.mensaje_vacio), false)
                }
            }
        }
    }

    internal fun sendComment(comment: String) {
        Log.e("user.getId()", user!!.id.toString())
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.sendCommentPetLost(preferences!!.email, preferences!!.token, pl!!.id, user!!.id, comment)
        call.enqueue(this)
    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        if (response.body().success === 1) {
            dialogGobal!!.correctSend(this, resources.getString(R.string.se_envio_comentario))
        } else if (response.body().success === 2) {
            dialogGobal!!.setDialogContent(resources.getString(R.string.error), response.body().message, false)
        } else if (response.body().success === 0) {
            dialogGobal!!.tokenDeprecated(this)
        }


    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        dialogGobal!!.errorConexion()
        Log.e("errro ", t.toString() + "")
    }
}
