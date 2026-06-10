package com.qingguang.qingnote.tasks.service

import com.qingguang.qingnote.tasks.TaskAttachmentDraft
import org.tasks.data.dao.TaskAttachmentDao
import org.tasks.data.entity.Attachment
import org.tasks.data.entity.TaskAttachment
import javax.inject.Inject

class TaskAttachmentService @Inject constructor(
    private val taskAttachmentDao: TaskAttachmentDao,
) {
    suspend fun getAttachments(taskId: Long): List<TaskAttachmentDraft> =
        taskAttachmentDao.getAttachments(taskId).map {
            TaskAttachmentDraft(remoteId = it.remoteId, name = it.name, uri = it.uri)
        }

    suspend fun getAttachmentCount(taskId: Long): Int =
        taskAttachmentDao.getAttachments(taskId).size

    suspend fun syncAttachments(taskId: Long, drafts: List<TaskAttachmentDraft>) {
        val existing = taskAttachmentDao.getAttachments(taskId)
        val remaining = drafts.mapNotNull { it.remoteId }.toSet()
        val removed = existing.map { it.remoteId }.filter { it !in remaining }
        if (removed.isNotEmpty()) {
            taskAttachmentDao.delete(taskId, removed)
            cleanupOrphanAttachments(removed)
        }
        drafts
            .filter { it.remoteId == null }
            .forEach { attachFile(taskId, it.name, it.uri) }
    }

    private suspend fun cleanupOrphanAttachments(remoteIds: List<String>) {
        val orphans = remoteIds.mapNotNull { remoteId ->
            val references = taskAttachmentDao.getAttachmentReferenceCount(remoteId)
            if (references == 0) taskAttachmentDao.getAttachment(remoteId) else null
        }
        if (orphans.isNotEmpty()) {
            taskAttachmentDao.delete(orphans)
        }
    }

    private suspend fun attachFile(taskId: Long, name: String, uri: String) {
        if (name.isBlank() || uri.isBlank()) return
        val file = TaskAttachment(name = name, uri = uri)
        taskAttachmentDao.insert(file)
        val saved = taskAttachmentDao.getAttachment(file.remoteId) ?: return
        val savedId = saved.id ?: return
        taskAttachmentDao.insert(
            listOf(
                Attachment(
                    task = taskId,
                    fileId = savedId,
                    attachmentUid = saved.remoteId,
                )
            )
        )
    }
}
