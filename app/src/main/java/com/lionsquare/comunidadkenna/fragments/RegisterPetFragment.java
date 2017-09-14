package com.lionsquare.comunidadkenna.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.databinding.FragmentRegisterPetBinding;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPetFragment extends AbstractSectionFragment {


    public static RegisterPetFragment newInstance() {
        RegisterPetFragment newsFragment = new RegisterPetFragment();
        Bundle arguments = new Bundle();
        newsFragment.setArguments(arguments);
        newsFragment.setRetainInstance(true);

        return newsFragment;
    }

    FragmentRegisterPetBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        activity = (AbstractAppActivity) getActivity();
        res = activity.getResources();

        beanSection = new BeanSection();
        beanSection.sectionNameId = R.string.registar;
        beanSection.sectionColorPrimaryId = R.color.transparent;
        beanSection.sectionColorPrimaryDarkId = R.color.register_color_primary;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register_pet, null, false);
        toolbar = binding.includeColpasing.guidesToolbar;
        collapsingToolbarLayout = binding.includeColpasing.collapsingToolbar;
        sectionFragmentCallbacks.updateSectionToolbar(beanSection, collapsingToolbarLayout, toolbar);

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

    }
}
