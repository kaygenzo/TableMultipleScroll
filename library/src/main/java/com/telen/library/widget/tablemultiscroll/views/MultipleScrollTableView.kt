package com.telen.library.widget.tablemultiscroll.views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.telen.library.widget.tablemultiscroll.R
import com.telen.library.widget.tablemultiscroll.adapter.DataRow
import com.telen.library.widget.tablemultiscroll.adapter.HeaderRow
import com.telen.library.widget.tablemultiscroll.adapter.TableMultipleScrollAdapter
import kotlinx.android.synthetic.main.view_table_multi_scroll.view.*

class MultipleScrollTableView: ConstraintLayout {

    data class StyleConfiguration(
            @DimenRes val cellTextSize: Int,
            val cellTextTypeface: Typeface,
            @ColorRes val cellDefaultBackgroundColor: Int,
            @ColorRes val cellDefaultTextColor: Int)

    data class TableConfiguration(
            @DimenRes val topHeaderHeight: Int,
            @DimenRes val leftHeaderWidth: Int,
            @DimenRes val cellHeight: Int,
            @DimenRes val cellWidth: Int)

    data class CellConfiguration(val text: String, val id: Int = 0)

    private var mMainAdapter: TableMultipleScrollAdapter
    private var mLeftHeaderAdapter: TableMultipleScrollAdapter

    private val tableData = mutableListOf<DataRow>()
    private val leftHeaderData: MutableList<DataRow> = mutableListOf()
    private var topHeaderData: HeaderRow? = null

    private var mainTableStyle: StyleConfiguration
    private var leftHeaderStyle: StyleConfiguration
    private var topHeaderStyle: StyleConfiguration
    private var tableConfiguration: TableConfiguration

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_table_multi_scroll, this)

        mainTableStyle =
            StyleConfiguration(
                cellTextSize = R.dimen.default_table_multiple_scroll_text_size,
                cellTextTypeface = Typeface.DEFAULT,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_cell_background_color,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_cell_text_color
            )

        topHeaderStyle =
            StyleConfiguration(
                cellTextSize = R.dimen.default_table_multiple_scroll_text_size,
                cellTextTypeface = Typeface.DEFAULT_BOLD,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color
            )

        leftHeaderStyle =
            StyleConfiguration(
                cellTextSize = R.dimen.default_table_multiple_scroll_text_size,
                cellTextTypeface = Typeface.DEFAULT_BOLD,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color
            )

        tableConfiguration =
            TableConfiguration(
                cellHeight = R.dimen.default_table_multiple_scroll_cell_height,
                cellWidth = R.dimen.default_table_multiple_scroll_cell_width,
                leftHeaderWidth = R.dimen.default_table_multiple_scroll_left_header_width,
                topHeaderHeight = R.dimen.default_table_multiple_scroll_top_header_height
            )

        mMainAdapter = TableMultipleScrollAdapter(tableData, null, tableConfiguration.cellWidth, tableConfiguration.cellHeight, mainTableStyle)

        mLeftHeaderAdapter = TableMultipleScrollAdapter(leftHeaderData, null, tableConfiguration.leftHeaderWidth, tableConfiguration.cellHeight, leftHeaderStyle)

        mainTableRecyclerRows.run {
            adapter = mMainAdapter
            layoutManager = LinearLayoutManager(context)
        }

        leftHeader.run {
            adapter = mLeftHeaderAdapter
            layoutManager = LinearLayoutManager(context)
        }

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

    fun setMainData(rows: List<List<CellConfiguration>>) {
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

    private fun refreshTopHeaderView() {
        topHeader.run {
            topHeader.removeAllViews()
            topHeaderData?.tableInfo?.forEach {
                addData(it.value, "", null, tableConfiguration.cellWidth, tableConfiguration.topHeaderHeight,
                        topHeaderStyle.cellDefaultBackgroundColor, topHeaderStyle.cellDefaultTextColor,
                        topHeaderStyle.cellTextSize, topHeaderStyle.cellTextTypeface)
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
}