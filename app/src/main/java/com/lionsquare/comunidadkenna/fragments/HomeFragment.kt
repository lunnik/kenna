package com.lionsquare.comunidadkenna.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.lionsquare.comunidadkenna.AbstractAppActivity
import com.lionsquare.comunidadkenna.R
import com.lionsquare.comunidadkenna.activitys.LostRegisterActivity
import com.lionsquare.comunidadkenna.activitys.PetLossListActivity
import com.lionsquare.comunidadkenna.activitys.WallPetActivity
import com.lionsquare.comunidadkenna.api.ServiceApi
import com.lionsquare.comunidadkenna.databinding.FragmentHomeBinding
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection
import com.lionsquare.comunidadkenna.model.Response
import com.lionsquare.comunidadkenna.utils.DialogGobal
import com.lionsquare.comunidadkenna.utils.Preferences

import retrofit2.Call
import retrofit2.Callback
import thebat.lib.validutil.ValidUtils

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [HomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class HomeFragment : AbstractSectionFragment(), Callback<Response> {

    private var mListener: OnFragmentInteractionListener? = null

    private var binding: FragmentHomeBinding? = null
    private var succes = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity = getActivity() as AbstractAppActivity
        res = activity!!.resources

        beanSection = BeanSection()
        beanSection!!.sectionNameId = R.string.inicio
        beanSection!!.sectionColorPrimaryId = R.color.home_color_primary
        beanSection!!.sectionColorPrimaryDarkId = R.color.home_color_primary_dark

        preferences = Preferences(activity)
        dialogGobal = DialogGobal(activity)


    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater!!, R.layout.fragment_home, null, false)
        }
        initSetUp()
        return binding!!.root
    }

    internal fun initSetUp() {

        sectionFragmentCallbacks!!.updateSectionToolbar(beanSection!!, binding!!.includeToolbar.pinnedToolbar)
        binding!!.amIvLostpet.visibility = View.GONE

        binding!!.blurredView.setBackgroundResource(R.drawable.back_menu)
        binding!!.blurredView.adjustViewBounds = true
        binding!!.blurredView.scaleType = ImageView.ScaleType.CENTER


        binding!!.amBtnLost.setOnClickListener(this)
        binding!!.amIvLostpet.setOnClickListener(this)

        // TODO: 26/09/2017 si es que el fragmento se recupera ya no es neceario volver a cargar el servico
        if (succes == -1) {
            if (ValidUtils.isNetworkAvailable(activity!!)) {
                binding!!.amLavLoader.visibility = View.VISIBLE
                val serviceApi = ServiceApi.retrofit.create(ServiceApi::class.java)
                val call = serviceApi.checkinStatusFolio(preferences!!.email, preferences!!.token)
                call.enqueue(this)
            } else {
                dialogGobal!!.sinInternet(activity)
            }
        } else {
            binding!!.amIvLostpet.visibility = View.VISIBLE
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is OnFragmentInteractionListener) {
            mListener = activity
        } else {
            throw RuntimeException(activity!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REGISTER_PET_LOST) {
            initSetUp()
        } else {

        }
    }

    override fun onClick(v: View) {
        var iMenu: Intent? = null
        when (v.id) {

            R.id.am_btn_lost -> {
                iMenu = Intent(activity, LostRegisterActivity::class.java)
                startActivityForResult(iMenu, REGISTER_PET_LOST)
            }

            R.id.am_iv_lostpet -> {
                iMenu = Intent(activity, PetLossListActivity::class.java)
                startActivity(iMenu)
            }
        }//changeFragment(RegisterPetFragment.newInstance(), RegisterPetFragment.TAG);

    }


    /**
     * recibe si es que exite un reporte activo
     */
    override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
        binding!!.amLavLoader.visibility = View.GONE
        if (response.body().success == 1) {
            succes = response.body().success!!
            binding!!.amIvLostpet.visibility = View.VISIBLE
            animateButton(binding!!.amIvLostpet)
        } else if (response.body().success == 2) {
            // no hay folios
        } else if (response.body().success == 0) {
            dialogGobal!!.tokenDeprecated(activity)
        }
    }

    override fun onFailure(call: Call<Response>, t: Throwable) {
        binding!!.amLavLoader.visibility = View.GONE
        Log.e("error", t.toString() + "")
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        val REGISTER_PET_LOST = 1011
        val TAG = HomeFragment::class.java.name

        fun newInstace(): HomeFragment {
            val newsFragment = HomeFragment()
            val arguments = Bundle()
            newsFragment.arguments = arguments
            newsFragment.retainInstance = true
            return newsFragment
        }
    }
}
