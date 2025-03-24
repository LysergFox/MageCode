package com.kitsune.magecode.model.lesson

import com.kitsune.magecode.model.enums.QuestionType

data class Question(
    val id: String,
    val type: QuestionType,
    val prompt: String,
    val sentence: String,
    val correctAnswer: List<String>,
    val options: List<String>,
    val language: String
)
