package com.qingguang.qingnote.bean

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
@Entity
data class Tag(
    @PrimaryKey val tag: String,
    @ColumnInfo(name = "create_time") val createTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_time") val updateTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_collected") var isCollected: Boolean = false,
    @ColumnInfo(name = "is_deleted") var isDeleted: Boolean = false,
    @ColumnInfo(name = "is_city_tag") var isCityTag: Boolean = false,
    @ColumnInfo(name = "count") var count: Int = 0,
) : Parcelable {
    override fun toString(): String {
        return tag
    }
}