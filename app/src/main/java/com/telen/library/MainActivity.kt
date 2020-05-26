package com.telen.library

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.telen.library.widget.tablemultiscroll.adapter.OnTableClickListener
import com.telen.library.widget.tablemultiscroll.views.CellConfiguration
import com.telen.library.widget.tablemultiscroll.views.Highlight
import com.telen.library.widget.tablemultiscroll.views.HighlightConflictStrategy
import com.telen.library.widget.tablemultiscroll.views.StyleConfiguration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        multipleScrollTableView.setTopHeaderStyle(
            StyleConfiguration(
                cellTextSize = R.dimen.default_table_multiple_scroll_text_size,
                cellTextTypeface = Typeface.DEFAULT_BOLD,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
                linesCount = 1,
                truncateStrategy = TextUtils.TruncateAt.END
        ))

        val leftHeaderData = mutableListOf<CellConfiguration>()
        val topHeaderData = mutableListOf<CellConfiguration>()
        val mainData = mutableListOf<List<CellConfiguration>>()

        val rowCount = 40
        val columnCount = 30

        for (i in 0 until rowCount) {
            leftHeaderData.add(CellConfiguration("Row_little_long_$i"))
        }

        for (i in 0 until columnCount) {
            topHeaderData.add(CellConfiguration("Column_little_long_$i", i))
        }

        for (i in 0 until rowCount) {
            val data = mutableListOf<CellConfiguration>()
            mainData.add(data)
            for (j in 0 until columnCount) {
                data.add(CellConfiguration("${(i*columnCount) + j}", j))
            }
        }

        val columnHighlights = mutableListOf<Highlight>()
        columnHighlights.add(Highlight(0, StyleConfiguration(cellDefaultBackgroundColor = R.color.colorAccent, cellDefaultTextColor = R.color.colorPrimary)))
        columnHighlights.add(Highlight(2, StyleConfiguration(cellDefaultBackgroundColor = R.color.colorPrimaryDark, cellDefaultTextColor = android.R.color.white)))
        multipleScrollTableView.setRowHighlights(columnHighlights)

        val rowsHighlights = mutableListOf<Highlight>()
        rowsHighlights.add(Highlight(0, StyleConfiguration(cellDefaultBackgroundColor = R.color.colorPrimary, cellDefaultTextColor = R.color.colorAccent)))
        rowsHighlights.add(Highlight(2, StyleConfiguration(cellDefaultBackgroundColor = android.R.color.black, cellDefaultTextColor = R.color.colorAccent)))
        multipleScrollTableView.setColumnHighlights(rowsHighlights)

        multipleScrollTableView.setHighlightsConflictStrategy(HighlightConflictStrategy.PriorityColumn)

        multipleScrollTableView.setOnTableClickListener(object : OnTableClickListener {
            override fun onCellClicked(text: String, row: Int, column: Int) {
                Toast.makeText(this@MainActivity, "text:$text row:$row column:$column", Toast.LENGTH_SHORT).show()
            }
        })

        multipleScrollTableView.setLeftHeaderData(leftHeaderData)
        multipleScrollTableView.setTopHeaderData(topHeaderData)
        multipleScrollTableView.setMainData(mainData)
    }
}
