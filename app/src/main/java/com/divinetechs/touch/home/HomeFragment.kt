package com.divinetechs.touch.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.divinetechs.touch.BaseFragment
import com.divinetechs.touch.MainActivity
import com.divinetechs.touch.R
import com.divinetechs.touch.databinding.FragmentHomeBinding
import com.divinetechs.touch.databinding.FragmentUsersListBinding


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_home
    }

    override fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity?.window?.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.primary)
        }
        (requireActivity() as MainActivity).toolBarVisibility(true, true)
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.touch))
        (requireActivity() as MainActivity).getCurrentFragment()
    }

    override fun setupListeners() {
        // Set up listeners
    }

    override fun loadData() {
        // Load data
    }



}