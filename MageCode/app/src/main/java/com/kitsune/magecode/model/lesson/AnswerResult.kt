package com.kitsune.magecode.model.lesson

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class AnswerResult(
    val question: Question,
    val userAnswer: @RawValue Any,
    val correctAnswer: List<String>,
    val isCorrect: Boolean
) : Parcelable
