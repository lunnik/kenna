package com.lionsquare.comunidadkenna.activitys

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.util.Log
import android.view.MenuItem
import android.view.View

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.adapter.CommentAdapter


import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.ActivityLostStatusBinding
import com.lionsquare.comunidadkenna.model.CommentDatum
import com.lionsquare.comunidadkenna.model.FolioPet
import com.lionsquare.comunidadkenna.model.Pet

import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences
import com.lionsquare.comunidadkenna.utils.StatusBarUtil

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import thebat.lib.validutil.ValidUtils


class LostStatusActivity : AppCompatActivity(), CommentAdapter.ClickListener, View.OnClickListener {

    private var preferences: Preferences? = null
    private var dialogGobal: DialogGobal? = null
    private var context: Context? = null
    private var commentAdapter: CommentAdapter? = null
    private var fl: FolioPet? = null
    private var pet: Pet? = null


    private var binding: ActivityLostStatusBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LostStatusActivity, R.layout.activity_lost_status)
        context = this
        preferences = Preferences(this)
        dialogGobal = DialogGobal(this)
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, binding!!.toolbar)
        if (intent.extras != null) {
            if (intent.extras.getParcelable<Parcelable>("FolioPet") != null && intent.extras.getParcelable<Parcelable>("pet") != null) {
                fl = intent.extras.getParcelable<Parcelable>("FolioPet") as FolioPet?
                pet = intent.extras.getParcelable<Parcelable>("pet") as Pet?
                initSetUp()

            }

            if (intent.extras.get("id") != null) {
                if (ValidUtils.isNetworkAvailable(this)) {
                    dialogGobal!!.progressIndeterminateStyle()
                    Log.e("id", intent.extras.getInt("id").toString())

                    val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
                    val call = serviceApi.getFolioIndividual(preferences!!.email, preferences!!.token, intent.extras.getInt("id"))
                    call.enqueue(object : Callback<FolioPet> {
                        override fun onResponse(call: Call<FolioPet>, response: Response<FolioPet>) {
                            dialogGobal!!.dimmis()
                            fl = response.body()
                            pet = fl!!.pet
                            initSetUp()
                        }

                        override fun onFailure(call: Call<FolioPet>, t: Throwable) {
                            dialogGobal!!.dimmis()
                            dialogGobal!!.errorConexion()
                        }
                    })

                } else {
                    dialogGobal!!.sinInternet(this)
                }

            }


        }


    }


    internal fun initSetUp() {

        setSupportActionBar(binding!!.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = ""

        }


        binding!!.titleToolbar.text = ""

        Glide.with(context).load(pet!!.images[0]).listener(object : RequestListener<String, GlideDrawable> {
            override fun onException(e: Exception, model: String, target: Target<GlideDrawable>, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: GlideDrawable, model: String, target: Target<GlideDrawable>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {


                return false
            }
        }).into(binding!!.alsIvPet)
        binding!!.alsTvMascota.text = pet!!.namePet
        if (pet!!.reward == 1) {
            binding!!.alsTvMoney.text = "$ " + pet!!.money
        } else {
            binding!!.ll2.visibility = View.GONE
        }

        if (pet!!.status == 1) {
            binding!!.alsTvEstatus.text = "Activo"

        }

        binding!!.apllBtnBaja.setOnClickListener(this)
        binding!!.apllBtnEncontado.setOnClickListener(this)
        binding!!.apllBtnPerdido.setOnClickListener(this)

        binding!!.alsTvTime.text = converteTimestamp(pet!!.timestamp)
        initRv(fl!!.commentData)


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


    private fun converteTimestamp(mileSegundos: String): CharSequence {
        val time = java.lang.Long.parseLong(mileSegundos) * 1000
        val txt = DateUtils.getRelativeTimeSpanString(time, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS) as String
        return Character.toUpperCase(txt[0]) + txt.substring(1)
    }


    internal fun initRv(list: List<CommentDatum>) {

        commentAdapter = CommentAdapter(context, list)
        commentAdapter!!.setClickListener(this)
        val mLayoutManager = LinearLayoutManager(context)
        binding!!.alsRvComment.layoutManager = mLayoutManager
        binding!!.alsRvComment.itemAnimator = DefaultItemAnimator()
        binding!!.alsRvComment.adapter = commentAdapter
    }

    override fun itemClicked(position: Int) {

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.apll_btn_encontado -> sendStatus(2)
            R.id.apll_btn_perdido -> sendStatus(3)
            R.id.apll_btn_baja -> sendStatus(4)
        }
    }

    internal fun sendStatus(status: Int) {
        dialogGobal!!.progressIndeterminateStyle()
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.changeStatusPet(preferences!!.email, preferences!!.token, pet!!.id, status)
        call.enqueue(object : Callback<com.lionsquare.comunidadkenna.model.Response> {
            override fun onResponse(call: Call<com.lionsquare.comunidadkenna.model.Response>, response: Response<com.lionsquare.comunidadkenna.model.Response>) {
                dialogGobal!!.dimmis()

                if (response.body().success == 1) {
                    dialogGobal!!.setDialogContent(response.body().message, "", false)

                } else if (response.body().success == 2) {
                    dialogGobal!!.setDialogContent(response.body().message, "", false)
                } else if (response.body().success == 0) {
                    dialogGobal!!.tokenDeprecated(this@LostStatusActivity)
                }

            }

            override fun onFailure(call: Call<com.lionsquare.comunidadkenna.model.Response>, t: Throwable) {
                dialogGobal!!.dimmis()
                dialogGobal!!.errorConexion()
            }
        })


    }
}
