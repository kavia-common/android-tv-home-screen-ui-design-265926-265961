package com.example.android_tv_home_screen_frontend

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.android_tv_home_screen_frontend.data.MovieItem
import com.example.android_tv_home_screen_frontend.data.MovieRow
import com.example.android_tv_home_screen_frontend.data.buildMockRows
import com.example.android_tv_home_screen_frontend.ui.components.InfoPaneView
import com.example.android_tv_home_screen_frontend.ui.components.MovieRowView
import com.example.android_tv_home_screen_frontend.ui.components.TopBarView

/**
 * PUBLIC_INTERFACE
 * Main Activity for Android TV Home screen.
 * Builds a single-page experience with:
 * - Top bar
 * - Multiple horizontal carousels (rows) of movie cards
 * - Bottom preview/info pane that updates on focus changes
 *
 * D-pad navigation:
 * - Left/Right navigates within a row
 * - Up/Down moves between rows and between rows and info pane buttons
 * - OK/Enter triggers button/card pressed animations (no real navigation)
 * - Back returns focus from info pane to the last focused row item; exiting the app on double back.
 */
class MainActivity : FragmentActivity(), MovieRowView.Listener {

    private lateinit var topBar: TopBarView
    private lateinit var rowsScroll: ScrollView
    private lateinit var rowsContainer: LinearLayout
    private lateinit var infoPane: InfoPaneView

    private lateinit var rows: List<MovieRow>
    private val rowViews = mutableListOf<MovieRowView>()
    private var lastFocusedRowIndex: Int = 0
    private var lastFocusedItemIndexInRow: Int = 0
    private var backPressedFromInfoPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rows = buildMockRows()

        topBar = findViewById(R.id.topBar)
        rowsScroll = findViewById(R.id.rowsScroll)
        rowsContainer = findViewById(R.id.rowsContainer)
        infoPane = findViewById(R.id.infoPane)

        buildRows()

        // Initialize info pane with first item's details
        if (rows.isNotEmpty() && rows.first().items.isNotEmpty()) {
            infoPane.update(rows.first().items.first())
        }

        // Focus first row's first item on launch for TV experience
        if (rowViews.isNotEmpty()) {
            rowViews.first().post { rowViews.first().focusFirst() }
        }

        // Buttons pressed animations (non-functional actions)
        infoPane.playButton.setOnClickListener {
            it.animate().scaleX(1.02f).scaleY(1.02f).setDuration(80).withEndAction {
                it.animate().scaleX(1.05f).scaleY(1.05f).setDuration(90).start()
            }.start()
        }
        infoPane.addButton.setOnClickListener {
            it.animate().alpha(0.8f).setDuration(60).withEndAction {
                it.animate().alpha(1f).setDuration(90).start()
            }.start()
        }
    }

    private fun buildRows() {
        rowsContainer.removeAllViews()
        rowViews.clear()
        rows.forEachIndexed { index, row ->
            // Row title
            val title = TextView(this).apply {
                text = row.title
                textSize = 26f
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.eg_text))
                setPadding(dp(24), dp(if (index == 0) 8 else 16), dp(24), dp(8))
            }
            rowsContainer.addView(title)

            // Horizontal row view
            val rowView = MovieRowView(this).apply {
                listener = this@MainActivity
                bind(index, row.items)
                isFocusable = true
                isFocusableInTouchMode = true
            }
            rowsContainer.addView(
                rowView,
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(340)).apply {
                    bottomMargin = dp(24)
                }
            )
            rowViews.add(rowView)
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    // MovieRowView.Listener implementations

    override fun onItemFocused(item: MovieItem, view: View, rowIndex: Int, itemIndex: Int) {
        lastFocusedRowIndex = rowIndex
        lastFocusedItemIndexInRow = itemIndex
        infoPane.update(item)
        ensureRowVisible(rowIndex)
        backPressedFromInfoPane = false
    }

    override fun onItemClicked(item: MovieItem) {
        // No navigation; optional visual feedback handled by card itself
    }

    override fun requestFocusUp(fromRow: Int) {
        if (fromRow <= 0) {
            // at top row, move focus to top bar
            topBar.requestFocus()
        } else {
            val target = rowViews.getOrNull(fromRow - 1)
            target?.focusFirst()
            ensureRowVisible(fromRow - 1)
        }
    }

    override fun requestFocusDown(fromRow: Int) {
        if (fromRow >= rowViews.lastIndex) {
            // move to info pane buttons
            infoPane.playButton.requestFocus()
            backPressedFromInfoPane = true
        } else {
            val target = rowViews.getOrNull(fromRow + 1)
            target?.focusFirst()
            ensureRowVisible(fromRow + 1)
        }
    }

    private fun ensureRowVisible(index: Int) {
        val rowView = rowViews.getOrNull(index) ?: return
        rowsScroll.post {
            val top = rowView.top
            val height = rowView.height
            val scrollY = rowsScroll.scrollY
            val containerHeight = rowsScroll.height
            val bottom = top + height
            val target = when {
                top < scrollY -> top
                bottom > scrollY + containerHeight -> bottom - containerHeight
                else -> scrollY
            }
            rowsScroll.smoothScrollTo(0, target)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                // Return focus from info pane to last focused card if needed
                if (infoPane.playButton.isFocused || infoPane.addButton.isFocused || backPressedFromInfoPane) {
                    val row = rowViews.getOrNull(lastFocusedRowIndex)
                    if (row != null && row.isShown) {
                        row.focusFirst() // fallback
                        row.post {
                            // try to focus the last known item
                            val container = row.getChildAt(0) as? LinearLayout
                            val v = container?.getChildAt(lastFocusedItemIndexInRow)
                            v?.requestFocus()
                        }
                    }
                    backPressedFromInfoPane = false
                    return true
                }
                // default back behavior exits app
                return super.onKeyDown(keyCode, event)
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                // If top bar focused, move to first row
                if (currentFocus === topBar && rowViews.isNotEmpty()) {
                    rowViews.first().focusFirst()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
