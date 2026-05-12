package org.tasks.filters

import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterImpl(
    override val title: String = "",
    override val sql: String? = null,
    override val valuesForNewTasks: String? = null,
    override val icon: String? = null,
    override val tint: Int = 0,
) : Filter() {
    override fun areItemsTheSame(other: FilterListItem): Boolean {
        return other is Filter && sql == other.sql
    }
}
