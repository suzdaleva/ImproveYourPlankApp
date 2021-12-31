package com.example.improveyourplank_app.workoutstracker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.example.improveyourplank_app.R
import com.example.improveyourplank_app.database.Workout
import com.example.improveyourplank_app.database.WorkoutDatabaseDao
import com.example.improveyourplank_app.databinding.ListItemWorkoutBinding
import kotlinx.coroutines.*
import android.view.animation.BounceInterpolator

import android.view.animation.TranslateAnimation


class WorkoutsSwipeAdapter(
    private val context: Context?,
    val dataSource: WorkoutDatabaseDao,
    private val clickListener: WorkoutFeedBackClickListener
) :
    RecyclerSwipeAdapter<WorkoutsSwipeAdapter.ViewHolder?>() {

    lateinit var workoutList: ArrayList<Workout>
    val database: WorkoutDatabaseDao = dataSource
    private var lastPosition = -1


    class ViewHolder private constructor(val binding: ListItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var swipeLayout: SwipeLayout
        var delete: ImageView
        var edit: ImageView
        var blackView: TextView

        fun bind(item: Workout, clickListener: WorkoutFeedBackClickListener) {
            binding.workout = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        init {
            swipeLayout = itemView.findViewById<View>(R.id.swipe) as SwipeLayout
            delete = itemView.findViewById<View>(R.id.Delete) as ImageView
            edit = itemView.findViewById<View>(R.id.Edit) as ImageView
            blackView = itemView.findViewById(R.id.blackView) as TextView
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWorkoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            workoutList = database.getAllWorkouts() as ArrayList<Workout>
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(holder.itemView.context, android.R.anim.slide_in_left)
            animation.duration = 400
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }
        holder.bind(workoutList.get(position), clickListener)
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown)



        holder.swipeLayout.addDrag(
            SwipeLayout.DragEdge.Right,
            holder.swipeLayout.findViewById(R.id.bottom_wraper)
        )
        holder.swipeLayout.addSwipeListener(object : SwipeLayout.SwipeListener {

            override fun onStartOpen(layout: SwipeLayout?) {}

            override fun onOpen(layout: SwipeLayout?) {}

            override fun onStartClose(layout: SwipeLayout?) {}

            override fun onClose(layout: SwipeLayout?) {}

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                if (leftOffset > -50) holder.blackView.visibility = View.INVISIBLE
                else if (leftOffset < -520) holder.blackView.visibility = View.VISIBLE
            }

            override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {}
        })


        holder.edit.setOnClickListener(View.OnClickListener { view ->
        })
        holder.delete.setOnClickListener(View.OnClickListener { v ->
            mItemManger.removeShownLayouts(holder.swipeLayout)
            CoroutineScope(Dispatchers.IO).launch {
                deleteWorkout(workoutList.get(position))
                delay(1000)
                holder.blackView.visibility = View.INVISIBLE
            }
            Toast.makeText(
                context,
                "Deleted ${workoutList.get(position).workoutId}",
                Toast.LENGTH_SHORT
            ).show()
            workoutList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, workoutList.size)
            mItemManger.closeAllItems()
        })
        mItemManger.bindView(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return workoutList.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    suspend fun deleteWorkout(item: Workout) {
        dataSource.deleteWorkout(item)
    }


}

class WorkoutFeedBackClickListener(val clickListener: (workoutId: Long) -> Unit) {
    fun onClick(workout: Workout) = clickListener(workout.workoutId)
}

class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {

    override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem.workoutId == newItem.workoutId
    }


    override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem == newItem
    }
}
