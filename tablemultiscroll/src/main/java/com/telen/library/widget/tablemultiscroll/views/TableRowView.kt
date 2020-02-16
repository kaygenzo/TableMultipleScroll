package com.telen.library.widget.tablemultiscroll.views

import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.telen.library.widget.tablemultiscroll.R
import kotlinx.android.synthetic.main.view_table_multiple_scroll_row_item.view.*

class TableRowView: LinearLayoutCompat {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_table_multiple_scroll_row_item, this)
    }

    fun addData(data: String, viewTag: String, viewId: Int? = null,
                @DimenRes width: Int, @DimenRes height: Int,
                @ColorRes backgroundColor: Int, @ColorRes textColor: Int,
                @DimenRes textSize: Int, textTypeface: Typeface,
                linesCount: Int, truncateStrategy: TextUtils.TruncateAt): View {
        val textView = TextView(context)
        val params = LayoutParams(resources.getDimensionPixelSize(width), ViewGroup.LayoutParams.MATCH_PARENT)
        params.setMargins(1,1,1,1)
        val view = textView.run {
            layoutParams = params
            setBackgroundResource(backgroundColor)
            setTextColor(ContextCompat.getColor(context, textColor))
            this.typeface = textTypeface
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX , resources.getDimension(textSize))
            tag = viewTag
            viewId?.let {
                id = it
            }
            gravity = Gravity.CENTER
            text = data
            setLines(linesCount)
            setSingleLine(linesCount == 1)
            ellipsize = truncateStrategy
            this
        }

        mainTableRowContainer.addView(view)

        return view
    }

    override fun removeAllViews() {
        mainTableRowContainer.removeAllViews()
    }
}