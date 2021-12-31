package com.example.improveyourplank_app.workoutstracker

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.improveyourplank_app.MainActivity
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.database.WorkoutDatabase
import com.example.improveyourplank_app.databinding.FragmentWorkoutsTrackerBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalTime


class WorkoutsTrackerFragment : Fragment() {
    lateinit var fabMenu: ExtendedFloatingActionButton
    lateinit var fabAddNew: ExtendedFloatingActionButton
    lateinit var fabDeleteAll: FloatingActionButton
    lateinit var fabAddNewText: TextView
    lateinit var fabDeleteAllText: TextView
    var isFABOpen = false
    lateinit var scrim: View
    lateinit var lottieDrawable: LottieDrawable
    lateinit var menuAnimator: ObjectAnimator
    lateinit var set: AnimatorSet
    lateinit var appBarLayout: AppBarLayout
    lateinit var motionLayout: MotionLayout

    @SuppressLint("NotifyDataSetChanged", "Recycle")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWorkoutsTrackerBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_workouts_tracker, container, false)
        val application = requireNotNull(this.activity).application

        val dataSource = WorkoutDatabase.getInstance(application).workoutDatabaseDao

        val viewModelFactory = WorkoutsTrackerViewModelFactory(dataSource, application)

        val workoutsTrackerViewModel =
            ViewModelProvider(this, viewModelFactory).get(WorkoutsTrackerViewModel::class.java)
        binding.workoutsTrackerViewModel = workoutsTrackerViewModel


        val adapter =
            WorkoutsSwipeAdapter(context, dataSource, WorkoutFeedBackClickListener { workoutId ->
                workoutsTrackerViewModel.onWorkoutClicked(workoutId)
            })
        binding.lifecycleOwner = this

        binding.workoutsList.adapter = adapter
        adapter.notifyDataSetChanged()
        binding.workoutsList.scheduleLayoutAnimation()


        fabMenu = binding.fabMenu
        fabAddNew = binding.fabAddNew
        fabDeleteAll = binding.fabDeleteAll
        fabAddNewText = binding.fanAddNewText
        fabDeleteAllText = binding.fanDeleteAllText
        //deleteAminButton = binding.deleteAnim
        scrim = binding.trackerScrim

        lottieDrawable = LottieDrawable()

        LottieCompositionFactory.fromRawRes(context, R.raw.delete_all).addListener { lottieComposition ->
            lottieDrawable.composition = lottieComposition
            lottieDrawable.scale=1f
            //lottieDrawable.playAnimation()
        }

        fabDeleteAll.setOnClickListener {
            lifecycleScope.launch() {
            lottieDrawable.playAnimation()
                delay(1500)
            workoutsTrackerViewModel.onDeleteAll()
            binding.workoutsList.isLayoutFrozen = false
            adapter.workoutList = ArrayList()
            adapter.notifyDataSetChanged()
            //Log.i("I/Improve", "Delete is clickable")
            closeFABMenu()}
        }
        //fabDeleteAll.scaleType = ImageView.ScaleType.FIT_CENTER
        fabDeleteAll.setImageDrawable(lottieDrawable)
        //fabDeleteAll.setScaleType(ImageView.ScaleType.FIT_CENTER)

        menuAnimator = ObjectAnimator.ofFloat(fabMenu, View.ROTATION, -90f, 0f)
        val deleteAllAnimator = ObjectAnimator.ofFloat(
            fabDeleteAll, View.TRANSLATION_Y,
            -240f
        )
        val addNewAnimator = ObjectAnimator.ofFloat(
            fabAddNew, View.TRANSLATION_Y,
            -460f
        )
        val deleteAllTextAnimator = ObjectAnimator.ofFloat(
            fabDeleteAllText, View.TRANSLATION_Y,
            -240f
        )
        val addNewTextAnimator = ObjectAnimator.ofFloat(
            fabAddNewText, View.TRANSLATION_Y,
            -460f
        )
        set = AnimatorSet()
        set.playTogether(
            deleteAllAnimator,
            addNewAnimator,
            menuAnimator,
            deleteAllTextAnimator,
            addNewTextAnimator
        )
        set.interpolator = MyBounceInterpolator()
        set.duration = 400

        fabMenu.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
                binding.workoutsList.isLayoutFrozen = true
                val params = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
                val behavior = params.behavior as AppBarLayout.Behavior?
                behavior!!.setDragCallback(object : DragCallback() {
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return false
                    }
                })
            } else {
                closeFABMenu()
                binding.workoutsList.isLayoutFrozen = false
            }
        }
//        fabDeleteAll.setOnClickListener {
//            workoutsTrackerViewModel.onDeleteAll()
//            adapter.workoutList = ArrayList()
//            adapter.notifyDataSetChanged()
//            closeFABMenu()
//        }

        workoutsTrackerViewModel.navigateToTimer.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController()
                    .navigate(WorkoutsTrackerFragmentDirections.actionWorkoutsTrackerFragmentToTimerFragment())
                //workoutsTrackerViewModel.doneNavigating()
            }

        })

        appBarLayout = binding.appBarLayout
        motionLayout = binding.motionLayout
        coordinateMotion()
        return binding.root

    }


    @SuppressLint("Recycle")
    private fun showFABMenu() {
        isFABOpen = true
        fabDeleteAll.visibility = View.VISIBLE
        fabAddNew.visibility = View.VISIBLE
        fabDeleteAllText.visibility = View.VISIBLE
        fabAddNewText.visibility = View.VISIBLE
        scrim.visibility = View.VISIBLE
        set.start()
    }

    private fun closeFABMenu() {
        isFABOpen = false
        set.reverse()
        set.hideFabs()
    }

    class MyBounceInterpolator : Interpolator {
        var mAmplitude = 0.2
        var mFrequency = 6.0

        constructor() {}
        constructor(amp: Double, freq: Double) {
            mAmplitude = amp
            mFrequency = freq
        }

        override fun getInterpolation(time: Float): Float {
            return (-1 * Math.pow(
                Math.E,
                -time / mAmplitude
            ) * Math.cos(mFrequency * time) + 1).toFloat()
        }
    }

    private fun AnimatorSet.hideFabs() {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (!isFABOpen) {
                    fabDeleteAllText.visibility = View.GONE
                    fabAddNewText.visibility = View.GONE
                    fabDeleteAll.visibility = View.GONE
                    fabAddNew.visibility = View.GONE
                    scrim.visibility = View.GONE
                    if (!(activity as MainActivity?)!!.isMenuAlreadyWhite) {
                        (activity as MainActivity?)!!.changeMenuIconColor(Color.BLACK)
                    }
                }
            }
        })
    }

    private fun coordinateMotion() {

        val listener = AppBarLayout.OnOffsetChangedListener { unused, verticalOffset ->
            val seekPosition = -verticalOffset / (appBarLayout.totalScrollRange.toFloat())
            motionLayout.progress = seekPosition
            if (motionLayout.progress == 1.0f) {
                (activity as MainActivity?)!!.changeMenuIconColor(Color.BLACK)
                (activity as MainActivity?)!!.setIsMenuAlreadyWhite(true)
            }
            if (motionLayout.progress < 1.0f) {
                (activity as MainActivity?)!!.changeMenuIconColor(Color.WHITE)
                (activity as MainActivity?)!!.setIsMenuAlreadyWhite(false)
            }
        }
        appBarLayout.addOnOffsetChangedListener(listener)
    }

}