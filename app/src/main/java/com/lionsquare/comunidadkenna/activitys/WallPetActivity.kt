package com.lionsquare.comunidadkenna.activitys

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle


import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem


import com.lionsquare.comunidadkenna.R


import com.lionsquare.comunidadkenna.adapter.PetLostAdapter
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.ActivityWallPetBinding
import com.lionsquare.comunidadkenna.model.ListLost
import com.lionsquare.comunidadkenna.model.Pet
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences
import com.lionsquare.comunidadkenna.utils.StatusBarUtil

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import thebat.lib.validutil.ValidUtils


class WallPetActivity : AppCompatActivity(), PetLostAdapter.ClickListener, Callback<ListLost> {

    internal lateinit var binding: ActivityWallPetBinding
    internal lateinit var petLostAdapter: PetLostAdapter
    private var petList: List<Pet>? = null
    private var context: Context? = null
    private var preferences: Preferences? = null
    private var dialogGobal: DialogGobal? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wall_pet)
        context = this
        preferences = Preferences(this)
        dialogGobal = DialogGobal(this)
        petList = ArrayList()
        initSetUp()

    }

    internal fun initSetUp() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Perdidos cerca de ti"

        }


        initRv(petList)
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, binding.awRvPet)
        StatusBarUtil.setPaddingSmart(this, binding.toolbar)
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview))
        //StatusBarUtil.setMargin(this, binding.refreshLayout);
        binding.refreshLayout.setProgressViewOffset(false, 100, 300)

        if (ValidUtils.isNetworkAvailable(this)) {
            getListLost()
        } else {
            dialogGobal!!.sinInternet(this)
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

    internal fun getListLost() {
        dialogGobal!!.progressIndeterminateStyle()
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.getListPetLost(preferences!!.email, preferences!!.token)
        call.enqueue(this)
    }

    internal fun initRv(list: List<Pet>?) {

        petLostAdapter = PetLostAdapter(this, list)
        petLostAdapter.setClickListener(this)
        val mLayoutManager = LinearLayoutManager(context)
        binding.awRvPet.layoutManager = mLayoutManager
        binding.awRvPet.itemAnimator = DefaultItemAnimator()
        binding.awRvPet.adapter = petLostAdapter
        petLostAdapter.setLoadMoreListener { position ->
            binding.awRvPet.post {
                if (petList!!.size > 15) {
                    val index = Integer.valueOf(petList!![position].id)!! - 1

                    //loadMore(index);
                }
            }
        }
    }


    override fun itemClicked(position: Int) {
        val iDetails = Intent(this, DetailsLostActivity::class.java)
        iDetails.putExtra("model", petList!![position])
        iDetails.putExtra("user", petList!![position].user)
        startActivity(iDetails)
        val pet = petList!![position]


    }

    override fun onResponse(call: Call<ListLost>, response: Response<ListLost>) {
        dialogGobal!!.dimmis()
        if (response.body().success == 1) {
            petList = response.body().listLost
            initRv(petList)
        } else if (response.body().success == 2) {
            //vacio
        } else if (response.body().success == 0) {
            dialogGobal!!.tokenDeprecated(this)
        }


    }

    override fun onFailure(call: Call<ListLost>, t: Throwable) {
        dialogGobal!!.dimmis()
        dialogGobal!!.errorConexionFinish(this)
        Log.e("err", t.toString())
    }
}
