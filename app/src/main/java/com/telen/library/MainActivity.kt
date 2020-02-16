package com.telen.library

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.telen.library.widget.tablemultiscroll.views.MultipleScrollTableView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        multipleScrollTableView.setTopHeaderStyle(
            MultipleScrollTableView.StyleConfiguration(
                cellTextSize = R.dimen.default_table_multiple_scroll_text_size,
                cellTextTypeface = Typeface.DEFAULT_BOLD,
                cellDefaultTextColor = R.color.default_table_multiple_scroll_header_text_color,
                cellDefaultBackgroundColor = R.color.default_table_multiple_scroll_header_background_color,
                linesCount = 1,
                truncateStrategy = TextUtils.TruncateAt.END
        ))

        val leftHeaderData = mutableListOf<MultipleScrollTableView.CellConfiguration>()
        val topHeaderData = mutableListOf<MultipleScrollTableView.CellConfiguration>()
        val mainData = mutableListOf<List<MultipleScrollTableView.CellConfiguration>>()

        val rowCount = 40
        val columnCount = 30

        for (i in 0 until rowCount) {
            leftHeaderData.add(MultipleScrollTableView.CellConfiguration("Row_little_long_$i"))
        }

        for (i in 0 until columnCount) {
            topHeaderData.add(MultipleScrollTableView.CellConfiguration("Column_little_long_$i", i))
        }

        for (i in 0 until rowCount) {
            val data = mutableListOf<MultipleScrollTableView.CellConfiguration>()
            mainData.add(data)
            for (j in 0 until columnCount) {
                data.add(MultipleScrollTableView.CellConfiguration("${(i*columnCount) + j}", j))
            }
        }

        multipleScrollTableView.setLeftHeaderData(leftHeaderData)
        multipleScrollTableView.setTopHeaderData(topHeaderData)
        multipleScrollTableView.setMainData(mainData)
    }
}
