package com.telen.library.widget.tablemultiscroll.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.telen.library.widget.tablemultiscroll.views.MultipleScrollTableView
import com.telen.library.widget.tablemultiscroll.views.TableRowView

data class DataRow(val tableInfo: Map<Int, String>)
data class HeaderRow(val tableInfo: Map<Int, String>)

class TableMultipleScrollAdapter(private val rows: List<DataRow>?,
                                 var headers: HeaderRow?,
                                 var cellWidth: Int, var cellHeight: Int,
                                 var style: MultipleScrollTableView.StyleConfiguration
): RecyclerView.Adapter<TableMultipleScrollAdapter.TableRowViewHolder>() {

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

        val backgroundColor = style.cellDefaultBackgroundColor
        val textColor = style.cellDefaultTextColor
        val textSize = style.cellTextSize
        val textTypeface = style.cellTextTypeface
        val linesCount = style.linesCount
        val truncateStrategy = style.truncateStrategy

        holder.view.removeAllViews()

        rows?.let {
            val rowData: DataRow = it[position]
            headers?.tableInfo?.forEach { entry ->
                val data = rowData.tableInfo[entry.key] ?: ""
                holder.view.addData(data, "", null, cellWidth, cellHeight,
                        backgroundColor, textColor, textSize, textTypeface, linesCount, truncateStrategy)
            } ?: rowData.tableInfo.forEach { entry ->
                holder.view.addData(entry.value, "", null, cellWidth, cellHeight,
                        backgroundColor, textColor, textSize, textTypeface, linesCount, truncateStrategy)
            }
        }
    }

    data class TableRowViewHolder(val view: TableRowView): RecyclerView.ViewHolder(view)
}