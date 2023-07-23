package io.github.kaygenzo.androidtable.api

interface OnTableClickListener {
    fun onCellClicked(text: String, row: Int, column: Int)
}