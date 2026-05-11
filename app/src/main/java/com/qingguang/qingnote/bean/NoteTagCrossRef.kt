package com.qingguang.qingnote.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity(
    primaryKeys = ["note_id", "tag"],
    foreignKeys = [ForeignKey(
        entity = Note::class,
        parentColumns = arrayOf("note_id"),
        childColumns = arrayOf("note_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    ), ForeignKey(
        entity = Tag::class,
        parentColumns = arrayOf("tag"),
        childColumns = arrayOf("tag"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class NoteTagCrossRef(
    @ColumnInfo(name = "note_id") val noteId: Long, @ColumnInfo(index = true) val tag: String
) : Parcelable