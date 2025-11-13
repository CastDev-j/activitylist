package com.example.activity_list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var workouts: MutableList<Workout>
    private lateinit var adapter: WorkoutAdapter
    private lateinit var tvUserName: TextView
    private lateinit var tvTotalWorkouts: TextView
    private lateinit var tvTotalCalories: TextView
    private lateinit var tvAvgDuration: TextView
    private lateinit var btnLogout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (UserManager.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)

        tvUserName = findViewById(R.id.tvUserName)
        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts)
        tvTotalCalories = findViewById(R.id.tvTotalCalories)
        tvAvgDuration = findViewById(R.id.tvAvgDuration)
        btnLogout = findViewById(R.id.btnLogout)
        
        tvUserName.text = UserManager.currentUser?.profileName ?: "Usuario"
        
        workouts = UserManager.getCurrentWorkouts()

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        adapter = WorkoutAdapter(workouts) { workout ->
            showWorkoutDetails(workout)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Swipe-to-delete for workouts
        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val workout = workouts.getOrNull(position)
                if (workout == null) {
                    adapter.notifyItemChanged(position)
                    return
                }

                AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.delete_workout_title)
                    .setMessage(R.string.delete_workout_message)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        workouts.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        updateStats()
                    }
                    .setNegativeButton(R.string.cancel) { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(position)
                    }
                    .show()
            }
        }
        ItemTouchHelper(swipeToDelete).attachToRecyclerView(recyclerView)

        val btnAddWorkout: MaterialButton = findViewById(R.id.fabAddWorkout)
        btnAddWorkout.setOnClickListener {
            showAddWorkoutDialog()
        }
        
        btnLogout.setOnClickListener {
            UserManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        updateStats()
    }

    private fun showAddWorkoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_workout, null)
        val spinnerWorkoutType: Spinner = dialogView.findViewById(R.id.spinnerWorkoutType)
        val etDuration: EditText = dialogView.findViewById(R.id.etDuration)
        val etCalories: EditText = dialogView.findViewById(R.id.etCalories)
        val etDistance: EditText = dialogView.findViewById(R.id.etDistance)
        val etDate: EditText = dialogView.findViewById(R.id.etDate)
        val tvDistanceLabel: TextView = dialogView.findViewById(R.id.tvDistanceLabel)

        // Configurar spinner
        val workoutTypes = WorkoutType.values().map { it.displayName }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWorkoutType.adapter = spinnerAdapter

        // Listener para mostrar/ocultar distancia según el tipo
        spinnerWorkoutType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = WorkoutType.values()[position]
                if (selectedType.hasDistance) {
                    tvDistanceLabel.visibility = View.VISIBLE
                    etDistance.visibility = View.VISIBLE
                } else {
                    tvDistanceLabel.visibility = View.GONE
                    etDistance.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.add_workout)
            .setView(dialogView)
            .setPositiveButton(R.string.add) { _, _ ->
                val selectedType = WorkoutType.values()[spinnerWorkoutType.selectedItemPosition]
                val duration = etDuration.text.toString().toIntOrNull() ?: 0
                val calories = etCalories.text.toString().toIntOrNull() ?: 0
                val distance = if (selectedType.hasDistance) {
                    etDistance.text.toString().toDoubleOrNull()
                } else {
                    null
                }
                val date = etDate.text.toString().ifEmpty { "N/A" }

                if (duration > 0 && calories > 0 && date.isNotEmpty()) {
                    val workout = Workout(selectedType, duration, calories, distance, date)
                    workouts.add(workout)
                    adapter.notifyItemInserted(workouts.size - 1)
                    updateStats()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showWorkoutDetails(workout: Workout) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_workout_details, null)
        val tvDetailType: TextView = dialogView.findViewById(R.id.tvDetailType)
        val tvDetailDate: TextView = dialogView.findViewById(R.id.tvDetailDate)
        val tvDetailDuration: TextView = dialogView.findViewById(R.id.tvDetailDuration)
        val tvDetailCalories: TextView = dialogView.findViewById(R.id.tvDetailCalories)
        val tvDetailDistance: TextView = dialogView.findViewById(R.id.tvDetailDistance)
        val tvDetailDistanceLabel: TextView = dialogView.findViewById(R.id.tvDetailDistanceLabel)

        tvDetailType.text = workout.type.displayName
        tvDetailDate.text = workout.date
        tvDetailDuration.text = "${workout.duration} min"
        tvDetailCalories.text = "${workout.calories}"

        if (workout.distance != null) {
            tvDetailDistanceLabel.visibility = View.VISIBLE
            tvDetailDistance.visibility = View.VISIBLE
            tvDetailDistance.text = "${workout.distance} km"
        } else {
            tvDetailDistanceLabel.visibility = View.GONE
            tvDetailDistance.visibility = View.GONE
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.workout_details)
            .setView(dialogView)
            .setPositiveButton(R.string.share) { _, _ ->
                shareWorkout(workout)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private fun shareWorkout(workout: Workout) {
        val shareText = buildString {
            append("${workout.type.displayName}\n")
            append("Fecha: ${workout.date}\n")
            append("Duración: ${workout.duration} min\n")
            append("Calorías: ${workout.calories}\n")
            if (workout.distance != null) {
                append("Distancia: ${workout.distance} km\n")
            }
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_workout)))
    }

    private fun updateStats() {
        tvTotalWorkouts.text = workouts.size.toString()
        tvTotalCalories.text = workouts.sumOf { it.calories }.toString()
        val avgDuration = if (workouts.isNotEmpty()) {
            workouts.sumOf { it.duration } / workouts.size
        } else {
            0
        }
        tvAvgDuration.text = "$avgDuration min"
    }
}
