package com.example.improveyourplank_app.timer

import android.graphics.drawable.Animatable

import android.graphics.drawable.Drawable
import android.media.AudioRecord.MetricsConstants.SOURCE

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.annotation.RequiresApi

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions

import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.database.WorkoutDatabase
import com.example.improveyourplank_app.databinding.FragmentTimerBinding
import com.example.improveyourplank_app.timepicker.CustomTimePicker

import kotlinx.coroutines.*


class TimerFragment : Fragment() {

    private lateinit var timerDrawable: ImageView
    private  lateinit var drawable: Drawable
    var isTimerStarted = false
    private lateinit var mediaPlayer : MediaPlayer

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("I/TimerFragment", "Timer fragment created")
        val binding: FragmentTimerBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = WorkoutDatabase.getInstance(application).workoutDatabaseDao
        val viewModelFactory = TimerViewModelFactory(dataSource, application)

        val timerViewModel =
            ViewModelProvider(
                this, viewModelFactory
            ).get(TimerViewModel::class.java)


        binding.timerViewModel = timerViewModel

        binding.lifecycleOwner = this
//        Log.i("Test", "Got here")
//        binding.timerAnimation.surfaceTextureListener =  object : TextureView.SurfaceTextureListener {
//            override fun onSurfaceTextureAvailable( surface: SurfaceTexture, width: Int, height: Int
//            ) {
//                Log.i("Test", "Got  1 here")
//                val surface = Surface(surface)
//                Log.i("Test", "Got 2 here")
//                val assetFileDescriptor: AssetFileDescriptor = context!!.assets!!.openFd("timer_animation.gif")
//                mediaPlayer = MediaPlayer()
//                Log.i("Test", "Got here")
//                mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
//                Log.i("Test", "And even here")
//                mediaPlayer.setSurface(surface)
//                mediaPlayer.isLooping = true
//                mediaPlayer.prepareAsync()
//                mediaPlayer.setOnPreparedListener(object: MediaPlayer.OnPreparedListener {
//                    override fun onPrepared(mp: MediaPlayer?) {
//                        //mediaPlayer.stop()
//                    }
//
//                })
//
//            }
//
//            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
//                return false
//            }
//
//            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
//                TODO("Not yet implemented")
//            }
//        }
//        val assets = context?.assets
//        val assetFileName = "timer_animation.gif"
//        //val source = ImageDecoder.createSource(assets!!, assetFileName)
//        val listener = ImageDecoder.OnHeaderDecodedListener { decoder,_,_ ->
//            decoder.setOnPartialImageListener { decodeException ->
//                true
//            }
//        }
//
//        GlobalScope.launch(Dispatchers.Default) {
//            // worker thread
//            val source = ImageDecoder.createSource(assets!!, assetFileName)
//            val drawable = ImageDecoder.decodeDrawable(source, listener)
//            GlobalScope.launch(Dispatchers.Main) {
//                // UI thread
//                binding.timerAnimation.setImageDrawable(drawable)
//                if (drawable is AnimatedImageDrawable) {
//                    drawable.start()
//                }
//            }
//        }
//        val drawable = ImageDecoder.decodeDrawable(source)
//        binding.timerAnimation.setImageDrawable(drawable)
//        if (drawable is AnimatedImageDrawable) {
//            drawable.start()
//        }
        //timerDrawable = binding.timerAnimation.drawable as GifDrawable
        timerDrawable = binding.timerAnimation

        binding.title.setOnClickListener {
            //Glide.with(this).clear(timerDrawable)
            drawable = timerDrawable.drawable as GifDrawable
            (drawable as Animatable).stop()
        }
//        Glide.with(this).asGif()
//            .load(R.drawable.timer_animation)
//            .into(timerDrawable)

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
            if(binding.timer.text == "00:01") {
                Glide.with(this).asGif()
                    .load(R.drawable.timer_animation)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(timerDrawable)



                    //isTimerStarted = true
            }
            binding.timer.text = DateUtils.formatElapsedTime(currentTime)})

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

}