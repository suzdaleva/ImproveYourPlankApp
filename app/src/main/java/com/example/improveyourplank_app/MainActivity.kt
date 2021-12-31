package com.example.improveyourplank_app

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.improveyourplank_app.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import io.alterac.blurkit.BlurLayout
import android.graphics.PorterDuff

import android.graphics.PorterDuffColorFilter

import android.graphics.ColorFilter
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.airbnb.lottie.value.LottieFrameInfo

import com.airbnb.lottie.value.SimpleLottieValueCallback

import com.airbnb.lottie.LottieProperty

import com.airbnb.lottie.model.KeyPath
import android.widget.Toast

import androidx.annotation.NonNull
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected

import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var blackScrim: TextView
    private lateinit var whiteScrim: TextView
    private lateinit var menu1 : LottieAnimationView
    var isTrackerFragment = false
    var isMenuAlreadyWhite = false

    val Int.dp: Int
    get()=(this*Resources.getSystem().displayMetrics.density + 0.5f).toInt()


    @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        blackScrim = binding.blackScrim
        whiteScrim = binding.whiteScrim
        menu1 = binding.menuIcon

        drawerLayout.setScrimColor(Color.TRANSPARENT)
        drawerLayout.drawerElevation = 0F
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.menuIcon.setOnClickListener {

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                if(isTrackerFragment) changeMenuIconColor(Color.BLACK)
                menu1.speed=1f
                menu1.playAnimation()
                //drawerLayout.open()
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
        }
        drawerLayout.setOnTouchListener { v, event ->
            if(drawerLayout.isOpen) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                drawerLayout.close()
                lifecycleScope.launch {
                    drawerLayout.close()
                    delay(300)
                    if(isTrackerFragment && !isMenuAlreadyWhite) changeMenuIconColor(Color.WHITE)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            true
        }

        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if(destination.id == R.id.workoutsTrackerFragment) {
                isTrackerFragment = true
                changeMenuIconColor(Color.WHITE)
            } else {
                isTrackerFragment = false
                changeMenuIconColor(Color.BLACK)
            }
        }
        drawerLayout.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                blackScrim.alpha = slideOffset
                whiteScrim.width = (280 * slideOffset).toInt().dp
            }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerStateChanged(newState: Int) {
                if (newState == DrawerLayout.STATE_SETTLING) {
                    lifecycleScope.launch {
                    if (drawerLayout.isOpen) {
                        delay(50)
                        menu1.speed=-1.2f
                        menu1.playAnimation()
                    }}
                }
            }
        })

}


    fun changeMenuIconColor(color:Int) {
        menu1.addValueCallback(
            KeyPath("**"),
            LottieProperty.COLOR_FILTER, {
                PorterDuffColorFilter(
                    color,
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        )
    }

    fun setIsMenuAlreadyWhite(boolean: Boolean) {
        isMenuAlreadyWhite = boolean
    }




    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


}
