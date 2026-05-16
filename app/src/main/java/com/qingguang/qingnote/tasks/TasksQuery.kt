package com.qingguang.qingnote.tasks

enum class TaskViewFilter {
    ALL,
    TODAY,
    OVERDUE,
    UPCOMING,
    WITH_DUE_DATE,
    WITHOUT_DUE_DATE,
    COMPLETED,
}

enum class TaskSortOrder {
    MODIFIED_DESC,
    MODIFIED_ASC,
    CREATED_DESC,
    CREATED_ASC,
    DUE_DATE_ASC,
    DUE_DATE_DESC,
    START_DATE_ASC,
    START_DATE_DESC,
    PRIORITY_DESC,
    PRIORITY_ASC,
    TITLE_ASC,
    TITLE_DESC,
    COMPLETED_DESC,
    COMPLETED_ASC,
    MY_ORDER_ASC,
    MY_ORDER_DESC,
    LIST_ASC,
    LIST_DESC,
    AUTO,
}

enum class TaskSortMode {
    NONE,
    DUE_DATE,
    START_DATE,
    PRIORITY,
    TITLE,
    MODIFIED,
    CREATED,
    LIST,
    COMPLETED,
    MY_ORDER,
    AUTO,
}

data class TaskEditData(
    val title: String,
    val notes: String?,
    val priority: Int,
    val startDate: Long,
    val dueDate: Long,
    val recurrence: String?,
    val repeatFrom: Int,
    val tagNames: List<String>?,
    val subtasks: List<TaskSubtaskDraft>?,
    val reminders: List<TaskReminderDraft>?,
    val attachments: List<TaskAttachmentDraft>?,
    val addToCalendar: Boolean,
    val calendarEndAtDueTime: Boolean,
)

data class TaskEditExtras(
    val reminders: List<TaskReminderDraft> = emptyList(),
    val tagNames: List<String> = emptyList(),
    val subtasks: List<TaskSubtaskDraft> = emptyList(),
    val attachments: List<TaskAttachmentDraft> = emptyList(),
)

data class TaskReminderDraft(
    val id: Long? = null,
    val time: Long,
    val type: Int,
    val repeat: Int = 0,
    val interval: Long = 0L,
)

data class TaskSubtaskDraft(
    val id: Long? = null,
    val title: String,
    val completed: Boolean = false,
)

data class TaskAttachmentDraft(
    val remoteId: String? = null,
    val name: String,
    val uri: String,
)

data class TaskDrawerTag(
    val name: String,
    val count: Int,
    val color: Int? = null,
    val icon: String? = null,
    val order: Int = -1,
)

data class TaskListExtras(
    val tagNames: List<String> = emptyList(),
    val subtasks: List<TaskSubtaskDraft> = emptyList(),
    val subtaskCount: Int = 0,
    val completedSubtaskCount: Int = 0,
    val attachmentCount: Int = 0,
    val reminderCount: Int = 0,
)
