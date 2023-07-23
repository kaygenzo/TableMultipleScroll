package io.github.kaygenzo.androidtable.internal

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.kaygenzo.androidtable.api.Highlight
import io.github.kaygenzo.androidtable.api.HighlightConflictStrategy
import io.github.kaygenzo.androidtable.api.OnTableClickListener
import io.github.kaygenzo.androidtable.api.StyleConfiguration
import io.github.kaygenzo.androidtable.internal.views.TableRowView

internal data class DataRow(val tableInfo: Map<Int, String>)
internal data class HeaderRow(val tableInfo: Map<Int, String>)

internal class AndroidTableAdapter(
    private val rows: List<DataRow>,
    var header: HeaderRow?,
    var cellWidth: Int,
    var cellHeight: Int,
    var style: StyleConfiguration,
    var rowsHighlights: Map<Int, Highlight>? = null,
    var columnsHighlights: Map<Int, Highlight>? = null,
    var highlightsConflictStrategy: HighlightConflictStrategy =
        HighlightConflictStrategy.PriorityColumn,
    var tableClickListener: OnTableClickListener? = null
) : RecyclerView.Adapter<AndroidTableAdapter.TableRowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableRowViewHolder {
        return TableRowViewHolder(TableRowView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(cellHeight)
            )
        })
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    override fun onBindViewHolder(holder: TableRowViewHolder, position: Int) {
        holder.view.removeAllViews()
        val rowData = rows[position]
        rowData.tableInfo.forEach { entry ->
            addData(holder.view, entry.value, position, entry.key)
        }
    }

    private fun addData(view: TableRowView, data: String, rowIndex: Int, columnIndex: Int) {
        //check if the row and column has to be highlighted
        val rowStyleToApply = rowsHighlights?.get(rowIndex)?.style
        val columnStyleToApply = columnsHighlights?.get(columnIndex)?.style

        //if no rows highlights found, check for columns highlights
        //in case of conflict, check the HighlightConflictStrategy applied
        val styleToApply = when (highlightsConflictStrategy) {
            HighlightConflictStrategy.PriorityColumn -> columnStyleToApply ?: rowStyleToApply
            else -> rowStyleToApply ?: columnStyleToApply
        }

        val backgroundColor =
            styleToApply?.cellDefaultBackgroundColor ?: style.cellDefaultBackgroundColor
        val textColor = styleToApply?.cellDefaultTextColor ?: style.cellDefaultTextColor
        val textSize = styleToApply?.cellTextSize ?: style.cellTextSize
        val textTypeface = styleToApply?.cellTextTypeface ?: style.cellTextTypeface
        val linesCount = styleToApply?.linesCount ?: style.linesCount
        val truncateStrategy = styleToApply?.truncateStrategy ?: style.truncateStrategy

        val cell = view.addData(
            data,
            cellWidth,
            StyleConfiguration(
                textSize,
                textTypeface,
                backgroundColor,
                textColor,
                linesCount,
                truncateStrategy
            )
        )

        cell.setOnClickListener {
            tableClickListener?.onCellClicked(data, rowIndex, columnIndex)
        }
    }

    data class TableRowViewHolder(val view: TableRowView) : RecyclerView.ViewHolder(view)
}