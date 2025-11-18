package com.example.android_tv_home_screen_frontend.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import com.example.android_tv_home_screen_frontend.ui.theme.ExecutiveGrayTheme

/**
 * A TV-optimized focusable card view used to represent a movie poster.
 * Shows a colored placeholder and title. When focused, scales up slightly and
 * displays a focus ring with glow.
 */
class MovieCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val poster: ImageView = ImageView(context)
    private val title: TextView = TextView(context)
    private val focusRing: View = View(context)

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        descendantFocusability = FOCUS_BEFORE_DESCENDANTS
        clipToPadding = false
        clipChildren = false

        val cardW = dp(ExecutiveGrayTheme.CardWidth)
        val cardH = dp(ExecutiveGrayTheme.CardHeight)
        layoutParams = LayoutParams(cardW, cardH)

        // Poster placeholder with rounded corners and subtle shadow
        val bg = GradientDrawable().apply {
            cornerRadius = dp(ExecutiveGrayTheme.CardRadius).toFloat()
            setColor(Color.parseColor("#D1D5DB"))
        }
        poster.apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = bg
            elevation = dp(2).toFloat()
        }

        // Title overlay
        title.apply {
            setTextColor(ExecutiveGrayTheme.Text)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, ExecutiveGrayTheme.BodyM.toFloat())
            setShadowLayer(6f, 0f, 0f, Color.parseColor("#80000000"))
            setPadding(dp(8), dp(6), dp(8), dp(6))
            maxLines = 1
            isSelected = true
        }

        // Focus ring overlay
        val ringDrawable = GradientDrawable().apply {
            cornerRadius = dp(ExecutiveGrayTheme.CardRadius + 4).toFloat()
            setStroke(dp(3), ExecutiveGrayTheme.FocusRing)
            setColor(Color.TRANSPARENT)
        }
        focusRing.apply {
            background = ringDrawable
            visibility = View.INVISIBLE
            elevation = dp(6).toFloat()
        }

        addView(poster, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(focusRing, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            // expand slightly to show ring outside
            val m = dp(4)
            setMargins(-m, -m, -m, -m)
        })
        addView(title, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM or Gravity.START
        })

        // Elevation shadow
        ViewCompat.setElevation(this, dp(4).toFloat())

        // Focus change animations
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                animate().scaleX(1.07f).scaleY(1.07f)
                    .setDuration(120)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
                focusRing.visibility = View.VISIBLE
                focusRing.alpha = 0f
                focusRing.animate().alpha(1f).setDuration(120).start()
            } else {
                animate().scaleX(1f).scaleY(1f)
                    .setDuration(120)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
                focusRing.animate().alpha(0f).setDuration(100).withEndAction {
                    focusRing.visibility = View.INVISIBLE
                }.start()
            }
        }

        // Pressed feedback
        setOnClickListener {
            // simple subtle press animation
            animate().scaleX(1.03f).scaleY(1.03f).setDuration(70).withEndAction {
                animate().scaleX(1.07f).scaleY(1.07f).setDuration(90).start()
            }.start()
        }
    }

    fun setPosterColor(hex: String) {
        val bg = (poster.background as? GradientDrawable) ?: GradientDrawable()
        bg.cornerRadius = dp(ExecutiveGrayTheme.CardRadius).toFloat()
        bg.setColor(Color.parseColor(hex))
        poster.background = bg
    }

    fun setTitleText(text: String) {
        title.text = text
        contentDescription = text
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
