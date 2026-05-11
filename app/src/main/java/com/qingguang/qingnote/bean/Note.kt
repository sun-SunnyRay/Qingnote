package com.qingguang.qingnote.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Ignore
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class NoteShowBean(
    @Embedded val note: Note,
    @Relation(
        parentColumn = "note_id",
        entityColumn = "tag",
        associateBy = Junction(NoteTagCrossRef::class)
    ) val tagList: List<Tag>,
    @Relation(
        parentColumn = "note_id",
        entityColumn = "note_comment_id"
    ) val commentList: List<Comment>? = null,
    @Relation(
        parentColumn = "note_id",
        entityColumn = "noteId",
    )
    val reminders: List<Reminder> = listOf(),
    @Relation(
        parentColumn = "parent_note_id",
        entityColumn = "note_id"
    )
    val parentNote: Note? = null
) : Parcelable {
    fun doesMatchSearchQuery(query: String): Boolean {
        val matchingCombinations = listOf<String>(
            "${note.noteTitle}${note.content}",
            "${note.noteTitle} ${note.content}",
            "${note.noteTitle?.firstOrNull()} ${note.content.firstOrNull() ?: ""}"
        )
        return matchingCombinations.any {
            it.contains(query, true)
        }
    }
}

@Serializable
@Parcelize
@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "note_id") var noteId: Long = 0,
    @ColumnInfo(name = "note_title") var noteTitle: String? = null,
    @ColumnInfo(name = "note_content") var content: String = "",
    @ColumnInfo(name = "location_info") var locationInfo: String? = null,
    @ColumnInfo(name = "weather_info") var weatherInfo: String? = null,
    @ColumnInfo(name = "city") var city: String? = null,
    @ColumnInfo(name = "create_time") var createTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_time") var updateTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_collected") var isCollected: Boolean = false,
    @ColumnInfo(name = "is_deleted") var isDeleted: Boolean = false,
    @ColumnInfo(name = "parent_note_id") var parentNoteId: Long? = null,
    var attachments: List<Attachment> = arrayListOf(),
    @Ignore var isHide: Boolean = false,
) : Parcelable

@Serializable
@Parcelize
@Entity(
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("note_id"),
        childColumns = arrayOf("note_comment_id"),
        onDelete = CASCADE
    )]
)
data class Comment(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "note_comment_id", index = true) val noteCommentId: Long,
    var text: String = "",
    @ColumnInfo(name = "create_time") var createTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_time") var updateTime: Long = System.currentTimeMillis(),
) : Parcelable
