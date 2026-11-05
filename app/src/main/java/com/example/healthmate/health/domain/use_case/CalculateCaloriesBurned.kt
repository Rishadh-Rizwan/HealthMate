package com.example.healthmate.health.domain.use_case

class CalculateCaloriesBurned {
    operator fun invoke(steps: Int): Int {

        return (steps * 0.04).toInt()
    }
}