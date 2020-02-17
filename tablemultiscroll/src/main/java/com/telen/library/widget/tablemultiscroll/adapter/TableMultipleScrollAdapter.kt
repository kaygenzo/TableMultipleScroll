package com.telen.library.widget.tablemultiscroll.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.telen.library.widget.tablemultiscroll.views.Highlight
import com.telen.library.widget.tablemultiscroll.views.HighlightConflictStrategy
import com.telen.library.widget.tablemultiscroll.views.StyleConfiguration
import com.telen.library.widget.tablemultiscroll.views.TableRowView

data class DataRow(val tableInfo: Map<Int, String>)
data class HeaderRow(val tableInfo: Map<Int, String>)

infix fun Any?.ifNull(block: () -> Unit) {
    if (this == null) block()
}

interface OnTableClickListener {
    fun onCellClicked(text: String, row: Int, column: Int)
}

class TableMultipleScrollAdapter(private val rows: List<DataRow>?,
                                 var headers: HeaderRow?,
                                 var cellWidth: Int, var cellHeight: Int,
                                 var style: StyleConfiguration,
                                 var rowsHighlights: Map<Int, Highlight>? = null,
                                 var columnsHighlights: Map<Int, Highlight>? = null,
                                 var highlightsConflictStrategy: HighlightConflictStrategy = HighlightConflictStrategy.PriorityColumn,
                                 var tableClickListener: OnTableClickListener? = null
): RecyclerView.Adapter<TableMultipleScrollAdapter.TableRowViewHolder>() {

    private val internalCellClickedListener : OnTableClickListener = object : OnTableClickListener {
        override fun onCellClicked(text: String, row: Int, column: Int) {
            tableClickListener?.onCellClicked(text, row, column)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableRowViewHolder {
        return TableRowViewHolder(TableRowView(parent.context).run {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(cellHeight))
            this
        })
    }

    override fun getItemCount(): Int {
        return rows?.size ?: 0
    }

    override fun onBindViewHolder(holder: TableRowViewHolder, position: Int) {

        holder.view.removeAllViews()

        rows?.let {
            val rowData: DataRow = it[position]

            //add data with id  order
            headers?.tableInfo?.forEach { entry ->
                val data = rowData.tableInfo[entry.key] ?: ""
                addData(holder.view, data, position, entry.key)

            } ?: rowData.tableInfo.forEach { entry ->

                addData(holder.view, entry.value, position)
            }

        }
    }

    private fun addData(view: TableRowView, data: String, position: Int, headerID: Int? = null) {

        //check if the row has to be highlighted
        var styleToApply= rowsHighlights?.get(position)?.style

        //if no rows highlights found, check for columns highlights
        //in case of conflict, check the HighlightConflictStrategy applied
        headerID?.let { columnID ->
            styleToApply = when(highlightsConflictStrategy) {
                HighlightConflictStrategy.PriorityColumn -> {
                    columnsHighlights?.get(columnID)?.style ?: styleToApply
                }
                else -> {
                    styleToApply ?: columnsHighlights?.get(columnID)?.style
                }
            }
        }

        val backgroundColor = styleToApply?.cellDefaultBackgroundColor ?: style.cellDefaultBackgroundColor
        val textColor = styleToApply?.cellDefaultTextColor ?: style.cellDefaultTextColor
        val textSize = styleToApply?.cellTextSize ?: style.cellTextSize
        val textTypeface = styleToApply?.cellTextTypeface ?: style.cellTextTypeface
        val linesCount = styleToApply?.linesCount ?: style.linesCount
        val truncateStrategy = styleToApply?.truncateStrategy ?: style.truncateStrategy

        val cell = view.addData(data, cellWidth, cellHeight, backgroundColor, textColor, textSize, textTypeface, linesCount, truncateStrategy)

        cell.setOnClickListener { internalCellClickedListener.onCellClicked(data, position, headerID ?: 0)}
    }

    data class TableRowViewHolder(val view: TableRowView): RecyclerView.ViewHolder(view)
}