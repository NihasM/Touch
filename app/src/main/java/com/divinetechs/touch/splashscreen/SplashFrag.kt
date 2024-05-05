package com.divinetechs.touch.splashscreen

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.divinetechs.touch.BaseFragment
import com.divinetechs.touch.Constants
import com.divinetechs.touch.MainActivity
import com.divinetechs.touch.R
import com.divinetechs.touch.SharedPreferences
import com.divinetechs.touch.databinding.FragmentChatScreenBinding
import com.divinetechs.touch.databinding.FragmentSplashBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SplashFrag : BaseFragment<FragmentSplashBinding>()  {
    lateinit var email: String
    lateinit var pass: String
    private lateinit var mAuth: FirebaseAuth

    override fun getLayoutRes(): Int {
        return R.layout.fragment_splash
    }

    override fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        }
        (requireActivity() as MainActivity).toolBarVisibility(false,false)
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.touch))
        mAuth = FirebaseAuth.getInstance()
        email = SharedPreferences.getString(requireContext(), Constants.UserId, "")
        pass = SharedPreferences.getString(requireContext(), Constants.Password, "")



        CoroutineScope(Dispatchers.Main).launch{
            delay(5000)
            login(email,pass)
        }

    }

    override fun setupListeners() {
        // Set up listeners
    }

    override fun loadData() {
        // Load data
    }

    fun login(email:String,pass:String){
        if(!email.isNullOrEmpty() && !pass.isNullOrEmpty()){
            mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_splashFrag_to_homepage)
                    } else {
                        findNavController().navigate(R.id.action_splashFrag_to_loginpage)
                    }
                })
        }else{
            findNavController().navigate(R.id.action_splashFrag_to_loginpage)
        }

    }
}