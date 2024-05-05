package com.divinetechs.touch

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.divinetechs.touch.chatlist.UsersListFrag
import com.divinetechs.touch.databinding.ActivityMainBinding
import com.divinetechs.touch.home.HomeFragment
import com.divinetechs.touch.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Declare binding variable
    var nav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.bottomNavigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    // Add method to change toolbar title
    fun setToolbarTitle(title: String) {
        binding.txtToolbarTitle.text = title
    }

    fun toolBarVisibility(view: Boolean, bottomNav: Boolean) {
        if (view == true) {
            binding.toolbar.visibility = View.VISIBLE
            Log.d("kool", "if: ")
        } else {
            binding.toolbar.visibility = View.GONE

            Log.d("kool", "else: ")
        }
        if (bottomNav == true) {
            binding.bottomNavigation.visibility = View.VISIBLE
            Log.d("kool", "if: ")
        } else {
            binding.bottomNavigation.visibility = View.GONE

            Log.d("kool", "else: ")
        }

    }

    fun progress(view: Boolean) {
        if (view == true) {
            binding.progressScreen.visibility = View.VISIBLE
        } else {
            binding.progressScreen.visibility = View.GONE
        }
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.navigation_home -> {
                    Log.d("kool", ": "+nav)
                    if (getCurrentFragment() !is HomeFragment && nav==true) {
                        replaceFragment(HomeFragment())
                        nav=false
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    if (getCurrentFragment() !is UsersListFrag) {
                        replaceFragment(UsersListFrag())
                        nav=true
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    if (getCurrentFragment() !is ProfileFragment) {
                        replaceFragment(ProfileFragment())
                        nav=true
                    }
                    return@OnNavigationItemSelectedListener true
                }
                else -> false
            }
        }

    fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.splash_host)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            // Specify the enter and exit animations
            setCustomAnimations(
                R.anim.slide_in_from_right, // enter animation
                R.anim.slide_out_to_left, // exit animation
                R.anim.slide_in_from_left, // pop enter animation
                R.anim.slide_out_to_right // pop exit animation
            )

            // Replace the fragment
            replace(R.id.splash_host, fragment)

            // Commit the transaction without adding it to the back stack
            commit()
        }
    }



}
