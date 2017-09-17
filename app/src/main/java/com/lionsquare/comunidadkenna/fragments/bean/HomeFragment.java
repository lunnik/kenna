package com.lionsquare.comunidadkenna.fragments.bean;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.databinding.FragmentHomeBinding;
import com.lionsquare.comunidadkenna.db.DbManager;
import com.lionsquare.comunidadkenna.fragments.AbstractSectionFragment;
import com.lionsquare.comunidadkenna.fragments.RegisterPetFragment;
import com.lionsquare.comunidadkenna.utils.DialogGobal;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends AbstractSectionFragment {

    private OnFragmentInteractionListener mListener;

    private FragmentHomeBinding binding;

    public HomeFragment newInstace() {
        HomeFragment newsFragment = new HomeFragment();
        Bundle arguments = new Bundle();
        newsFragment.setArguments(arguments);
        newsFragment.setRetainInstance(true);
        return newsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();

        beanSection = new BeanSection();
        beanSection.sectionNameId = R.string.inicio;
        beanSection.sectionColorPrimaryId = R.color.primaryColor;
        beanSection.sectionColorPrimaryDarkId = R.color.primaryColorDark;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, null, false);

        return binding.getRoot();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
