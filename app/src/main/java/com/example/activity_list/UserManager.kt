package com.example.activity_list

object UserManager {
    private val users = mutableListOf<User>()
    var currentUser: User? = null

    init {
        if (users.isEmpty()) {
            val defaultNames = listOf("Ana", "Luis", "Carlos", "Marta")
            defaultNames.forEach { name ->
                val user = User(name, sampleWorkoutsFor(name))
                users.add(user)
            }
        }
    }

    private fun sampleWorkoutsFor(name: String): MutableList<Workout> {
        val rnd = kotlin.random.Random(name.hashCode())
        val count = rnd.nextInt(1, 5)
        val list = mutableListOf<Workout>()
        repeat(count) { idx ->
            val type = WorkoutType.values()[rnd.nextInt(WorkoutType.values().size)]
            val duration = rnd.nextInt(20, 61) 
            val calories = rnd.nextInt(120, 601) 
            val distance = if (type.hasDistance) {
                val km = rnd.nextDouble(1.0, 20.0)
                kotlin.math.round(km * 10.0) / 10.0
            } else null
            val day = rnd.nextInt(1, 29) 
            val date = "2025-11-" + day.toString().padStart(2, '0')

            list.add(
                Workout(
                    type = type,
                    duration = duration,
                    calories = calories,
                    distance = distance,
                    date = date
                )
            )
        }
        return list
    }

    fun createProfile(profileName: String): Boolean {
        if (users.any { it.profileName == profileName }) {
            return false
        }
        val newUser = User(profileName)
        users.add(newUser)
        return true
    }

    fun selectProfile(profileName: String): Boolean {
        val user = users.find { it.profileName == profileName }
        if (user != null) {
            currentUser = user
            return true
        }
        return false
    }

    fun logout() {
        currentUser = null
    }

    fun getAllProfiles(): List<String> {
        return users.map { it.profileName }
    }

    fun getCurrentWorkouts(): MutableList<Workout> {
        return currentUser?.workouts ?: mutableListOf()
    }
}
