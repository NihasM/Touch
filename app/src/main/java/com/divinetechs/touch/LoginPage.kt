package com.divinetechs.touch

import android.content.res.ColorStateList
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.divinetechs.touch.databinding.FragmentLoginPageBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener

class LoginPage : BaseFragment<FragmentLoginPageBinding>() {
    lateinit var email: String
    lateinit var pass: String
    private lateinit var mAuth: FirebaseAuth

    override fun getLayoutRes(): Int {
        return R.layout.fragment_login_page

    }


    override fun setupViews() {

        (requireActivity() as MainActivity).toolBarVisibility(false,false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        }

        mAuth = FirebaseAuth.getInstance()

        binding.passInputLayout.setEndIconTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary)))
        binding.edtpass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = s?.isNotEmpty() ?: false
                binding.passInputLayout.endIconMode = if (hasText) TextInputLayout.END_ICON_PASSWORD_TOGGLE else TextInputLayout.END_ICON_NONE
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_registrationPage)
        }

        binding.btnlogin.setOnClickListener {
            email=binding.edtUsername.text.toString()
            pass=binding.edtpass.text.toString()
            if(email.equals("")){
                binding.textInputLayout.error="Please enter your contact no."
            }else if(pass.equals("")){
                binding.passInputLayout.error="Please enter password"
                binding.textInputLayout.error=null
            }else{
                binding.passInputLayout.error=null
                binding.textInputLayout.error=null
                login(email,pass)
            }
        }
    }

    override fun setupListeners() {
        // Set up listeners
    }

    override fun loadData() {
        // Load data
    }

    fun login(email:String,pass:String){

        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    Log.d("kool", "createUserWithEmail:success")
                    SharedPreferences.saveString(requireContext(), Constants.UserId, email)
                    SharedPreferences.saveString(requireContext(), Constants.Password, pass)
                    findNavController().navigate(R.id.action_login_to_homepage)
                } else {
                    Log.d("kool", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(getActivity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
                }
            })
    }

}
