package com.lionsquare.comunidadkenna.fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.airbnb.lottie.LottieAnimationView
import com.lionsquare.comunidadkenna.AbstractAppActivity
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.activitys.DetailsLostActivity
import com.lionsquare.comunidadkenna.activitys.MainActivity
import com.lionsquare.comunidadkenna.activitys.MenuActivity
import com.lionsquare.comunidadkenna.adapter.PetLostAdapter
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.FragmentWallPetBinding
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection
import com.lionsquare.comunidadkenna.model.ListLost
import com.lionsquare.comunidadkenna.model.Pet
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences
import com.lionsquare.comunidadkenna.utils.StatusBarUtil
import com.marcoscg.infoview.InfoView

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import thebat.lib.validutil.ValidUtils

/**
 * A simple [Fragment] subclass.
 */
class WallPetFragment : AbstractSectionFragment(), Callback<ListLost>, PetLostAdapter.ClickListener {

    internal var binding: FragmentWallPetBinding? = null

    internal lateinit var petLostAdapter: PetLostAdapter
    private var petList: List<Pet>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity = getActivity() as AbstractAppActivity
        res = activity!!.resources

        beanSection = BeanSection()
        beanSection!!.sectionNameId = R.string.perdidos
        beanSection!!.sectionColorPrimaryId = R.color.wall_pet_color_primary
        beanSection!!.sectionColorPrimaryDarkId = R.color.wall_pet_color_primary_dark

        preferences = Preferences(activity)
        dialogGobal = DialogGobal(activity)
        petList = ArrayList()

    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initSetUp();
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_wall_pet, null, false)
        }
        activity = activity
        res = activity!!.resources
        initSetUp()
        return binding!!.root
    }

    internal fun initSetUp() {
        toolbar = binding!!.includeToolbar.pinnedToolbar
        sectionFragmentCallbacks!!.updateSectionToolbar(beanSection!!, toolbar!!)

        infoView = binding!!.infoView
        infoView!!.setTitle(getString(R.string.opps))
        infoView!!.setMessage(getString(R.string.eso_no_deberia_haber_ocurrido))
        infoView!!.setIconRes(R.drawable.ic_sad_emoji)
        infoView!!.setButtonText(getString(R.string.intentar_de_nuevo))
        infoView!!.setButtonTextColorRes(R.color.colorAccent)
        infoView!!.setOnTryAgainClickListener { Toast.makeText(activity, "Try again clicked!", Toast.LENGTH_SHORT).show() }

        if (petList!!.isEmpty()) {
            if (ValidUtils.isNetworkAvailable(activity!!)) {
                getListLost()
            } else {
                dialogGobal!!.sinInternet(activity)
            }
        } else {
            initRv(petList)

        }


    }

    internal fun getListLost() {
        infoView!!.setProgress(true)
        val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
        val call = serviceApi.getListPetLost(preferences!!.email, preferences!!.token)
        call.enqueue(this)
    }

    internal fun initRv(list: List<Pet>?) {

        petLostAdapter = PetLostAdapter(activity, list)
        petLostAdapter.setClickListener(this)
        val mLayoutManager = LinearLayoutManager(activity)
        binding!!.awRvPet.layoutManager = mLayoutManager
        binding!!.awRvPet.itemAnimator = DefaultItemAnimator()
        binding!!.awRvPet.adapter = petLostAdapter
        petLostAdapter.setLoadMoreListener { position ->
            binding!!.awRvPet.post {
                if (petList!!.size > 15) {
                    val index = Integer.valueOf(petList!![position].id)!! - 1

                    //loadMore(index);
                }
            }
        }
    }


    override fun onClick(v: View) {


    }

    override fun itemClicked(position: Int) {
        val iDetails = Intent(activity, DetailsLostActivity::class.java)
        iDetails.putExtra("model", petList!![position])
        iDetails.putExtra("user", petList!![position].user)
        startActivity(iDetails)
        val pet = petList!![position]


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val a: Activity
        if (context is Activity) {
            a = context
            try {
                sectionFragmentCallbacks = a as AbstractSectionFragment.SectionFragmentCallbacks
            } catch (e: ClassCastException) {
                throw ClassCastException(activity!!.toString() + " must implement SectionFragmentCallbacks")
            }

        }
    }

    override fun onResponse(call: Call<ListLost>, response: Response<ListLost>) {
        infoView!!.visibility = View.GONE
        if (response.body().success == 1) {
            petList = response.body().listLost
            initRv(petList)
        } else if (response.body().success == 2) {
            dialogGobal!!.noMatches(activity)
        } else if (response.body().success == 0) {
            dialogGobal!!.tokenDeprecated(activity)
        }


    }

    override fun onFailure(call: Call<ListLost>, t: Throwable) {
        infoView!!.setProgress(false)
        dialogGobal!!.errorConexionFinish(activity)
        Log.e("err", t.toString())
    }


    // TODO: 26/09/2017 regresa a la posicion o el rv
    fun returnFisrtItem() {
        binding!!.awRvPet.smoothScrollToPosition(0)
        //binding.awRvPet.scrollToPosition(0);
    }

    companion object {

        fun newInstace(): WallPetFragment {
            val wallPetFragment = WallPetFragment()
            val arguments = Bundle()
            wallPetFragment.arguments = arguments
            wallPetFragment.retainInstance = true
            return wallPetFragment
        }

        val TAG = WallPetFragment::class.java.name
    }


}
