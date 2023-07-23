package io.github.kaygenzo.androidtable.internal.views

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import io.github.kaygenzo.androidtable.databinding.ViewTableMultipleScrollRowItemBinding
import io.github.kaygenzo.androidtable.api.StyleConfiguration

class TableRowView: LinearLayoutCompat {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var binding: ViewTableMultipleScrollRowItemBinding =
        ViewTableMultipleScrollRowItemBinding.inflate(LayoutInflater.from(context), this, true)

    fun addData(
        data: String,
        @DimenRes width: Int,
        style: StyleConfiguration
    ): View {
        val textView = TextView(context)
        val params = LayoutParams(
            resources.getDimensionPixelSize(width),
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        params.setMargins(1, 1, 1, 1)
        return textView.apply {
            layoutParams = params
            setBackgroundResource(style.cellDefaultBackgroundColor)
            setTextColor(ContextCompat.getColor(context, style.cellDefaultTextColor))
            typeface = style.cellTextTypeface
            setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(style.cellTextSize))
            gravity = Gravity.CENTER
            text = data
            setLines(style.linesCount)
            setSingleLine(style.linesCount == 1)
            ellipsize = style.truncateStrategy
            binding.mainTableRowContainer.addView(this)
        }
    }

    override fun removeAllViews() {
        binding.mainTableRowContainer.removeAllViews()
    }
}