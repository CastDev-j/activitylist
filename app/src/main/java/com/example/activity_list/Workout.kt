package com.example.activity_list

import java.io.Serializable

data class Workout(
    val type: WorkoutType,
    val duration: Int, 
    val calories: Int,
    val distance: Double?,
    val date: String
) : Serializable

enum class WorkoutType(val displayName: String, val hasDistance: Boolean) {
    RUNNING("Carrera", true),
    CYCLING("Ciclismo", true),
    SWIMMING("Natación", true),
    YOGA("Yoga", false),
    WEIGHT_TRAINING("Entrenamiento con Pesas", false)
}
