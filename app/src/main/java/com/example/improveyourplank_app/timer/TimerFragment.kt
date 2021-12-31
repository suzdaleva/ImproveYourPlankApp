package com.example.improveyourplank_app.timer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.database.WorkoutDatabase
import com.example.improveyourplank_app.databinding.FragmentTimerBinding
import com.example.improveyourplank_app.timepicker.CustomTimePicker
import com.google.android.material.timepicker.MaterialTimePicker
import io.alterac.blurkit.BlurLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerFragment : Fragment() {
    var blurStarted: Boolean = false
    private lateinit var blurLayout: BlurLayout
    private var movement = 150f
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("I/TimerFragment", "Timer fragment created")
        val binding: FragmentTimerBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        blurLayout = binding.blurLayout

        blurLayout.animate().translationY(movement).setDuration(1500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    movement = if (movement > 0) -150F else 150F
                    blurLayout.animate().translationY(movement).setDuration(1500).setListener(this)
                        .start()
                }
            }).start()
//            blurLayout.startBlur()
//        blurLayout.invalidate()
        lifecycleScope.launch {
            delay(1)
            blurLayout.lockView()

        }

        val application = requireNotNull(this.activity).application
        val dataSource = WorkoutDatabase.getInstance(application).workoutDatabaseDao
        val viewModelFactory = TimerViewModelFactory(dataSource, application)

        val timerViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(TimerViewModel::class.java)


        binding.timerViewModel = timerViewModel

        binding.lifecycleOwner = this

        timerViewModel.isTargetTimeSet.observe(viewLifecycleOwner, Observer {

            val customTimePicker: CustomTimePicker = CustomTimePicker.Builder()
                .setTitleText("Your Current Goal Is:")
                .setSecond(0)
                .setMinute(0)
                .setPositiveButtonText("OK")
                .setNegativeButtonText("CANCEL")
                .build()
            customTimePicker.show(childFragmentManager, "CustomTimePickerDialog")
            timerViewModel.timerRunning = false

            // on clicking the positive button of the time picker
            // dialog update the TextView accordingly
            customTimePicker.addOnNegativeButtonClickListener(){
                timerViewModel.onStartAndPause()
            }
            customTimePicker.addOnPositiveButtonClickListener {
                val pickedMinutes: Int = customTimePicker.minute
                val pickedSeconds: Int = customTimePicker.second
                Log.i("I/Improve", "${customTimePicker.minute}")
                Log.i("I/Improve", "${customTimePicker.second}")
                // check for single digit hour hour and minute
                // and update TextView accordingly
                val formattedTime: String = when {
                    pickedMinutes >= 10 -> {
                        if (pickedSeconds < 10) {
                            "${customTimePicker.minute}:0${customTimePicker.second}"
                        } else {
                            "${customTimePicker.minute}:${customTimePicker.second}"
                        }
                    }
                    else -> {
                        if (pickedSeconds < 10) {
                            "0${customTimePicker.minute}:0${customTimePicker.second}"
                        } else {
                            "0${customTimePicker.minute}:${customTimePicker.second}"
                        }
                    }
                }
                binding.targetTime.text = formattedTime
                timerViewModel._targetTime.value = customTimePicker.minute*60L + customTimePicker.second
                timerViewModel.onStartAndPause()
            }
        })




        timerViewModel.currentTime.observe(viewLifecycleOwner, Observer{
            currentTime ->

            binding.timer.text = DateUtils.formatElapsedTime(currentTime)
            if (timerViewModel.currentTime.value!! == 1L) {
                blurLayout.unlockView()
                lifecycleScope.launch {
                    for(i in 1..80) {
                        val params: ConstraintLayout.LayoutParams = blurLayout.layoutParams as ConstraintLayout.LayoutParams
                        params.setMargins(68, params.topMargin-4, 0, 0)
                        blurLayout.layoutParams = params
                        //blurLayout.fps = 60
                        blurLayout.invalidate()
                        //blurLayout.blurRadius = 5
                        blurLayout.startBlur()
                        delay(1)
                    }
                }
                //blurLayout.fps = 0
                //blurLayout.lockView()
            }
//            blurLayout?.lockView()
//            blurLayout?.invalidate()
//            blurLayout?.startBlur()

        })

        timerViewModel.navigateToWorkoutFeedback.observe(viewLifecycleOwner, Observer { workout ->
            workout?.let {
                this.findNavController().navigate(
                    TimerFragmentDirections
                        .actionTimerFragmentToWorkoutFeedbackFragment(workout.workoutId))
                //timerViewModel.doneNavigating()
            }
        })
        return binding.root
    }



    override fun onStart(){
        super.onStart()

//        blurLayout.startBlur()
//        blurLayout.lockView()
//        if(!blurStarted) {
//        blurLayout.startBlur()
//            blurLayout.lockView()
//        blurStarted = true
//        }
    }

    override fun onStop(){
        super.onStop()
        //blurLayout.pauseBlur()
    }

}