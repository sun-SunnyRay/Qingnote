package org.tasks.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Update
import org.tasks.data.Logger
import org.tasks.data.TaskContainer
import org.tasks.data.UUIDHelper
import org.tasks.data.db.Database
import org.tasks.data.db.SuspendDbUtils.chunkedMap
import org.tasks.data.db.SuspendDbUtils.eachChunk
import org.tasks.data.entity.Alarm
import org.tasks.data.entity.Task
import org.tasks.data.sql.Criterion
import org.tasks.data.sql.Functions
import org.tasks.time.DateTimeUtils2

private const val MAX_TIME = 9999999999999

@Dao
abstract class TaskDao(private val database: Database) {

    @Query("SELECT * FROM tasks WHERE _id = :id LIMIT 1")
    abstract suspend fun fetch(id: Long): Task?

    suspend fun fetch(ids: List<Long>): List<Task> = ids.chunkedMap(this::fetchInternal)

    @Query("SELECT * FROM tasks WHERE _id IN (:ids)")
    internal abstract suspend fun fetchInternal(ids: List<Long>): List<Task>

    @Query("SELECT COUNT(1) FROM tasks WHERE timerStart > 0 AND deleted = 0")
    abstract suspend fun activeTimers(): Int

    @Query("SELECT COUNT(1) FROM tasks INNER JOIN alarms ON tasks._id = alarms.task WHERE deleted = 0 AND completed = 0 AND type = ${Alarm.TYPE_SNOOZE}")
    abstract suspend fun snoozedReminders(): Int

    @Query("SELECT COUNT(1) FROM tasks INNER JOIN notification ON tasks._id = notification.task")
    abstract suspend fun hasNotifications(): Int

    @Query("SELECT * FROM tasks WHERE remoteId = :remoteId")
    abstract suspend fun fetch(remoteId: String): Task?

    @Query("SELECT * FROM tasks")
    abstract suspend fun getAll(): List<Task>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :start AND :end AND deleted = 0")
    abstract suspend fun getTasksByDueDate(start: Long, end: Long): List<Task>

    suspend fun fetchTasks(query: String): List<TaskContainer> {
        val start = DateTimeUtils2.currentTimeMillis()
        val result = fetchRaw(RoomRawQuery(query))
        val end = DateTimeUtils2.currentTimeMillis()
        Logger.v("TaskDao") { "${end - start}ms: ${query.replace(Regex("\\s+"), " ").trim()}" }
        return result
    }

    @RawQuery
    internal abstract suspend fun fetchRaw(query: RoomRawQuery): List<TaskContainer>

    suspend fun count(query: String): Int {
        val start = DateTimeUtils2.currentTimeMillis()
        val result = countRaw(RoomRawQuery(query))
        val end = DateTimeUtils2.currentTimeMillis()
        Logger.v("TaskDao") { "${end - start}ms: ${query.replace(Regex("\\s+"), " ").trim()}" }
        return result
    }

    @RawQuery
    internal abstract suspend fun countRaw(query: RoomRawQuery): Int

    suspend fun touch(ids: List<Long>, now: Long = DateTimeUtils2.currentTimeMillis()) =
        ids.eachChunk { internalTouch(it, now) }

    @Query("UPDATE tasks SET modified = :now WHERE _id in (:ids)")
    internal abstract suspend fun internalTouch(ids: List<Long>, now: Long = DateTimeUtils2.currentTimeMillis())

    @Query("UPDATE tasks SET collapsed = :collapsed, modified = :now WHERE _id IN (:ids)")
    abstract suspend fun setCollapsed(ids: List<Long>, collapsed: Boolean, now: Long = DateTimeUtils2.currentTimeMillis())

    suspend fun getChildren(id: Long): List<Long> = getChildren(listOf(id))

    @Query("""
WITH RECURSIVE recursive_tasks (task) AS (
    SELECT _id
    FROM tasks
    WHERE parent IN (:ids)
    UNION ALL
    SELECT _id
    FROM tasks
             INNER JOIN recursive_tasks ON recursive_tasks.task = tasks.parent
    WHERE tasks.deleted = 0)
SELECT task
FROM recursive_tasks
    """)
    abstract suspend fun getChildren(ids: List<Long>): List<Long>

    @Query("""
WITH RECURSIVE recursive_tasks (task, parent) AS (
    SELECT _id, parent FROM tasks WHERE _id = :parent
    UNION ALL
    SELECT _id, tasks.parent FROM tasks
        INNER JOIN recursive_tasks ON recursive_tasks.parent = tasks._id
    WHERE tasks.deleted = 0
)
SELECT task
FROM recursive_tasks
""")
    abstract suspend fun getParents(parent: Long): List<Long>

    @Insert
    abstract suspend fun insert(task: Task): Long

    suspend fun update(task: Task, original: Task? = null): Boolean {
        if (!task.insignificantChange(original)) {
            task.modificationDate = DateTimeUtils2.currentTimeMillis()
        }
        return updateInternal(task) == 1
    }

    @Update
    internal abstract suspend fun updateInternal(task: Task): Int

    @Update
    abstract suspend fun updateInternal(tasks: List<Task>)

    suspend fun createNew(task: Task): Long {
        task.id = Task.NO_ID
        if (task.creationDate == 0L) {
            task.creationDate = DateTimeUtils2.currentTimeMillis()
        }
        if (Task.isUuidEmpty(task.remoteId)) {
            task.remoteId = UUIDHelper.newUUID()
        }
        val insert = insert(task)
        task.id = insert
        return task.id
    }

    /** Generates SQL clauses  */
    object TaskCriteria {
        /** @return tasks that have not yet been completed or deleted
         */
        fun activeAndVisible(): Criterion = Criterion.and(
            Task.COMPLETION_DATE.lte(0),
            Task.DELETION_DATE.lte(0),
            Task.HIDE_UNTIL.lte(Functions.now())
        )
    }
}