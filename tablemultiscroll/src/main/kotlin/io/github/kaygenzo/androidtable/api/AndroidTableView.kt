package io.github.kaygenzo.androidtable.api

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kaygenzo.androidtable.R
import io.github.kaygenzo.androidtable.databinding.ViewTableMultiScrollBinding
import io.github.kaygenzo.androidtable.internal.AndroidTableAdapter
import io.github.kaygenzo.androidtable.internal.DataRow
import io.github.kaygenzo.androidtable.internal.HeaderRow
import io.github.kaygenzo.androidtable.internal.views.CustomHorizontalScrollView

class AndroidTableView : ConstraintLayout {

    private val rowsHighlights = mutableMapOf<Int, Highlight>()
    private val columnHighlights = mutableMapOf<Int, Highlight>()

    private val defaultHeaderStyle = StyleConfiguration(
        cellTextTypeface = Typeface.DEFAULT_BOLD,
        cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
        cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color
    )
    private var leftHeaderStyle = defaultHeaderStyle
    private var topHeaderStyle = defaultHeaderStyle
    private var mainTableStyle = StyleConfiguration()

    private var tableConfiguration = TableConfiguration()

    private val tableData = mutableListOf<DataRow>()
    private val leftHeaderData: MutableList<DataRow> = mutableListOf()
    private var topHeaderData: HeaderRow? = null

    private var mMainAdapter = AndroidTableAdapter(
        tableData,
        null,
        tableConfiguration.cellWidth,
        tableConfiguration.cellHeight,
        mainTableStyle,
        rowsHighlights,
        columnHighlights
    )
    private var mLeftHeaderAdapter = AndroidTableAdapter(
        leftHeaderData,
        null,
        tableConfiguration.leftHeaderWidth,
        tableConfiguration.cellHeight,
        leftHeaderStyle
    )

    private val binding: ViewTableMultiScrollBinding =
        ViewTableMultiScrollBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        with(binding) {
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
        rows.forEach { columns -> tableData.add(DataRow(columns.toTableInfo())) }
        mMainAdapter.notifyDataSetChanged()
    }

    fun setTopHeaderData(data: List<CellConfiguration>?) {
        topHeaderData = if (data != null) {
            HeaderRow(data.toTableInfo())
        } else {
            null
        }

        refreshEmptyCell()
        refreshTopHeaderView()
        mMainAdapter.header = topHeaderData
        mMainAdapter.notifyDataSetChanged()

        with(binding) {
            if (topHeaderData != null) {
                topHeader.visibility = View.VISIBLE
                mainTableHorizontalScroll.mirror = topHeaderScroll
            } else {
                topHeader.visibility = View.GONE
                mainTableHorizontalScroll.mirror = null
            }
        }
    }

    fun setLeftHeaderData(data: List<CellConfiguration>) {
        leftHeaderData.clear()
        if (data.isNotEmpty()) {
            data.forEach {
                val map = mutableMapOf<Int, String>()
                map[0] = it.text
                leftHeaderData.add(DataRow(map))
            }
        }

        refreshEmptyCell()

        mLeftHeaderAdapter.notifyDataSetChanged()

        with(binding) {
            if (leftHeaderData.isNotEmpty()) {
                leftHeader.visibility = View.VISIBLE
                mainTableRecyclerRows.mirror = leftHeader
            } else {
                leftHeader.visibility = View.GONE
                mainTableRecyclerRows.mirror = null
            }
        }
    }

    fun setTopLeftCellCustomView(view: View) {
        binding.emptyHeaderCell.run {
            removeAllViews()
            addView(view)
            invalidate()
        }
    }

    fun cleanTopLeftCell() {
        binding.emptyHeaderCell.run {
            removeAllViews()
            invalidate()
        }
    }

    private fun initShadows() {
        with(binding) {
            initRecyclerShadow(mainTableRecyclerRows, shadowTopMain)
            initRecyclerShadow(mainTableRecyclerRows, shadowTopLeftHeader)
            initScrollviewShadow(
                mainTableHorizontalScroll,
                arrayOf(shadowLeftMain, shadowLeftTopHeader)
            )
        }
    }

    private fun initRecyclerShadow(recycler: RecyclerView, shadowView: View) {
        val linearLayoutManager = recycler.layoutManager as LinearLayoutManager
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            private var isFirst = false

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstCompleteItemPosition =
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstCompleteItemPosition == 0) {
                    val fadeOut = AlphaAnimation(1f, 0f)
                    fadeOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            shadowView.alpha = 0f
                            isFirst = true
                        }
                    })
                    fadeOut.duration = 100
                    shadowView.startAnimation(fadeOut)
                } else if (isFirst) {
                    isFirst = false
                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.setAnimationListener(object : Animation.AnimationListener {
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

    private fun initScrollviewShadow(
        scrollView: CustomHorizontalScrollView,
        shadowViews: Array<View>
    ) {
        scrollView.customScrollListener = object : CustomHorizontalScrollView.OnPositionCallback {

            override fun onBeginScrolling() {
                shadowViews.forEach {
                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.setAnimationListener(object : Animation.AnimationListener {
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
                shadowViews.forEach {
                    val fadeOut = AlphaAnimation(1f, 0f)
                    fadeOut.setAnimationListener(object : Animation.AnimationListener {
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

    private fun refreshTopHeaderView() {
        with(binding) {
            topHeader.apply {
                removeAllViews()
                topHeaderData?.tableInfo?.forEach {
                    addData(it.value, tableConfiguration.cellWidth, topHeaderStyle)
                }
                invalidate()
            }
        }
    }

    private fun refreshEmptyCell() {
        val height = when (topHeaderData) {
            null -> 0
            else -> resources.getDimensionPixelSize(tableConfiguration.topHeaderHeight)
        }

        val width = when (leftHeaderData.isEmpty()) {
            true -> 0
            false -> resources.getDimensionPixelSize(tableConfiguration.leftHeaderWidth)
        }

        binding.emptyHeaderCell.run {
            layoutParams = LayoutParams(width, height)
            invalidate()
        }
    }

    private fun List<CellConfiguration>.toTableInfo(): Map<Int, String> {
        val mapData = mutableMapOf<Int, String>()
        this.forEach { cell ->
            mapData[cell.id] = cell.text
        }
        return mapData
    }
}