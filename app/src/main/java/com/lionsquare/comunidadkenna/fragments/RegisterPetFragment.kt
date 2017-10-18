package com.lionsquare.comunidadkenna.fragments


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.CompoundButton

import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
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
import com.lionsquare.comunidadkenna.activitys.LostRegisterActivity
import com.lionsquare.comunidadkenna.adapter.ImagePetAdapter
import com.lionsquare.comunidadkenna.adapter.SpinnerCustomAdapter
import com.lionsquare.comunidadkenna.api.RBParseo
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.ActivityLostBinding
import com.lionsquare.comunidadkenna.databinding.FragmentRegisterPetBinding
import com.lionsquare.comunidadkenna.db.DbManager
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection
import com.lionsquare.comunidadkenna.model.Breed
import com.lionsquare.comunidadkenna.model.Response
import com.lionsquare.comunidadkenna.model.SpinnerObject
import com.lionsquare.comunidadkenna.model.User
import com.lionsquare.comunidadkenna.task.FileFromBitmap
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.widgets.NumberTextWatcher
import com.lionsquare.multiphotopicker.photopicker.activity.PickImageActivity

import java.util.ArrayList
import java.util.Calendar

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import thebat.lib.validutil.ValidUtils

/**
 * A simple [Fragment] subclass.
 */
class RegisterPetFragment : AbstractSectionFragment(), OnMapReadyCallback, FileFromBitmap.CommunicationChannel, Callback<Response>, AdapterView.OnItemSelectedListener {

    private var googleMap: GoogleMap? = null
    private var mapView: MapView? = null


    private var lat: Double = 0.toDouble()
    private var lng: Double = 0.toDouble()
    private var user: User? = null
    lateinit var files: List<MultipartBody.Part>
    private var imagePetAdapter: ImagePetAdapter? = null

    private var breed: String? = null

    lateinit var CustomListViewValuesArr: ArrayList<SpinnerObject>

    internal var binding: FragmentRegisterPetBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity = getActivity() as AbstractAppActivity
        res = activity!!.resources

        beanSection = BeanSection()
        beanSection!!.sectionNameId = R.string.registar
        beanSection!!.sectionColorPrimaryId = R.color.register_color_primary
        beanSection!!.sectionColorPrimaryDarkId = R.color.register_color_primary_dark

