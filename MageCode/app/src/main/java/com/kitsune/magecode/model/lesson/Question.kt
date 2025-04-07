package com.kitsune.magecode.model.lesson

import android.os.Parcelable
import com.kitsune.magecode.model.enums.QuestionType
import kotlinx.parcelize.Parcelize


@Parcelize
data class Question(
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val prompt: String = "",
    val sentence: String = "",
    val correctAnswer: List<String> = emptyList(),
    val options: List<String> = emptyList(),
    val pairs: List<KeyValuePair>? = null,
    val language: String = ""
) : Parcelable
