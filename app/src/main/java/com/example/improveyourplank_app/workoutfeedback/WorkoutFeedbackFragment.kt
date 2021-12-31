package com.example.improveyourplank_app.workoutfeedback

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.database.WorkoutDatabase
import com.example.improveyourplank_app.databinding.FragmentWorkoutFeedbackBinding
import com.example.improveyourplank_app.workoutstracker.WorkoutsTrackerFragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class WorkoutFeedbackFragment : Fragment() {
    lateinit var fabMenu: ExtendedFloatingActionButton
    lateinit var fabSave: LinearLayout
    lateinit var fabDelete: LinearLayout
    lateinit var fabRedo: LinearLayout
    lateinit var toWorkoutsButton: Button
    lateinit var scrim: View
    var isFABOpen = false
    lateinit var menuAnimator: ObjectAnimator
    lateinit var set: AnimatorSet


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("I/TimerFragment", "Feedback fragment created")
        val binding: FragmentWorkoutFeedbackBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_workout_feedback, container, false)
        val application = requireNotNull(this.activity).application
        val arguments = WorkoutFeedbackFragmentArgs.fromBundle(requireArguments())

        val dataSource = WorkoutDatabase.getInstance(application).workoutDatabaseDao
        val viewModelFactory = WorkoutFeedbackViewModelFactory(arguments.workoutKey, dataSource)

        val workoutFeedbackViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(WorkoutFeedbackViewModel::class.java)
        binding.workoutFeedbackViewModel = workoutFeedbackViewModel
        binding.lifecycleOwner = this

        workoutFeedbackViewModel.navigateToTimer.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController()
                    .navigate(WorkoutFeedbackFragmentDirections.actionWorkoutFeedbackFragmentToTimerFragment())
                workoutFeedbackViewModel.doneNavigating()
            }

        })
        workoutFeedbackViewModel.navigateToWorkoutsTracker.observe(
            viewLifecycleOwner,
            Observer {
                if (it == true) {
                    this.findNavController()
                        .navigate(WorkoutFeedbackFragmentDirections.actionWorkoutFeedbackFragmentToWorkoutsTrackerFragment())
                    workoutFeedbackViewModel.doneNavigatingToWorkoutsTracker()
                }
            })

        workoutFeedbackViewModel.isSaved.observe(
            viewLifecycleOwner,
            Observer {
                if (it == true) Toast.makeText(
                    context,
                    "Your workout is successfully saved",
                    Toast.LENGTH_SHORT
                ).show()
                else Toast.makeText(
                    context,
                    "Your have already deleted this workout",
                    Toast.LENGTH_SHORT
                ).show()
                closeFABMenu()
            })

        workoutFeedbackViewModel.isDeleted.observe(
            viewLifecycleOwner,
            Observer {
                if (it == true) {
                    Toast.makeText(
                        context,
                        "Your workout is successfully deleted",
                        Toast.LENGTH_SHORT
                    ).show()
                    closeFABMenu()
                    binding.workoutTimeValue.text = "00:00"
                    binding.targetTimeValue.text = "00:00"
                    binding.feedbackValue.text = "Not sure yet..."
                }
            })

        fabMenu = binding.fabMenu
        fabSave = binding.fabSave
        fabDelete = binding.fabDelete
        fabRedo = binding.fabRedo
        binding.dontAsk.setOnClickListener {
            binding.dontAsk.playAnimation()
            workoutFeedbackViewModel.onSetWorkoutFeedback(1)
        }
        binding.exhausted.setOnClickListener {
            binding.exhausted.playAnimation()
            workoutFeedbackViewModel.onSetWorkoutFeedback(2)
        }
        binding.soSo.setOnClickListener {
            binding.soSo.playAnimation()
            workoutFeedbackViewModel.onSetWorkoutFeedback(3)
        }
        binding.prettyGood.setOnClickListener {
            binding.prettyGood.playAnimation()
            workoutFeedbackViewModel.onSetWorkoutFeedback(4)
        }
        binding.feelingGreat.setOnClickListener {
            binding.feelingGreat.playAnimation()
            workoutFeedbackViewModel.onSetWorkoutFeedback(5)
        }
        toWorkoutsButton = binding.toWorkoutsButton
        scrim = binding.feedbackScrim

        menuAnimator = ObjectAnimator.ofFloat(fabMenu, View.ROTATION, -90f, 0f)
        val deleteAnimator = ObjectAnimator.ofFloat(
            fabDelete, View.TRANSLATION_Y,
            -240f
        )
        val redoAnimator = ObjectAnimator.ofFloat(
            fabRedo, View.TRANSLATION_Y,
            -460f
        )
        val saveAnimator = ObjectAnimator.ofFloat(
            fabSave, View.TRANSLATION_Y,
            -680f
        )
        set = AnimatorSet()
        set.playTogether(deleteAnimator, redoAnimator, saveAnimator, menuAnimator)
        set.interpolator = WorkoutsTrackerFragment.MyBounceInterpolator()
        set.duration = 400

        fabMenu.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }


        return binding.root
    }

    @SuppressLint("Recycle")
    private fun showFABMenu() {
        isFABOpen = true
        fabDelete.visibility = View.VISIBLE
        fabRedo.visibility = View.VISIBLE
        fabSave.visibility = View.VISIBLE
        scrim.visibility = View.VISIBLE
        toWorkoutsButton.isEnabled = false
        set.start()
    }

    private fun closeFABMenu() {
        isFABOpen = false
        set.reverse()
        set.hideFabs()
        toWorkoutsButton.isEnabled = true
    }

    internal class MyBounceInterpolator : Interpolator {
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
                    fabDelete.visibility = View.GONE
                    fabRedo.visibility = View.GONE
                    fabSave.visibility = View.GONE
                    scrim.visibility = View.GONE
                }
            }
        })
    }

}