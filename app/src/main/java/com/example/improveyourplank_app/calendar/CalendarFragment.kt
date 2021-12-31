package com.example.improveyourplank_app.calendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.improveyourplank_app.MainActivity
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.calendar.adapters.CardStackAdapter
import com.example.improveyourplank_app.databinding.FragmentCalendarBinding
import com.myapp.annoteapp.extensions.CalendarGridView
import com.myapp.annoteapp.models.SharedViewModel
import com.myapp.annoteapp.utils.CalendarProperties
import com.yuyakaido.android.cardstackview.*
import java.util.*

class CalendarFragment : Fragment(), CardStackListener {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private val adapter by lazy {
        CardStackAdapter(
            requireContext(),
            CalendarProperties(requireContext()),
            this.activity as MainActivity
        )
    }
    var isDraggedRight = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        manager = CardStackLayoutManager(context, this)
        val binding: FragmentCalendarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        cardStackView = binding.cardStackView
        manager = CardStackLayoutManager(context, this).apply {
            setStackFrom(StackFrom.Top)
            setStackFrom(StackFrom.Top)
            setVisibleCount(3)
            setTranslationInterval(30.0f)
            setScaleInterval(0.95f)
            setSwipeThreshold(0.1f)
            setMaxDegree(20.0f)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
            setOverlayInterpolator(LinearInterpolator())
            topPosition = CalendarProperties.FIRST_VISIBLE_PAGE + sharedViewModel.currentPosition.value!!
        }
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
        cardStackView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState== RecyclerView.SCROLL_STATE_IDLE) {
                    if (!manager.cardStackSetting.canScrollVertical && manager.topPosition != 0) {
                        val setting = RewindAnimationSetting.Builder()
                            .setDirection(Direction.Bottom)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(LinearInterpolator())
                            .build()
                        manager.setRewindAnimationSetting(setting)
                        cardStackView.rewind()
                        manager.cardStackSetting.canScrollVertical = true
                        manager.cardStackSetting.canScrollHorizontal = true
                    }
                }
            }

        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity!!.finish()
                }
            })

        return binding.root
    }


    @SuppressLint("ResourceAsColor")
    override fun onCardDragging(direction: Direction, ratio: Float) {
        if (direction == Direction.Top) {
            manager.cardStackSetting.canScrollVertical = false
            manager.cardStackSetting.canScrollHorizontal = false
        }

        manager.findViewByPosition(manager.topPosition + 1)?.alpha = 0.5f + ratio/2+0.1f
        manager.findViewByPosition(manager.topPosition + 2)?.alpha = 0.1f + ratio/2
        manager.findViewByPosition(manager.topPosition + 2)?.findViewById<CalendarGridView>(R.id.calendarGridView)!!.alpha = 0.0f
        manager.findViewByPosition(manager.topPosition + 1)?.findViewById<CalendarGridView>(R.id.calendarGridView)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition + 2)?.findViewById<TextView>(R.id.month)!!.alpha = 0.0f
        manager.findViewByPosition(manager.topPosition + 2)?.findViewById<TextView>(R.id.year)!!.alpha = 0.0f
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")

    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
        manager.findViewByPosition(manager.topPosition + 1)?.alpha = 0.5f
        manager.findViewByPosition(manager.topPosition + 2)?.alpha = 0.1f

    }

    @SuppressLint("ResourceAsColor")
    override fun onCardAppeared(view: View, position: Int) {
        val cardView = view as CardView
        manager.findViewByPosition(manager.topPosition + 1)?.alpha = 0.5f
        manager.findViewByPosition(manager.topPosition+1)?.findViewById<CalendarGridView>(R.id.calendarGridView)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition+1)?.findViewById<TextView>(R.id.month)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition+1)?.findViewById<TextView>(R.id.year)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition)?.findViewById<CalendarGridView>(R.id.calendarGridView)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition)?.findViewById<TextView>(R.id.month)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition)?.findViewById<TextView>(R.id.year)!!.alpha = 1.0f
        manager.findViewByPosition(manager.topPosition + 2)?.alpha = 0.1f
        cardView.alpha = 1f
        isDraggedRight = false

    }

    override fun onCardDisappeared(view: View, position: Int) {
    }
}