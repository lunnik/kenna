package com.lionsquare.comunidadkenna.activitys

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.iid.FirebaseInstanceId
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.ActivityLocationPickerBinding
import com.lionsquare.comunidadkenna.db.DbManager
import com.lionsquare.comunidadkenna.model.CheckoutLogin
import com.lionsquare.comunidadkenna.model.RecoverProfile
import com.lionsquare.comunidadkenna.model.Register
import com.lionsquare.comunidadkenna.model.User
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences
import com.lionsquare.comunidadkenna.utils.StatusBarUtil

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import thebat.lib.validutil.ValidUtils

class LocationPickerActivity : AppCompatActivity(), View.OnClickListener, Callback<CheckoutLogin> {
    internal lateinit var binding: ActivityLocationPickerBinding
    private var preferences: Preferences? = null
    private var dbManager: DbManager? = null

    private var dialogGobal: DialogGobal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_picker)
        StatusBarUtil.darkMode(this)
        dialogGobal = DialogGobal(this)
        initSetUp()

    }

    private fun initSetUp() {
        preferences = Preferences(this)
        dbManager = DbManager(this).open()
        //  Log.e("token",FirebaseInstanceId.getInstance().getToken());

        if (dbManager!!.user != null) {
            val iMenu = Intent(this, MenuActivity::class.java)
            startActivity(iMenu)
            finish()
        } else {
            binding.placeSearchDialogOkTV.setOnClickListener(this)

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                verifyPermission()
            } else {
                binding.placeSearchDialogOkTV.isEnabled = true
                if (ValidUtils.isNetworkAvailable(this))
                    checkoutLogin()
                else {

                    dialogGobal!!.sinInternet(this)

                }

            }
        }

        binding.placeSearchDialogCancelTV.setOnClickListener(this)


    }

    internal fun checkoutLogin() {
        dialogGobal!!.progressIndeterminateStyle()
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.checkoutEmail(preferences!!.email)
        call.enqueue(this)
    }

    override fun onRestart() {
        super.onRestart()
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            verifyPermission()
        } else {
            binding.placeSearchDialogOkTV.isEnabled = true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                if (place != null) {
                    val latLng = place.latLng
                    if (ValidUtils.isNetworkAvailable(this))
                        sendPrefile(latLng)
                    else
                        dialogGobal!!.sinInternet(this)

                    binding.placeSearchDialogOkTV.visibility = View.GONE
                    binding.placeSearchDialogCancelTV.visibility = View.GONE
                } else {
                    Log.e("error", "sdfsgrfger")

                }
            }
        }

    }

    private fun locationPlacesIntent() {
        try {

            val builder = PlacePicker.IntentBuilder()

            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)

        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
            Log.e("error lat", e.toString())
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
            Log.e("error lat", e.toString())
        }

    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.place_search_dialog_cancel_TV -> finish()
            R.id.place_search_dialog_ok_TV -> locationPlacesIntent()
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun verifyPermission() {
        val writePermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
            binding.placeSearchDialogOkTV.isEnabled = false
        } else {
            //saveComments();
            binding.placeSearchDialogOkTV.isEnabled = true
            checkoutLogin()
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            showSnackBar()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMIS_LOCATION)
        }
    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(binding.alpRoot, R.string.permission_location, Snackbar.LENGTH_INDEFINITE)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMIS_LOCATION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //saveComments();
                binding.placeSearchDialogOkTV.isEnabled = true
                checkoutLogin()
            } else {
                binding.placeSearchDialogOkTV.isEnabled = false
                showSnackBar()
            }
        }
    }

    private fun sendPrefile(latLng: LatLng) {
        dialogGobal!!.progressIndeterminateStyle()
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.registerProfile(preferences!!.name, preferences!!.email, preferences!!.imagePerfil, FirebaseInstanceId.getInstance().token, preferences!!.typeLogin, latLng.latitude, latLng.longitude)

        call.enqueue(object : Callback<Register> {
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                dialogGobal!!.dimmis()
                dbManager!!.insertUser(preferences!!.name, preferences!!.email, preferences!!.imagePerfil,
                        preferences!!.cover, preferences!!.typeLogin, preferences!!.tokenSosial, preferences!!.token,
                        latLng.latitude, latLng.longitude)
                val iMenu = Intent(this@LocationPickerActivity, MenuActivity::class.java)
                startActivity(iMenu)
                finish()
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                dialogGobal!!.dimmis()
                Log.e("error de conexion", t.toString())

                MaterialDialog.Builder(this@LocationPickerActivity)
                        .title(R.string.error)
                        .content(R.string.ocurrio_un_error_al_contectar)
                        .cancelable(false)
                        .negativeText(R.string.intentar_otra_vez)
                        .onNegative { dialog, which ->
                            if (!ValidUtils.isNetworkAvailable(this@LocationPickerActivity))
                                sendPrefile(latLng)
                            else
                                dialogGobal!!.sinInternet(this@LocationPickerActivity)
                        }
                        .positiveText(R.string.salir)
                        .onPositive { dialog, which -> finish() }
                        .progressIndeterminateStyle(true)
                        .show()
            }
        })

    }

    override fun onResponse(call: Call<CheckoutLogin>, response: Response<CheckoutLogin>) {
        dialogGobal!!.dimmis()
        if (response.body().success == 1) {
            // TODO: 31/07/2017 actulizamos el perfil ya sea que cambio de cuanta
            if (preferences!!.typeLogin != response.body().type_account)
                diferenteAccount()
            else
                recoverProfileData()
        }
    }

    override fun onFailure(call: Call<CheckoutLogin>, t: Throwable) {
        Log.e("error de conexion", t.toString())
        dialogGobal!!.dimmis()
    }

    // TODO: 31/07/2017 si se accede con la misma cuenta solo se recuperan los datos del servidor con el correo
    internal fun recoverProfileData() {
        dialogGobal!!.setDialog(resources.getString(R.string.recuperando_datos))
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.recoverProfile(preferences!!.email, FirebaseInstanceId.getInstance().token)
        call.enqueue(object : Callback<RecoverProfile> {
            override fun onResponse(call: Call<RecoverProfile>, response: Response<RecoverProfile>) {
                dialogGobal!!.dimmis()
                if (response.body().success == 1) {
                    val user = response.body().user
                    dbManager!!.insertUser(preferences!!.name, preferences!!.email, preferences!!.imagePerfil,
                            preferences!!.cover, preferences!!.typeLogin, preferences!!.tokenSosial, user.token,
                            user.lat, user.lng)
                    val iMenu = Intent(this@LocationPickerActivity, MenuActivity::class.java)
                    startActivity(iMenu)
                    finish()
                    dbManager!!.close()
                }
            }

            override fun onFailure(call: Call<RecoverProfile>, t: Throwable) {
                dialogGobal!!.dimmis()
                Log.e("error", t.toString())
                dialogGobal!!.errorConexionFinish(this@LocationPickerActivity)
            }
        })

    }


    // TODO: 31/07/2017 cuando es iferentes ala cuenta con la que estabas pero es le mismo coreo se aztulizan los perfiles
    internal fun diferenteAccount() {
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.updateProfile(
                preferences!!.email, preferences!!.name, preferences!!.imagePerfil, FirebaseInstanceId.getInstance().token, preferences!!.typeLogin)
        call.enqueue(object : Callback<RecoverProfile> {
            override fun onResponse(call: Call<RecoverProfile>, response: Response<RecoverProfile>) {
                if (response.body().success == 1) {
                    val user = response.body().user
                    dbManager!!.insertUser(preferences!!.name, preferences!!.email, preferences!!.imagePerfil,
                            preferences!!.cover, preferences!!.typeLogin, preferences!!.tokenSosial, user.token,
                            user.lat, user.lng)
                    val iMenu = Intent(this@LocationPickerActivity, MenuActivity::class.java)
                    startActivity(iMenu)
                    finish()
                    dbManager!!.close()
                }
            }

            override fun onFailure(call: Call<RecoverProfile>, t: Throwable) {
                dialogGobal!!.errorConexionFinish(this@LocationPickerActivity)
            }
        })
    }

    companion object {
        private val PLACE_PICKER_REQUEST = 1
        private val PERMIS_LOCATION = 1
    }
}
