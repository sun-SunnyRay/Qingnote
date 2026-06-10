package com.qingguang.qingnote.db.repo

import androidx.room.Query
import androidx.room.Transaction
import com.qingguang.qingnote.bean.Note
import com.qingguang.qingnote.bean.NoteShowBean
import com.qingguang.qingnote.bean.NoteTagCrossRef
import com.qingguang.qingnote.bean.Tag
import com.qingguang.qingnote.db.dao.NoteDao
import com.qingguang.qingnote.db.dao.NoteTagCrossRefDao
import com.qingguang.qingnote.db.dao.TagDao
import com.qingguang.qingnote.db.dao.TagNoteDao
import com.qingguang.qingnote.utils.CityRegexUtils
import com.qingguang.qingnote.utils.TopicUtils
import com.qingguang.qingnote.ui.page.SortTime
import kotlinx.coroutines.flow.Flow
import org.zeroturnaround.zip.FileSource.pair

class TagNoteRepo(
    private val noteDao: NoteDao,
    private val tagNoteDao: TagNoteDao,
    private val tagDao: TagDao,
    private val noteTagCrossRefDao: NoteTagCrossRefDao
) {

    fun queryAllNoteList(): List<Note> {
        return noteDao.queryAllData()
    }

    fun queryAllTagList(): List<Tag> {
        return tagDao.queryAllTagList().filterNot { it.tag.isBlank() }
    }

    fun queryAllTagFlow(): Flow<List<Tag>> = tagDao.queryAll()

    fun updateTag(tag: Tag) {
        tagDao.update(tag)
    }

    fun queryAllMemosFlow(sortTime: String): Flow<List<NoteShowBean>> {
        return when (sortTime) {
            SortTime.UPDATE_TIME_DESC.name -> {
                tagNoteDao.getAll("update_time", "desc")
            }

            SortTime.UPDATE_TIME_ASC.name -> {
                tagNoteDao.getAll("update_time", "asc")
            }

            SortTime.CREATE_TIME_DESC.name -> {
                tagNoteDao.getAll("create_time", "desc")
            }

            SortTime.CREATE_TIME_ASC.name -> {
                tagNoteDao.getAll("create_time", "asc")
            }

            else -> {
                tagNoteDao.getAll("update_time", "desc")
            }
        }
    }

    fun getAllRandom(): Flow<List<NoteShowBean>> = tagNoteDao.getAllRandom()

    fun queryAllNoteShowBeanList(): List<NoteShowBean> = tagNoteDao.getAllNoteWithTagList()

    fun countNoteListWithByTag(tagName: String): Int {
        return tagNoteDao.countNoteListWithByTag(tagName)
    }

    fun getNoteListWithByTag(tagName: String): Flow<List<NoteShowBean>> {
        return tagNoteDao.getNoteListWithByTag(tagName)
    }

    fun getTagListByNoteId(noteId: Long): Flow<List<Tag>> {
        return tagNoteDao.getTagListByNoteId(noteId)
    }

    @Query("")
    @Transaction
    fun insertOrUpdate(card: Note) {
        val tagList = TopicUtils.getTopicListByString(card.content)
//        if (card.locationInfo.isNullOrBlank()) {
//            val pair = CityRegexUtils.getCityByString(card.content.trim())
//            card.locationInfo = pair?.second
//            if (card.locationInfo != null) {
//                card.content = pair?.first ?: ""
//            }
//        }
        val noteId = noteDao.insert(card)
        if (tagList.isEmpty()) {
            val tempTag = Tag(tag = "")
            tagDao.insertOrUpdate(tempTag)
            noteTagCrossRefDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId = noteId, tag = tempTag.tag))
            return
        }
        tagList.forEach {
            tagDao.insertOrUpdate(it)
            noteTagCrossRefDao.insertNoteTagCrossRef(NoteTagCrossRef(noteId = noteId, tag = it.tag))
        }
    }

    @Query("")
    @Transaction
    fun deleteNote(card: Note, tags: List<Tag>) {
        noteDao.delete(card)
        tags.forEach {
            tagDao.deleteOrUpdate(it)
            noteTagCrossRefDao.deleteCrossRef(NoteTagCrossRef(noteId = card.noteId, tag = it.tag))
        }
    }

    fun queryNoteById(noteId: Int): Note = noteDao.queryById(noteId)

    fun getNoteShowBeanById(noteId: Long): NoteShowBean? = tagNoteDao.getNoteShowBeanById(noteId)

    fun getNoteShowBeanByIdFlow(noteId: Long): Flow<NoteShowBean?> = tagNoteDao.getNoteShowBeanByIdFlow(noteId)

    fun getNotesOnSelectedDate(selectedDate: String): List<NoteShowBean> = tagNoteDao.getNoteShowOnDate(selectedDate)

    suspend fun getAllDistinctYears(): List<String> {
        return tagNoteDao.getAllDistinctYears()
    }

    fun getNotesByYear(year: String): Flow<List<NoteShowBean>> = tagNoteDao.getNotesByYear(year)

    fun getNotesByCreateTimeRange(startTime: Long, endTime: Long): Flow<List<NoteShowBean>> = tagNoteDao.getNotesByCreateTimeRange(startTime, endTime)


    fun getAllLocationInfo(): Flow<List<String>> = noteDao.getAllLocationInfo()

    fun getNotesByLocationInfo(targetInfo: String): Flow<List<NoteShowBean>> = noteDao.getNotesByLocationInfo(targetInfo)

    fun clearLocationInfo(locationInfo: String) {
        noteDao.clearLocationInfo(locationInfo)
    }

    fun getCommentsByParentId(parentNoteId: Long): Flow<List<NoteShowBean>> = tagNoteDao.getCommentsByParentId(parentNoteId)

}