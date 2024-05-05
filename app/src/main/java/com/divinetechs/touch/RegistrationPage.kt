package com.divinetechs.touch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.divinetechs.touch.chatlist.Users
import com.divinetechs.touch.databinding.FragmentRegistrationPageBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RegistrationPage : BaseFragment<FragmentRegistrationPageBinding>() {
    lateinit var username: String
    lateinit var email: String
    lateinit var pass: String
    lateinit var cnfpass: String
    var chkpass=false
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mStorageRef: StorageReference
    private lateinit var imageUri: Uri


    override fun getLayoutRes(): Int {
        return R.layout.fragment_registration_page

    }

    override fun setupViews() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
        }
        mAuth = FirebaseAuth.getInstance()
        (requireActivity() as MainActivity).toolBarVisibility(false,false)

        binding.edtConfirmPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if(binding.edtpass.text.toString()!=binding.edtConfirmPass.text.toString()){
                   binding.inputConfirmPass.error="Your password not match"
                   chkpass=false
                   if(binding.edtConfirmPass.text.toString()==""){
                       binding.inputConfirmPass.error=null
                   }
               }else{
                   binding.inputConfirmPass.error=null
                   chkpass=true
               }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnSignup.setOnClickListener {
            username=binding.edtUsername.text.toString()
            email=binding.edtContactno.text.toString()
            pass=binding.edtpass.text.toString()
            cnfpass=binding.edtConfirmPass.text.toString()
            /*if(username==""){
                binding.textInputName.error="Please enter your user name"
            }else if(email==""){
                binding.textInputName.error=null
                binding.passInputContactno.error="Please enter your contact no."
            }else if(pass==""){
                binding.passInputContactno.error=null
                binding.passInputPass.error="Please enter your password"
            }else if(cnfpass==""){
                binding.passInputPass.error=null
                binding.inputConfirmPass.error="Please confirm your password"
            }else{
                binding.inputConfirmPass.error=null
                Toast.makeText(requireContext(), "OK!", Toast.LENGTH_SHORT).show()
            }
            findNavController().navigate(R.id.action_signup_to_chatlist)*/
            (requireActivity() as MainActivity).progress(true)
            signup(username,email,pass)
        }

        binding.imgBtn.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()   			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }
    }

    override fun setupListeners() {
        // Set up listeners
    }

    override fun loadData() {
        // Load data
    }


    fun signup(username: String, email: String, pass: String) {
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(requireActivity(), OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(username,email,mAuth.currentUser?.uid!!)
                    Log.d("kool", "createUserWithEmail:success")
                    SharedPreferences.saveString(requireContext(), Constants.UserId, email)
                    SharedPreferences.saveString(requireContext(), Constants.Password, pass)
                } else {
                    Log.w("kool", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(getActivity(), R.string.signup_fail, Toast.LENGTH_SHORT).show()
                    (requireActivity() as MainActivity).progress(false)
                }
            })
    }

    fun addUserToDatabase(name: String, email: String, uid: String){

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(Users(name,email,uid))

        if(imageUri!=null){
            uploadprofile()
        }

    }

    fun uploadprofile(){
        /*if(imageUri!=null){
            mStorageRef = FirebaseStorage.getInstance().getReference("Users/"+mAuth.currentUser?.uid)
            mStorageRef.putFile(imageUri).addOnSuccessListener {

                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()


            }.addOnFailureListener {
                Toast.makeText(context, "Image uploaded failed", Toast.LENGTH_SHORT).show()
            }
        }else{
            findNavController().navigate(R.id.action_signup_to_homepage)
        }*/

        PhotoUploader.uploadPhoto(
            requireContext(),
            mAuth,
            imageUri,
            { onPhotoUploadSuccess() },
            { onPhotoUploadFailure() }
        )

    }

    private fun onPhotoUploadSuccess() {
        (requireActivity() as MainActivity).progress(false)
        findNavController().navigate(R.id.action_signup_to_homepage)
    }

    private fun onPhotoUploadFailure() {
        (requireActivity() as MainActivity).progress(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri: Uri? = data?.data
        // Now you can use the 'uri' variable
        if (uri != null) {
            // Do something with the uri
            binding.imgprofile.setImageURI(uri)
            imageUri = uri
        }
    }



}