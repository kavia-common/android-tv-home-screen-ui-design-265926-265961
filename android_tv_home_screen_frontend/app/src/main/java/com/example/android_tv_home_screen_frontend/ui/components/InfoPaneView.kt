package com.example.android_tv_home_screen_frontend.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.android_tv_home_screen_frontend.data.MovieItem
import com.example.android_tv_home_screen_frontend.ui.theme.ExecutiveGrayTheme

/**
 * Bottom preview/info pane showing metadata and actions for the focused movie.
 */
class InfoPaneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val title = TextView(context)
    private val meta = TextView(context)
    private val synopsis = TextView(context)
    private val actions = LinearLayout(context)
    val playButton = Button(context)
    val addButton = Button(context)

    init {
        orientation = VERTICAL
        val bg = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(
                Color.parseColor("#FFFFFFFF"),
                Color.parseColor("#F5F6F8")
            )
        ).apply {
            cornerRadius = dp(16).toFloat()
        }
        background = bg
        elevation = dp(4).toFloat()
        setPadding(dp(20))

        title.apply {
            setTextColor(ExecutiveGrayTheme.Text)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, ExecutiveGrayTheme.TitleL.toFloat())
            setLineSpacing(0f, 1.0f)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        }
        meta.apply {
            setTextColor(ExecutiveGrayTheme.Primary)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, ExecutiveGrayTheme.BodyM.toFloat())
            setPadding(0, dp(6), 0, 0)
        }
        synopsis.apply {
            setTextColor(ExecutiveGrayTheme.Text)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, ExecutiveGrayTheme.BodyL.toFloat())
            setPadding(0, dp(12), 0, 0)
            maxLines = 3
            ellipsize = TextUtils.TruncateAt.END
        }

        actions.orientation = HORIZONTAL
        actions.gravity = Gravity.START
        actions.setPadding(0, dp(16), 0, 0)

        stylePrimary(playButton, "Play", ExecutiveGrayTheme.Success)
        styleSecondary(addButton, "Add to List")

        actions.addView(playButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        actions.addView(addButton, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            leftMargin = dp(16)
        })

        addView(title, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(meta, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(synopsis, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(actions, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        // Key handling for Left/Right between buttons
        setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        if (addButton.isFocused) {
                            playButton.requestFocus()
                            return@setOnKeyListener true
                        }
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (playButton.isFocused) {
                            addButton.requestFocus()
                            return@setOnKeyListener true
                        }
                    }
                }
            }
            false
        }
    }

    fun update(item: MovieItem) {
        title.text = item.title
        val duration = "${item.durationMinutes}m"
        val genres = item.genres.joinToString(" • ")
        meta.text = "${item.year} • ${item.rating} • $duration • $genres"
        synopsis.text = item.synopsis
        contentDescription = "${item.title}. $genres. ${item.synopsis.take(80)}"
    }

    private fun stylePrimary(button: Button, text: String, color: Int) {
        button.text = text
        button.isFocusable = true
        button.isFocusableInTouchMode = true
        val bg = GradientDrawable().apply {
            cornerRadius = dp(12).toFloat()
            setColor(color)
        }
        button.background = bg
        button.setTextColor(Color.WHITE)
        button.setPadding(dp(24), dp(12), dp(24), dp(12))
        button.setOnFocusChangeListener { v, hasFocus ->
            v.animate().scaleX(if (hasFocus) 1.05f else 1f).scaleY(if (hasFocus) 1.05f else 1f).setDuration(100).start()
        }
    }

    private fun styleSecondary(button: Button, text: String) {
        button.text = text
        button.isFocusable = true
        button.isFocusableInTouchMode = true
        val bg = GradientDrawable().apply {
            cornerRadius = dp(12).toFloat()
            setColor(Color.TRANSPARENT)
            setStroke(dp(2), ExecutiveGrayTheme.Primary)
        }
        button.background = bg
        button.setTextColor(ExecutiveGrayTheme.Primary)
        button.setPadding(dp(24), dp(12), dp(24), dp(12))
        button.setOnFocusChangeListener { v, hasFocus ->
            v.animate().scaleX(if (hasFocus) 1.05f else 1f).scaleY(if (hasFocus) 1.05f else 1f).setDuration(100).start()
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
