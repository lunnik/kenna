package com.lionsquare.comunidadkenna.activitys

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem

import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.adapter.OwnPetAdapter
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.ActivityPetLossListBinding
import com.lionsquare.comunidadkenna.model.FolioPet
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

class PetLossListActivity : AppCompatActivity(), OwnPetAdapter.ClickListener, Callback<List<FolioPet>> {
    internal lateinit var binding: ActivityPetLossListBinding
    private var ownPetAdapter: OwnPetAdapter? = null
    private var folioPets: List<FolioPet>? = null
    private var context: Context? = null
    private var preferences: Preferences? = null
    private var dialogGobal: DialogGobal? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_loss_list)
        context = this
        preferences = Preferences(this)
        dialogGobal = DialogGobal(this)
        folioPets = ArrayList()
        initSetUp()

    }

    internal fun initSetUp() {
        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        }


        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, binding.apllRvPetOwn)
        StatusBarUtil.setPaddingSmart(this, binding.toolbar)
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview))


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
        val call = serviceApi.getFolioLostPet(preferences!!.email, preferences!!.token)
        call.enqueue(this)
    }


    internal fun initRv(list: List<FolioPet>?) {

        ownPetAdapter = OwnPetAdapter(context, list)
        ownPetAdapter!!.setClickListener(this)
        val mLayoutManager = LinearLayoutManager(context)
        binding.apllRvPetOwn.layoutManager = mLayoutManager
        binding.apllRvPetOwn.itemAnimator = DefaultItemAnimator()
        binding.apllRvPetOwn.adapter = ownPetAdapter

    }


    override fun itemClicked(position: Int) {
        val fp = folioPets!![position]
        val i = Intent(this, LostStatusActivity::class.java)
        i.putExtra("FolioPet", fp)
        i.putExtra("pet", fp.pet)

        startActivity(i)

    }

    override fun onResponse(call: Call<List<FolioPet>>, response: Response<List<FolioPet>>) {
        dialogGobal!!.dimmis()
        folioPets = response.body()
        initRv(folioPets)

    }

    override fun onFailure(call: Call<List<FolioPet>>, t: Throwable) {
        dialogGobal!!.dimmis()
        dialogGobal!!.errorConexion()
    }
}
