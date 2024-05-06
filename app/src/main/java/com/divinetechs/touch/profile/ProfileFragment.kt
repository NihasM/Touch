package com.divinetechs.touch.profile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.divinetechs.touch.BaseFragment
import com.divinetechs.touch.MainActivity
import com.divinetechs.touch.R
import com.divinetechs.touch.databinding.FragmentHomeBinding
import com.divinetechs.touch.databinding.FragmentProfileBinding
import com.divinetechs.touch.userlist.Users
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private var imageUri: Uri? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_profile
    }

    override fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity?.window?.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.primary)
        }
        (requireActivity() as MainActivity).toolBarVisibility(true, true)
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.profile))
        (requireActivity() as MainActivity).progress(true)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(Users::class.java)
                    if(mAuth.currentUser?.uid == currentUser?.uid) {
                        binding.edtUsername.setText(currentUser?.name)
                        binding.useremail.setText(currentUser?.email)
                        Glide.with(requireContext())
                            .load(currentUser?.profileImageUrl) // assuming you have a field for profile image URL in Users class
                            .placeholder(R.drawable.newprofileimg) // optional placeholder while loading
                            .error(R.drawable.newprofileimg) // optional error placeholder
                            //.transition(DrawableTransitionOptions.withCrossFade()) // optional transition
                            .into(binding.imgprofile)
                    }
                    (requireActivity() as MainActivity).progress(false)
                }

                (requireActivity() as MainActivity).progress(false)
            }

            override fun onCancelled(error: DatabaseError) {
                (requireActivity() as MainActivity).progress(false)
            }
        })

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