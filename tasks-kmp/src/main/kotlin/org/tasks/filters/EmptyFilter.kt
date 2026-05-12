package org.tasks.filters

import kotlinx.parcelize.Parcelize

@Parcelize
data class EmptyFilter(
    override val sql: String? = "WHERE 0",
    override val title: String = ""
) : Filter() {
    override fun areItemsTheSame(other: FilterListItem): Boolean = false
}