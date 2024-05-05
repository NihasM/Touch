package com.divinetechs.touch.chatlist

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.divinetechs.touch.BaseFragment
import com.divinetechs.touch.MainActivity
import com.divinetechs.touch.R
import com.divinetechs.touch.databinding.FragmentUsersListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UsersListFrag : BaseFragment<FragmentUsersListBinding>() {

    private lateinit var userList: ArrayList<Users>
    private lateinit var adapter: UsersAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun getLayoutRes(): Int {
        return R.layout.fragment_users_list
    }


    override fun setupViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        }
        (requireActivity() as MainActivity).toolBarVisibility(true,true)
        (requireActivity() as MainActivity).setToolbarTitle(getString(R.string.search))
        (requireActivity() as MainActivity).getCurrentFragment()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        (requireActivity() as MainActivity).progress(true)
        userList= ArrayList()
        adapter = UsersAdapter(requireActivity(), userList)

        binding.rcyUser.layoutManager=LinearLayoutManager(context)
        binding.rcyUser.adapter=adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(Users::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                Log.d("kool", "onDataChange: "+userList.size)
                adapter.notifyDataSetChanged()
                (requireActivity() as MainActivity).progress(false)
            }

            override fun onCancelled(error: DatabaseError) {
                (requireActivity() as MainActivity).progress(false)
            }
        })



    }

    override fun setupListeners() {
        // Set up listeners
    }

    override fun loadData() {
        // Load data
    }
}