package com.kitsune.magecode.view.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.view.DragEvent
import android.view.Gravity
import android.widget.*
import androidx.core.graphics.toColorInt
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question

@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
class DragOrderView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    private var dragEnabled = true
    private val dropArea: LinearLayout
    private val optionsLayout: LinearLayout
    private val keyMap = mutableMapOf<String, String>()

    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)
        background = context.getDrawable(R.drawable.lesson_background)

        val promptText = TextView(context).apply {
            text = getStringByKey(question.prompt)
            applyTextOutline(this)
            textSize = 20f
            setPadding(16, 16, 16, 32)
            setTextColor(resources.getColor(R. color. white))
            typeface = resources.getFont(R.font.compaspro)
        }
        addView(promptText)

        dropArea = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            background = context.getDrawable(R.drawable.stone_button)
            setPadding(16, 16, 16, 16)
        }
        dropArea.setOnDragListener(createDropListener(dropArea))
        addView(dropArea)

        optionsLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(16, 32, 16, 16)
        }
        optionsLayout.setOnDragListener(createDropListener(optionsLayout))

        // Render the options
        question.options.forEach { key ->
            val localized = getStringByKey(key)
            keyMap[localized] = key
            val view = createDraggableText(localized)
            optionsLayout.addView(view)
        }
        addView(optionsLayout)

        val submitButton = Button(context).apply {
            text = context.getString(R.string.check)
            background = context.getDrawable(R.drawable.stone_button)
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(32, 16, 32, 16)
            setOnClickListener { btn ->
                val answerKeys = (0 until dropArea.childCount).mapNotNull {
                    val text = (dropArea.getChildAt(it) as? TextView)?.text?.toString()
                    keyMap[text]
                }

                val isCorrect = controller.checkAnswer(question, answerKeys)

                for (i in 0 until dropArea.childCount) {
                    val child = dropArea.getChildAt(i) as? TextView ?: continue
                    child.setBackgroundColor(if (isCorrect) resources.getColor(R.color.green_500) else resources.getColor(R.color.red_500))
                    child.isEnabled = false
                }

                for (i in 0 until optionsLayout.childCount) {
                    val child = optionsLayout.getChildAt(i) as? TextView ?: continue
                    child.isEnabled = false
                }

                dragEnabled = false
                btn.isEnabled = false
            }
        }
        addView(submitButton)
    }

    private fun getStringByKey(key: String): String {
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) context.getString(resId) else key
    }

    private fun createDraggableText(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            applyTextOutline(this)
            textSize = 16f
            setTextColor(resources.getColor(R.color.white))
            background = context.getDrawable(R.drawable.stone_button)
            typeface = resources.getFont(R.font.compaspro)
            setPadding(24, 16, 24, 16)

            val params = MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            params.setMargins(12, 12, 12, 12)
            layoutParams = params

            setOnLongClickListener {
                val clipData = ClipData.newPlainText("label", this.text)
                startDragAndDrop(clipData, DragShadowBuilder(this), this, 0)
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

    private fun applyTextOutline(textView: TextView) {
        textView.setShadowLayer(3f, 0f, 0f, resources.getColor(R.color.black))
    }
}

