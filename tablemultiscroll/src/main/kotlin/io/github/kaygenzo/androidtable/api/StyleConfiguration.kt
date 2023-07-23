package io.github.kaygenzo.androidtable.api

import android.graphics.Typeface
import android.text.TextUtils
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import io.github.kaygenzo.androidtable.R

data class StyleConfiguration(
    @DimenRes val cellTextSize: Int = R.dimen.default_table_multiple_scroll_text_size,
    val cellTextTypeface: Typeface = Typeface.DEFAULT,
    @ColorRes val cellDefaultBackgroundColor: Int =
        R.color.default_table_multiple_scroll_cell_background_color,
    @ColorRes val cellDefaultTextColor: Int = R.color.default_table_multiple_scroll_cell_text_color,
    val linesCount: Int = 1,
    val truncateStrategy: TextUtils.TruncateAt = TextUtils.TruncateAt.END
)