package com.example.healthmate.health.domain.use_case

import com.example.healthmate.health.domain.model.CalorieEstimate

class CalculateCaloriesBurned {

    operator fun invoke(steps: Int): Int {
        return CalorieEstimate.fromSteps(steps)
    }
}