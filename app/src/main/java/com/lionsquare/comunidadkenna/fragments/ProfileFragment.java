package com.lionsquare.comunidadkenna.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lionsquare.comunidadkenna.AbstractAppActivity;
import com.lionsquare.comunidadkenna.R;
import com.lionsquare.comunidadkenna.databinding.FragmentProfileBinding;
import com.lionsquare.comunidadkenna.fragments.bean.BeanSection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends AbstractSectionFragment {


    public static ProfileFragment newInstance() {
        ProfileFragment newsFragment = new ProfileFragment();

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
        beanSection.sectionNameId = R.string.perfil;
        beanSection.sectionColorPrimaryId = R.color.news_color_primary;
        beanSection.sectionColorPrimaryDarkId = R.color.news_color_primary_dark;
    }

    FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, null, false);
        toolbar= binding.toolbar;
        sectionFragmentCallbacks.updateSectionToolbar(beanSection, toolbar);
        sectionFragmentCallbacks.setSearchViewVisible(true);
        return binding.getRoot();

    }

    @Override
    public void onClick(View v) {

    }
}
