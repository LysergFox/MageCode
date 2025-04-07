package com.kitsune.magecode.view.factory

import android.content.Context
import android.view.View
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.enums.QuestionType
import com.kitsune.magecode.model.lesson.Question
import com.kitsune.magecode.view.DragOrderView
import com.kitsune.magecode.view.FillInTheBlankView
import com.kitsune.magecode.view.MatchPairsView
import com.kitsune.magecode.view.MultipleChoiceView

object QuestionViewFactory {
    fun createView(
        question: Question,
        context: Context,
        controller: QuestionController
    ): View {
        return when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> MultipleChoiceView(context, question, controller)
            QuestionType.FILL_IN_THE_BLANK -> FillInTheBlankView(context, question, controller)
            QuestionType.MATCH_PAIRS-> MatchPairsView(context, question, controller)
            QuestionType.DRAG_ORDER -> DragOrderView(context, question, controller)
            else -> throw IllegalArgumentException("Unknown question type: ${question.type}")
        }
    }
}