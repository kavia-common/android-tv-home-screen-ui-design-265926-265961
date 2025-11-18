package com.example.android_tv_home_screen_frontend.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.example.android_tv_home_screen_frontend.data.MovieItem
import com.example.android_tv_home_screen_frontend.ui.theme.ExecutiveGrayTheme

/**
 * A horizontally-scrollable row of MovieCardView items optimized for TV D-pad navigation.
 * Emits callbacks on item focus for updating preview pane.
 */
class MovieRowView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : HorizontalScrollView(context, attrs) {

    interface Listener {
        fun onItemFocused(item: MovieItem, view: View, rowIndex: Int, itemIndex: Int)
        fun onItemClicked(item: MovieItem)
        fun requestFocusUp(fromRow: Int)
        fun requestFocusDown(fromRow: Int)
    }

    private val container = LinearLayout(context)
    private var items: List<MovieItem> = emptyList()
    private var rowIndex: Int = 0
    var listener: Listener? = null

    init {
        isHorizontalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        container.orientation = LinearLayout.HORIZONTAL
        container.clipChildren = false
        container.clipToPadding = false
        container.setPadding(dp(ExecutiveGrayTheme.SafeMargin), 0, dp(ExecutiveGrayTheme.SafeMargin), 0)
        addView(
            container,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )
    }

    fun bind(rowIndex: Int, items: List<MovieItem>) {
        this.rowIndex = rowIndex
        this.items = items
        container.removeAllViews()
        items.forEachIndexed { index, item ->
            val card = MovieCardView(context).apply {
                setPosterColor(item.colorHex)
                setTitleText(item.title)
                // focus listener to notify preview updates and auto scroll
                onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                    if (hasFocus) {
                        listener?.onItemFocused(item, v, rowIndex, index)
                        ensureViewCentered(v)
                    }
                }
                setOnClickListener {
                    listener?.onItemClicked(item)
                }
            }
            val lp = LinearLayout.LayoutParams(
                dp(ExecutiveGrayTheme.CardWidth),
                dp(ExecutiveGrayTheme.CardHeight)
            ).apply {
                if (index > 0) marginStart = dp(ExecutiveGrayTheme.CardSpacing)
            }
            container.addView(card, lp)
        }

        // Ensure first item is focusable entry point
        if (container.childCount > 0) {
            container.getChildAt(0).isFocusable = true
        }
    }

    fun focusFirst() {
        if (container.childCount > 0) {
            container.getChildAt(0).requestFocus()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    listener?.requestFocusUp(rowIndex)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    listener?.requestFocusDown(rowIndex)
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    // if at start, consume to avoid bouncing focus to toolbar
                    val focusedIdx = focusedChildIndex()
                    if (focusedIdx <= 0) {
                        smoothScrollTo(0, 0)
                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    val focusedIdx = focusedChildIndex()
                    if (focusedIdx >= container.childCount - 1) {
                        // at end, keep focus, allow no-op
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    private fun focusedChildIndex(): Int {
        val focused = findFocus() ?: return -1
        return container.indexOfChild(focused)
    }

    private fun ensureViewCentered(v: View) {
        val childLeft = v.left
        val childWidth = v.width
        val width = width
        val childCenter = childLeft + childWidth / 2
        val targetScroll = childCenter - width / 2
        smoothScrollTo(targetScroll, 0)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
