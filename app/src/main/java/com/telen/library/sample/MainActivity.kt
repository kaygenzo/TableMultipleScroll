package com.telen.library.sample

import android.graphics.Typeface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.telen.library.R
import com.telen.library.databinding.ActivityMainBinding
import io.github.kaygenzo.androidtable.api.CellConfiguration
import io.github.kaygenzo.androidtable.api.Highlight
import io.github.kaygenzo.androidtable.api.HighlightConflictStrategy
import io.github.kaygenzo.androidtable.api.OnTableClickListener
import io.github.kaygenzo.androidtable.api.StyleConfiguration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        binding.multipleScrollTableView.setTopHeaderStyle(
            StyleConfiguration(
                cellTextTypeface = Typeface.SERIF,
                cellDefaultTextColor = R.color.colorAccent,
                cellDefaultBackgroundColor = R.color.colorPrimaryDark,
            )
        )

        val leftHeaderData = mutableListOf<CellConfiguration>()
        val topHeaderData = mutableListOf<CellConfiguration>()
        val mainData = mutableListOf<List<CellConfiguration>>()

        val rowCount = 40
        val columnCount = 30

        for (i in 0 until rowCount) {
            leftHeaderData.add(CellConfiguration("LH_$i"))
        }

        for (i in 0 until columnCount) {
            topHeaderData.add(CellConfiguration("TH_$i", i))
        }

        for (i in 0 until rowCount) {
            val data = mutableListOf<CellConfiguration>()
            mainData.add(data)
            for (j in 0 until columnCount) {
                data.add(CellConfiguration("${(i * columnCount) + j}", j))
            }
        }

        val columnHighlights = mutableListOf<Highlight>()
        columnHighlights.add(
            Highlight(
                0,
                StyleConfiguration(
                    cellDefaultBackgroundColor = R.color.colorAccent,
                    cellDefaultTextColor = R.color.colorPrimary
                )
            )
        )
        columnHighlights.add(
            Highlight(
                2,
                StyleConfiguration(
                    cellDefaultBackgroundColor = R.color.colorPrimaryDark,
                    cellDefaultTextColor = android.R.color.white
                )
            )
        )
        binding.multipleScrollTableView.setColumnHighlights(columnHighlights)

        val rowsHighlights = mutableListOf<Highlight>()
        rowsHighlights.add(
            Highlight(
                0,
                StyleConfiguration(
                    cellDefaultBackgroundColor = R.color.colorPrimary,
                    cellDefaultTextColor = R.color.colorAccent
                )
            )
        )
        rowsHighlights.add(
            Highlight(
                2,
                StyleConfiguration(
                    cellDefaultBackgroundColor = android.R.color.black,
                    cellDefaultTextColor = R.color.colorAccent
                )
            )
        )
        binding.multipleScrollTableView.setRowHighlights(rowsHighlights)

        binding.multipleScrollTableView.setHighlightsConflictStrategy(
            HighlightConflictStrategy.PriorityRow
        )

        binding.multipleScrollTableView.setOnTableClickListener(object : OnTableClickListener {
            override fun onCellClicked(text: String, row: Int, column: Int) {
                Toast.makeText(
                    this@MainActivity,
                    "text:$text row:$row column:$column",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        binding.multipleScrollTableView.setLeftHeaderData(leftHeaderData)
        binding.multipleScrollTableView.setTopHeaderData(topHeaderData)
        binding.multipleScrollTableView.setMainData(mainData)

        val spinner = Spinner(this).apply {
            adapter =
                ArrayAdapter(this@MainActivity, R.layout.adapter_item, arrayOf("Test1", "Test2"))
        }
        binding.multipleScrollTableView.setTopLeftCellCustomView(spinner)
    }
}
