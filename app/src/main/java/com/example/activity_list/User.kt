package com.example.activity_list

data class User(
    val profileName: String,
    val workouts: MutableList<Workout> = mutableListOf()
)
