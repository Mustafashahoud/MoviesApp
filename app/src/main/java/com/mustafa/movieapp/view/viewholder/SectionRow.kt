package com.mustafa.movieapp.view.viewholder

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class SectionRow(
    var section: Int = 0,
    var row: Int = 0
) {

    fun nextSection() {
        this.section++
        this.row = 0
    }

    fun nextRow() = row++
}