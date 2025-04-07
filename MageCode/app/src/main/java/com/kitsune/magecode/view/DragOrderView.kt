package com.kitsune.magecode.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.view.DragEvent
import android.view.Gravity
import android.widget.*
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question
import androidx.core.graphics.toColorInt

@SuppressLint("ViewConstructor")
class DragOrderView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    private var dragEnabled = true
    private val dropArea: LinearLayout
    private val optionsLayout: LinearLayout

    init {
        orientation = VERTICAL
        setPadding(24, 24, 24, 24)

        val promptText = TextView(context).apply {
            text = question.prompt
            textSize = 18f
            setPadding(0, 0, 0, 16)
        }
        addView(promptText)

        dropArea = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            background = context.getDrawable(android.R.drawable.dialog_holo_light_frame)
            setPadding(16, 16, 16, 16)
        }
        dropArea.setOnDragListener(createDropListener(dropArea))
        addView(dropArea)

        optionsLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)
        }
        optionsLayout.setOnDragListener(createDropListener(optionsLayout))

        question.options.forEach { option ->
            val view = createDraggableText(option)
            optionsLayout.addView(view)
        }
        addView(optionsLayout)

        val submit = Button(context).apply {
            text = "Submit"
            setBackgroundColor("#6200EE".toColorInt())
            setTextColor(Color.WHITE)
            setOnClickListener { it ->
                val answer = (0 until dropArea.childCount).mapNotNull {
                    (dropArea.getChildAt(it) as? TextView)?.text?.toString()
                }

                val isCorrect = controller.checkAnswer(question, answer)

                for (i in 0 until dropArea.childCount) {
                    val child = dropArea.getChildAt(i) as? TextView ?: continue
                    child.setBackgroundColor(if (isCorrect) Color.GREEN else Color.RED)
                    child.isEnabled = false
                }

                for (i in 0 until optionsLayout.childCount) {
                    val child = optionsLayout.getChildAt(i) as? TextView ?: continue
                    child.isEnabled = false
                }
                dragEnabled = false

                it.isEnabled = false
            }
        }
        addView(submit)
    }

    private fun createDraggableText(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            textSize = 16f
            setPadding(24, 16, 24, 16)
            setBackgroundResource(android.R.drawable.btn_default)
            setTextColor(Color.BLACK)

            val params = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.setMargins(12, 12, 12, 12)
            layoutParams = params

            setOnLongClickListener {
                val clipData = ClipData.newPlainText("label", this.text)
                this.startDragAndDrop(clipData, DragShadowBuilder(this), this, 0)
                true
            }
        }
    }

    private fun createDropListener(targetLayout: LinearLayout): OnDragListener {
        return OnDragListener { _, event ->
            if (!dragEnabled) return@OnDragListener true

            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val draggedView = event.localState as? TextView ?: return@OnDragListener true
                    val parent = draggedView.parent as? LinearLayout
                    parent?.removeView(draggedView)
                    targetLayout.addView(draggedView)
                }
            }
            true
        }
    }
}
