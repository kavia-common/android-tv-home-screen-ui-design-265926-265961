package com.example.android_tv_home_screen_frontend.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.example.android_tv_home_screen_frontend.ui.theme.ExecutiveGrayTheme

/**
 * Fixed top navigation bar with app title and non-functional icons.
 */
class TopBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val title = TextView(context)
    private val rightIcons = LinearLayout(context)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(ExecutiveGrayTheme.SafeMargin), dp(12), dp(ExecutiveGrayTheme.SafeMargin), dp(12))

        val bg = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(
                ExecutiveGrayTheme.Surface,
                Color.parseColor("#F3F4F6")
            )
        )
        background = bg
        elevation = dp(6).toFloat()

        // Logo placeholder circle
        val logo = View(context).apply {
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(ExecutiveGrayTheme.Primary)
            }
        }
        addView(logo, LayoutParams(dp(36), dp(36)))

        title.apply {
            setTextColor(ExecutiveGrayTheme.Text)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, ExecutiveGrayTheme.TitleM.toFloat())
            text = "Executive TV"
            setPadding(dp(12), 0, 0, 0)
        }
        addView(title, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))

        rightIcons.orientation = HORIZONTAL
        val icon1 = buildSquareIcon()
        val icon2 = buildSquareIcon()
        rightIcons.addView(icon1)
        rightIcons.addView(View(context), LayoutParams(dp(12), 1))
        rightIcons.addView(icon2)
        addView(rightIcons, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    private fun buildSquareIcon(): ImageView {
        return ImageView(context).apply {
            contentDescription = "icon"
            background = GradientDrawable().apply {
                cornerRadius = dp(8).toFloat()
                setColor(Color.parseColor("#E5E7EB"))
            }
            layoutParams = LayoutParams(dp(40), dp(40))
            isFocusable = true
            isFocusableInTouchMode = true
            setOnFocusChangeListener { v, hasFocus ->
                v.animate().scaleX(if (hasFocus) 1.05f else 1f).scaleY(if (hasFocus) 1.05f else 1f).setDuration(100).start()
            }
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
