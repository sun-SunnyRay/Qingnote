package org.tasks.filters

import kotlinx.parcelize.Parcelize
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Task
import org.tasks.data.sql.Criterion
import org.tasks.data.sql.QueryTemplate
import org.tasks.themes.TasksIcons

@Parcelize
data class MyTasksFilter(
    override val title: String,
    override var filterOverride: String? = null,
) : AstridOrderingFilter() {
    override val icon: String
        get() = TasksIcons.ALL_INBOX
    override val sql: String
        get() = QueryTemplate()
            .where(
                Criterion.and(
                    TaskDao.TaskCriteria.activeAndVisible(),
                    Task.PARENT.eq(0)
                )
            ).toString()

    override fun areItemsTheSame(other: FilterListItem): Boolean {
        return other is MyTasksFilter
    }

    companion object {
        fun create() = MyTasksFilter("filter_my_tasks")
    }
}
