package com.telen.library.widget.tablemultiscroll.views

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.telen.library.widget.tablemultiscroll.R
import com.telen.library.widget.tablemultiscroll.adapter.DataRow
import com.telen.library.widget.tablemultiscroll.adapter.HeaderRow
import com.telen.library.widget.tablemultiscroll.adapter.OnTableClickListener
import com.telen.library.widget.tablemultiscroll.adapter.TableMultipleScrollAdapter
import kotlinx.android.synthetic.main.view_table_multi_scroll.view.*

data class StyleConfiguration(
    @DimenRes val cellTextSize: Int = R.dimen.default_table_multiple_scroll_text_size,
    val cellTextTypeface: Typeface = Typeface.DEFAULT,
    @ColorRes val cellDefaultBackgroundColor: Int = R.color.default_table_multiple_scroll_cell_background_color,
    @ColorRes val cellDefaultTextColor: Int = R.color.default_table_multiple_scroll_cell_text_color,
    val linesCount: Int = 1,
    val truncateStrategy: TextUtils.TruncateAt = TextUtils.TruncateAt.END)

data class TableConfiguration(
    @DimenRes val topHeaderHeight: Int = R.dimen.default_table_multiple_scroll_top_header_height,
    @DimenRes val leftHeaderWidth: Int = R.dimen.default_table_multiple_scroll_left_header_width,
    @DimenRes val cellHeight: Int = R.dimen.default_table_multiple_scroll_cell_height,
    @DimenRes val cellWidth: Int = R.dimen.default_table_multiple_scroll_cell_width)

data class CellConfiguration(val text: String, val id: Int = 0)

data class Highlight(val index: Int, val style: StyleConfiguration)

enum class HighlightConflictStrategy {
    PriorityRow,
    PriorityColumn
}

class MultipleScrollTableView: ConstraintLayout {

    private var mMainAdapter: TableMultipleScrollAdapter
    private var mLeftHeaderAdapter: TableMultipleScrollAdapter

    private val tableData = mutableListOf<DataRow>()
    private val leftHeaderData: MutableList<DataRow> = mutableListOf()
    private var topHeaderData: HeaderRow? = null

    private var mainTableStyle: StyleConfiguration
    private var leftHeaderStyle: StyleConfiguration
    private var topHeaderStyle: StyleConfiguration
    private var tableConfiguration: TableConfiguration

