package org.tasks.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.tasks.data.Redacted
import org.tasks.data.db.Table

@Serializable
@Parcelize
@Entity(tableName = "userActivity")
data class UserActivity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    @Transient
    var id: Long? = null,
    @ColumnInfo(name = "remoteId")
    var remoteId: String? = Task.NO_UUID,
    @Redacted
    @ColumnInfo(name = "message")
    var message: String? = "",
    @ColumnInfo(name = "picture")
    var picture: String? = "",
    @ColumnInfo(name = "target_id")
    @Transient
    var targetId: String? = Task.NO_UUID,
    @ColumnInfo(name = "created_at")
    var created: Long? = 0L,
) : Parcelable {
    companion object {
        val TABLE = Table("userActivity")
        val TASK = TABLE.column("target_id")
        val MESSAGE = TABLE.column("message")
    }
}