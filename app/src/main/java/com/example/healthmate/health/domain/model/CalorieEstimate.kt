package com.example.healthmate.health.domain.model

/**
 * Rough estimate: approximately 4 kcal per 100 steps.
 */
object CalorieEstimate {

    fun fromSteps(steps: Int): Int {
        return (steps.coerceAtLeast(0) * 0.04).toInt()
    }
}