    private val rowsHighlights = mutableMapOf<Int, Highlight>()
    private val columnHighlights = mutableMapOf<Int, Highlight>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_table_multi_scroll, this)

        mainTableStyle = StyleConfiguration()

        topHeaderStyle =
            StyleConfiguration(
                cellTextTypeface = Typeface.DEFAULT_BOLD,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color
            )

        leftHeaderStyle =
            StyleConfiguration(
                cellTextTypeface = Typeface.DEFAULT_BOLD,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color
            )

        tableConfiguration = TableConfiguration()

        mMainAdapter = TableMultipleScrollAdapter(tableData, null, tableConfiguration.cellWidth, tableConfiguration.cellHeight,
            mainTableStyle, rowsHighlights, columnHighlights)

        mLeftHeaderAdapter = TableMultipleScrollAdapter(leftHeaderData, null, tableConfiguration.leftHeaderWidth, tableConfiguration.cellHeight,
            leftHeaderStyle)

        mainTableRecyclerRows.run {
            adapter = mMainAdapter
            layoutManager = LinearLayoutManager(context)
        }

        leftHeader.run {
            adapter = mLeftHeaderAdapter
            layoutManager = LinearLayoutManager(context)
        }

        initShadows()

        topHeaderScroll.mirror = mainTableHorizontalScroll
        leftHeader.mirror = mainTableRecyclerRows
    }

    fun setTableStyle(config: StyleConfiguration) {
        this.mainTableStyle = config
        this.mMainAdapter.style = config
    }

    fun setLeftHeaderStyle(config: StyleConfiguration) {
        this.leftHeaderStyle = config
        this.mLeftHeaderAdapter.style = config
    }

    fun setTopHeaderStyle(config: StyleConfiguration) {
        this.topHeaderStyle = config
    }

    fun setTableConfiguration(config: TableConfiguration) {
        this.tableConfiguration = config
        mMainAdapter.cellHeight = config.cellHeight
        mMainAdapter.cellWidth = config.cellWidth

        mLeftHeaderAdapter.cellHeight = config.cellHeight
        mLeftHeaderAdapter.cellWidth = config.leftHeaderWidth

        refreshEmptyCell()
    }

    fun setRowHighlights(highlights: List<Highlight>) {
        this.rowsHighlights.clear()
        highlights.forEach {
            this.rowsHighlights[it.index] = it
        }

        mMainAdapter.notifyDataSetChanged()
    }

    fun setColumnHighlights(highlights: List<Highlight>) {
        this.columnHighlights.clear()
        highlights.forEach {
            this.columnHighlights[it.index] = it
        }

        mMainAdapter.notifyDataSetChanged()
    }

    fun setHighlightsConflictStrategy(strategy: HighlightConflictStrategy) {
        mMainAdapter.highlightsConflictStrategy = strategy
        mMainAdapter.notifyDataSetChanged()
    }

    fun setOnTableClickListener(listener: OnTableClickListener) {
        mMainAdapter.tableClickListener = listener
    }

    fun setMainData(rows: List<List<CellConfiguration>>) {
        tableData.clear()
        rows.forEach { columns ->
            val mapData = mutableMapOf<Int, String>()
            tableData.add(DataRow(mapData))
            columns.forEach { cell ->
                mapData[cell.id] = cell.text
            }
        }

        mMainAdapter.notifyDataSetChanged()
    }

    fun setTopHeaderData(data: List<CellConfiguration>?) {
        if(data != null) {
            val map = mutableMapOf<Int, String>()
            data.forEach {
                map[it.id] = it.text
            }
            topHeaderData = HeaderRow(map)
        } else {
            topHeaderData = null
        }

        refreshEmptyCell()

        refreshTopHeaderView()
        mMainAdapter.headers = topHeaderData
        mMainAdapter.notifyDataSetChanged()

        if(topHeaderData != null) {
            topHeader.visibility = View.VISIBLE
            mainTableHorizontalScroll.mirror = topHeaderScroll
        }
        else {
            topHeader.visibility = View.GONE
            mainTableHorizontalScroll.mirror = null
        }
    }

    fun setLeftHeaderData(data: List<CellConfiguration>) {
        leftHeaderData.clear()
        if(data.isNotEmpty()) {
            data.forEach {
                val map = mutableMapOf<Int, String>()
                map[0] = it.text
                leftHeaderData.add(DataRow(map))
            }
        }

        refreshEmptyCell()

        mLeftHeaderAdapter.notifyDataSetChanged()

        if(leftHeaderData.isNotEmpty()) {
            leftHeader.visibility = View.VISIBLE
            mainTableRecyclerRows.mirror = leftHeader
        }
        else {
            leftHeader.visibility = View.GONE
            mainTableRecyclerRows.mirror = null
        }
    }

    fun setTopLeftCellCustomView(view: View) {
        emptyHeaderCell.run {
            removeAllViews()
            addView(view)
            invalidate()
        }
    }

    fun cleanTopLeftCell() {
        emptyHeaderCell.run {
            removeAllViews()
            invalidate()
        }
    }

    private fun refreshTopHeaderView() {
        topHeader.run {
            topHeader.removeAllViews()
            topHeaderData?.tableInfo?.forEach {
                addData(it.value, tableConfiguration.cellWidth, tableConfiguration.topHeaderHeight, topHeaderStyle)
            }
            invalidate()
        }
    }

    private fun refreshEmptyCell() {

        val height = when(topHeaderData) {
            null -> 0
            else -> resources.getDimensionPixelSize(tableConfiguration.topHeaderHeight)
        }

        val width = when(leftHeaderData.isEmpty()) {
            true -> 0
            false -> resources.getDimensionPixelSize(tableConfiguration.leftHeaderWidth)
        }

        emptyHeaderCell.run {
            layoutParams = LayoutParams(width, height)
            invalidate()
        }
    }

    private fun initShadows() {
        initRecyclerShadow(mainTableRecyclerRows, shadowTopMain)
        initRecyclerShadow(mainTableRecyclerRows, shadowTopLeftHeader)
        initScrollviewShadow(mainTableHorizontalScroll, arrayOf(shadowLeftMain, shadowLeftTopHeader))
    }

    private fun initScrollviewShadow(scrollView: CustomHorizontalScrollView, shadowViews: Array<View>) {
        scrollView.customScrollListener = object: CustomHorizontalScrollView.OnPositionCallback {

            override fun onBeginScrolling() {
                Log.d("DEBUG","onBeginScrolling")
                shadowViews.forEach {
                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            it.alpha = 1f
                        }
                    })
                    fadeIn.duration = 100
                    it.startAnimation(fadeIn)
                }
            }

            override fun onBackToStart() {
                Log.d("DEBUG","onBackToStart")
                shadowViews.forEach {
                    val fadeOut = AlphaAnimation(1f, 0f)
                    fadeOut.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            it.alpha = 0f
                        }
                    })
                    fadeOut.duration = 100
                    it.startAnimation(fadeOut)
                }
            }
        }
    }

    private fun initRecyclerShadow(recycler: RecyclerView, shadowView: View) {
        val linearLayoutManager = recycler.layoutManager as LinearLayoutManager
        recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            private var isFirst = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstCompleteItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if(firstCompleteItemPosition == 0) {
                    val fadeOut = AlphaAnimation(1f, 0f)
                    fadeOut.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            shadowView.alpha = 0f
                            isFirst = true
                        }
                    })
                    fadeOut.duration = 100
                    shadowView.startAnimation(fadeOut)
                }
                else if(isFirst) {
                    isFirst = false
                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            shadowView.alpha = 1f
                        }
                    })
                    fadeIn.duration = 100
                    shadowView.startAnimation(fadeIn)
                }
            }
        })
    }
}