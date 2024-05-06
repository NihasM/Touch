package com.divinetechs.touch

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.divinetechs.touch.userlist.Users
import com.divinetechs.touch.databinding.FragmentRegistrationPageBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
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
    private var imageUri: Uri? = null



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
            if(username.isNullOrEmpty()){
                binding.textInputName.error="Please enter username"
                binding.passInputContactno.isErrorEnabled=false
                binding.passInputPass.isErrorEnabled=false
                binding.inputConfirmPass.isErrorEnabled=false
            }else if(email.isNullOrEmpty()){
                binding.textInputName.isErrorEnabled=false
                binding.passInputPass.isErrorEnabled=false
                binding.inputConfirmPass.isErrorEnabled=false
                binding.textInputName.isErrorEnabled=false
                binding.passInputContactno.error="Please enter email"
            }else if(!isValidEmail(email)){
                binding.textInputName.isErrorEnabled=false
                binding.passInputPass.isErrorEnabled=false
                binding.inputConfirmPass.isErrorEnabled=false
                binding.passInputContactno.error="Please enter vaild email"
            }else if(pass.isNullOrEmpty()){
                binding.textInputName.isErrorEnabled=false
                binding.passInputContactno.isErrorEnabled=false
                binding.inputConfirmPass.isErrorEnabled=false
                binding.passInputContactno.isErrorEnabled=false
                binding.passInputPass.error="Please enter password"
            }else if(pass.length<6){
                binding.textInputName.isErrorEnabled=false
                binding.passInputContactno.isErrorEnabled=false
                binding.inputConfirmPass.isErrorEnabled=false
                binding.passInputPass.error="Password length should be 6 or more then that"
            }else if(cnfpass.isNullOrEmpty()){
                binding.passInputPass.isErrorEnabled=false
                binding.inputConfirmPass.error="Please confirm your password"
            }else if(imageUri==null){
                binding.passInputPass.isErrorEnabled=false
                showSnackbar("Please upload profile photo")
            }
            else if(chkpass){
                binding.inputConfirmPass.isErrorEnabled=false
                (requireActivity() as MainActivity).progress(true)
                signup(username,email,pass)
            }

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
        val userRef = mDbRef.child("user").child(uid)

        // Create a Users object with name, email, and UID
        val user = Users(name, email, uid)

        // Set user data in the database
        userRef.setValue(user).addOnSuccessListener {
            // If user data is successfully set, now upload profile image
            if(imageUri != null){
                uploadProfile(uid)
            } else {
                onPhotoUploadSuccess()
            }
        }.addOnFailureListener { exception ->
            // Handle failures
            Log.e("TAG", "Failed to add user: $exception")
            onPhotoUploadFailure()
        }
    }

    fun uploadProfile(uid: String) {
        // Create a storage reference with the UID of the user
        mStorageRef = FirebaseStorage.getInstance().getReference("Users/$uid/profile.jpg")

        // Upload the image
        mStorageRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
            // Get the download URL of the uploaded image
            mStorageRef.downloadUrl.addOnSuccessListener { uri ->
                // Update the user data in the database with the download URL of the image
                val userRef = FirebaseDatabase.getInstance().getReference("user/$uid")
                userRef.child("profileImageUrl").setValue(uri.toString()).addOnSuccessListener {
                    // On successful upload, navigate to the homepage
                    onPhotoUploadSuccess()
                }.addOnFailureListener { exception ->
                    // Handle failures
                    Log.e("TAG", "Failed to update profile image URL: $exception")
                    onPhotoUploadFailure()
                }
            }
        }.addOnFailureListener { exception ->
            // Handle failures
            Log.e("TAG", "Failed to upload profile image: $exception")
            onPhotoUploadFailure()
        }
    }

    private fun onPhotoUploadSuccess() {
        (requireActivity() as MainActivity).progress(false)
        showSnackbar("Successfully signed up")
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

    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }


}