        dbManager = DbManager(activity).open()
        dialogGobal = DialogGobal(activity)
        user = dbManager!!.user
        files = ArrayList<MultipartBody.Part>()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_register_pet, null, false)
        }
        toolbar = binding!!.includeToolbar.pinnedToolbar
        sectionFragmentCallbacks!!.updateSectionToolbar(beanSection!!, toolbar!!)
        initSetUp(savedInstanceState)
        return binding!!.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.register_pet, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        if (id == R.id.action_send) {
            if (ValidUtils.isNetworkAvailable(activity!!))
                sendData()
            else
                dialogGobal!!.sinInternet(activity)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    internal fun initSetUp(savedInstanceState: Bundle?) {

        if (mapView == null) {
            mapView = binding!!.map
            mapView!!.onCreate(savedInstanceState)
            mapView!!.onResume()
            mapView!!.getMapAsync(this)
        }
        binding!!.alBtnPhoto.setOnClickListener {
            // TODO: 27/07/2017 contexto , num de fotos, moddo simpelo multiple ver camara, y preview
            //ImageSelectorActivity.start(LostRegisterActivity.this, 5, 1, false, false, true);
            openImagePickerIntent()
        }
        binding!!.alTxtMoney.addTextChangedListener(NumberTextWatcher(binding!!.alTxtMoney, "#,###"))

        if (binding!!.alCbReward.isChecked)
            binding!!.alTxtMoney.visibility = View.VISIBLE
        else
            binding!!.alTxtMoney.visibility = View.GONE

        binding!!.alCbReward.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                binding!!.alTxtMoney.visibility = View.VISIBLE
            else
                binding!!.alTxtMoney.visibility = View.GONE
        }

        binding!!.alSpBreed.onItemSelectedListener = this

        binding!!.alBtnChangeLoc.setOnClickListener(this)


        lat = user!!.lat!!
        lng = user!!.lng!!

        CustomListViewValuesArr = ArrayList()
        val listBreed = Breed.breedList()
        for (i in listBreed.indices) {
            CustomListViewValuesArr.add(i, SpinnerObject(listBreed[i]))
        }
        val res = resources
        val adapter = SpinnerCustomAdapter(activity, R.layout.spinner_dropdown, CustomListViewValuesArr, res)
        binding!!.alSpBreed.adapter = adapter


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
                val place = PlacePicker.getPlace(activity!!, data!!)
                if (place != null) {
                    val latLng = place.latLng
                    updateLoc(latLng)
                } else {
                    Log.e("error", "sdfsgrfger")

                }
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == PickImageActivity.PICKER_REQUEST_CODE) {
            val images = data!!.extras.getStringArrayList(PickImageActivity.KEY_DATA_RESULT)
            if (images != null && !images.isEmpty()) {
                val sb = StringBuilder("")
                for (i in images.indices) {
                    sb.append("Photo" + (i + 1) + ":" + images[i])
                    sb.append("\n")
                }
                Log.e("images", sb.toString())

            }
            // TODO: 30/08/2017 este asyntask es para reducir el tamaño de lafotos
            val fileFromBitmap = FileFromBitmap(images, activity)
            fileFromBitmap.setmCommChListner(this)
            fileFromBitmap.execute()

            /*   for (int pos = 0; pos < images.size(); pos++) {
                String item = images.get(pos);
                File file = new File(item);
                RequestBody file1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part1 = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), file1);
                files.add(part1);
            }*/

        }


    }


    internal fun updateLoc(latLng: LatLng) {
        googleMap!!.clear()
        val marker = googleMap!!.addMarker(
                MarkerOptions().position(latLng))

        val mCircle = googleMap!!.addCircle(CircleOptions()
                .center(latLng)
                .radius(500.0)
                .strokeColor(resources.getColor(R.color.blue_circul))
                .strokeWidth(3f)
                .fillColor(resources.getColor(R.color.blue_circul))
        )
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))


        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14f)
        googleMap!!.animateCamera(cameraUpdate)
        lat = latLng.latitude
        lng = latLng.longitude

    }

    override fun onResume() {
        mapView!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap!!.uiSettings.isScrollGesturesEnabled = false
        this.googleMap!!.uiSettings.setAllGesturesEnabled(false)
        this.googleMap!!.uiSettings.isMapToolbarEnabled = false
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        addMaker()
    }


    internal fun addMaker() {
        try {

            if (googleMap != null) {

                googleMap!!.clear()
                val user = dbManager!!.user
                val latLng = LatLng(user!!.lat!!, user.lng!!)
                val marker = googleMap!!.addMarker(
                        MarkerOptions().position(latLng))

                val mCircle = googleMap!!.addCircle(CircleOptions()
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
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("error ", "maops")
        }


    }

    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        dialogGobal!!.dimmis()
        if (response.body().success == 1) {
            MaterialDialog.Builder(activity!!)
                    .title(R.string.send)
                    .content(R.string.se_envio_la_alerta_correctamente)
                    .cancelable(false)
                    .positiveText(R.string.ok)
                    .onPositive { dialog, which ->
                        activity!!.setResult(Activity.RESULT_OK)
                        activity!!.finish()
                    }
                    .show()
        } else if (response.body().success == 2) {
            MaterialDialog.Builder(activity!!)
                    .title(R.string.error)
                    .content(response.body().message)
                    .cancelable(false)
                    .positiveText(R.string.ok)
                    .onPositive { dialog, which -> }
                    .show()
        } else if (response.body().success == 0) {
            dialogGobal!!.tokenDeprecated(activity)
        } else {
            MaterialDialog.Builder(activity!!)
                    .title(R.string.error)
                    .content(R.string.ocurrio_un_error_al_procesar_tu_solicitud)
                    .cancelable(true)
                    .positiveText(R.string.reintentar)
                    .onPositive { dialog, which ->
                        if (ValidUtils.isNetworkAvailable(activity!!))
                            sendData()
                        else
                            dialogGobal!!.sinInternet(activity)
                    }
                    .show()
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        dialogGobal!!.dimmis()
        dialogGobal!!.errorConexionFinish(activity)
        Log.e("error", t.toString() + "")
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.al_btn_change_loc -> locationPlacesIntent()
        }
    }

    private fun sendData() {

        // Reset errors.
        binding!!.alTxtNamePet.error = null
        binding!!.alTxtMoney.error = null


        // Store values at the time of the login attempt.
        val namePet = binding!!.alTxtNamePet.text.toString()


        var reward = "0"
        var money = "0"

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (breed == "") {
            val shake = AnimationUtils.loadAnimation(context, R.anim.shake)
            binding!!.alSpBreed.startAnimation(shake)
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(namePet)) {
            binding!!.alTxtNamePet.error = getString(R.string.error_field_required)
            focusView = binding!!.alTxtNamePet
            cancel = true
        } else if (files.isEmpty()) {
            dialogGobal!!.setDialogContent(resources.getString(R.string.faltan_datos), resources.getString(R.string.debes_agregar_al_menos_una_foto), false)
            val shake = AnimationUtils.loadAnimation(activity, R.anim.shake)
            binding!!.alBtnPhoto.startAnimation(shake)
            cancel = true
        }

        if (binding!!.alCbReward.isChecked) {
            reward = "1"
            val s = binding!!.alTxtMoney.text.toString()
            Log.e("money", s)
            if (s.length > 0) {
                money = binding!!.alTxtMoney.text.toString()

                val length = money.length
                var result = ""
                for (i in 0 until length) {
                    val character = money[i]
                    if (Character.isDigit(character)) {
                        result += character
                    }
                }
                money = result.substring(0, result.length - 2)
            } else {
                binding!!.alTxtMoney.error = getString(R.string.error_field_required)
                focusView = binding!!.alTxtMoney
                cancel = true
            }


        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null)
                focusView.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            dialogGobal!!.progressIndeterminateStyle()

            val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
            val call = serviceApi.sendReportLostPet(
                    RBParseo.parseoText(user!!.email),
                    RBParseo.parseoText(user!!.token),
                    RBParseo.parseoText(lat.toString()),
                    RBParseo.parseoText(lng.toString()),
                    RBParseo.parseoText(binding!!.alTxtNamePet.text.toString()),
                    RBParseo.parseoText(breed),
                    RBParseo.parseoText(reward),
                    RBParseo.parseoText(money),
                    files,
                    RBParseo.parseoText(Calendar.getInstance().time.toString())
            )
            call.enqueue(this)
        }
    }

    private fun openImagePickerIntent() {
        val mIntent = Intent(activity, PickImageActivity::class.java)
        mIntent.putExtra(PickImageActivity.KEY_LIMIT_MAX_IMAGE, 5)
        mIntent.putExtra(PickImageActivity.KEY_LIMIT_MIN_IMAGE, 1)
        activity!!.startActivityForResult(mIntent, PickImageActivity.PICKER_REQUEST_CODE)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (position == 0) {
            breed = ""
        } else {
            breed = parent.getItemAtPosition(position).toString()
        }


    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun setCommunication(files: List<MultipartBody.Part>, images: List<String>) {
        this.files = files
        imagePetAdapter = ImagePetAdapter(activity, images)
        val horizontalLayoutManagaer = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding!!.alRvImage.layoutManager = horizontalLayoutManagaer
        binding!!.alRvImage.adapter = imagePetAdapter
    }

    companion object {


        fun newInstance(): RegisterPetFragment {
            val newsFragment = RegisterPetFragment()
            val arguments = Bundle()
            newsFragment.arguments = arguments
            newsFragment.retainInstance = true
            return newsFragment
        }

        private val PLACE_PICKER_REQUEST = 1
        val TAG = ProfileUserFragment::class.java.name
    }
}
