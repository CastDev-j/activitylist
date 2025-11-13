package com.example.activity_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WorkoutAdapter(
    private val workouts: List<Workout>,
    private val onItemClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWorkoutType: TextView = itemView.findViewById(R.id.tvWorkoutType)
        val tvWorkoutDate: TextView = itemView.findViewById(R.id.tvWorkoutDate)
        val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
        val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        val distanceContainer: View = itemView.findViewById(R.id.distanceContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        holder.tvWorkoutType.text = workout.type.displayName
        holder.tvWorkoutDate.text = workout.date
        holder.tvDuration.text = "${workout.duration} min"
        holder.tvCalories.text = "${workout.calories}"
        
        if (workout.distance != null) {
            holder.distanceContainer.visibility = View.VISIBLE
            holder.tvDistance.text = "${workout.distance} km"
        } else {
            holder.distanceContainer.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick(workout)
        }
    }

    override fun getItemCount() = workouts.size
}
