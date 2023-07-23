package io.github.kaygenzo.androidtable.api

import androidx.annotation.DimenRes
import io.github.kaygenzo.androidtable.R

class TableConfiguration(
    @DimenRes val topHeaderHeight: Int = R.dimen.default_table_multiple_scroll_top_header_height,
    @DimenRes val leftHeaderWidth: Int = R.dimen.default_table_multiple_scroll_left_header_width,
    @DimenRes val cellHeight: Int = R.dimen.default_table_multiple_scroll_cell_height,
    @DimenRes val cellWidth: Int = R.dimen.default_table_multiple_scroll_cell_width
)