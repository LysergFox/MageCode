package com.kitsune.magecode.model.lesson

data class Lesson(
    val id: String,
    val questions: List<Question>,
    val xpReward: Int
)
