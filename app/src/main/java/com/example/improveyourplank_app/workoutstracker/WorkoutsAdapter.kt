package com.example.improveyourplank_app.workoutstracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.improveyourplank_app.database.Workout
import com.example.improveyourplank_app.databinding.ListItemWorkoutBinding

class WorkoutsAdapter(val clickListener: WorkoutFeedBackClickListener) : ListAdapter<Workout, WorkoutsAdapter.ViewHolder>(WorkoutDiffCallback()) {

    class ViewHolder private constructor(val binding: ListItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Workout, clickListener : WorkoutFeedBackClickListener) {
            binding.workout = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemWorkoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}

//class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
//
//    override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
//        return oldItem.workoutId == newItem.workoutId
//    }
//
//
//    override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
//        return oldItem == newItem
//    }
//}
//class WorkoutFeedBackClickListener(val clickListener : (workoutId : Long) -> Unit) {
//    fun onClick(workout: Workout) = clickListener(workout.workoutId)
//}


